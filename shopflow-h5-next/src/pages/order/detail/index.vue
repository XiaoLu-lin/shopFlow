<template>
  <view class="page" role="main">
    <view class="hero-card">
      <text class="title" role="heading" aria-level="1">订单详情</text>
      <text class="desc">已接回旧站订单结构，当前支持查看和基础订单操作。</text>
    </view>

    <view class="goods-list" v-if="orderGoods.length">
      <view v-for="item in orderGoods" :key="item.id" class="goods-card">
        <image class="goods-image" :src="item.picUrl" mode="aspectFill" />
        <view class="goods-body">
          <text class="goods-name">{{ item.goodsName }}</text>
          <text class="goods-spec">{{ item.specifications.join(' / ') || '默认规格' }}</text>
          <view class="goods-foot">
            <text class="goods-price">¥ {{ item.price }}</text>
            <text class="goods-count">x {{ item.number }}</text>
          </view>
        </view>
      </view>
    </view>

    <view v-if="orderInfo" class="panel">
      <text class="panel-title">收货信息</text>
      <text class="panel-copy">{{ orderInfo.consignee }} {{ orderInfo.mobile }}</text>
      <text class="panel-copy">{{ orderInfo.address }}</text>
    </view>

    <view v-if="orderInfo" class="panel">
      <text class="panel-title">订单信息</text>
      <view class="row"><text class="label">订单编号</text><text class="value">{{ orderInfo.orderSn }}</text></view>
      <view class="row"><text class="label">下单时间</text><text class="value">{{ orderInfo.addTime || '--' }}</text></view>
      <view class="row"><text class="label">订单状态</text><text class="value value--brand">{{ orderInfo.orderStatusText }}</text></view>
      <view class="row"><text class="label">订单备注</text><text class="value">{{ orderInfo.message || '--' }}</text></view>
    </view>

    <view v-if="orderInfo" class="panel">
      <text class="panel-title">金额明细</text>
      <view class="row"><text class="label">商品金额</text><text class="value">¥ {{ orderInfo.goodsPrice }}</text></view>
      <view class="row"><text class="label">运费</text><text class="value">¥ {{ orderInfo.freightPrice }}</text></view>
      <view class="row"><text class="label">优惠券</text><text class="value">- ¥ {{ orderInfo.couponPrice || 0 }}</text></view>
      <view class="row row--strong"><text class="label">实付</text><text class="price">¥ {{ orderInfo.actualPrice }}</text></view>
    </view>

    <view v-if="orderInfo?.expNo || orderInfo?.expName" class="panel">
      <text class="panel-title">物流信息</text>
      <view class="row"><text class="label">快递公司</text><text class="value">{{ orderInfo.expName || orderInfo.expCode || '--' }}</text></view>
      <view class="row"><text class="label">快递单号</text><text class="value">{{ orderInfo.expNo || '--' }}</text></view>
    </view>

    <view v-if="orderInfo" class="action-row">
      <view
        v-if="orderInfo.handleOption.cancel"
        class="ghost-btn"
        role="button"
        @click="handleOrderAction(orderInfo.id, 'cancel')"
      >取消订单</view>
      <view
        v-if="orderInfo.handleOption.delete"
        class="ghost-btn"
        role="button"
        @click="handleOrderAction(orderInfo.id, 'delete')"
      >删除订单</view>
      <view
        v-if="orderInfo.handleOption.comment && commentGoodsId"
        class="ghost-btn"
        role="button"
        @click="goCommentPost(commentGoodsId)"
      >去评价</view>
      <view
        v-if="orderInfo.handleOption.refund"
        class="ghost-btn"
        role="button"
        @click="handleRefund(orderInfo.id)"
      >申请退款</view>
      <view
        v-if="orderInfo.handleOption.aftersale"
        class="ghost-btn"
        role="button"
        @click="goAftersaleApply(orderInfo.id)"
      >申请售后</view>
      <view
        v-if="orderInfo.handleOption.confirm"
        class="dark-btn"
        role="button"
        @click="handleOrderAction(orderInfo.id, 'confirm')"
      >确认收货</view>
      <view
        v-if="orderInfo.handleOption.pay"
        class="primary-btn"
        role="button"
        @click="goPay(orderInfo.id)"
      >去支付</view>
    </view>
  </view>
</template>

<script setup lang="ts">
import { onShow } from '@dcloudio/uni-app'
import { computed, onBeforeUnmount, onMounted, ref } from 'vue'
import { cancelUserOrder, confirmUserOrder, deleteUserOrder, fetchOrderDetail, refundUserOrder } from '@/entities/order/api'

type UniPageWithOptions = {
  options?: Record<string, unknown>
}

const orderInfo = ref<Awaited<ReturnType<typeof fetchOrderDetail>>['orderInfo'] | null>(null)
const orderGoods = ref<Awaited<ReturnType<typeof fetchOrderDetail>>['orderGoods']>([])
const commentGoodsId = computed(() => {
  const firstGoods = orderGoods.value[0]

  if (!firstGoods || firstGoods.comment) {
    return 0
  }

  return Number(firstGoods.id || 0)
})

bootstrap()
onShow(() => {
  void bootstrap()
})

onMounted(() => {
  if (typeof window === 'undefined') {
    return
  }

  window.addEventListener('hashchange', handleHashChange)
})

onBeforeUnmount(() => {
  if (typeof window === 'undefined') {
    return
  }

  window.removeEventListener('hashchange', handleHashChange)
})

async function bootstrap() {
  const currentOrderId = resolveOrderId()

  if (!currentOrderId) {
    orderInfo.value = null
    orderGoods.value = []
    return
  }

  try {
    const result = await fetchOrderDetail(currentOrderId)
    orderInfo.value = result.orderInfo
    orderGoods.value = result.orderGoods || []
  } catch (error) {
    console.error(error)
  }
}

function resolveOrderId() {
  if (typeof window !== 'undefined') {
    const hash = window.location.hash || ''
    const queryIndex = hash.indexOf('?')

    if (queryIndex >= 0) {
      const params = new URLSearchParams(hash.slice(queryIndex + 1))
      const value = params.get('orderId')

      if (value) {
        return value
      }
    }
  }

  const pageOptions = (getCurrentPages()[getCurrentPages().length - 1] as UniPageWithOptions | undefined)?.options || {}
  return typeof pageOptions.orderId === 'string' ? pageOptions.orderId : ''
}

function handleHashChange() {
  void bootstrap()
}

function goPay(id: string) {
  uni.navigateTo({
    url: `/pages/order/payment/index?orderId=${id}`,
  })
}

function goAftersaleApply(id: string) {
  uni.navigateTo({
    url: `/pages/user/refund-apply/index?orderId=${id}`,
  })
}

async function handleRefund(id: string) {
  try {
    await refundUserOrder(Number(id))
    await bootstrap()
    uni.showToast({
      title: '已申请订单退款',
      icon: 'none',
    })
  } catch (error) {
    console.error(error)
  }
}

function goCommentPost(goodsId: number) {
  if (!goodsId) {
    return
  }

  uni.navigateTo({
    url: `/pages/order/comment-post/index?goodsId=${goodsId}`,
  })
}

async function handleOrderAction(id: string, action: 'cancel' | 'delete' | 'confirm') {
  try {
    if (action === 'cancel') {
      await cancelUserOrder(Number(id))
    } else if (action === 'delete') {
      await deleteUserOrder(Number(id))
    } else {
      await confirmUserOrder(Number(id))
    }
    await bootstrap()
    uni.showToast({
      title: action === 'cancel' ? '已取消订单' : action === 'delete' ? '已删除订单' : '已确认收货',
      icon: 'none',
    })
  } catch (error) {
    console.error(error)
  }
}
</script>

<style scoped lang="scss">
.page {
  min-height: 100vh;
  padding: 20rpx 20rpx 40rpx;
  background: linear-gradient(180deg, #ffffff 0%, #f6f8fb 100%);
}

.hero-card,
.panel,
.goods-card {
  border-radius: 12rpx;
  background: #ffffff;
  box-shadow: 0 10rpx 24rpx rgba(23, 32, 51, 0.06);
}

.hero-card,
.panel {
  padding: 22rpx;
}

.panel,
.goods-list,
.action-row {
  margin-top: 16rpx;
}

.title,
.panel-title {
  display: block;
  font-size: 28rpx;
  line-height: 1.3;
  color: #172033;
}

.desc,
.panel-copy,
.label {
  display: block;
  margin-top: 8rpx;
  font-size: 22rpx;
  line-height: 1.4;
  color: #748194;
}

.goods-list {
  display: grid;
  gap: 14rpx;
}

.goods-card {
  display: flex;
  gap: 16rpx;
  padding: 16rpx;
}

.goods-image {
  width: 176rpx;
  height: 176rpx;
  border-radius: 10rpx;
  background: #f3f6fb;
}

.goods-body {
  min-width: 0;
  flex: 1;
}

.goods-name,
.value {
  font-size: 24rpx;
  line-height: 1.35;
  color: #172033;
}

.goods-spec,
.goods-count {
  display: block;
  margin-top: 8rpx;
  font-size: 21rpx;
  line-height: 1.35;
  color: #748194;
}

.goods-foot,
.row,
.action-row {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16rpx;
}

.goods-price,
.price {
  font-size: 28rpx;
  line-height: 1.2;
  font-weight: 600;
  color: #172033;
}

.row {
  margin-top: 12rpx;
}

.value--brand {
  color: #1677ff;
}

.ghost-btn,
.dark-btn,
.primary-btn {
  display: flex;
  align-items: center;
  justify-content: center;
  height: 76rpx;
  padding: 0 20rpx;
  border-radius: 12rpx;
  font-size: 22rpx;
}

.ghost-btn {
  background: #ffffff;
  color: #5f6b7c;
  box-shadow: 0 10rpx 24rpx rgba(23, 32, 51, 0.06);
}

.dark-btn {
  background: #172033;
  color: #ffffff;
}

.primary-btn {
  background: #1677ff;
  color: #ffffff;
}
</style>
