<template>
  <view class="page">
    <scroll-view scroll-y class="page-scroll">
      <view class="page-scroll-inner">
        <view v-if="orderInfo" class="hero-card">
          <text class="hero-eyebrow">ShopFlow</text>
          <text class="hero-title">{{ orderInfo.orderStatusText || '订单详情' }}</text>
        </view>

        <view v-if="orderInfo" class="summary-card">
          <view class="summary-row">
            <view class="summary-copy">
              <text class="summary-title">订单编号 {{ orderInfo.orderSn }}</text>
              <text class="summary-meta">下单时间 {{ orderInfo.addTime || '--' }}</text>
            </view>
            <text class="summary-price">¥ {{ orderInfo.actualPrice }}</text>
          </view>
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

        <view v-if="orderInfo" class="field-card">
          <text class="section-title">收货信息</text>
          <text class="section-copy">{{ orderInfo.consignee }} {{ orderInfo.mobile }}</text>
          <text class="section-copy section-copy--strong">{{ orderInfo.address }}</text>
        </view>

        <view v-if="orderInfo" class="field-card">
          <text class="section-title">订单信息</text>
          <view class="info-row">
            <text class="info-label">订单状态</text>
            <text class="info-value info-value--brand">{{ orderInfo.orderStatusText }}</text>
          </view>
          <view class="info-row">
            <text class="info-label">订单编号</text>
            <text class="info-value">{{ orderInfo.orderSn }}</text>
          </view>
          <view class="info-row">
            <text class="info-label">下单时间</text>
            <text class="info-value">{{ orderInfo.addTime || '--' }}</text>
          </view>
          <view class="info-row">
            <text class="info-label">订单备注</text>
            <text class="info-value">{{ orderInfo.message || '--' }}</text>
          </view>
        </view>

        <view v-if="orderInfo" class="field-card">
          <text class="section-title">金额明细</text>
          <view class="info-row">
            <text class="info-label">商品金额</text>
            <text class="info-value">¥ {{ orderInfo.goodsPrice }}</text>
          </view>
          <view class="info-row">
            <text class="info-label">运费</text>
            <text class="info-value">¥ {{ orderInfo.freightPrice }}</text>
          </view>
          <view class="info-row">
            <text class="info-label">优惠券</text>
            <text class="info-value">- ¥ {{ orderInfo.couponPrice || 0 }}</text>
          </view>
          <view class="info-row info-row--strong">
            <text class="info-label">实付金额</text>
            <text class="pay-price">¥ {{ orderInfo.actualPrice }}</text>
          </view>
        </view>

        <view v-if="showLogistics" class="field-card">
          <text class="section-title">物流信息</text>
          <view class="info-row">
            <text class="info-label">快递公司</text>
            <text class="info-value">{{ orderInfo?.expName || orderInfo?.expCode || '--' }}</text>
          </view>
          <view class="info-row">
            <text class="info-label">快递单号</text>
            <text class="info-value">{{ orderInfo?.expNo || '--' }}</text>
          </view>
        </view>
      </view>
    </scroll-view>

    <view v-if="hasActions" class="footer-bar">
      <view class="action-row">
        <view
          v-if="orderInfo?.handleOption.cancel"
          class="ghost-btn"
          @click="handleOrderAction(orderInfo.id, 'cancel')"
        >
          取消订单
        </view>
        <view
          v-if="orderInfo?.handleOption.delete"
          class="ghost-btn"
          @click="handleOrderAction(orderInfo.id, 'delete')"
        >
          删除订单
        </view>
        <view
          v-if="orderInfo?.handleOption.refund"
          class="ghost-btn ghost-btn--brand"
          @click="goAftersaleApply(orderInfo.id)"
        >
          申请售后
        </view>
        <view
          v-if="orderInfo?.handleOption.confirm"
          class="ghost-btn ghost-btn--brand"
          @click="handleOrderAction(orderInfo.id, 'confirm')"
        >
          确认收货
        </view>
        <view
          v-if="orderInfo?.handleOption.pay"
          class="primary-btn"
          @click="goPay(orderInfo.id)"
        >
          去支付
        </view>
      </view>
    </view>
  </view>
</template>

<script setup lang="ts">
import { computed, ref } from 'vue'
import { onShow } from '@dcloudio/uni-app'
import {
  cancelUserOrder,
  confirmUserOrder,
  deleteUserOrder,
  fetchOrderDetail,
} from '@/entities/order/api'

type UniPageWithOptions = {
  options?: Record<string, unknown>
}

const pageOptions = (getCurrentPages()[getCurrentPages().length - 1] as UniPageWithOptions | undefined)?.options || {}
const orderId = typeof pageOptions.orderId === 'string' ? pageOptions.orderId : ''

const orderInfo = ref<Awaited<ReturnType<typeof fetchOrderDetail>>['orderInfo'] | null>(null)
const orderGoods = ref<Awaited<ReturnType<typeof fetchOrderDetail>>['orderGoods']>([])

const hasActions = computed(() => {
  const handle = orderInfo.value?.handleOption
  return Boolean(handle && (handle.cancel || handle.delete || handle.refund || handle.confirm || handle.pay))
})
const showLogistics = computed(() => Boolean(orderInfo.value?.expNo || orderInfo.value?.expName || orderInfo.value?.expCode))

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
  padding: 18rpx 20rpx calc(168rpx + env(safe-area-inset-bottom));
}

.hero-card,
.summary-card,
.goods-card,
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

.hero-title {
  display: block;
  margin-top: 6rpx;
  font-size: 30rpx;
  line-height: 1.3;
  font-weight: 700;
  color: rgb(var(--sf-color-ink));
}

.summary-card,
.goods-list,
.field-card {
  margin-top: 14rpx;
}

.summary-row,
.goods-bottom,
.info-row,
.action-row {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16rpx;
}

.summary-copy,
.goods-body {
  min-width: 0;
  flex: 1;
}

.summary-title,
.section-title,
.goods-name,
.info-value {
  display: block;
  color: rgb(var(--sf-color-ink));
}

.summary-title,
.section-title {
  font-size: 24rpx;
  line-height: 1.35;
  font-weight: 600;
}

.summary-meta,
.section-copy,
.info-label,
.goods-spec,
.goods-count {
  display: block;
  color: rgb(var(--sf-color-text-secondary));
}

.summary-meta,
.section-copy,
.info-label,
.goods-spec {
  margin-top: 6rpx;
  font-size: 20rpx;
  line-height: 1.45;
}

.summary-price,
.goods-price,
.pay-price {
  font-size: 28rpx;
  line-height: 1.2;
  font-weight: 700;
  color: rgb(var(--sf-color-price));
}

.goods-list {
  display: grid;
  gap: 12rpx;
}

.goods-card {
  display: flex;
  align-items: flex-start;
  gap: 16rpx;
  padding: 14rpx;
}

.goods-image {
  width: 148rpx;
  height: 148rpx;
  flex-shrink: 0;
  border-radius: var(--sf-radius-image);
  background: rgb(var(--sf-color-mist));
}

.goods-name {
  display: -webkit-box;
  overflow: hidden;
  font-size: 23rpx;
  line-height: 1.4;
  font-weight: 600;
  -webkit-box-orient: vertical;
  -webkit-line-clamp: 2;
}

.goods-bottom {
  margin-top: 12rpx;
}

.goods-count {
  font-size: 20rpx;
  line-height: 1.3;
}

.section-copy--strong,
.info-value--brand {
  color: rgb(var(--sf-color-brand-deep));
}

.info-row {
  align-items: flex-start;
  margin-top: 14rpx;
}

.info-label {
  min-width: 120rpx;
  margin-top: 0;
}

.info-value {
  min-width: 0;
  flex: 1;
  margin-top: 0;
  text-align: right;
  font-size: 21rpx;
  line-height: 1.45;
}

.info-row--strong {
  margin-top: 18rpx;
  padding-top: 18rpx;
  border-top: 2rpx solid rgb(var(--sf-color-divider));
}

.footer-bar {
  margin: 0 20rpx 20rpx;
  padding: 16rpx 18rpx calc(16rpx + env(safe-area-inset-bottom));
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
  min-width: 126rpx;
  height: 68rpx;
  padding: 0 22rpx;
  border-radius: 999px;
  font-size: 20rpx;
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
