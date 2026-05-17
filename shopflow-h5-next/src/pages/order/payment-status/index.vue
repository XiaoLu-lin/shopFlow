<template>
  <view class="page">
    <view class="status-card">
      <view class="status-icon" :class="statusClass">{{ statusIcon }}</view>
      <text class="eyebrow">ShopFlow Result</text>
      <text class="title">{{ title }}</text>
      <text class="desc">{{ description }}</text>
      <text v-if="orderInfo?.orderSn" class="order-copy">订单编号：{{ orderInfo.orderSn }}</text>
      <text v-if="shouldAutoRedirect" class="redirect-copy">3 秒后自动跳转到订单列表</text>
    </view>

    <view class="submit-btn" @click="goOrderList">查看订单</view>
  </view>
</template>

<script setup lang="ts">
import { computed, ref } from 'vue'
import { fetchOrderDetail } from '@/entities/order/api'
import { clearPrepayData, resolvePaymentResultCopy } from '@/features/order/payment-utils'

type UniPageWithOptions = {
  options?: Record<string, unknown>
}

const pageOptions = (getCurrentPages()[getCurrentPages().length - 1] as UniPageWithOptions | undefined)?.options || {}
const status = typeof pageOptions.status === 'string' ? pageOptions.status : 'failed'
const orderId = typeof pageOptions.orderId === 'string' ? pageOptions.orderId : ''

const orderInfo = ref<Awaited<ReturnType<typeof fetchOrderDetail>>['orderInfo'] | null>(null)

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
  if (resultCopy.value.accent === 'success') return 'status-icon--success'
  if (resultCopy.value.accent === 'warning' || resultCopy.value.accent === 'pending') return 'status-icon--warning'
  return 'status-icon--error'
})
const statusIcon = computed(() => {
  if (resultCopy.value.accent === 'success') return '✓'
  if (resultCopy.value.accent === 'warning' || resultCopy.value.accent === 'pending') return '!'
  return '×'
})

clearPrepayData()
bootstrap()

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

  if (shouldAutoRedirect.value) {
    setTimeout(() => {
      goOrderList()
    }, 3000)
  }
}

function goOrderList() {
  uni.redirectTo({
    url: '/pages/user/order-list/index?active=0',
  })
}
</script>

<style scoped lang="scss">
.page {
  min-height: 100vh;
  padding: 60rpx 20rpx 20rpx;
  background: rgb(var(--sf-color-page));
}

.status-card {
  padding: 40rpx 28rpx;
  border: 2rpx solid rgb(var(--sf-color-line));
  border-radius: 20rpx;
  background: rgb(var(--sf-color-shell));
  box-shadow: var(--sf-shadow-soft);
  text-align: center;
}

.status-icon {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 120rpx;
  height: 120rpx;
  margin: 0 auto;
  border-radius: 999px;
  font-size: 46rpx;
}

.status-icon--success {
  background: rgba(var(--sf-color-success), 0.12);
  color: rgb(var(--sf-color-success));
}

.status-icon--warning {
  background: rgba(var(--sf-color-warning), 0.14);
  color: rgb(var(--sf-color-warning));
}

.status-icon--error {
  background: rgb(var(--sf-color-price-soft));
  color: rgb(var(--sf-color-price));
}

.eyebrow {
  display: block;
  margin-top: 22rpx;
  font-size: 18rpx;
  line-height: 1.2;
  letter-spacing: 2rpx;
  text-transform: uppercase;
  color: rgb(var(--sf-color-brand-deep));
}

.title {
  display: block;
  margin-top: 12rpx;
  font-size: 34rpx;
  line-height: 1.24;
  font-weight: 600;
  color: rgb(var(--sf-color-ink));
}

.desc,
.order-copy,
.redirect-copy {
  display: block;
  margin-top: 12rpx;
  font-size: 22rpx;
  line-height: 1.5;
  color: rgb(var(--sf-color-text-secondary));
}

.submit-btn {
  display: flex;
  align-items: center;
  justify-content: center;
  height: 88rpx;
  margin-top: 20rpx;
  border-radius: 999px;
  background: linear-gradient(135deg, rgb(var(--sf-color-brand)) 0%, rgb(var(--sf-color-brand-light)) 100%);
  font-size: 24rpx;
  font-weight: 600;
  color: rgb(var(--sf-color-shell));
}
</style>
