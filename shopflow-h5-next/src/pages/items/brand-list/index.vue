<template>
  <view class="page">
    <view class="hero-card">
      <text class="hero-title">品牌馆</text>
      <text class="hero-desc">先按品牌卡片流展示，点击后可继续进入品牌详情。</text>
    </view>

    <view class="brand-grid">
      <view v-for="item in brandList" :key="item.id" class="brand-card" @click="goBrand(item.id)">
        <image v-if="item.picUrl" class="brand-image" :src="item.picUrl" mode="aspectFill" />
        <view v-else class="brand-image brand-image--empty" />
        <text class="brand-name">{{ item.name }}</text>
        <text class="brand-desc">{{ item.desc }}</text>
      </view>
    </view>
  </view>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { fetchBrandList, type BrandInfo } from '@/entities/goods/api'

const brandList = ref<BrandInfo[]>([])

bootstrap()

async function bootstrap() {
  try {
    const result = await fetchBrandList({ page: 1, limit: 20 })
    brandList.value = result.list || []
  } catch (error) {
    console.error(error)
  }
}

function goBrand(id: number) {
  uni.navigateTo({
    url: `/pages/items/brand/index?brandId=${id}`,
  })
}
</script>

<style scoped lang="scss">
.page {
  min-height: 100vh;
  padding: 20rpx;
  background: linear-gradient(180deg, #ffffff 0%, #f6f8fb 100%);
}

.hero-card {
  padding: 22rpx;
  border-radius: 12rpx;
  background: #ffffff;
  box-shadow: 0 10rpx 24rpx rgba(23, 32, 51, 0.06);
}

.hero-title {
  display: block;
  font-size: 28rpx;
  line-height: 1.3;
  color: #172033;
}

.hero-desc {
  display: block;
  margin-top: 8rpx;
  font-size: 22rpx;
  line-height: 1.4;
  color: #748194;
}

.brand-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 14rpx;
  margin-top: 16rpx;
}

.brand-card {
  overflow: hidden;
  border-radius: 12rpx;
  background: #ffffff;
  box-shadow: 0 10rpx 24rpx rgba(23, 32, 51, 0.06);
}

.brand-image {
  width: 100%;
  height: 220rpx;
  background: #f3f6fb;
}

.brand-image--empty {
  background: #edf4ff;
}

.brand-name {
  display: block;
  padding: 16rpx 16rpx 0;
  font-size: 25rpx;
  line-height: 1.3;
  color: #172033;
}

.brand-desc {
  display: block;
  padding: 8rpx 16rpx 16rpx;
  font-size: 21rpx;
  line-height: 1.4;
  color: #748194;
}
</style>
