<template>
  <view class="page">
    <button class="search-entry" @click="goSearch">
      <view class="search-icon">⌕</view>
      <view class="search-copy">
        <text class="search-title">搜索商品 / 品牌 / 专题</text>
        <text class="search-desc">搜索页已经接上，先把匿名浏览主链路跑通</text>
      </view>
      <text class="search-tag">搜索</text>
    </button>

    <view v-if="bannerList.length" class="hero-card">
      <swiper
        class="hero-swiper"
        indicator-dots
        autoplay
        circular
        :interval="3200"
        indicator-color="rgba(255,255,255,0.5)"
        indicator-active-color="#1677ff"
      >
        <swiper-item v-for="(banner, index) in bannerList" :key="`${banner.url}-${index}`">
          <image class="hero-image" :src="banner.url" mode="aspectFill" />
        </swiper-item>
      </swiper>
    </view>

    <view v-else class="empty-card">
      <text class="empty-title">首页数据还没有加载出来</text>
      <text class="empty-desc">
        当前会继续请求 `/wx/home/index`。如果本地后端没有启动，这里会先显示静态占位。
      </text>
    </view>

    <view v-if="channelList.length" class="section-card">
      <view class="channel-grid">
        <button
          v-for="channel in channelEntries"
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
    </view>

    <view v-if="couponList.length" class="section">
      <view class="section-head">
        <text class="section-title">优惠券</text>
        <text class="section-more">领取后下单自动抵扣</text>
      </view>
      <view class="coupon-list">
        <view v-for="coupon in couponList.slice(0, 2)" :key="coupon.id" class="coupon-card">
          <view>
            <text class="coupon-name">{{ coupon.name }}</text>
            <text class="coupon-desc">{{ coupon.desc }}</text>
          </view>
          <view class="coupon-side">
            <text class="coupon-amount">¥{{ coupon.discount }}</text>
            <text class="coupon-tag">{{ coupon.tag }}</text>
          </view>
        </view>
      </view>
    </view>

    <view v-if="newGoodsList.length" class="section">
      <view class="section-head">
        <text class="section-title">新品首发</text>
        <text class="section-more">持续迁移中</text>
      </view>
      <view class="goods-grid">
        <button
          v-for="item in newGoodsList.slice(0, 4)"
          :key="item.id"
          class="goods-card"
          @click="goDetail(item.id)"
        >
          <image class="goods-image" :src="item.picUrl" mode="aspectFill" />
          <view class="goods-body">
            <text class="goods-name">{{ item.name }}</text>
            <text class="goods-brief">{{ item.brief }}</text>
            <view class="goods-price-row">
              <text class="goods-price">¥ {{ item.retailPrice }}</text>
              <text class="goods-badge">新品</text>
            </view>
          </view>
        </button>
      </view>
    </view>
  </view>
</template>

<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import { fetchHome, type HomePayload } from '@/entities/home/api'

const home = ref<HomePayload | null>(null)

const bannerList = computed(() => home.value?.banner || [])
const channelList = computed(() => home.value?.channel || [])
const couponList = computed(() => home.value?.couponList || [])
const newGoodsList = computed(() => home.value?.newGoodsList || [])
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

  return channelList.value.slice(0, 8).map((item, index) => ({
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

function goCatalog() {
  uni.navigateTo({
    url: '/pages/items/catalog/index',
  })
}

function goChannel(url: string) {
  uni.navigateTo({
    url,
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
  padding: 24rpx;
  background: linear-gradient(180deg, #ffffff 0%, #f6f8fb 100%);
}

.search-entry {
  display: flex;
  align-items: center;
  gap: 20rpx;
  width: 100%;
  padding: 22rpx 24rpx;
  border: 0;
  border-radius: 22rpx;
  background: #ffffff;
  box-shadow: 0 12rpx 28rpx rgba(23, 32, 51, 0.06);
  text-align: left;
}

.search-icon {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 72rpx;
  height: 72rpx;
  border-radius: 18rpx;
  background: #eaf3ff;
  color: #1677ff;
  font-size: 32rpx;
}

.search-copy {
  min-width: 0;
  flex: 1;
}

.search-title {
  display: block;
  font-size: 28rpx;
  line-height: 1.35;
  color: #172033;
}

.search-desc {
  display: block;
  margin-top: 6rpx;
  font-size: 22rpx;
  line-height: 1.4;
  color: #7a8798;
}

.search-tag {
  padding: 10rpx 16rpx;
  border-radius: 999px;
  background: #edf5ff;
  color: #1677ff;
  font-size: 22rpx;
}

.hero-card {
  margin-top: 20rpx;
  padding: 12rpx;
  border-radius: 22rpx;
  background: #ffffff;
  box-shadow: 0 12rpx 28rpx rgba(23, 32, 51, 0.06);
}

.hero-swiper {
  height: 300rpx;
  border-radius: 18rpx;
  overflow: hidden;
}

.hero-image {
  width: 100%;
  height: 100%;
}

.empty-card {
  margin-top: 20rpx;
  padding: 28rpx;
  border-radius: 22rpx;
  background: #ffffff;
  box-shadow: 0 12rpx 28rpx rgba(23, 32, 51, 0.06);
}

.empty-title {
  display: block;
  font-size: 28rpx;
  line-height: 1.35;
  color: #172033;
}

.empty-desc {
  display: block;
  margin-top: 10rpx;
  font-size: 23rpx;
  line-height: 1.55;
  color: #748194;
}

.section-card {
  margin-top: 20rpx;
  padding: 16rpx;
  border-radius: 22rpx;
  background: #ffffff;
  box-shadow: 0 12rpx 28rpx rgba(23, 32, 51, 0.06);
}

.channel-grid {
  display: grid;
  grid-template-columns: repeat(4, minmax(0, 1fr));
  gap: 12rpx;
}

.channel-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 10rpx;
  padding: 12rpx 8rpx;
  border: 0;
  border-radius: 18rpx;
  background: #f9fbff;
}

.channel-icon-wrap {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 88rpx;
  height: 88rpx;
  border-radius: 20rpx;
  background: #edf4ff;
}

.channel-icon {
  width: 46rpx;
  height: 46rpx;
}

.channel-name {
  font-size: 22rpx;
  line-height: 1.35;
  color: #4b5870;
  text-align: center;
}

.section {
  margin-top: 22rpx;
}

.section-head {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 14rpx;
}

.section-title {
  font-size: 30rpx;
  font-weight: 600;
  line-height: 1.3;
  color: #172033;
}

.section-more {
  font-size: 22rpx;
  color: #7b8799;
}

.goods-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 16rpx;
}

.coupon-list {
  display: grid;
  gap: 14rpx;
}

.coupon-card {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 18rpx;
  padding: 22rpx 24rpx;
  border-radius: 22rpx;
  background: linear-gradient(135deg, #fff7e6 0%, #ffffff 100%);
  box-shadow: 0 12rpx 28rpx rgba(23, 32, 51, 0.06);
}

.coupon-name {
  display: block;
  font-size: 28rpx;
  line-height: 1.35;
  color: #172033;
}

.coupon-desc {
  display: block;
  margin-top: 8rpx;
  font-size: 22rpx;
  line-height: 1.45;
  color: #7a8798;
}

.coupon-side {
  display: flex;
  flex-direction: column;
  align-items: flex-end;
  gap: 8rpx;
  flex-shrink: 0;
}

.coupon-amount {
  font-size: 36rpx;
  font-weight: 600;
  line-height: 1;
  color: #d97706;
}

.coupon-tag {
  padding: 6rpx 12rpx;
  border-radius: 999px;
  background: rgba(217, 119, 6, 0.12);
  color: #b45309;
  font-size: 20rpx;
}

.goods-card {
  overflow: hidden;
  border: 0;
  border-radius: 22rpx;
  background: #ffffff;
  box-shadow: 0 12rpx 28rpx rgba(23, 32, 51, 0.06);
  text-align: left;
}

.goods-image {
  width: 100%;
  height: 260rpx;
  background: #f5f7fb;
}

.goods-body {
  padding: 18rpx;
}

.goods-name {
  display: block;
  font-size: 26rpx;
  line-height: 1.4;
  color: #172033;
}

.goods-brief {
  display: block;
  margin-top: 8rpx;
  font-size: 22rpx;
  line-height: 1.45;
  color: #788496;
}

.goods-price-row {
  display: flex;
  align-items: center;
  gap: 10rpx;
  margin-top: 12rpx;
}

.goods-price {
  font-size: 28rpx;
  font-weight: 600;
  color: #172033;
}

.goods-badge {
  padding: 6rpx 10rpx;
  border-radius: 999px;
  background: #edf5ff;
  color: #1677ff;
  font-size: 20rpx;
}
</style>
