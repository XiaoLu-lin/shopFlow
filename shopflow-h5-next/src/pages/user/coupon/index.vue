<template>
  <view class="page">
    <view class="hero-card">
      <text class="title">优惠券</text>
      <text class="desc">保持旧站未使用 / 已使用 / 已过期三种状态语义，列表接口已接通。</text>
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
        <view class="coupon-side" :class="couponToneClass">
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
      <text class="empty-title">当前状态下没有优惠券</text>
      <text class="empty-desc">等你领券或下单后，这里就会出现真实记录。</text>
    </view>
  </view>
</template>

<script setup lang="ts">
import { onShow } from '@dcloudio/uni-app'
import { computed, ref } from 'vue'
import { fetchUserCouponList, type UserCouponItem } from '@/entities/user/api'
import { COUPON_TABS, normalizeListTab } from '../user-list-utils'

type UniPageWithOptions = {
  options?: Record<string, unknown>
}

const pageOptions = (getCurrentPages()[getCurrentPages().length - 1] as UniPageWithOptions | undefined)?.options || {}
const activeTab = ref(normalizeListTab(typeof pageOptions.active === 'string' ? pageOptions.active : undefined, COUPON_TABS.length))
const loading = ref(true)
const coupons = ref<UserCouponItem[]>([])

const couponToneClass = computed(() => {
  if (activeTab.value === 0) {
    return 'coupon-side--brand'
  }

  if (activeTab.value === 1) {
    return 'coupon-side--dark'
  }

  return 'coupon-side--muted'
})

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
  padding: 20rpx 20rpx 40rpx;
  background: linear-gradient(180deg, #ffffff 0%, #f6f8fb 100%);
}

.hero-card,
.coupon-card,
.empty-card {
  border-radius: 12rpx;
  background: #ffffff;
  box-shadow: 0 10rpx 24rpx rgba(23, 32, 51, 0.06);
}

.hero-card,
.empty-card {
  padding: 22rpx;
}

.title,
.empty-title {
  display: block;
  font-size: 28rpx;
  line-height: 1.3;
  color: #172033;
}

.desc,
.empty-desc {
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

.coupon-list {
  display: grid;
  gap: 14rpx;
  margin-top: 16rpx;
}

.coupon-card {
  display: flex;
  overflow: hidden;
}

.coupon-side,
.skeleton-side {
  display: flex;
  width: 164rpx;
  flex-shrink: 0;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 18rpx 12rpx;
}

.coupon-side--brand {
  background: #1677ff;
  color: #ffffff;
}

.coupon-side--dark {
  background: #253044;
  color: #ffffff;
}

.coupon-side--muted {
  background: #e9eef5;
  color: #4f5d72;
}

.coupon-side-copy {
  font-size: 20rpx;
  opacity: 0.86;
}

.coupon-side-price {
  margin-top: 8rpx;
  font-size: 38rpx;
  line-height: 1;
}

.coupon-body {
  min-width: 0;
  flex: 1;
  padding: 20rpx;
}

.coupon-head {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12rpx;
}

.coupon-title {
  min-width: 0;
  flex: 1;
  font-size: 24rpx;
  line-height: 1.35;
  color: #172033;
}

.coupon-tag {
  flex-shrink: 0;
  border-radius: 8rpx;
  background: #f6f8fb;
  padding: 6rpx 10rpx;
  font-size: 18rpx;
  color: #748194;
}

.coupon-desc,
.coupon-time {
  display: block;
  margin-top: 10rpx;
  font-size: 22rpx;
  line-height: 1.45;
  color: #748194;
}

.coupon-card--skeleton {
  min-height: 156rpx;
}

.skeleton-side {
  background: #edf1f6;
}

.skeleton-line {
  height: 20rpx;
  margin-top: 12rpx;
  border-radius: 999px;
  background: #edf1f6;
}

.skeleton-line--title {
  width: 220rpx;
  margin-top: 0;
}

.skeleton-line--short {
  width: 140rpx;
}

.empty-card {
  margin-top: 16rpx;
  text-align: center;
}
</style>
