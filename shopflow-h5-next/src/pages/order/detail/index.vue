<template>
  <view class="page">
    <view v-if="orderInfo" class="summary-card">
      <view class="summary-head">
        <view>
          <text class="eyebrow">ShopFlow Order</text>
          <text class="title">{{ orderInfo.orderStatusText }}</text>
          <text class="desc">订单编号 {{ orderInfo.orderSn }}</text>
        </view>
        <text class="summary-price">¥ {{ orderInfo.actualPrice }}</text>
      </view>
      <text class="summary-copy">下单时间 {{ orderInfo.addTime || '--' }}</text>
    </view>

    <view v-if="orderGoods.length" class="goods-list">
      <view v-for="item in orderGoods" :key="item.id" class="goods-card">
        <image class="goods-image" :src="item.picUrl" mode="aspectFill" />
        <view class="goods-body">
          <text class="goods-name">{{ item.goodsName }}</text>
          <text class="goods-spec">{{ item.specifications.join(' / ') || '默认规格' }}</text>
          <view class="goods-bottom">
            <text class="goods-price">¥ {{ item.price }}</text>
            <text class="goods-count">x {{ item.number }}</text>
          </view>
        </view>
      </view>
    </view>

    <view v-if="orderInfo" class="section-card">
      <text class="section-title">收货信息</text>
      <text class="section-copy">{{ orderInfo.consignee }} {{ orderInfo.mobile }}</text>
      <text class="section-copy section-copy--strong">{{ orderInfo.address }}</text>
    </view>

    <view v-if="orderInfo" class="section-card">
      <text class="section-title">订单信息</text>
      <view class="info-row"><text class="info-label">订单编号</text><text class="info-value">{{ orderInfo.orderSn }}</text></view>
      <view class="info-row"><text class="info-label">下单时间</text><text class="info-value">{{ orderInfo.addTime || '--' }}</text></view>
      <view class="info-row"><text class="info-label">订单状态</text><text class="info-value info-value--brand">{{ orderInfo.orderStatusText }}</text></view>
      <view class="info-row"><text class="info-label">订单备注</text><text class="info-value">{{ orderInfo.message || '--' }}</text></view>
    </view>

    <view v-if="orderInfo" class="section-card">
      <text class="section-title">金额明细</text>
      <view class="info-row"><text class="info-label">商品金额</text><text class="info-value">¥ {{ orderInfo.goodsPrice }}</text></view>
      <view class="info-row"><text class="info-label">运费</text><text class="info-value">¥ {{ orderInfo.freightPrice }}</text></view>
      <view class="info-row"><text class="info-label">优惠券</text><text class="info-value">- ¥ {{ orderInfo.couponPrice || 0 }}</text></view>
      <view class="info-row info-row--strong"><text class="info-label">实付金额</text><text class="pay-price">¥ {{ orderInfo.actualPrice }}</text></view>
    </view>

    <view v-if="orderInfo?.expNo || orderInfo?.expName" class="section-card">
      <text class="section-title">物流信息</text>
      <view class="info-row"><text class="info-label">快递公司</text><text class="info-value">{{ orderInfo.expName || orderInfo.expCode || '--' }}</text></view>
      <view class="info-row"><text class="info-label">快递单号</text><text class="info-value">{{ orderInfo.expNo || '--' }}</text></view>
    </view>

    <view v-if="orderInfo" class="action-row">
      <view v-if="orderInfo.handleOption.cancel" class="ghost-btn" @click="handleOrderAction(orderInfo.id, 'cancel')">取消订单</view>
      <view v-if="orderInfo.handleOption.delete" class="ghost-btn" @click="handleOrderAction(orderInfo.id, 'delete')">删除订单</view>
      <view v-if="orderInfo.handleOption.refund" class="ghost-btn ghost-btn--brand" @click="goAftersaleApply(orderInfo.id)">申请售后</view>
      <view v-if="orderInfo.handleOption.confirm" class="ghost-btn ghost-btn--brand" @click="handleOrderAction(orderInfo.id, 'confirm')">确认收货</view>
      <view v-if="orderInfo.handleOption.pay" class="primary-btn" @click="goPay(orderInfo.id)">去支付</view>
    </view>
  </view>
</template>

<script setup lang="ts">
import { onShow } from '@dcloudio/uni-app'
import { ref } from 'vue'
import { cancelUserOrder, confirmUserOrder, deleteUserOrder, fetchOrderDetail } from '@/entities/order/api'

type UniPageWithOptions = {
  options?: Record<string, unknown>
}

const pageOptions = (getCurrentPages()[getCurrentPages().length - 1] as UniPageWithOptions | undefined)?.options || {}
const orderId = typeof pageOptions.orderId === 'string' ? pageOptions.orderId : ''

const orderInfo = ref<Awaited<ReturnType<typeof fetchOrderDetail>>['orderInfo'] | null>(null)
const orderGoods = ref<Awaited<ReturnType<typeof fetchOrderDetail>>['orderGoods']>([])

bootstrap()
onShow(() => {
  void bootstrap()
})

async function bootstrap() {
  if (!orderId) {
    return
  }

  try {
    const result = await fetchOrderDetail(orderId)
    orderInfo.value = result.orderInfo
    orderGoods.value = result.orderGoods || []
  } catch (error) {
    console.error(error)
    uni.showToast({
      title: '订单详情加载失败',
      icon: 'none',
    })
  }
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
    uni.showToast({
      title: '订单操作失败',
      icon: 'none',
    })
  }
}
</script>

<style scoped lang="scss">
.page {
  min-height: 100vh;
  padding: 24rpx 20rpx 40rpx;
  background: rgb(var(--sf-color-page));
}

.summary-card,
.goods-card,
.section-card {
  border: 2rpx solid rgb(var(--sf-color-line));
  border-radius: 16rpx;
  background: rgb(var(--sf-color-shell));
  box-shadow: var(--sf-shadow-card);
}

.summary-card,
.section-card {
  padding: 24rpx;
}

.summary-card {
  background: linear-gradient(135deg, rgb(var(--sf-color-brand-soft)) 0%, rgb(var(--sf-color-shell)) 72%);
}

.summary-head,
.goods-bottom,
.info-row,
.action-row {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16rpx;
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
.summary-copy,
.section-copy,
.info-label,
.goods-spec,
.goods-count {
  display: block;
  margin-top: 8rpx;
  font-size: 21rpx;
  line-height: 1.45;
  color: rgb(var(--sf-color-text-secondary));
}

.summary-price,
.goods-price,
.pay-price {
  font-size: 30rpx;
  line-height: 1.2;
  font-weight: 600;
  color: rgb(var(--sf-color-price));
}

.summary-copy {
  margin-top: 18rpx;
}

.goods-list,
.section-card,
.action-row {
  margin-top: 18rpx;
}

.goods-list {
  display: grid;
  gap: 14rpx;
}

.goods-card {
  display: flex;
  gap: 16rpx;
  padding: 18rpx;
}

.goods-image {
  width: 148rpx;
  height: 148rpx;
  flex-shrink: 0;
  border-radius: var(--sf-radius-image);
  background: rgb(var(--sf-color-mist));
}

.goods-body {
  min-width: 0;
  flex: 1;
}

.goods-name,
.section-title,
.info-value {
  display: block;
  font-size: 25rpx;
  line-height: 1.36;
  color: rgb(var(--sf-color-ink));
}

.section-copy--strong {
  color: rgb(var(--sf-color-ink));
}

.info-row {
  margin-top: 14rpx;
}

.info-row--strong {
  margin-top: 18rpx;
  padding-top: 18rpx;
  border-top: 2rpx solid rgb(var(--sf-color-divider));
}

.info-value--brand {
  color: rgb(var(--sf-color-brand-deep));
}

.action-row {
  flex-wrap: wrap;
  justify-content: flex-end;
}

.ghost-btn,
.primary-btn {
  display: flex;
  align-items: center;
  justify-content: center;
  min-width: 132rpx;
  height: 74rpx;
  padding: 0 22rpx;
  border-radius: 999px;
  font-size: 21rpx;
  line-height: 1.2;
}

.ghost-btn {
  border: 2rpx solid rgb(var(--sf-color-line));
  background: rgb(var(--sf-color-shell));
  color: rgb(var(--sf-color-text-secondary));
}

.ghost-btn--brand {
  border-color: rgb(var(--sf-color-brand-soft));
  background: rgb(var(--sf-color-brand-soft));
  color: rgb(var(--sf-color-brand-deep));
}

.primary-btn {
  background: linear-gradient(135deg, rgb(var(--sf-color-brand)) 0%, rgb(var(--sf-color-brand-light)) 100%);
  color: rgb(var(--sf-color-shell));
}
</style>
