<template>
  <view class="page">
    <scroll-view scroll-y class="page-scroll">
      <view class="page-scroll-inner">
        <view class="hero-card">
          <text class="hero-eyebrow">ShopFlow</text>
          <text class="hero-title">支付订单</text>
        </view>

        <view v-if="orderInfo" class="summary-card">
          <view class="summary-row">
            <view class="summary-copy">
              <text class="summary-title">订单编号</text>
              <text class="summary-meta">{{ orderInfo.orderSn }}</text>
            </view>
            <text class="summary-price">¥ {{ orderInfo.actualPrice }}</text>
          </view>
        </view>

        <view class="field-card">
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
              <text class="pay-state">{{ payWay === option.key ? '已选' : option.stateLabel }}</text>
            </view>
          </view>
        </view>

        <view class="field-card">
          <view class="summary-row">
            <view class="summary-copy">
              <text class="summary-title">支付提醒</text>
              <text class="summary-meta">支付成功后会自动跳转到订单结果页。</text>
            </view>
            <text class="safe-tag">安全支付</text>
          </view>
        </view>
      </view>
    </scroll-view>

    <view class="footer-bar">
      <view class="footer-main">
        <text class="footer-label">确认后继续支付</text>
        <text class="footer-copy">保持当前支付链路与回跳逻辑不变</text>
      </view>
      <view class="submit-btn" :class="{ 'submit-btn--disabled': payWay !== 'wx' }" @click="pay">
        去支付
      </view>
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
  display: flex;
  flex-direction: column;
  min-height: 0;
  height: calc(100vh - var(--window-top, 0px) - var(--window-bottom, 0px));
  background:
    radial-gradient(circle at top left, rgba(109, 140, 184, 0.14), rgba(109, 140, 184, 0) 24%),
    linear-gradient(180deg, rgba(244, 247, 251, 0.96) 0%, rgb(var(--sf-color-page)) 100%);
  overflow: hidden;
}

.page-scroll {
  flex: 1;
  min-height: 0;
}

.page-scroll-inner {
  padding: 18rpx 20rpx calc(176rpx + env(safe-area-inset-bottom));
}

.hero-card,
.summary-card,
.field-card,
.footer-bar {
  border: 2rpx solid rgb(var(--sf-color-line));
  border-radius: var(--sf-radius-card);
  background: rgb(var(--sf-color-shell));
  box-shadow: var(--sf-shadow-soft);
}

.hero-card,
.summary-card,
.field-card {
  padding: 18rpx;
}

.hero-card {
  background:
    radial-gradient(circle at top right, rgba(209, 223, 241, 0.72) 0%, rgba(209, 223, 241, 0) 38%),
    linear-gradient(145deg, rgba(251, 252, 254, 0.98) 0%, rgba(238, 244, 251, 0.98) 100%);
}

.hero-eyebrow {
  display: block;
  font-size: 18rpx;
  line-height: 1.2;
  letter-spacing: 0.12em;
  text-transform: uppercase;
  color: rgb(var(--sf-color-brand));
}

.hero-title,
.summary-title,
.section-title,
.pay-title {
  display: block;
  color: rgb(var(--sf-color-ink));
}

.hero-title {
  margin-top: 6rpx;
  font-size: 30rpx;
  line-height: 1.3;
  font-weight: 700;
}

.summary-card,
.field-card {
  margin-top: 14rpx;
}

.summary-row,
.footer-bar,
.pay-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16rpx;
}

.summary-copy,
.pay-copy,
.footer-main {
  min-width: 0;
  flex: 1;
}

.summary-title,
.section-title,
.pay-title {
  font-size: 24rpx;
  line-height: 1.35;
  font-weight: 600;
}

.summary-meta,
.pay-desc,
.footer-label,
.footer-copy {
  display: block;
  margin-top: 6rpx;
  font-size: 20rpx;
  line-height: 1.45;
  color: rgb(var(--sf-color-text-secondary));
}

.summary-price {
  font-size: 28rpx;
  line-height: 1.2;
  font-weight: 700;
  color: rgb(var(--sf-color-price));
}

.pay-list {
  display: grid;
  gap: 12rpx;
  margin-top: 14rpx;
}

.pay-item {
  padding: 16rpx;
  border-radius: var(--sf-radius-card);
  border: 2rpx solid rgb(var(--sf-color-line));
  background: linear-gradient(180deg, rgba(247, 250, 253, 0.98) 0%, rgba(255, 255, 255, 0.98) 100%);
}

.pay-item--active {
  border-color: rgba(var(--sf-color-brand), 0.24);
  background: linear-gradient(145deg, rgba(237, 243, 248, 0.98) 0%, rgba(223, 231, 241, 0.98) 100%);
}

.pay-item--disabled {
  opacity: 0.62;
}

.pay-state,
.safe-tag {
  flex-shrink: 0;
  padding: 8rpx 14rpx;
  border-radius: 999px;
  background: rgb(var(--sf-color-brand-soft));
  font-size: 18rpx;
  line-height: 1.2;
  color: rgb(var(--sf-color-brand-deep));
}

.footer-bar {
  margin: 0 20rpx 20rpx;
  padding: 16rpx 18rpx calc(16rpx + env(safe-area-inset-bottom));
}

.submit-btn {
  display: flex;
  align-items: center;
  justify-content: center;
  min-width: 136rpx;
  height: 72rpx;
  padding: 0 24rpx;
  border-radius: 999px;
  background: linear-gradient(135deg, rgb(var(--sf-color-brand)) 0%, rgb(var(--sf-color-brand-light)) 100%);
  font-size: 22rpx;
  line-height: 1.2;
  font-weight: 600;
  color: rgb(var(--sf-color-shell));
}

.submit-btn--disabled {
  opacity: 0.45;
}
</style>
