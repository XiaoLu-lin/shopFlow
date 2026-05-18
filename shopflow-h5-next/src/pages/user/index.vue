<template>
  <view class="page">
    <scroll-view scroll-y class="page-scroll">
      <view class="page-scroll-inner">
        <view class="hero-panel">
          <view class="hero-top">
            <view class="hero-main">
              <view class="avatar">
                <image v-if="session.avatar" class="avatar-image" :src="session.avatar" mode="aspectFill" />
                <text v-else class="avatar-fallback">{{ avatarFallback }}</text>
              </view>
              <view class="hero-copy">
                <text class="eyebrow">{{ heroCopy.eyebrow }}</text>
                <text class="title">{{ heroCopy.title }}</text>
                <text class="desc">{{ heroCopy.description }}</text>
              </view>
            </view>
            <view class="setting-btn" @click="goProfile">设置</view>
          </view>
        </view>

        <view class="section-card order-section">
          <view class="section-head">
            <text class="section-title">我的订单</text>
            <text class="section-action" @click="goOrderList(0)">全部订单</text>
          </view>
          <view class="order-grid">
            <view
              v-for="shortcut in orderShortcuts"
              :key="shortcut.label"
              class="order-item"
              @click="goToUrl(shortcut.url)"
            >
              <view class="order-icon">{{ shortcut.iconText }}</view>
              <text class="order-value">{{ shortcut.value }}</text>
              <text class="order-label">{{ shortcut.label }}</text>
            </view>
          </view>
        </view>

        <view class="section-card service-section">
          <view class="section-head">
            <text class="section-title">常用服务</text>
          </view>
          <view class="service-list">
            <view
              v-for="link in quickLinks"
              :key="link.label"
              class="service-item"
              @click="handleQuickLink(link)"
            >
              <view class="service-copy">
                <text class="service-title">{{ link.label }}</text>
                <text class="service-desc">{{ link.copy }}</text>
              </view>
              <text class="service-arrow">›</text>
            </view>
          </view>
        </view>
      </view>
    </scroll-view>
  </view>
</template>

<script setup lang="ts">
import { computed, ref } from 'vue'
import { onShow } from '@dcloudio/uni-app'
import { fetchUserIndex } from '@/entities/user/api'
import { getSessionSnapshot } from '@/shared/compat/session-adapter'
import { buildOrderShortcutEntries, buildProfileServiceEntries, resolveUserHeroCopy } from './dashboard-utils'
import { resolveQuickLinkAction, type QuickLinkItem } from './entry-utils'

const session = ref(getSessionSnapshot())
const order = ref({
  unpaid: 0,
  unship: 0,
  unrecv: 0,
  uncomment: 0,
})

const quickLinks: QuickLinkItem[] = buildProfileServiceEntries()

const avatarFallback = computed(() => (session.value.nickName || '我').slice(0, 1))
const heroCopy = computed(() =>
  resolveUserHeroCopy({
    nickName: session.value.nickName,
    token: session.value.token,
    mobile: session.value.mobile,
  }),
)
const orderShortcuts = computed(() => buildOrderShortcutEntries(order.value))

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
    uni.showToast({
      title: '个人中心加载失败',
      icon: 'none',
    })
  }
}

function goOrderList(active: number) {
  uni.navigateTo({
    url: `/pages/user/order-list/index?active=${active}`,
  })
}

function goToUrl(url: string) {
  if (url === '/pages/order/cart/index') {
    uni.switchTab({
      url,
    })
    return
  }

  uni.navigateTo({
    url,
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
  display: flex;
  flex-direction: column;
  min-height: 0;
  height: calc(100vh - var(--window-top, 0px) - var(--window-bottom, 0px));
  background: rgb(var(--sf-color-page));
  overflow: hidden;
}

.page-scroll {
  flex: 1;
  min-height: 0;
}

.page-scroll-inner {
  padding: 24rpx 20rpx 40rpx;
}

.hero-panel,
.section-card {
  background: rgb(var(--sf-color-shell));
  border-radius: 24rpx;
  box-shadow: var(--sf-shadow-soft);
}

.hero-panel {
  position: relative;
  overflow: hidden;
  padding: 28rpx 24rpx 34rpx;
  background: linear-gradient(145deg, rgb(var(--sf-color-brand)) 0%, rgb(var(--sf-color-brand-light)) 100%);
  box-shadow: var(--sf-shadow-brand);
}

.hero-panel::after {
  content: '';
  position: absolute;
  top: -86rpx;
  right: -32rpx;
  width: 220rpx;
  height: 220rpx;
  border-radius: 999px;
  background: rgba(255, 255, 255, 0.12);
}

.hero-top,
.hero-main,
.section-head,
.service-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16rpx;
}

.hero-top {
  position: relative;
  z-index: 1;
  align-items: flex-start;
}

.hero-main {
  min-width: 0;
  flex: 1;
  align-items: flex-start;
  justify-content: flex-start;
}

.avatar {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 96rpx;
  height: 96rpx;
  flex-shrink: 0;
  border-radius: 999px;
  background: linear-gradient(145deg, rgba(255, 255, 255, 0.96), rgba(229, 237, 246, 0.94));
  box-shadow: 0 12rpx 28rpx rgba(32, 52, 82, 0.16);
  overflow: hidden;
}

.avatar-image {
  width: 100%;
  height: 100%;
}

.avatar-fallback {
  font-size: 30rpx;
  font-weight: 700;
  color: rgb(var(--sf-color-brand));
}

.hero-copy {
  min-width: 0;
  flex: 1;
}

.eyebrow {
  display: block;
  font-size: 18rpx;
  line-height: 1.2;
  letter-spacing: 3rpx;
  text-transform: uppercase;
  color: rgba(255, 255, 255, 0.72);
}

.title {
  display: block;
  margin-top: 10rpx;
  font-size: 38rpx;
  line-height: 1.24;
  font-weight: 700;
  color: rgb(var(--sf-color-shell));
}

.desc,
.order-label,
.service-desc {
  display: block;
  margin-top: 8rpx;
  font-size: 21rpx;
  line-height: 1.45;
  color: rgb(var(--sf-color-text-secondary));
}

.desc {
  margin-top: 10rpx;
  font-size: 22rpx;
  color: rgba(255, 255, 255, 0.84);
}

.setting-btn {
  flex-shrink: 0;
  padding: 12rpx 20rpx;
  border-radius: 999px;
  background: rgba(255, 255, 255, 0.18);
  border: 1rpx solid rgba(255, 255, 255, 0.18);
  font-size: 20rpx;
  line-height: 1.2;
  color: rgb(var(--sf-color-shell));
}

.section-card {
  margin-top: 18rpx;
  padding: 24rpx;
  border: 2rpx solid rgb(var(--sf-color-line));
}

.section-head {
  align-items: center;
}

.section-title,
.service-title {
  display: block;
  font-size: 26rpx;
  line-height: 1.35;
  color: rgb(var(--sf-color-ink));
}

.section-action {
  flex-shrink: 0;
  font-size: 20rpx;
  line-height: 1.2;
  color: rgb(var(--sf-color-brand));
}

.order-grid {
  display: grid;
  grid-template-columns: repeat(5, minmax(0, 1fr));
  gap: 10rpx;
  margin-top: 18rpx;
}

.order-item {
  text-align: center;
}

.order-icon {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 68rpx;
  height: 68rpx;
  margin: 0 auto 10rpx;
  border-radius: 20rpx;
  background: rgb(var(--sf-color-brand-soft));
  color: rgb(var(--sf-color-brand));
  font-size: 24rpx;
  font-weight: 700;
}

.order-value {
  display: block;
  font-size: 28rpx;
  line-height: 1.2;
  font-weight: 700;
  color: rgb(var(--sf-color-ink));
}

.order-label {
  margin-top: 6rpx;
  font-size: 20rpx;
}

.service-section {
  padding-bottom: 10rpx;
}

.service-list {
  margin-top: 10rpx;
}

.service-item {
  padding: 22rpx 4rpx;
  border-bottom: 2rpx solid rgb(var(--sf-color-divider));
}

.service-item:last-child {
  border-bottom: 0;
}

.service-copy {
  min-width: 0;
  flex: 1;
}

.service-title {
  font-size: 24rpx;
}

.service-desc {
  font-size: 20rpx;
}

.service-arrow {
  flex-shrink: 0;
  font-size: 34rpx;
  line-height: 1;
  color: rgb(var(--sf-color-text-hint));
}
</style>
