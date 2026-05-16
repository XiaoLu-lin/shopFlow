<template>
  <view class="page">
    <view class="home-shell">
      <view class="shop-header">
        <text class="brand-name">ShopFlow</text>
        <button class="header-search" @click="goSearch">
          <text class="search-placeholder">搜索商品</text>
        </button>
      </view>

      <swiper
        v-if="bannerList.length"
        class="hero-swiper"
        indicator-dots
        autoplay
        circular
        :interval="3200"
        indicator-color="rgba(255,255,255,0.55)"
        indicator-active-color="#ffffff"
      >
        <swiper-item v-for="(banner, index) in bannerList" :key="`${banner.url}-${index}`">
          <image class="hero-image" :src="banner.url" mode="aspectFill" />
        </swiper-item>
      </swiper>

      <view v-if="homeChannelEntries.length" class="channel-grid">
        <button
          v-for="channel in homeChannelEntries"
          :key="channel.id"
          class="channel-item"
          @click="goChannel(channel.target)"
        >
          <view class="channel-icon-wrap">
            <image class="channel-icon" :src="channel.iconUrl" mode="aspectFit" />
          </view>
          <text class="channel-name">{{ channel.name }}</text>
        </button>
      </view>

      <button v-if="showCoupons" class="coupon-banner" @click="goCoupon">
        <view>
          <text class="coupon-title">{{ firstCoupon?.name || '优惠券' }}</text>
          <text class="coupon-desc">{{ firstCoupon?.desc || '新人专享福利' }}</text>
        </view>
        <text class="coupon-action">领取</text>
      </button>

      <view v-if="showFlashSale" class="flash-section">
        <view class="flash-head">
          <text class="flash-title">限时秒杀</text>
          <text class="flash-time">02:18:36</text>
        </view>
        <scroll-view scroll-x class="flash-scroll">
          <view class="flash-list">
            <button
              v-for="item in flashSaleList"
              :key="item.id"
              class="flash-card"
              @click="goDetail(item.id)"
            >
              <image class="flash-image" :src="item.picUrl" mode="aspectFill" />
              <text class="price">¥{{ item.grouponPrice || item.retailPrice }}</text>
            </button>
          </view>
        </scroll-view>
      </view>
    </view>

    <view class="feed-section">
      <view class="feed-tabs">
        <button
          v-for="tab in feedTabs"
          :key="tab.key"
          class="feed-tab"
          :class="{ 'feed-tab--active': activeFeed === tab.key }"
          @click="activeFeed = tab.key"
        >
          {{ tab.label }}
        </button>
      </view>

      <view v-if="selectedGoodsList.length" class="goods-grid">
        <button
          v-for="item in selectedGoodsList"
          :key="item.id"
          class="goods-card"
          @click="goDetail(item.id)"
        >
          <image class="goods-image" :src="item.picUrl" mode="aspectFit" />
          <view class="goods-body">
            <text class="goods-name">{{ item.name }}</text>
            <text class="goods-brief">{{ item.brief || '品质好物，安心精选' }}</text>
            <view class="goods-price-row">
              <text class="goods-price">¥{{ item.retailPrice }}</text>
              <text v-if="activeFeed === 'new'" class="goods-tag">新品</text>
              <text v-else-if="activeFeed === 'hot'" class="goods-tag">热卖</text>
            </view>
          </view>
        </button>
      </view>

      <view v-else class="empty-card">
        <text class="empty-title">暂无商品</text>
        <text class="empty-desc">换个分类或稍后再来看看。</text>
      </view>
    </view>
  </view>
</template>

<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import { fetchHome, type HomePayload } from '@/entities/home/api'
import { resolveHomeFeed, shouldShowCoupons, shouldShowFlashSale, type HomeFeedKey } from './home-display-utils'

const home = ref<HomePayload | null>(null)
const activeFeed = ref<HomeFeedKey>('recommend')

const feedTabs: Array<{ key: HomeFeedKey; label: string }> = [
  { key: 'recommend', label: '推荐' },
  { key: 'new', label: '新品' },
  { key: 'hot', label: '热销' },
]

const bannerList = computed(() => home.value?.banner || [])
const channelList = computed(() => home.value?.channel || [])
const firstCoupon = computed(() => home.value?.couponList?.[0])
const flashSaleList = computed(() => home.value?.grouponList?.slice(0, 8) || [])
const showCoupons = computed(() => shouldShowCoupons(home.value))
const showFlashSale = computed(() => shouldShowFlashSale(home.value))
const selectedGoodsList = computed(() => resolveHomeFeed(activeFeed.value, home.value).slice(0, 20))
const homeChannelEntries = computed(() => channelEntries.value.slice(0, 8))
const channelEntries = computed(() => {
  const targets = [
    '/pages/items/catalog/index',
    '/pages/items/hot/index',
    '/pages/items/new/index',
    '/pages/items/groupon/index',
    '/pages/items/brand-list/index',
    '/pages/items/topic-list/index',
    '/pages/items/search/index',
    '/pages/login/index',
  ]

  return channelList.value.map((item, index) => ({
    ...item,
    target: targets[index] || '/pages/items/catalog/index',
  }))
})

onMounted(async () => {
  try {
    home.value = await fetchHome()
  } catch (error) {
    console.error(error)
  }
})

function goSearch() {
  uni.navigateTo({
    url: '/pages/items/search/index',
  })
}

function goChannel(url: string) {
  uni.navigateTo({
    url,
  })
}

function goCoupon() {
  uni.navigateTo({
    url: '/pages/user/coupon/index',
  })
}

function goDetail(id: number) {
  uni.navigateTo({
    url: `/pages/items/detail/index?id=${id}`,
  })
}
</script>

<style scoped lang="scss">
.page {
  min-height: 100vh;
  padding: 0 0 40rpx;
  background: rgb(var(--sf-color-page));
}

.home-shell {
  margin: 0 auto;
  padding: 22rpx 18rpx 24rpx;
  border-radius: 0 0 16px 16px;
  background: #f4f6fa;
}

.shop-header {
  display: flex;
  align-items: center;
  gap: 14rpx;
  padding: 0 0 18rpx;
}

.brand-name {
  flex-shrink: 0;
  font-size: 30rpx;
  line-height: 1.2;
  font-weight: 700;
  color: rgb(var(--sf-color-brand));
}

.header-search {
  display: flex;
  min-width: 0;
  flex: 1;
  align-items: center;
  height: 58rpx;
  padding: 0 26rpx;
  border: 2rpx solid rgb(var(--sf-color-line));
  border-radius: 999px;
  background: rgb(var(--sf-color-shell));
  color: rgb(var(--sf-color-text-hint));
  text-align: left;
}

.search-placeholder {
  font-size: 24rpx;
}

.empty-card {
  border: 2rpx solid rgb(var(--sf-color-line));
  background: rgb(var(--sf-color-shell));
  box-shadow: var(--sf-shadow-card);
}

.hero-swiper {
  width: 100%;
  height: 330rpx;
  overflow: hidden;
  border-radius: 8px;
  background: rgb(var(--sf-color-brand-soft));
}

.hero-image {
  width: 100%;
  height: 100%;
}

.channel-grid {
  display: grid;
  grid-template-columns: repeat(4, minmax(0, 1fr));
  gap: 14rpx 12rpx;
  margin-top: 22rpx;
  padding: 2rpx 2rpx 4rpx;
}

.channel-item {
  display: flex;
  width: 100%;
  min-width: 0;
  flex-direction: column;
  align-items: center;
  justify-content: flex-start;
  gap: 6rpx;
  min-height: 104rpx;
  padding: 10rpx 6rpx 12rpx;
  border: 2rpx solid rgb(var(--sf-color-line));
  border-radius: 8px;
  background: rgb(var(--sf-color-shell));
  line-height: 1;
}

.channel-item::after {
  border: 0;
}

.channel-icon-wrap {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 60rpx;
  height: 60rpx;
  border-radius: 8px;
  box-sizing: border-box;
  background: rgba(var(--sf-color-brand-soft), 0.74);
}

.channel-item:nth-child(2) .channel-icon-wrap {
  background: rgba(var(--sf-color-price-soft), 0.9);
}

.channel-item:nth-child(3) .channel-icon-wrap {
  border: 2rpx solid rgb(var(--sf-color-line));
  background: rgb(var(--sf-color-shell));
}

.channel-icon {
  width: 40rpx;
  height: 40rpx;
}

.channel-name {
  max-width: 100%;
  overflow: hidden;
  color: rgb(var(--sf-color-ink));
  font-size: 20rpx;
  font-weight: 500;
  line-height: 1.35;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.coupon-banner {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 20rpx;
  width: 100%;
  margin-top: 28rpx;
  padding: 22rpx 28rpx;
  border: 2rpx solid rgb(var(--sf-color-line));
  border-radius: 8px;
  background: linear-gradient(90deg, rgb(var(--sf-color-brand-soft)) 0%, rgb(var(--sf-color-shell)) 100%);
  text-align: left;
}

.coupon-title,
.coupon-desc {
  display: block;
}

.coupon-title {
  color: rgb(var(--sf-color-ink));
  font-size: 26rpx;
  font-weight: 700;
}

.coupon-desc {
  margin-top: 6rpx;
  color: rgb(var(--sf-color-text-secondary));
  font-size: 21rpx;
}

.coupon-action {
  flex-shrink: 0;
  color: rgb(var(--sf-color-price));
  font-size: 26rpx;
  font-weight: 700;
}

.flash-section {
  margin-top: 26rpx;
}

.flash-head {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.flash-title {
  color: rgb(var(--sf-color-ink));
  font-size: 32rpx;
  font-weight: 800;
}

.flash-time {
  color: rgb(var(--sf-color-text-secondary));
  font-size: 22rpx;
}

.flash-scroll {
  margin-top: 16rpx;
  white-space: nowrap;
}

.flash-list {
  display: flex;
  gap: 14rpx;
}

.flash-card {
  width: 228rpx;
  flex-shrink: 0;
  padding: 14rpx;
  border: 2rpx solid rgb(var(--sf-color-line));
  border-radius: 8px;
  background: rgb(var(--sf-color-shell));
  text-align: left;
}

.flash-image {
  width: 100%;
  height: 142rpx;
  border-radius: 6px;
  background: rgb(var(--sf-color-mist));
}

.price,
.goods-price {
  color: rgb(var(--sf-color-price));
  font-weight: 700;
}

.price {
  display: block;
  margin-top: 8rpx;
  font-size: 24rpx;
}

.feed-section {
  margin-top: 30rpx;
  padding: 0 18rpx;
  border: 0;
  background: transparent;
  box-shadow: none;
}

.feed-tabs {
  position: relative;
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 8rpx;
  padding: 8rpx;
  border: 2rpx solid rgba(var(--sf-color-line), 0.72);
  border-radius: 999px;
  background: linear-gradient(180deg, #eef1f5 0%, #f3f5f8 100%);
}

.feed-tab {
  position: relative;
  height: 62rpx;
  border-radius: 999px;
  background: transparent;
  color: rgb(var(--sf-color-text-secondary));
  font-size: 25rpx;
  font-weight: 700;
  letter-spacing: 1rpx;
}

.feed-tab::after {
  border: 0;
}

.feed-tab--active {
  background: rgb(var(--sf-color-shell));
  color: rgb(var(--sf-color-brand));
  box-shadow: 0 6rpx 14rpx rgba(44, 62, 80, 0.08);
}

.feed-tab--active::before {
  position: absolute;
  right: 34%;
  bottom: 8rpx;
  left: 34%;
  height: 4rpx;
  border-radius: 999px;
  background: rgb(var(--sf-color-brand));
  content: '';
}

.goods-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 16rpx;
  margin-top: 24rpx;
}

.goods-card {
  width: 100%;
  min-width: 0;
  border: 2rpx solid rgb(var(--sf-color-line));
  border-radius: 8px;
  background: rgb(var(--sf-color-shell));
  box-shadow: 0 4rpx 12rpx rgba(44, 62, 80, 0.04);
  text-align: left;
}

.goods-card::after {
  border: 0;
}

.goods-image {
  display: block;
  width: calc(100% - 32rpx);
  margin: 16rpx 16rpx 0;
  aspect-ratio: 1 / 1.08;
  border-radius: 6px;
  background: rgb(var(--sf-color-mist));
}

.goods-body {
  padding: 18rpx 20rpx 22rpx;
}

.goods-name,
.goods-brief {
  display: -webkit-box;
  overflow: hidden;
  -webkit-box-orient: vertical;
}

.goods-name {
  min-height: 66rpx;
  color: rgb(var(--sf-color-ink));
  font-size: 25rpx;
  font-weight: 600;
  line-height: 1.35;
  -webkit-line-clamp: 2;
}

.goods-brief {
  min-height: 32rpx;
  margin-top: 10rpx;
  color: rgb(var(--sf-color-text-secondary));
  font-size: 20rpx;
  font-weight: 600;
  line-height: 1.35;
  -webkit-line-clamp: 1;
}

.goods-price-row {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 8rpx;
  margin-top: 16rpx;
  min-height: 34rpx;
}

.goods-price {
  font-size: 29rpx;
  line-height: 1;
}

.goods-tag {
  padding: 1rpx 5rpx;
  border: 1rpx solid rgba(var(--sf-color-price), 0.22);
  border-radius: 3px;
  background: rgb(var(--sf-color-price-soft));
  color: rgb(var(--sf-color-price));
  font-size: 16rpx;
  font-weight: 600;
  line-height: 1.2;
}

.empty-card {
  margin-top: 18rpx;
  padding: 36rpx 24rpx;
  text-align: center;
}

.empty-title,
.empty-desc {
  display: block;
}

.empty-title {
  color: rgb(var(--sf-color-ink));
  font-size: 26rpx;
  font-weight: 700;
}

.empty-desc {
  margin-top: 8rpx;
  color: rgb(var(--sf-color-text-secondary));
  font-size: 22rpx;
}
</style>
