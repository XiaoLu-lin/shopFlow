<template>
  <view class="page">
    <view class="hero-card">
      <text class="title">我的收藏</text>
      <text class="desc">已接回旧站收藏列表与取消收藏接口，点击卡片可回到商品详情。</text>
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

const loading = ref(true)
const list = ref<CollectItem[]>([])

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
  padding: 20rpx 20rpx 40rpx;
  background: linear-gradient(180deg, #ffffff 0%, #f6f8fb 100%);
}

.hero-card,
.collect-card,
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

.collect-list {
  display: grid;
  gap: 14rpx;
  margin-top: 16rpx;
}

.collect-card {
  display: flex;
  gap: 14rpx;
  padding: 18rpx;
}

.collect-main {
  display: flex;
  min-width: 0;
  flex: 1;
  gap: 14rpx;
}

.collect-image,
.skeleton-image {
  width: 144rpx;
  height: 144rpx;
  flex-shrink: 0;
  border-radius: 10rpx;
  background: #edf1f6;
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
  font-size: 24rpx;
  line-height: 1.4;
  color: #172033;
}

.collect-brief {
  display: -webkit-box;
  margin-top: 10rpx;
  overflow: hidden;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  font-size: 22rpx;
  line-height: 1.45;
  color: #748194;
}

.price-row {
  display: flex;
  align-items: flex-end;
  gap: 10rpx;
  margin-top: 14rpx;
}

.price {
  font-size: 30rpx;
  line-height: 1.1;
  color: #1677ff;
}

.origin-price {
  font-size: 20rpx;
  color: #a0aaba;
  text-decoration: line-through;
}

.ghost-btn {
  align-self: flex-end;
  padding: 12rpx 16rpx;
  border-radius: 10rpx;
  background: #f6f8fb;
  font-size: 22rpx;
  color: #5f6b7c;
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
  width: 120rpx;
}

.empty-card {
  margin-top: 16rpx;
  text-align: center;
}
</style>
