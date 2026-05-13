<template>
  <view class="page">
    <view class="hero-card">
      <view class="hero-head">
        <view class="hero-main">
          <view class="avatar">
            <image v-if="session.avatar" class="avatar-image" :src="session.avatar" mode="aspectFill" />
            <text v-else class="avatar-fallback">{{ avatarFallback }}</text>
          </view>
          <view class="hero-copy">
            <text class="title">{{ session.nickName || '欢迎来到我的' }}</text>
            <text class="desc">
              {{ session.token ? '当前已兼容旧登录态，可继续使用历史账号信息。' : '当前未检测到登录态，访问受保护页面时会继续跳转登录。' }}
            </text>
          </view>
        </view>
        <view class="setting-btn" @click="goProfile">设置</view>
      </view>
    </view>

    <view class="stats-grid">
      <view class="stats-card" @click="goOrderList(0)">
        <text class="stats-label">订单提醒</text>
        <text class="stats-value">{{ totalOrderBadge }}</text>
        <text class="stats-copy">查看待付款、待发货与待收货</text>
      </view>
      <view class="stats-card" @click="handleQuickLink({ label: '优惠券', type: 'coupon', copy: '' })">
        <text class="stats-label">优惠券</text>
        <text class="stats-value">查看</text>
        <text class="stats-copy">查看可用与历史优惠券</text>
      </view>
    </view>

    <view class="panel">
      <view class="panel-head">
        <text class="panel-title">订单状态</text>
        <view class="panel-link" @click="goOrderList(0)">全部订单</view>
      </view>
      <view class="order-grid">
        <view class="order-chip" @click="goOrderList(1)">
          <text class="order-value">{{ order.unpaid }}</text>
          <text class="order-label">待付款</text>
        </view>
        <view class="order-chip" @click="goOrderList(2)">
          <text class="order-value">{{ order.unship }}</text>
          <text class="order-label">待发货</text>
        </view>
        <view class="order-chip" @click="goOrderList(3)">
          <text class="order-value">{{ order.unrecv }}</text>
          <text class="order-label">待收货</text>
        </view>
        <view class="order-chip" @click="goOrderList(4)">
          <text class="order-value">{{ order.uncomment }}</text>
          <text class="order-label">已完成</text>
        </view>
      </view>
    </view>

    <view class="quick-grid">
      <view
        v-for="link in quickLinks"
        :key="link.label"
        class="quick-card"
        @click="handleQuickLink(link)"
      >
        <text class="quick-title">{{ link.label }}</text>
        <text class="quick-copy">{{ link.copy }}</text>
      </view>
    </view>
  </view>
</template>

<script setup lang="ts">
import { onShow } from '@dcloudio/uni-app'
import { computed, ref } from 'vue'
import { fetchUserIndex } from '@/entities/user/api'
import { getSessionSnapshot } from '@/shared/compat/session-adapter'
import { resolveQuickLinkAction, type QuickLinkItem } from './entry-utils'

const session = ref(getSessionSnapshot())
const order = ref({
  unpaid: 0,
  unship: 0,
  unrecv: 0,
  uncomment: 0,
})

const quickLinks: QuickLinkItem[] = [
  { label: '我的订单', type: 'order', copy: '查看全部订单记录' },
  { label: '购物车', type: 'cart', copy: '回到购物车继续结算' },
  { label: '我的收藏', type: 'collect', copy: '查看收藏商品与取消收藏' },
  { label: '收货地址', type: 'address', copy: '管理常用收货地址' },
  { label: '退款/售后', type: 'refund', copy: '查看售后进度与退款状态' },
  { label: '帮助中心', type: 'help', copy: '查看常见问题与使用说明' },
  { label: '服务中心', type: 'service', copy: '聚合客服、反馈与帮助入口' },
]

const avatarFallback = computed(() => (session.value.nickName || '我').slice(0, 1))
const totalOrderBadge = computed(() => order.value.unpaid + order.value.unship + order.value.unrecv + order.value.uncomment)

bootstrap()
onShow(() => {
  session.value = getSessionSnapshot()
  void bootstrap()
})

async function bootstrap() {
  try {
    const result = await fetchUserIndex()
    order.value = result.order || order.value
  } catch (error) {
    console.error(error)
  }
}

function goOrderList(active: number) {
  uni.navigateTo({
    url: `/pages/user/order-list/index?active=${active}`,
  })
}

function goCart() {
  uni.switchTab({
    url: '/pages/order/cart/index',
  })
}

function goPlaceholder(name: string) {
  uni.showToast({
    title: `${name}下一批补齐`,
    icon: 'none',
  })
}

function goProfile() {
  const action = resolveQuickLinkAction({
    label: '设置',
    type: 'profile',
    copy: '',
  })

  if (action.action === 'navigateTo') {
    uni.navigateTo({
      url: action.url,
    })
  }
}

function handleQuickLink(link: QuickLinkItem) {
  const action = resolveQuickLinkAction(link)

  if (action.action === 'navigateTo') {
    uni.navigateTo({
      url: action.url,
    })
    return
  }

  if (action.action === 'switchTab') {
    uni.switchTab({
      url: action.url,
    })
    return
  }

  uni.showToast({
    title: action.title,
    icon: 'none',
  })
}
</script>

<style scoped lang="scss">
.page {
  min-height: 100vh;
  padding: 20rpx 20rpx 40rpx;
  background: linear-gradient(180deg, #ffffff 0%, #f6f8fb 100%);
}

.hero-card,
.stats-card,
.panel,
.quick-card {
  border-radius: 12rpx;
  background: #ffffff;
  box-shadow: 0 10rpx 24rpx rgba(23, 32, 51, 0.06);
}

.hero-card,
.panel {
  padding: 22rpx;
}

.hero-head,
.hero-main,
.panel-head {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16rpx;
}

.hero-main {
  min-width: 0;
  flex: 1;
  justify-content: flex-start;
}

.avatar {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 92rpx;
  height: 92rpx;
  border-radius: 999px;
  background: #edf5ff;
  overflow: hidden;
}

.avatar-image {
  width: 100%;
  height: 100%;
}

.avatar-fallback {
  font-size: 30rpx;
  color: #1677ff;
}

.hero-copy {
  min-width: 0;
  flex: 1;
}

.title,
.panel-title,
.quick-title {
  display: block;
  font-size: 28rpx;
  line-height: 1.3;
  color: #172033;
}

.desc,
.stats-label,
.stats-copy,
.order-label,
.quick-copy {
  display: block;
  margin-top: 8rpx;
  font-size: 22rpx;
  line-height: 1.4;
  color: #748194;
}

.setting-btn,
.panel-link {
  padding: 10rpx 16rpx;
  border-radius: 999px;
  background: #edf5ff;
  font-size: 21rpx;
  line-height: 1.2;
  color: #1677ff;
}

.stats-grid,
.quick-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 14rpx;
  margin-top: 16rpx;
}

.stats-card,
.quick-card {
  padding: 20rpx;
}

.stats-value,
.order-value {
  display: block;
  margin-top: 14rpx;
  font-size: 34rpx;
  line-height: 1.2;
  font-weight: 600;
  color: #172033;
}

.order-grid {
  display: grid;
  grid-template-columns: repeat(4, minmax(0, 1fr));
  gap: 12rpx;
  margin-top: 16rpx;
}

.order-chip {
  padding: 18rpx 10rpx;
  border-radius: 12rpx;
  background: #f7faff;
  text-align: center;
}

.quick-copy {
  font-size: 20rpx;
}
</style>
