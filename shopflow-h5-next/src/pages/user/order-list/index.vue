<template>
  <view class="page">
    <scroll-view scroll-y class="page-scroll">
      <view class="page-scroll-inner">
        <view class="hero-card">
          <text class="hero-eyebrow">ShopFlow</text>
          <text class="hero-title">我的订单</text>
        </view>

        <view class="summary-strip">
          <view v-for="item in orderOverviewStats" :key="item.label" class="summary-card">
            <text class="summary-label">{{ item.label }}</text>
            <text class="summary-value">{{ item.value }}</text>
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
            <view class="order-head" @click="goOrderDetail(order.id)">
              <view class="order-head-copy">
                <text class="order-sn">订单编号 {{ order.orderSn }}</text>
                <text class="order-meta">{{ order.goodsList.length }} 件商品 · 含运费 ¥ {{ order.freightPrice }}</text>
              </view>
              <text class="order-status" :class="resolveOrderStatusClass(order.orderStatusText)">
                {{ order.orderStatusText }}
              </text>
            </view>

            <view class="goods-list">
              <view
                v-for="goods in order.goodsList"
                :key="`${order.id}-${goods.goodsName}-${goods.picUrl}`"
                class="goods-row"
                @click="goOrderDetail(order.id)"
              >
                <image class="goods-image" :src="goods.picUrl" mode="aspectFill" />
                <view class="goods-body">
                  <text class="goods-name">{{ goods.goodsName }}</text>
                  <text class="goods-spec">{{ goods.specifications.join(' / ') || '默认规格' }}</text>
                </view>
                <text class="goods-count">x {{ goods.number }}</text>
              </view>
            </view>

            <view class="order-footer">
              <view class="order-total">
                <text class="order-total-label">实付金额</text>
                <text class="order-total-price">¥ {{ order.actualPrice }}</text>
              </view>

              <view class="action-row">
                <view class="ghost-btn" @click.stop="goOrderDetail(order.id)">
                  查看详情
                </view>
                <view
                  v-if="order.handleOption.cancel"
                  class="ghost-btn"
                  @click.stop="handleOrderAction(order.id, 'cancel')"
                >
                  取消订单
                </view>
                <view
                  v-if="order.handleOption.delete"
                  class="ghost-btn"
                  @click.stop="handleOrderAction(order.id, 'delete')"
                >
                  删除订单
                </view>
                <view
                  v-if="order.handleOption.refund"
                  class="ghost-btn ghost-btn--brand"
                  @click.stop="goAftersaleApply(order.id)"
                >
                  申请售后
                </view>
                <view
                  v-if="order.handleOption.confirm"
                  class="ghost-btn ghost-btn--brand"
                  @click.stop="handleOrderAction(order.id, 'confirm')"
                >
                  确认收货
                </view>
                <view
                  v-if="order.handleOption.pay"
                  class="primary-btn"
                  @click.stop="goPay(order.id)"
                >
                  去支付
                </view>
              </view>
            </view>
          </view>
        </view>

        <view v-else class="empty-card">
          <text class="empty-title">{{ emptyState.title }}</text>
          <text class="empty-desc">{{ emptyState.description }}</text>
        </view>
      </view>
    </scroll-view>
  </view>
</template>

<script setup lang="ts">
import { computed, ref } from 'vue'
import { onShow } from '@dcloudio/uni-app'
import { cancelUserOrder, confirmUserOrder, deleteUserOrder } from '@/entities/order/api'
import {
  fetchUserIndex,
  fetchUserOrderList,
  type UserOrderItem,
  type UserOrderStats,
} from '@/entities/user/api'
import { resolveOrderEmptyState, resolveOrderOverviewStats } from '@/features/order/order-display-utils'
import { ORDER_TABS, normalizeListTab, resolveOrderStatusClass } from '../user-list-utils'

type UniPageWithOptions = {
  options?: Record<string, unknown>
}

const pageOptions = (getCurrentPages()[getCurrentPages().length - 1] as UniPageWithOptions | undefined)?.options || {}
const activeTab = ref(normalizeListTab(typeof pageOptions.active === 'string' ? pageOptions.active : undefined, ORDER_TABS.length))
const orders = ref<UserOrderItem[]>([])
const orderStats = ref<UserOrderStats>({
  unpaid: 0,
  unship: 0,
  unrecv: 0,
  uncomment: 0,
})

const emptyState = computed(() => resolveOrderEmptyState(activeTab.value))
const orderOverviewStats = computed(() =>
  resolveOrderOverviewStats([
    { label: '待付款', count: orderStats.value.unpaid },
    { label: '待发货', count: orderStats.value.unship },
    { label: '待收货', count: orderStats.value.unrecv },
  ]),
)

bootstrap()
onShow(() => {
  void bootstrap()
})

async function bootstrap() {
  try {
    const [summary, result] = await Promise.all([
      fetchUserIndex().catch(() => null),
      fetchUserOrderList({
        showType: activeTab.value,
        page: 1,
        limit: 10,
      }),
    ])

    if (summary?.order) {
      orderStats.value = summary.order
    }

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

function goAftersaleApply(id: number) {
  uni.navigateTo({
    url: `/pages/user/refund-apply/index?orderId=${id}`,
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
  padding: 18rpx 20rpx 28rpx;
}

.hero-card,
.summary-card,
.tab-chip,
.order-card,
.empty-card {
  border: 2rpx solid rgb(var(--sf-color-line));
  border-radius: var(--sf-radius-card);
  background: rgb(var(--sf-color-shell));
  box-shadow: var(--sf-shadow-soft);
}

.hero-card {
  padding: 18rpx;
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

.summary-strip {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 10rpx;
  margin-top: 14rpx;
}

.summary-card {
  padding: 16rpx 10rpx;
  text-align: center;
}

.summary-label,
.summary-value,
.order-meta,
.goods-spec,
.goods-count,
.order-total-label,
.empty-desc {
  display: block;
  color: rgb(var(--sf-color-text-secondary));
}

.summary-label {
  font-size: 20rpx;
  line-height: 1.3;
}

.summary-value {
  margin-top: 6rpx;
  font-size: 21rpx;
  line-height: 1.3;
  font-weight: 600;
}

.tab-scroll {
  margin-top: 14rpx;
  white-space: nowrap;
}

.tab-row {
  display: inline-flex;
  gap: 10rpx;
  padding: 2rpx 2rpx 4rpx;
}

.tab-chip {
  padding: 12rpx 22rpx;
  border-radius: 999px;
  font-size: 21rpx;
  line-height: 1.2;
  color: rgb(var(--sf-color-text-secondary));
}

.tab-chip--active {
  background: linear-gradient(145deg, rgba(237, 243, 248, 0.98) 0%, rgba(223, 231, 241, 0.98) 100%);
  color: rgb(var(--sf-color-brand));
  font-weight: 700;
}

.order-list {
  display: grid;
  gap: 14rpx;
  margin-top: 14rpx;
}

.order-card {
  padding: 18rpx;
}

.order-head,
.goods-row,
.order-footer,
.action-row {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 16rpx;
}

.order-head-copy,
.goods-body,
.order-total {
  min-width: 0;
  flex: 1;
}

.order-sn,
.goods-name,
.order-total-price,
.empty-title {
  display: block;
  color: rgb(var(--sf-color-ink));
}

.order-sn {
  font-size: 24rpx;
  line-height: 1.35;
  font-weight: 600;
}

.order-meta {
  margin-top: 6rpx;
  font-size: 20rpx;
  line-height: 1.45;
}

.order-status {
  flex-shrink: 0;
  padding: 8rpx 14rpx;
  border-radius: 999px;
  font-size: 19rpx;
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

.goods-list {
  display: grid;
  gap: 10rpx;
  margin-top: 14rpx;
}

.goods-row {
  align-items: center;
  padding: 12rpx;
  border-radius: var(--sf-radius-card);
  background: linear-gradient(180deg, rgba(247, 250, 253, 0.98) 0%, rgba(255, 255, 255, 0.98) 100%);
}

.goods-image {
  width: 120rpx;
  height: 120rpx;
  flex-shrink: 0;
  border-radius: var(--sf-radius-image);
  background: rgb(var(--sf-color-mist));
}

.goods-name {
  display: -webkit-box;
  overflow: hidden;
  font-size: 23rpx;
  line-height: 1.36;
  font-weight: 600;
  -webkit-box-orient: vertical;
  -webkit-line-clamp: 2;
}

.goods-spec {
  margin-top: 6rpx;
  font-size: 20rpx;
  line-height: 1.45;
}

.goods-count {
  flex-shrink: 0;
  font-size: 20rpx;
  line-height: 1.3;
}

.order-footer {
  margin-top: 14rpx;
  padding-top: 14rpx;
  border-top: 2rpx solid rgb(var(--sf-color-divider));
}

.order-total-label {
  font-size: 20rpx;
  line-height: 1.35;
}

.order-total-price {
  margin-top: 6rpx;
  font-size: 28rpx;
  line-height: 1.2;
  font-weight: 700;
  color: rgb(var(--sf-color-price));
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

.empty-card {
  margin-top: 14rpx;
  padding: 28rpx 22rpx;
  text-align: center;
}

.empty-title {
  font-size: 24rpx;
  line-height: 1.35;
  font-weight: 600;
}

.empty-desc {
  margin-top: 8rpx;
  font-size: 20rpx;
  line-height: 1.45;
}
</style>
