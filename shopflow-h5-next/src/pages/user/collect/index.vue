<template>
  <view class="page">
    <view class="hero-card">
      <view class="hero-row">
        <view class="hero-avatar">藏</view>
        <view class="hero-copy">
          <text class="eyebrow">{{ hero.eyebrow }}</text>
          <text class="title">{{ hero.title }}</text>
        </view>
      </view>
      <text class="desc">{{ hero.description }}</text>
    </view>

    <view v-if="loading" class="collect-list">
      <view v-for="index in 3" :key="index" class="collect-card">
        <view class="skeleton-image"></view>
        <view class="collect-body">
          <view class="skeleton-line skeleton-line--title"></view>
          <view class="skeleton-line"></view>
          <view class="skeleton-line skeleton-line--short"></view>
        </view>
      </view>
    </view>

    <view v-else-if="list.length" class="collect-list">
      <view v-for="item in list" :key="item.id" class="collect-card">
        <view class="collect-main" @click="goDetail(item.valueId)">
          <image class="collect-image" :src="item.picUrl" mode="aspectFill" />
          <view class="collect-body">
            <text class="collect-title">{{ item.name }}</text>
            <text class="collect-brief">{{ item.brief }}</text>
            <view class="price-row">
              <text class="price">¥{{ item.retailPrice }}</text>
              <text class="origin-price">¥{{ item.counterPrice }}</text>
            </view>
          </view>
        </view>
        <view class="ghost-btn" @click="removeCollect(item.valueId)">删除</view>
      </view>
    </view>

    <view v-else class="empty-card">
      <text class="empty-title">还没有收藏商品</text>
      <text class="empty-desc">先去逛逛商品页，遇到喜欢的先收起来。</text>
    </view>
  </view>
</template>

<script setup lang="ts">
import { onShow } from '@dcloudio/uni-app'
import { ref } from 'vue'
import { fetchCollectList, toggleCollect, type CollectItem } from '@/entities/user/api'
import { resolveUserPageHero } from '../page-display-utils'

const loading = ref(true)
const list = ref<CollectItem[]>([])
const hero = resolveUserPageHero('collect')

bootstrap()
onShow(() => {
  void bootstrap()
})

async function bootstrap() {
  loading.value = true

  try {
    const result = await fetchCollectList({
      type: 0,
      page: 1,
      limit: 20,
    })
    list.value = result.list || []
  } catch (error) {
    console.error(error)
  } finally {
    loading.value = false
  }
}

function goDetail(id: number) {
  uni.navigateTo({
    url: `/pages/items/detail/index?id=${id}`,
  })
}

async function removeCollect(valueId: number) {
  try {
    await toggleCollect(valueId)
    uni.showToast({
      title: '已取消收藏',
      icon: 'none',
    })
    await bootstrap()
  } catch (error) {
    console.error(error)
    uni.showToast({
      title: '取消收藏失败',
      icon: 'none',
    })
  }
}
</script>

<style scoped lang="scss">
.page {
  min-height: 100vh;
  padding: 14rpx 14rpx 32rpx;
  background: linear-gradient(180deg, rgb(var(--sf-color-brand-soft)) 0%, rgb(var(--sf-color-page)) 26%, #ffffff 100%);
}

.collect-card,
.empty-card {
  border-radius: var(--sf-radius-card);
  background: #ffffff;
  border: 1px solid rgb(var(--sf-color-line));
  box-shadow: var(--sf-shadow-soft);
}

.hero-card {
  border-radius: var(--sf-radius-card);
  padding: 14rpx 16rpx 16rpx;
  color: #ffffff;
  background: linear-gradient(145deg, rgb(var(--sf-color-brand)) 0%, rgb(var(--sf-color-brand-light)) 100%);
  box-shadow: var(--sf-shadow-brand);
}

.hero-card,
.empty-card {
  padding: 14rpx 16rpx;
}

.hero-row {
  display: flex;
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
.collect-title {
  color: rgb(var(--sf-color-ink));
}

.empty-desc,
.collect-brief {
  color: rgb(var(--sf-color-text-secondary));
}

.collect-list {
  display: grid;
  gap: 8rpx;
  margin-top: 10rpx;
}

.collect-card {
  display: flex;
  gap: 10rpx;
  padding: 12rpx;
}

.collect-main {
  display: flex;
  min-width: 0;
  flex: 1;
  gap: 10rpx;
}

.collect-image,
.skeleton-image {
  width: 120rpx;
  height: 120rpx;
  flex-shrink: 0;
  border-radius: var(--sf-radius-card);
  background: rgb(var(--sf-color-divider));
}

.collect-body {
  min-width: 0;
  flex: 1;
}

.collect-title {
  display: -webkit-box;
  overflow: hidden;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  font-size: 20rpx;
  line-height: 1.4;
  color: rgb(var(--sf-color-ink));
}

.collect-brief {
  display: -webkit-box;
  margin-top: 6rpx;
  overflow: hidden;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  font-size: 18rpx;
  line-height: 1.45;
  color: rgb(var(--sf-color-text-secondary));
}

.price-row {
  display: flex;
  align-items: flex-end;
  gap: 6rpx;
  margin-top: 8rpx;
}

.price {
  font-size: 26rpx;
  line-height: 1.1;
  color: rgb(var(--sf-color-price));
}

.origin-price {
  font-size: 16rpx;
  color: rgb(var(--sf-color-text-hint));
  text-decoration: line-through;
}

.ghost-btn {
  align-self: flex-end;
  padding: 8rpx 12rpx;
  border-radius: var(--sf-radius-card);
  background: rgb(var(--sf-color-brand-soft));
  font-size: 18rpx;
  color: rgb(var(--sf-color-brand));
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
  width: 108rpx;
}

.empty-card {
  margin-top: 10rpx;
  text-align: center;
}
</style>
