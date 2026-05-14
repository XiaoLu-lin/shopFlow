<template>
  <view class="page" role="main">
    <view class="status-card">
      <view class="status-icon" :class="statusClass">{{ statusIcon }}</view>
      <text class="title" role="heading" aria-level="1">{{ title }}</text>
      <text class="desc">{{ description }}</text>
      <text v-if="orderInfo?.orderSn" class="order-copy">订单编号：{{ orderInfo.orderSn }}</text>
      <text v-if="shouldAutoRedirect" class="redirect-copy">3 秒后自动跳转到订单列表</text>
    </view>

    <view class="submit-btn" role="button" @click="goOrderList">查看订单</view>
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
  padding: 48rpx 20rpx 20rpx;
  background: linear-gradient(180deg, #ffffff 0%, #f6f8fb 100%);
}

.status-card {
  padding: 36rpx 24rpx;
  border-radius: 12rpx;
  background: #ffffff;
  box-shadow: 0 10rpx 24rpx rgba(23, 32, 51, 0.06);
  text-align: center;
}

.status-icon {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 112rpx;
  height: 112rpx;
  margin: 0 auto;
  border-radius: 999px;
  font-size: 44rpx;
}

.status-icon--success {
  background: #edf9f0;
  color: #1f9d55;
}

.status-icon--warning {
  background: #fff7e8;
  color: #b7791f;
}

.status-icon--error {
  background: #fff1f0;
  color: #d84d3a;
}

.title {
  display: block;
  margin-top: 20rpx;
  font-size: 30rpx;
  line-height: 1.3;
  color: #172033;
}

.desc,
.order-copy,
.redirect-copy {
  display: block;
  margin-top: 10rpx;
  font-size: 22rpx;
  line-height: 1.45;
  color: #748194;
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
