<template>
  <view class="page">
    <view class="hero-card">
      <view class="hero-row">
        <view class="hero-avatar">券</view>
        <view class="hero-copy">
          <text class="eyebrow">{{ hero.eyebrow }}</text>
          <text class="title">{{ hero.title }}</text>
        </view>
      </view>
      <text class="desc">{{ hero.description }}</text>
    </view>

    <scroll-view scroll-x class="tab-scroll">
      <view class="tab-row">
        <view
          v-for="(tab, index) in COUPON_TABS"
          :key="tab"
          class="tab-chip"
          :class="{ 'tab-chip--active': index === activeTab }"
          @click="switchTab(index)"
        >
          {{ tab }}
        </view>
      </view>
    </scroll-view>

    <view v-if="loading" class="coupon-list">
      <view v-for="index in 3" :key="index" class="coupon-card coupon-card--skeleton">
        <view class="skeleton-side"></view>
        <view class="coupon-body">
          <view class="skeleton-line skeleton-line--title"></view>
          <view class="skeleton-line"></view>
          <view class="skeleton-line skeleton-line--short"></view>
        </view>
      </view>
    </view>

    <view v-else-if="coupons.length" class="coupon-list">
      <view v-for="coupon in coupons" :key="coupon.id" class="coupon-card">
        <view class="coupon-side" :class="`coupon-side--${couponTone}`">
          <text class="coupon-side-copy">立减</text>
          <text class="coupon-side-price">¥{{ coupon.discount }}</text>
        </view>
        <view class="coupon-body">
          <view class="coupon-head">
            <text class="coupon-title">{{ coupon.name }}</text>
            <text class="coupon-tag">{{ coupon.tag }}</text>
          </view>
          <text class="coupon-desc">{{ coupon.desc }}</text>
          <text class="coupon-time">有效期至 {{ coupon.endTime }}</text>
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
import { onShow } from '@dcloudio/uni-app'
import { computed, ref } from 'vue'
import { fetchUserCouponList, type UserCouponItem } from '@/entities/user/api'
import { COUPON_TABS, normalizeListTab } from '../user-list-utils'
import { resolveCouponEmptyState, resolveCouponTone, resolveUserPageHero } from '../page-display-utils'

type UniPageWithOptions = {
  options?: Record<string, unknown>
}

const pageOptions = (getCurrentPages()[getCurrentPages().length - 1] as UniPageWithOptions | undefined)?.options || {}
const activeTab = ref(normalizeListTab(typeof pageOptions.active === 'string' ? pageOptions.active : undefined, COUPON_TABS.length))
const loading = ref(true)
const coupons = ref<UserCouponItem[]>([])
const hero = resolveUserPageHero('coupon')

const couponTone = computed(() => resolveCouponTone(activeTab.value))
const emptyState = computed(() => resolveCouponEmptyState(activeTab.value))

bootstrap()
onShow(() => {
  void bootstrap()
})

async function bootstrap() {
  loading.value = true

  try {
    const result = await fetchUserCouponList({
      status: activeTab.value,
      page: 1,
      limit: 10,
    })
    coupons.value = result.list || []
  } catch (error) {
    console.error(error)
  } finally {
    loading.value = false
  }
}

async function switchTab(index: number) {
  if (index === activeTab.value) {
    return
  }

  activeTab.value = index
  await bootstrap()
}
</script>

<style scoped lang="scss">
.page {
  min-height: 100vh;
  padding: 14rpx 14rpx 32rpx;
  background: linear-gradient(180deg, rgb(var(--sf-color-brand-soft)) 0%, rgb(var(--sf-color-page)) 26%, #ffffff 100%);
}

.hero-card,
.coupon-card,
.empty-card {
  border-radius: var(--sf-radius-card);
  background: #ffffff;
  border: 1px solid rgb(var(--sf-color-line));
  box-shadow: var(--sf-shadow-soft);
}

.hero-card {
  padding: 14rpx 16rpx 16rpx;
  color: #ffffff;
  background: linear-gradient(145deg, rgb(var(--sf-color-brand)) 0%, rgb(var(--sf-color-brand-light)) 100%);
  box-shadow: var(--sf-shadow-brand);
}

.empty-card {
  padding: 12rpx 14rpx;
}

.hero-row,
.coupon-head,
.tab-row {
  display: flex;
}

.hero-row,
.coupon-head {
  align-items: center;
  gap: 10rpx;
}

.hero-avatar {
  display: flex;
  width: 56rpx;
  height: 56rpx;
  flex-shrink: 0;
  align-items: center;
  justify-content: center;
  border-radius: var(--sf-radius-card);
  background: linear-gradient(145deg, rgba(255, 255, 255, 0.96), rgba(229, 237, 246, 0.92));
  font-size: 20rpx;
  font-weight: 700;
  color: rgb(var(--sf-color-brand));
}

.hero-copy {
  min-width: 0;
  flex: 1;
}

.eyebrow {
  display: block;
  font-size: 16rpx;
  letter-spacing: 0.12em;
  text-transform: uppercase;
  color: rgba(255, 255, 255, 0.78);
}

.title,
.empty-title {
  display: block;
  margin-top: 4rpx;
  font-size: 22rpx;
  line-height: 1.28;
  color: inherit;
}

.desc,
.empty-desc {
  display: block;
  margin-top: 8rpx;
  font-size: 18rpx;
  line-height: 1.42;
}

.desc {
  color: rgba(255, 255, 255, 0.88);
}

.empty-title,
.coupon-title {
  color: rgb(var(--sf-color-ink));
}

.empty-desc,
.coupon-desc,
.coupon-time {
  color: rgb(var(--sf-color-text-secondary));
}

.tab-scroll {
  margin-top: 10rpx;
  white-space: nowrap;
}

.tab-row {
  display: inline-flex;
  gap: 6rpx;
}

.tab-chip {
  padding: 8rpx 14rpx;
  border-radius: 999px;
  background: rgba(255, 255, 255, 0.94);
  border: 1px solid rgb(var(--sf-color-line));
  font-size: 18rpx;
  color: rgb(var(--sf-color-text-secondary));
  box-shadow: var(--sf-shadow-card);
}

.tab-chip--active {
  background: rgb(var(--sf-color-brand));
  border-color: rgb(var(--sf-color-brand));
  color: #ffffff;
}

.coupon-list {
  display: grid;
  gap: 8rpx;
  margin-top: 10rpx;
}

.coupon-card {
  display: flex;
  overflow: hidden;
}

.coupon-side,
.skeleton-side {
  display: flex;
  width: 124rpx;
  flex-shrink: 0;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 14rpx 8rpx;
}

.coupon-side--brand {
  background: linear-gradient(180deg, rgb(var(--sf-color-brand)) 0%, rgb(var(--sf-color-brand-light)) 100%);
  color: #ffffff;
}

.coupon-side--deep {
  background: rgb(var(--sf-color-brand-deep));
  color: #ffffff;
}

.coupon-side--muted {
  background: rgb(var(--sf-color-brand-soft));
  color: rgb(var(--sf-color-brand-deep));
}

.coupon-side-copy {
  font-size: 16rpx;
  opacity: 0.86;
}

.coupon-side-price {
  margin-top: 4rpx;
  font-size: 30rpx;
  line-height: 1;
}

.coupon-body {
  min-width: 0;
  flex: 1;
  padding: 14rpx;
}

.coupon-head {
  align-items: center;
  justify-content: space-between;
  gap: 8rpx;
}

.coupon-title {
  min-width: 0;
  flex: 1;
  font-size: 20rpx;
  line-height: 1.35;
  color: rgb(var(--sf-color-ink));
}

.coupon-tag {
  padding: 3rpx 7rpx;
  border-radius: var(--sf-radius-card);
  background: rgba(255, 255, 255, 0.92);
  border: 1px solid rgb(var(--sf-color-line));
  font-size: 16rpx;
  color: rgb(var(--sf-color-brand));
}

.coupon-desc,
.coupon-time {
  margin-top: 6rpx;
  font-size: 17rpx;
  line-height: 1.45;
}

.coupon-card--skeleton {
  min-height: 132rpx;
}

.skeleton-side {
  background: rgb(var(--sf-color-divider));
}

.skeleton-line {
  height: 16rpx;
  margin-top: 8rpx;
  border-radius: 999px;
  background: rgb(var(--sf-color-divider));
}

.skeleton-line--title {
  width: 180rpx;
  margin-top: 0;
}

.skeleton-line--short {
  width: 112rpx;
}

.empty-card {
  margin-top: 10rpx;
  padding-top: 12rpx;
  padding-bottom: 12rpx;
}
</style>
