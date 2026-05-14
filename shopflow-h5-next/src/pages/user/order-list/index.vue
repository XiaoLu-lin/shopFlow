<template>
  <view class="page" role="main">
    <view class="hero-card">
      <text class="title" role="heading" aria-level="1">我的订单</text>
      <text class="desc">已接回旧站订单查询语义，支持按状态筛选与基础操作。</text>
    </view>

    <scroll-view scroll-x class="tab-scroll">
      <view class="tab-row">
        <view
          v-for="(tab, index) in ORDER_TABS"
          :key="tab"
          class="tab-chip"
          :class="{ 'tab-chip--active': index === activeTab }"
          @click="switchTab(index)"
        >
          {{ tab }}
        </view>
      </view>
    </scroll-view>

    <view v-if="orders.length" class="order-list">
      <view v-for="order in orders" :key="order.id" class="order-card">
        <view @click="goOrderDetail(order.id)">
          <view class="order-head">
            <text class="order-sn">订单号 {{ order.orderSn }}</text>
            <text class="order-status" :class="resolveOrderStatusClass(order.orderStatusText)">{{ order.orderStatusText }}</text>
          </view>

          <view class="goods-wrap">
            <view v-for="goods in order.goodsList" :key="`${order.id}-${goods.goodsName}-${goods.picUrl}`" class="goods-item">
              <image class="goods-image" :src="goods.picUrl" mode="aspectFill" />
              <view class="goods-body">
                <text class="goods-name">{{ goods.goodsName }}</text>
                <text class="goods-spec">{{ goods.specifications.join(' / ') || '默认规格' }}</text>
              </view>
              <text class="goods-count">x {{ goods.number }}</text>
            </view>
          </view>
        </view>

        <view class="price-row">
          <text class="price-copy">含运费 ¥ {{ order.freightPrice }}</text>
          <text class="price-value">实付 ¥ {{ order.actualPrice }}</text>
        </view>

        <view class="action-row">
          <view
            v-if="order.handleOption.cancel"
            class="ghost-btn"
            role="button"
            @click="handleOrderAction(order.id, 'cancel')"
          >取消订单</view>
          <view
            v-if="order.handleOption.delete"
            class="ghost-btn"
            role="button"
            @click="handleOrderAction(order.id, 'delete')"
          >删除订单</view>
          <view
            v-if="resolveCommentGoodsId(order)"
            class="ghost-btn"
            role="button"
            @click="goCommentPost(resolveCommentGoodsId(order))"
          >去评价</view>
          <view
            v-if="order.handleOption.confirm"
            class="dark-btn"
            role="button"
            @click="handleOrderAction(order.id, 'confirm')"
          >确认收货</view>
          <view
            v-if="order.handleOption.pay"
            class="primary-btn"
            role="button"
            @click="goPay(order.id)"
          >去支付</view>
        </view>
      </view>
    </view>

    <view v-else class="empty-card">
      <text class="empty-title">当前状态下还没有订单记录</text>
      <text class="empty-desc">等你下完一单后，这里就会有真实数据了。</text>
    </view>
  </view>
</template>

<script setup lang="ts">
import { onShow } from '@dcloudio/uni-app'
import { ref } from 'vue'
import { cancelUserOrder, confirmUserOrder, deleteUserOrder } from '@/entities/order/api'
import { fetchUserOrderList, type UserOrderItem } from '@/entities/user/api'
import { ORDER_TABS, normalizeListTab, resolveOrderStatusClass } from '../user-list-utils'

type UniPageWithOptions = {
  options?: Record<string, unknown>
}

const pageOptions = (getCurrentPages()[getCurrentPages().length - 1] as UniPageWithOptions | undefined)?.options || {}
const activeTab = ref(normalizeListTab(typeof pageOptions.active === 'string' ? pageOptions.active : undefined, ORDER_TABS.length))
const orders = ref<UserOrderItem[]>([])

bootstrap()
onShow(() => {
  void bootstrap()
})

async function bootstrap() {
  try {
    const result = await fetchUserOrderList({
      showType: activeTab.value,
      page: 1,
      limit: 10,
    })
    orders.value = result.list || []
  } catch (error) {
    console.error(error)
  }
}

async function switchTab(index: number) {
  if (index === activeTab.value) {
    return
  }
  activeTab.value = index
  await bootstrap()
}

function goOrderDetail(id: number) {
  uni.navigateTo({
    url: `/pages/order/detail/index?orderId=${id}`,
  })
}

function goPay(id: number) {
  uni.navigateTo({
    url: `/pages/order/payment/index?orderId=${id}`,
  })
}

function resolveCommentGoodsId(order: UserOrderItem) {
  if (!order.handleOption.comment) {
    return 0
  }

  const firstGoods = order.goodsList[0]

  if (!firstGoods || firstGoods.comment) {
    return 0
  }

  return Number(firstGoods.id || 0)
}

function goCommentPost(goodsId: number) {
  if (!goodsId) {
    return
  }

  uni.navigateTo({
    url: `/pages/order/comment-post/index?goodsId=${goodsId}`,
  })
}

async function handleOrderAction(orderId: number, action: 'cancel' | 'delete' | 'confirm') {
  try {
    if (action === 'cancel') {
      await cancelUserOrder(orderId)
    } else if (action === 'delete') {
      await deleteUserOrder(orderId)
    } else {
      await confirmUserOrder(orderId)
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
.order-card,
.empty-card {
  border-radius: 12rpx;
  background: #ffffff;
  box-shadow: 0 10rpx 24rpx rgba(23, 32, 51, 0.06);
}

.hero-card {
  padding: 22rpx;
}

.title {
  display: block;
  font-size: 28rpx;
  line-height: 1.3;
  color: #172033;
}

.desc,
.price-copy,
.goods-spec,
.empty-desc,
.order-sn {
  display: block;
  margin-top: 8rpx;
  font-size: 22rpx;
  line-height: 1.4;
  color: #748194;
}

.tab-scroll {
  margin-top: 16rpx;
  white-space: nowrap;
}

.tab-row {
  display: inline-flex;
  gap: 12rpx;
}

.tab-chip {
  padding: 12rpx 20rpx;
  border-radius: 999px;
  background: #ffffff;
  font-size: 22rpx;
  color: #5f6b7c;
  box-shadow: 0 10rpx 24rpx rgba(23, 32, 51, 0.06);
}

.tab-chip--active {
  background: #1677ff;
  color: #ffffff;
}

.order-list {
  display: grid;
  gap: 14rpx;
  margin-top: 16rpx;
}

.order-card {
  padding: 18rpx;
}

.order-head,
.goods-item,
.price-row,
.action-row {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16rpx;
}

.order-status {
  font-size: 22rpx;
}

.goods-wrap {
  display: grid;
  gap: 12rpx;
  margin-top: 14rpx;
}

.goods-item {
  padding: 14rpx;
  border-radius: 10rpx;
  background: #f7faff;
}

.goods-image {
  width: 120rpx;
  height: 120rpx;
  border-radius: 10rpx;
  background: #ffffff;
}

.goods-body {
  min-width: 0;
  flex: 1;
}

.goods-name,
.price-value,
.empty-title {
  font-size: 24rpx;
  line-height: 1.35;
  color: #172033;
}

.goods-count {
  font-size: 21rpx;
  color: #748194;
}

.price-row,
.action-row {
  margin-top: 14rpx;
}

.ghost-btn,
.dark-btn,
.primary-btn {
  display: flex;
  align-items: center;
  justify-content: center;
  height: 72rpx;
  padding: 0 18rpx;
  border-radius: 12rpx;
  font-size: 21rpx;
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

.empty-card {
  margin-top: 16rpx;
  padding: 32rpx 24rpx;
  text-align: center;
}
</style>
