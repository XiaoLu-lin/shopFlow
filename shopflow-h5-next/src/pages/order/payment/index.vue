<template>
  <view class="page" role="main">
    <view class="hero-card">
      <text class="title" role="heading" aria-level="1">支付订单</text>
      <text class="desc">当前先保持旧站“微信支付优先”的兼容路径。</text>
    </view>

    <view v-if="orderInfo" class="panel">
      <view class="row">
        <text class="label">订单编号</text>
        <text class="value">{{ orderInfo.orderSn }}</text>
      </view>
      <view class="row row--strong">
        <text class="label">实付金额</text>
        <text class="price">¥ {{ orderInfo.actualPrice }}</text>
      </view>
    </view>

    <view class="panel">
      <text class="panel-title">选择支付方式</text>
      <view class="pay-list">
        <view class="pay-item" :class="{ 'pay-item--active': payWay === 'wx' }" role="button" @click="payWay = 'wx'">
          <view>
            <text class="pay-title">微信支付</text>
            <text class="pay-copy">{{ inWechat ? '当前在微信内，可优先走 JSAPI' : '当前环境会优先尝试 H5 支付地址' }}</text>
          </view>
          <text class="pay-state">已选</text>
        </view>
        <view class="pay-item" :class="{ 'pay-item--active': payWay === 'ali' }" role="button" @click="payWay = 'ali'">
          <view>
            <text class="pay-title">支付宝</text>
            <text class="pay-copy">保留入口，链路待后续补齐</text>
          </view>
          <text class="pay-state">可选</text>
        </view>
      </view>
    </view>

    <view class="submit-btn" role="button" @click="pay">去支付</view>
  </view>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { fetchOrderDetail, submitOrderH5Pay, submitOrderPrepay } from '@/entities/order/api'
import { buildPaymentStatusRouteQuery, persistPrepayData } from '@/features/order/payment-utils'

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
  }
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

  if (payWay.value === 'ali') {
    uni.showToast({
      title: '支付宝待补齐',
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
    orderInfo: {
      orderIds,
    },
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
  padding: 20rpx;
  background: linear-gradient(180deg, #ffffff 0%, #f6f8fb 100%);
}

.hero-card,
.panel {
  padding: 22rpx;
  border-radius: 12rpx;
  background: #ffffff;
  box-shadow: 0 10rpx 24rpx rgba(23, 32, 51, 0.06);
}

.panel {
  margin-top: 16rpx;
}

.title {
  display: block;
  font-size: 28rpx;
  line-height: 1.3;
  color: #172033;
}

.desc,
.label,
.pay-copy {
  display: block;
  margin-top: 8rpx;
  font-size: 22rpx;
  line-height: 1.4;
  color: #748194;
}

.row {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16rpx;
}

.row--strong {
  margin-top: 14rpx;
}

.value,
.pay-title {
  font-size: 24rpx;
  line-height: 1.3;
  color: #172033;
}

.price {
  font-size: 32rpx;
  line-height: 1.2;
  font-weight: 600;
  color: #172033;
}

.panel-title {
  display: block;
  font-size: 25rpx;
  line-height: 1.3;
  color: #172033;
}

.pay-list {
  display: grid;
  gap: 12rpx;
  margin-top: 14rpx;
}

.pay-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16rpx;
  padding: 18rpx;
  border: 2rpx solid #e7edf5;
  border-radius: 12rpx;
  background: #ffffff;
}

.pay-item--active {
  border-color: #1677ff;
  background: #edf5ff;
}

.pay-state {
  font-size: 21rpx;
  color: #1677ff;
}

.submit-btn {
  display: flex;
  align-items: center;
  justify-content: center;
  height: 84rpx;
  margin-top: 20rpx;
  border-radius: 12rpx;
  background: #1677ff;
  font-size: 24rpx;
  color: #ffffff;
}
</style>
