<template>
  <view class="page">
    <view class="page-head">
      <view>
        <text class="eyebrow">ShopFlow Pay</text>
        <text class="title">支付订单</text>
        <text class="desc">确认支付金额与方式后继续完成本次订单。</text>
      </view>
    </view>

    <view v-if="orderInfo" class="summary-card">
      <view class="summary-row">
        <text class="summary-label">订单编号</text>
        <text class="summary-value">{{ orderInfo.orderSn }}</text>
      </view>
      <view class="summary-row summary-row--strong">
        <text class="summary-label">实付金额</text>
        <text class="summary-price">¥ {{ orderInfo.actualPrice }}</text>
      </view>
    </view>

    <view class="section-card">
      <text class="section-title">选择支付方式</text>
      <view class="pay-list">
        <view
          v-for="option in paymentOptions"
          :key="option.key"
          class="pay-item"
          :class="{
            'pay-item--active': payWay === option.key,
            'pay-item--disabled': !option.enabled,
          }"
          @click="selectPayWay(option.key, option.enabled)"
        >
          <view class="pay-copy">
            <text class="pay-title">{{ option.title }}</text>
            <text class="pay-desc">{{ option.description }}</text>
          </view>
          <view class="pay-meta">
            <text class="pay-state">{{ option.stateLabel }}</text>
            <text v-if="payWay === option.key" class="pay-check">✓</text>
          </view>
        </view>
      </view>
    </view>

    <view
      class="submit-btn"
      :class="{ 'submit-btn--disabled': payWay !== 'wx' }"
      @click="pay"
    >
      去支付
    </view>
  </view>
</template>

<script setup lang="ts">
import { computed, ref } from 'vue'
import { fetchOrderDetail, submitOrderH5Pay, submitOrderPrepay } from '@/entities/order/api'
import { buildPaymentStatusRouteQuery, persistPrepayData } from '@/features/order/payment-utils'
import { resolvePaymentMethodOptions } from '@/features/order/order-display-utils'

type UniPageWithOptions = {
  options?: Record<string, unknown>
}

const pageOptions = (getCurrentPages()[getCurrentPages().length - 1] as UniPageWithOptions | undefined)?.options || {}
const orderId = typeof pageOptions.orderId === 'string' ? pageOptions.orderId : ''
const orderIdsText = typeof pageOptions.orderIds === 'string' ? pageOptions.orderIds : orderId
const orderIds = orderIdsText.split(',').map((item) => item.trim()).filter(Boolean)

const payWay = ref<'wx' | 'ali'>('wx')
const orderInfo = ref<Awaited<ReturnType<typeof fetchOrderDetail>>['orderInfo'] | null>(null)
const inWechat = isWechatBrowser()
const paymentOptions = computed(() => resolvePaymentMethodOptions(inWechat))

declare global {
  interface Window {
    WeixinJSBridge?: {
      invoke: (
        name: 'getBrandWCPayRequest',
        payload: object,
        callback: (result: { err_msg: string }) => void,
      ) => void
    }
  }
}

bootstrap()

async function bootstrap() {
  if (!orderId) {
    return
  }

  try {
    const result = await fetchOrderDetail(orderId)
    orderInfo.value = result.orderInfo
  } catch (error) {
    console.error(error)
    uni.showToast({
      title: '支付信息加载失败',
      icon: 'none',
    })
  }
}

function selectPayWay(nextWay: 'wx' | 'ali', enabled: boolean) {
  if (!enabled) {
    uni.showToast({
      title: '该支付方式暂未开放',
      icon: 'none',
    })
    return
  }

  payWay.value = nextWay
}

function goStatus(status: 'success' | 'cancel' | 'failed') {
  const query = buildPaymentStatusRouteQuery(orderIds)
  const search = buildQueryString({
    status,
    ...query,
  })
  uni.redirectTo({
    url: `/pages/order/payment-status/index?${search}`,
  })
}

async function pay() {
  if (!orderId) {
    return
  }

  if (payWay.value !== 'wx') {
    uni.showToast({
      title: '该支付方式暂未开放',
      icon: 'none',
    })
    return
  }

  try {
    // #ifdef MP-WEIXIN
    await payInMiniProgram()
    return
    // #endif

    // #ifndef MP-WEIXIN
    await payInH5()
    // #endif
  } catch (error) {
    console.error(error)

    if (isPaymentCancelled(error)) {
      goStatus('cancel')
      return
    }

    goStatus('failed')
  }
}

async function payInMiniProgram() {
  const prepayResult = await submitOrderPrepay(orderIds)

  await uni.requestPayment({
    provider: 'wxpay',
    timeStamp: String(prepayResult.timeStamp || ''),
    nonceStr: String(prepayResult.nonceStr || ''),
    package: String(prepayResult.packageValue || ''),
    signType: resolveMpSignType(prepayResult.signType),
    paySign: String(prepayResult.paySign || ''),
  })

  goStatus('success')
}

async function payInH5() {
  if (inWechat) {
    const prepayResult = await submitOrderPrepay(orderIds)
    const payload = persistPrepayData(prepayResult)
    const bridge = window.WeixinJSBridge
    if (!bridge || !payload) {
      goStatus('success')
      return
    }

    const readyBridge = bridge as NonNullable<typeof window.WeixinJSBridge>
    const readyPayload = payload as NonNullable<typeof payload>

    readyBridge.invoke('getBrandWCPayRequest', readyPayload, (response) => {
      if (response.err_msg === 'get_brand_wcpay_request:ok') {
        goStatus('success')
        return
      }
      if (response.err_msg === 'get_brand_wcpay_request:cancel') {
        goStatus('cancel')
        return
      }
      goStatus('failed')
    })
    return
  }

  const h5PayResult = await submitOrderH5Pay(orderIds)
  if (h5PayResult.mwebUrl) {
    window.location.href = h5PayResult.mwebUrl
    return
  }

  goStatus('success')
}

function isWechatBrowser() {
  // #ifdef H5
  return /micromessenger/i.test(window.navigator.userAgent)
  // #endif

  return false
}

function buildQueryString(params: Record<string, string>) {
  return Object.entries(params)
    .filter(([, value]) => value !== undefined && value !== null && value !== '')
    .map(([key, value]) => `${encodeURIComponent(key)}=${encodeURIComponent(value)}`)
    .join('&')
}

function isPaymentCancelled(error: unknown) {
  if (!error || typeof error !== 'object') {
    return false
  }

  const errMsg = 'errMsg' in error ? String(error.errMsg || '') : ''
  return errMsg.includes('cancel')
}

function resolveMpSignType(signType?: string): 'MD5' | 'RSA' | 'HMAC-SHA256' {
  if (signType === 'RSA' || signType === 'HMAC-SHA256') {
    return signType
  }

  return 'MD5'
}
</script>

<style scoped lang="scss">
.page {
  min-height: 100vh;
  padding: 24rpx 20rpx 40rpx;
  background: rgb(var(--sf-color-page));
}

.page-head,
.summary-card,
.section-card {
  border: 2rpx solid rgb(var(--sf-color-line));
  border-radius: 16rpx;
  background: rgb(var(--sf-color-shell));
  box-shadow: var(--sf-shadow-card);
}

.page-head,
.summary-card,
.section-card {
  padding: 24rpx;
}

.page-head {
  background: linear-gradient(135deg, rgb(var(--sf-color-brand-soft)) 0%, rgb(var(--sf-color-shell)) 72%);
}

.eyebrow {
  display: block;
  font-size: 18rpx;
  line-height: 1.2;
  letter-spacing: 2rpx;
  text-transform: uppercase;
  color: rgb(var(--sf-color-brand-deep));
}

.title {
  display: block;
  margin-top: 10rpx;
  font-size: 34rpx;
  line-height: 1.24;
  font-weight: 600;
  color: rgb(var(--sf-color-ink));
}

.desc,
.summary-label,
.summary-value,
.pay-desc {
  display: block;
  margin-top: 8rpx;
  font-size: 21rpx;
  line-height: 1.45;
  color: rgb(var(--sf-color-text-secondary));
}

.desc {
  margin-top: 10rpx;
  font-size: 22rpx;
}

.summary-card,
.section-card,
.submit-btn {
  margin-top: 18rpx;
}

.summary-row {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16rpx;
}

.summary-row--strong {
  margin-top: 16rpx;
  padding-top: 16rpx;
  border-top: 2rpx solid rgb(var(--sf-color-divider));
}

.summary-price {
  font-size: 34rpx;
  line-height: 1.2;
  font-weight: 600;
  color: rgb(var(--sf-color-price));
}

.section-title,
.pay-title {
  display: block;
  font-size: 26rpx;
  line-height: 1.35;
  color: rgb(var(--sf-color-ink));
}

.pay-list {
  display: grid;
  gap: 14rpx;
  margin-top: 18rpx;
}

.pay-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16rpx;
  padding: 20rpx 18rpx;
  border: 2rpx solid rgb(var(--sf-color-line));
  border-radius: 16rpx;
  background: rgb(var(--sf-color-shell));
}

.pay-item--active {
  border-color: rgb(var(--sf-color-brand));
  background: rgb(var(--sf-color-brand-soft));
}

.pay-item--disabled {
  opacity: 0.72;
}

.pay-copy {
  min-width: 0;
  flex: 1;
}

.pay-meta {
  display: flex;
  flex-direction: column;
  align-items: flex-end;
  gap: 10rpx;
}

.pay-state {
  padding: 8rpx 14rpx;
  border-radius: 999px;
  background: rgba(var(--sf-color-shell), 0.9);
  font-size: 19rpx;
  line-height: 1.2;
  color: rgb(var(--sf-color-brand-deep));
}

.pay-check {
  font-size: 22rpx;
  line-height: 1;
  color: rgb(var(--sf-color-brand));
}

.submit-btn {
  display: flex;
  align-items: center;
  justify-content: center;
  height: 88rpx;
  border-radius: 999px;
  background: linear-gradient(135deg, rgb(var(--sf-color-brand)) 0%, rgb(var(--sf-color-brand-light)) 100%);
  font-size: 24rpx;
  font-weight: 600;
  color: rgb(var(--sf-color-shell));
}

.submit-btn--disabled {
  background: rgb(var(--sf-color-line));
  color: rgb(var(--sf-color-text-secondary));
}
</style>
