<template>
  <view class="page">
    <view class="hero-card">
      <text class="hero-title">拼团专区</text>
      <text class="hero-desc">先展示成团人数和优惠力度，后续继续补完整拼团链路。</text>
    </view>

    <view class="goods-list">
      <view v-for="item in goodsList" :key="item.id" class="goods-card" @click="goDetail(item.id)">
        <image class="goods-image" :src="item.picUrl" mode="aspectFill" />
        <view class="goods-body">
          <text class="goods-name">{{ item.name }}</text>
          <text class="goods-brief">{{ item.brief }}</text>
          <view class="tag-row">
            <text class="tag">{{ item.grouponMember }}人成团</text>
            <text class="tag tag--blue">立减 {{ item.grouponDiscount }} 元</text>
          </view>
          <view class="goods-price-row">
            <text class="goods-price">¥ {{ item.retailPrice }}</text>
            <text class="goods-origin">¥ {{ item.counterPrice }}</text>
          </view>
        </view>
      </view>
    </view>
  </view>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { fetchGrouponList, type GrouponListItem } from '@/entities/goods/api'

const goodsList = ref<GrouponListItem[]>([])

bootstrap()

async function bootstrap() {
  try {
    const result = await fetchGrouponList({ page: 1, limit: 20 })
    goodsList.value = result.list || []
  } catch (error) {
    console.error(error)
  }
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

.goods-list {
  display: grid;
  gap: 14rpx;
  margin-top: 16rpx;
}

.goods-card {
  display: flex;
  gap: 16rpx;
  padding: 16rpx;
  border-radius: 12rpx;
  background: #ffffff;
  box-shadow: 0 10rpx 24rpx rgba(23, 32, 51, 0.06);
}

.goods-image {
  width: 176rpx;
  height: 176rpx;
  border-radius: 10rpx;
  background: #f3f6fb;
}

.goods-body {
  min-width: 0;
  flex: 1;
}

.goods-name {
  display: block;
  font-size: 25rpx;
  line-height: 1.35;
  color: #172033;
}

.goods-brief {
  display: block;
  margin-top: 8rpx;
  font-size: 21rpx;
  line-height: 1.4;
  color: #748194;
}

.tag-row {
  display: flex;
  flex-wrap: wrap;
  gap: 8rpx;
  margin-top: 10rpx;
}

.tag {
  padding: 6rpx 12rpx;
  border-radius: 999px;
  background: #f3f6fb;
  font-size: 20rpx;
  line-height: 1.2;
  color: #5f6b7c;
}

.tag--blue {
  background: #edf5ff;
  color: #1677ff;
}

.goods-price-row {
  display: flex;
  align-items: flex-end;
  gap: 10rpx;
  margin-top: 14rpx;
}

.goods-price {
  font-size: 28rpx;
  font-weight: 600;
  line-height: 1.2;
  color: #172033;
}

.goods-origin {
  font-size: 21rpx;
  line-height: 1.2;
  color: #9aa5b5;
  text-decoration: line-through;
}
</style>
