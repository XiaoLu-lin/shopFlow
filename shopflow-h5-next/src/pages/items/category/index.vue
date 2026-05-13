<template>
  <view class="page">
    <view class="hero-card">
      <text class="hero-title">{{ pageTitle }}</text>
      <text class="hero-desc">{{ categoryDesc }}</text>
    </view>

    <view class="goods-grid">
      <view v-for="item in goodsList" :key="item.id" class="goods-card" @click="goDetail(item.id)">
        <image class="goods-image" :src="item.picUrl" mode="aspectFill" />
        <view class="goods-body">
          <text class="goods-name">{{ item.name }}</text>
          <text class="goods-brief">{{ item.brief }}</text>
          <view class="goods-price-row">
            <text class="goods-price">¥ {{ item.retailPrice }}</text>
            <text class="goods-origin">¥ {{ item.counterPrice }}</text>
          </view>
        </view>
      </view>
    </view>

    <view v-if="!goodsList.length && !loading" class="empty-card">
      <text class="empty-title">当前分类还没有商品</text>
      <text class="empty-desc">后面我会继续补筛选、排序和分页。</text>
    </view>
  </view>
</template>

<script setup lang="ts">
import { computed, ref } from 'vue'
import { fetchGoodsCategory, fetchGoodsList, type GoodsCategoryPayload, type GoodsListItem } from '@/entities/goods/api'

type UniPageWithOptions = {
  options?: Record<string, unknown>
}

const pageOptions = (getCurrentPages()[getCurrentPages().length - 1] as UniPageWithOptions | undefined)?.options || {}
const categoryId = typeof pageOptions.categoryId === 'string' ? pageOptions.categoryId : ''
const categoryName = typeof pageOptions.title === 'string' ? decodeURIComponent(pageOptions.title) : ''

const loading = ref(false)
const categoryInfo = ref<GoodsCategoryPayload | null>(null)
const goodsList = ref<GoodsListItem[]>([])

const pageTitle = computed(() => categoryName || categoryInfo.value?.currentCategory?.name || '分类商品')
const categoryDesc = computed(
  () => categoryInfo.value?.currentCategory?.desc || '当前分类商品列表已经接通，后续会继续补排序与筛选。',
)

bootstrap()

async function bootstrap() {
  if (!categoryId) {
    return
  }

  loading.value = true
  try {
    const [categoryResult, goodsResult] = await Promise.all([
      fetchGoodsCategory(categoryId),
      fetchGoodsList({ categoryId, page: 1, limit: 20 }),
    ])
    categoryInfo.value = categoryResult
    goodsList.value = goodsResult.list || []
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

.goods-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 14rpx;
  margin-top: 16rpx;
}

.goods-card {
  overflow: hidden;
  border-radius: 12rpx;
  background: #ffffff;
  box-shadow: 0 10rpx 24rpx rgba(23, 32, 51, 0.06);
}

.goods-image {
  width: 100%;
  height: 260rpx;
  background: #f3f6fb;
}

.goods-body {
  padding: 16rpx;
}

.goods-name {
  display: block;
  font-size: 24rpx;
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

.goods-price-row {
  display: flex;
  align-items: flex-end;
  gap: 8rpx;
  margin-top: 12rpx;
}

.goods-price {
  font-size: 27rpx;
  line-height: 1.2;
  font-weight: 600;
  color: #172033;
}

.goods-origin {
  font-size: 20rpx;
  line-height: 1.2;
  color: #9aa5b5;
  text-decoration: line-through;
}

.empty-card {
  margin-top: 16rpx;
  padding: 28rpx 24rpx;
  border-radius: 12rpx;
  background: #ffffff;
  text-align: center;
}

.empty-title {
  display: block;
  font-size: 25rpx;
  line-height: 1.3;
  color: #172033;
}

.empty-desc {
  display: block;
  margin-top: 8rpx;
  font-size: 22rpx;
  line-height: 1.35;
  color: #748194;
}
</style>
