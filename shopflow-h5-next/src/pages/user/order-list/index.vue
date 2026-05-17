<template>
  <view class="page">
    <view class="page-head">
      <view>
        <text class="eyebrow">ShopFlow Orders</text>
        <text class="title">我的订单</text>
        <text class="desc">按状态查看订单进度，处理支付、收货和售后操作。</text>
      </view>
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
        <view class="order-top" @click="goOrderDetail(order.id)">
          <view>
            <text class="order-label">订单编号</text>
            <text class="order-sn">{{ order.orderSn }}</text>
          </view>
          <text class="order-status" :class="resolveOrderStatusClass(order.orderStatusText)">
            {{ order.orderStatusText }}
          </text>
        </view>

        <view class="goods-wrap" @click="goOrderDetail(order.id)">
          <view
            v-for="goods in order.goodsList"
            :key="`${order.id}-${goods.goodsName}-${goods.picUrl}`"
            class="goods-item"
          >
            <image class="goods-image" :src="goods.picUrl" mode="aspectFill" />
            <view class="goods-body">
              <text class="goods-name">{{ goods.goodsName }}</text>
              <text class="goods-spec">{{ goods.specifications.join(' / ') || '默认规格' }}</text>
            </view>
            <text class="goods-count">x {{ goods.number }}</text>
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
            @click="handleOrderAction(order.id, 'cancel')"
          >
            取消订单
          </view>
          <view
            v-if="order.handleOption.delete"
            class="ghost-btn"
            @click="handleOrderAction(order.id, 'delete')"
          >
            删除订单
          </view>
          <view
            v-if="order.handleOption.confirm"
            class="ghost-btn ghost-btn--brand"
            @click="handleOrderAction(order.id, 'confirm')"
          >
            确认收货
          </view>
          <view
            v-if="order.handleOption.pay"
            class="primary-btn"
            @click="goPay(order.id)"
          >
            去支付
          </view>
        </view>
      </view>
    </view>

    <view v-else class="empty-card">
      <text class="empty-title">{{ emptyState.title }}</text>
      <text class="empty-desc">{{ emptyState.description }}</text>
    </view>
  </view>
</template>

<script setup lang="ts">
import { computed, ref } from 'vue'
import { onShow } from '@dcloudio/uni-app'
import { cancelUserOrder, confirmUserOrder, deleteUserOrder } from '@/entities/order/api'
import { fetchUserOrderList, type UserOrderItem } from '@/entities/user/api'
import { resolveOrderEmptyState } from '@/features/order/order-display-utils'
import { ORDER_TABS, normalizeListTab, resolveOrderStatusClass } from '../user-list-utils'

type UniPageWithOptions = {
  options?: Record<string, unknown>
}

const pageOptions = (getCurrentPages()[getCurrentPages().length - 1] as UniPageWithOptions | undefined)?.options || {}
const activeTab = ref(normalizeListTab(typeof pageOptions.active === 'string' ? pageOptions.active : undefined, ORDER_TABS.length))
const orders = ref<UserOrderItem[]>([])
const emptyState = computed(() => resolveOrderEmptyState(activeTab.value))

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
    uni.showToast({
      title: '订单加载失败',
      icon: 'none',
    })
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

.page-head,
.order-card,
.empty-card {
  border: 2rpx solid rgb(var(--sf-color-line));
  border-radius: 16rpx;
  background: rgb(var(--sf-color-shell));
  box-shadow: var(--sf-shadow-card);
}

.page-head {
  padding: 26rpx 24rpx;
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
.order-label,
.order-sn,
.goods-spec,
.goods-count,
.price-copy,
.empty-desc {
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

.tab-scroll {
  margin-top: 18rpx;
  white-space: nowrap;
}

.tab-row {
  display: inline-flex;
  gap: 12rpx;
  padding: 2rpx 2rpx 4rpx;
}

.tab-chip {
  padding: 14rpx 22rpx;
  border-radius: 999px;
  background: rgb(var(--sf-color-shell));
  font-size: 22rpx;
  line-height: 1.2;
  color: rgb(var(--sf-color-text-secondary));
  box-shadow: var(--sf-shadow-card);
}

.tab-chip--active {
  background: rgb(var(--sf-color-brand));
  color: rgb(var(--sf-color-shell));
}

.order-list {
  display: grid;
  gap: 16rpx;
  margin-top: 18rpx;
}

.order-card {
  padding: 20rpx;
}

.order-top,
.goods-item,
.price-row,
.action-row {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16rpx;
}

.order-status {
  flex-shrink: 0;
  padding: 8rpx 14rpx;
  border-radius: 999px;
  font-size: 20rpx;
  line-height: 1.2;
}

.order-status--pending {
  background: rgb(var(--sf-color-brand-soft));
  color: rgb(var(--sf-color-brand-deep));
}

.order-status--done {
  background: rgb(var(--sf-color-mist));
  color: rgba(var(--sf-color-ink), 0.72);
}

.order-status--muted {
  background: rgb(var(--sf-color-divider));
  color: rgba(var(--sf-color-ink), 0.6);
}

.goods-wrap {
  display: grid;
  gap: 12rpx;
  margin-top: 18rpx;
}

.goods-item {
  padding: 14rpx;
  border-radius: 14rpx;
  background: rgb(var(--sf-color-page));
}

.goods-image {
  width: 120rpx;
  height: 120rpx;
  flex-shrink: 0;
  border-radius: var(--sf-radius-image);
  background: rgb(var(--sf-color-shell));
}

.goods-body {
  min-width: 0;
  flex: 1;
}

.goods-name,
.price-value,
.empty-title {
  display: block;
  font-size: 25rpx;
  line-height: 1.36;
  color: rgb(var(--sf-color-ink));
}

.price-value {
  font-weight: 600;
  color: rgb(var(--sf-color-price));
}

.price-row,
.action-row {
  margin-top: 16rpx;
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
  height: 72rpx;
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

.empty-card {
  margin-top: 18rpx;
  padding: 48rpx 28rpx;
  text-align: center;
}
</style>
