<template>
  <view class="page">
    <view class="page-search">
      <view class="search-bar" @click="goSearch">
        <text class="search-copy">搜索商品、品牌和优惠活动</text>
        <text class="search-action">搜索</text>
      </view>
    </view>

    <scroll-view scroll-y class="page-scroll">
      <view class="page-scroll-inner">
        <view class="home-shell">
          <view class="hero-card">
            <text class="hero-eyebrow">{{ heroCopy.eyebrow }}</text>
            <text class="hero-title">{{ heroCopy.title }}</text>
          </view>

          <swiper
            v-if="bannerList.length"
            class="banner-swiper"
            indicator-dots
            circular
            autoplay
            :interval="3200"
            indicator-color="rgba(74,111,165,0.22)"
            indicator-active-color="#4A6FA5"
          >
            <swiper-item v-for="(banner, index) in bannerList" :key="`${banner.url}-${index}`">
              <image class="banner-image" :src="banner.url" mode="aspectFill" />
            </swiper-item>
          </swiper>

          <view v-if="channelEntries.length" class="channel-grid">
            <view
              v-for="channel in channelEntries"
              :key="channel.id"
              class="channel-card"
              @click="goChannel(channel.target)"
            >
              <view class="channel-icon-wrap">
                <image
                  v-if="channel.iconUrl"
                  class="channel-icon"
                  :src="channel.iconUrl"
                  mode="aspectFit"
                />
                <text v-else class="channel-icon-fallback">{{ channel.name.slice(0, 1) }}</text>
              </view>
              <text class="channel-label">{{ channel.name }}</text>
            </view>
          </view>

          <view v-if="showCoupons" class="benefit-card" @click="goCoupon">
            <view class="benefit-copy">
              <text class="benefit-title">新人福利</text>
              <text class="benefit-desc">{{ couponSummary }}</text>
            </view>
            <text class="benefit-action">去查看</text>
          </view>

          <view v-if="showFlashSale" class="groupon-card">
            <view class="section-head">
              <view>
                <text class="section-title">拼团专区</text>
                <text class="section-desc">多人拼单更划算</text>
              </view>
              <text class="section-action" @click="goChannel('/pages/items/groupon/index')">全部</text>
            </view>

            <scroll-view scroll-x class="groupon-scroll">
              <view class="groupon-list">
                <view
                  v-for="item in flashSaleList"
                  :key="item.id"
                  class="groupon-item"
                  @click="goDetail(item.id)"
                >
                  <image class="groupon-image" :src="item.picUrl" mode="aspectFill" />
                  <text class="groupon-name">{{ item.name }}</text>
                  <view class="groupon-price-row">
                    <text class="groupon-price">¥{{ item.grouponPrice || item.retailPrice }}</text>
                    <text
                      v-if="item.grouponPrice && item.grouponPrice !== item.retailPrice"
                      class="groupon-origin"
                    >
                      ¥{{ item.retailPrice }}
                    </text>
                  </view>
                </view>
              </view>
            </scroll-view>
          </view>
        </view>

        <view class="feed-section">
          <view class="feed-tabs">
            <view
              v-for="tab in feedTabs"
              :key="tab.key"
              class="feed-tab"
              :class="{ 'feed-tab--active': activeFeed === tab.key }"
              @click="activeFeed = tab.key"
            >
              {{ tab.label }}
            </view>
          </view>

          <view v-if="selectedGoodsList.length" class="goods-list">
            <view
              v-for="item in selectedGoodsList"
              :key="item.id"
              class="goods-card"
              @click="goDetail(item.id)"
            >
              <image class="goods-image" :src="item.picUrl" mode="aspectFill" />
              <view class="goods-body">
                <view class="goods-top">
                  <text class="goods-name">{{ item.name }}</text>
                  <text v-if="activeFeedTag" class="goods-tag">{{ activeFeedTag }}</text>
                </view>
                <text class="goods-brief">{{ item.brief || '精选好物已整理到更轻松的一屏结构里。' }}</text>
                <view class="goods-bottom">
                  <view class="goods-price-row">
                    <text class="goods-price">¥{{ item.retailPrice }}</text>
                    <text v-if="item.counterPrice" class="goods-origin">¥{{ item.counterPrice }}</text>
                  </view>
                  <text v-if="item.grouponDiscount" class="goods-extra">{{ item.grouponDiscount }} 折起</text>
                </view>
              </view>
            </view>
          </view>

          <view v-else class="empty-card">
            <text class="empty-title">暂时还没有推荐内容</text>
            <text class="empty-desc">换个分类看看，或者稍后再回来刷新首页。</text>
          </view>
        </view>
      </view>
    </scroll-view>
  </view>
</template>

<script setup lang="ts">
import { computed, ref } from 'vue'
import { onShow } from '@dcloudio/uni-app'
import { fetchHome, type HomePayload } from '@/entities/home/api'
import {
  createFirstTriggerSkipper,
  resolveHomeChannelEntries,
  resolveHomeFeed,
  resolveHomeHeroCopy,
  shouldShowCoupons,
  shouldShowFlashSale,
  type HomeFeedKey,
} from './home-display-utils'

const home = ref<HomePayload | null>(null)
const activeFeed = ref<HomeFeedKey>('recommend')
const heroCopy = resolveHomeHeroCopy()
const shouldSkipCurrentShow = createFirstTriggerSkipper()

const TAB_BAR_ROUTES = new Set([
  '/pages/home/index',
  '/pages/items/catalog/index',
  '/pages/order/cart/index',
  '/pages/user/index',
])

const feedTabs: Array<{ key: HomeFeedKey; label: string }> = [
  { key: 'recommend', label: '推荐' },
  { key: 'new', label: '新品' },
  { key: 'hot', label: '热销' },
]

const bannerList = computed(() => home.value?.banner || [])
const channelEntries = computed(() => resolveHomeChannelEntries(home.value?.channel || []).slice(0, 8))
const showCoupons = computed(() => shouldShowCoupons(home.value))
const showFlashSale = computed(() => shouldShowFlashSale(home.value))
const flashSaleList = computed(() => (home.value?.grouponList || []).slice(0, 6))
const selectedGoodsList = computed(() => resolveHomeFeed(activeFeed.value, home.value).slice(0, 10))
const activeFeedTag = computed(() => {
  if (activeFeed.value === 'new') {
    return '新品'
  }

  if (activeFeed.value === 'hot') {
    return '热卖'
  }

  return ''
})
const couponSummary = computed(() => {
  const couponCount = home.value?.couponList.length || 0
  const firstCoupon = home.value?.couponList[0]

  if (!firstCoupon) {
    return '可领取专享优惠，提交订单时直接使用。'
  }

  return `${couponCount} 张可领 · ${firstCoupon.name}${firstCoupon.desc ? ` · ${firstCoupon.desc}` : ''}`
})

void bootstrap()
onShow(() => {
  if (shouldSkipCurrentShow()) {
    return
  }

  void bootstrap()
})

async function bootstrap() {
  try {
    home.value = await fetchHome()
  } catch (error) {
    console.error(error)
    uni.showToast({
      title: '首页加载失败',
      icon: 'none',
    })
  }
}

function goSearch() {
  uni.navigateTo({
    url: '/pages/items/search/index',
  })
}

function goChannel(url: string) {
  if (TAB_BAR_ROUTES.has(url)) {
    uni.switchTab({
      url,
    })
    return
  }

  uni.navigateTo({
    url,
  })
}

function goCoupon() {
  uni.navigateTo({
    url: '/pages/user/coupon/index?active=0',
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
  display: flex;
  flex-direction: column;
  min-height: 0;
  height: calc(100vh - var(--window-top, 0px) - var(--window-bottom, 0px));
  background:
    radial-gradient(circle at top left, rgba(109, 140, 184, 0.14), rgba(109, 140, 184, 0) 24%),
    linear-gradient(180deg, rgba(244, 247, 251, 0.96) 0%, rgb(var(--sf-color-page)) 100%);
  overflow: hidden;
}

.page-search {
  position: relative;
  z-index: 2;
  flex-shrink: 0;
  padding: calc(var(--window-top, 0px) + 18rpx) 20rpx 12rpx;
}

.page-scroll {
  flex: 1;
  min-height: 0;
}

.page-scroll-inner {
  padding: 0 20rpx 28rpx;
}

.home-shell,
.feed-section {
  display: grid;
  gap: 14rpx;
}

.feed-section {
  margin-top: 14rpx;
}

.search-bar,
.hero-card,
.banner-swiper,
.channel-card,
.benefit-card,
.groupon-card,
.feed-tab,
.goods-card,
.empty-card {
  border: 2rpx solid rgb(var(--sf-color-line));
  border-radius: var(--sf-radius-card);
  background: rgb(var(--sf-color-shell));
  box-shadow: var(--sf-shadow-soft);
}

.search-bar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16rpx;
  min-height: 76rpx;
  padding: 0 22rpx;
}

.search-copy,
.search-action,
.hero-eyebrow,
.section-desc,
.benefit-desc,
.groupon-origin,
.goods-brief,
.goods-origin,
.goods-extra,
.empty-desc {
  color: rgb(var(--sf-color-text-secondary));
}

.search-copy {
  font-size: 23rpx;
  line-height: 1.4;
}

.search-action {
  flex-shrink: 0;
  font-size: 22rpx;
  font-weight: 600;
  color: rgb(var(--sf-color-brand));
}

.hero-card,
.benefit-card,
.groupon-card,
.empty-card {
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

.hero-title,
.section-title,
.benefit-title,
.goods-name,
.empty-title {
  display: block;
  color: rgb(var(--sf-color-ink));
}

.hero-title {
  margin-top: 6rpx;
  font-size: 30rpx;
  line-height: 1.3;
  font-weight: 700;
}

.banner-swiper {
  width: 100%;
  height: 276rpx;
  overflow: hidden;
}

.banner-image {
  width: 100%;
  height: 100%;
}

.channel-grid {
  display: grid;
  grid-template-columns: repeat(4, minmax(0, 1fr));
  gap: 10rpx;
}

.channel-card {
  display: grid;
  justify-items: center;
  gap: 8rpx;
  padding: 16rpx 10rpx 14rpx;
}

.channel-icon-wrap {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 62rpx;
  height: 62rpx;
  border-radius: var(--sf-radius-card);
  background: linear-gradient(145deg, rgba(237, 243, 248, 0.98) 0%, rgba(223, 231, 241, 0.98) 100%);
}

.channel-icon {
  width: 38rpx;
  height: 38rpx;
}

.channel-icon-fallback {
  font-size: 22rpx;
  line-height: 1;
  font-weight: 700;
  color: rgb(var(--sf-color-brand));
}

.channel-label {
  display: block;
  max-width: 100%;
  overflow: hidden;
  font-size: 20rpx;
  line-height: 1.35;
  color: rgb(var(--sf-color-ink));
  text-overflow: ellipsis;
  white-space: nowrap;
}

.benefit-card,
.section-head,
.groupon-price-row,
.goods-top,
.goods-bottom,
.goods-price-row {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16rpx;
}

.benefit-copy,
.goods-body {
  min-width: 0;
  flex: 1;
}

.benefit-title,
.section-title {
  font-size: 26rpx;
  line-height: 1.35;
  font-weight: 700;
}

.benefit-desc,
.section-desc {
  display: block;
  margin-top: 6rpx;
  font-size: 20rpx;
  line-height: 1.45;
}

.benefit-action,
.section-action {
  flex-shrink: 0;
  padding: 10rpx 18rpx;
  border-radius: 999px;
  background: rgb(var(--sf-color-brand-soft));
  font-size: 20rpx;
  line-height: 1.2;
  color: rgb(var(--sf-color-brand-deep));
}

.groupon-scroll {
  margin-top: 14rpx;
  white-space: nowrap;
}

.groupon-list {
  display: inline-flex;
  gap: 12rpx;
}

.groupon-item {
  width: 184rpx;
  padding: 10rpx;
  border-radius: var(--sf-radius-card);
  background: linear-gradient(180deg, rgba(247, 250, 253, 0.98) 0%, rgba(255, 255, 255, 0.98) 100%);
}

.groupon-image {
  width: 100%;
  height: 164rpx;
  border-radius: var(--sf-radius-image);
  background: rgb(var(--sf-color-mist));
}

.groupon-name {
  display: -webkit-box;
  margin-top: 10rpx;
  overflow: hidden;
  font-size: 21rpx;
  line-height: 1.4;
  color: rgb(var(--sf-color-ink));
  -webkit-box-orient: vertical;
  -webkit-line-clamp: 2;
}

.groupon-price-row {
  justify-content: flex-start;
  gap: 10rpx;
  margin-top: 10rpx;
}

.groupon-price,
.goods-price {
  font-size: 26rpx;
  line-height: 1.2;
  font-weight: 700;
  color: rgb(var(--sf-color-price));
}

.groupon-origin,
.goods-origin {
  font-size: 19rpx;
  line-height: 1.2;
  text-decoration: line-through;
}

.feed-tabs {
  display: flex;
  flex-wrap: wrap;
  gap: 12rpx;
}

.feed-tab {
  padding: 12rpx 22rpx;
  font-size: 21rpx;
  line-height: 1.2;
  color: rgb(var(--sf-color-text-secondary));
}

.feed-tab--active {
  background: linear-gradient(145deg, rgba(237, 243, 248, 0.98) 0%, rgba(223, 231, 241, 0.98) 100%);
  color: rgb(var(--sf-color-brand));
  font-weight: 700;
}

.goods-list {
  display: grid;
  gap: 14rpx;
}

.goods-card {
  display: flex;
  align-items: flex-start;
  gap: 16rpx;
  padding: 14rpx;
}

.goods-image {
  width: 156rpx;
  height: 156rpx;
  flex-shrink: 0;
  border-radius: var(--sf-radius-image);
  background: rgb(var(--sf-color-mist));
}

.goods-top {
  align-items: flex-start;
}

.goods-name {
  display: -webkit-box;
  overflow: hidden;
  font-size: 24rpx;
  line-height: 1.4;
  font-weight: 600;
  -webkit-box-orient: vertical;
  -webkit-line-clamp: 2;
}

.goods-tag {
  flex-shrink: 0;
  padding: 8rpx 14rpx;
  border-radius: 999px;
  background: rgb(var(--sf-color-brand-soft));
  font-size: 18rpx;
  line-height: 1.2;
  color: rgb(var(--sf-color-brand-deep));
}

.goods-brief {
  display: -webkit-box;
  margin-top: 10rpx;
  overflow: hidden;
  font-size: 20rpx;
  line-height: 1.45;
  -webkit-box-orient: vertical;
  -webkit-line-clamp: 2;
}

.goods-bottom {
  margin-top: 14rpx;
}

.goods-price-row {
  justify-content: flex-start;
  gap: 10rpx;
}

.goods-extra {
  flex-shrink: 0;
  font-size: 19rpx;
  line-height: 1.3;
}

.empty-card {
  text-align: center;
}

.empty-title {
  font-size: 24rpx;
  line-height: 1.35;
  font-weight: 600;
}

.empty-desc {
  display: block;
  margin-top: 8rpx;
  font-size: 20rpx;
  line-height: 1.45;
}
</style>
