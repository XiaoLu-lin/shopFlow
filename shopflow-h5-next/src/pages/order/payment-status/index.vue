<template>
  <view class="page">
    <scroll-view scroll-y class="page-scroll">
      <view class="page-scroll-inner">
        <view class="result-card">
          <view class="result-icon" :class="statusClass">{{ statusIcon }}</view>
          <text class="result-eyebrow">ShopFlow</text>
          <text class="result-title">{{ title }}</text>
          <text class="result-desc">{{ description }}</text>
          <text v-if="orderInfo?.orderSn" class="result-copy">订单编号：{{ orderInfo.orderSn }}</text>
          <text v-if="shouldAutoRedirect" class="result-copy">3 秒后自动跳转到订单列表</text>
        </view>
      </view>
    </scroll-view>

    <view class="footer-bar">
      <view v-if="orderId" class="ghost-btn" @click="goOrderDetail">
        订单详情
      </view>
      <view class="submit-btn" @click="goOrderList">查看订单</view>
    </view>
  </view>
</template>

<script setup lang="ts">
import { computed, ref } from 'vue'
import { onUnload } from '@dcloudio/uni-app'
import { fetchOrderDetail } from '@/entities/order/api'
import {
  clearPrepayData,
  createTimeoutController,
  resolvePaymentResultCopy,
} from '@/features/order/payment-utils'

type UniPageWithOptions = {
  options?: Record<string, unknown>
}

const pageOptions = (getCurrentPages()[getCurrentPages().length - 1] as UniPageWithOptions | undefined)?.options || {}
const status = typeof pageOptions.status === 'string' ? pageOptions.status : 'failed'
const orderId = typeof pageOptions.orderId === 'string' ? pageOptions.orderId : ''

const orderInfo = ref<Awaited<ReturnType<typeof fetchOrderDetail>>['orderInfo'] | null>(null)
const redirectTimer = createTimeoutController()

const resultCopy = computed(() =>
  resolvePaymentResultCopy({
    routeStatus: status,
    orderStatusText: orderInfo.value?.orderStatusText,
  }),
)

const title = computed(() => resultCopy.value.title)
const description = computed(() => resultCopy.value.description)
const shouldAutoRedirect = computed(() => resultCopy.value.autoRedirect)
const statusClass = computed(() => {
  if (resultCopy.value.accent === 'success') return 'result-icon--success'
  if (resultCopy.value.accent === 'warning' || resultCopy.value.accent === 'pending') return 'result-icon--warning'
  return 'result-icon--error'
})
const statusIcon = computed(() => {
  if (resultCopy.value.accent === 'success') return '✓'
  if (resultCopy.value.accent === 'warning' || resultCopy.value.accent === 'pending') return '!'
  return '×'
})

clearPrepayData()
void bootstrap()
onUnload(() => {
  redirectTimer.clear()
})

async function bootstrap() {
  if (!orderId || status !== 'success') {
    return
  }

  try {
    const result = await fetchOrderDetail(orderId)
    orderInfo.value = result.orderInfo
  } catch (error) {
    console.error(error)
  }

  redirectTimer.clear()
  if (shouldAutoRedirect.value) {
    redirectTimer.schedule(() => {
      goOrderList()
    }, 3000)
  }
}

function goOrderDetail() {
  if (!orderId) {
    return
  }

  redirectTimer.clear()
  uni.redirectTo({
    url: `/pages/order/detail/index?orderId=${orderId}`,
  })
}

function goOrderList() {
  redirectTimer.clear()
  uni.redirectTo({
    url: '/pages/user/order-list/index?active=0',
  })
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
  padding: 28rpx 20rpx calc(176rpx + env(safe-area-inset-bottom));
}

.result-card,
.footer-bar {
  border: 2rpx solid rgb(var(--sf-color-line));
  border-radius: var(--sf-radius-card);
  background: rgb(var(--sf-color-shell));
  box-shadow: var(--sf-shadow-soft);
}

.result-card {
  padding: 30rpx 24rpx;
  text-align: center;
}

.result-icon {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 108rpx;
  height: 108rpx;
  margin: 0 auto;
  border-radius: 999px;
  font-size: 42rpx;
  font-weight: 700;
}

.result-icon--success {
  background: rgba(var(--sf-color-success), 0.12);
  color: rgb(var(--sf-color-success));
}

.result-icon--warning {
  background: rgba(var(--sf-color-warning), 0.14);
  color: rgb(var(--sf-color-warning));
}

.result-icon--error {
  background: rgb(var(--sf-color-price-soft));
  color: rgb(var(--sf-color-price));
}

.result-eyebrow,
.result-desc,
.result-copy {
  display: block;
  color: rgb(var(--sf-color-text-secondary));
}

.result-eyebrow {
  margin-top: 20rpx;
  font-size: 18rpx;
  line-height: 1.2;
  letter-spacing: 0.12em;
  text-transform: uppercase;
  color: rgb(var(--sf-color-brand));
}

.result-title {
  display: block;
  margin-top: 8rpx;
  font-size: 30rpx;
  line-height: 1.3;
  font-weight: 700;
  color: rgb(var(--sf-color-ink));
}

.result-desc,
.result-copy {
  margin-top: 10rpx;
  font-size: 21rpx;
  line-height: 1.5;
}

.footer-bar {
  display: flex;
  align-items: center;
  justify-content: flex-end;
  gap: 12rpx;
  margin: 0 20rpx 20rpx;
  padding: 16rpx 18rpx calc(16rpx + env(safe-area-inset-bottom));
}

.ghost-btn,
.submit-btn {
  display: flex;
  align-items: center;
  justify-content: center;
  min-width: 136rpx;
  height: 72rpx;
  padding: 0 24rpx;
  border-radius: 999px;
  font-size: 22rpx;
  line-height: 1.2;
  font-weight: 600;
}

.ghost-btn {
  border: 2rpx solid rgb(var(--sf-color-line));
  background: rgb(var(--sf-color-shell));
  color: rgb(var(--sf-color-text-secondary));
}

.submit-btn {
  background: linear-gradient(135deg, rgb(var(--sf-color-brand)) 0%, rgb(var(--sf-color-brand-light)) 100%);
  color: rgb(var(--sf-color-shell));
}
</style>
