<template>
  <view class="page">
    <view class="browse-shell">
      <view
        class="browse-hero"
        :class="hero.tone === 'brand' ? 'browse-hero--brand' : 'browse-hero--soft'"
      >
        <view class="browse-hero-head">
          <view class="browse-hero-copy">
            <text class="browse-hero-eyebrow">{{ hero.eyebrow }}</text>
            <text class="browse-hero-title">{{ hero.title }}</text>
            <text v-if="showHeroDescription" class="browse-hero-desc">{{ hero.description }}</text>
          </view>
        </view>
      </view>

      <view v-if="brand" class="browse-panel">
        <view class="browse-panel-head">
          <text class="browse-panel-title">品牌简介</text>
        </view>
        <text class="browse-rich-copy">
          {{ resolveGoodsBrief(brand.desc, '品牌内容正在整理中，相关商品已经先收在下面。') }}
        </text>
      </view>

      <view v-if="goodsList.length" class="browse-goods-list">
        <view
          v-for="item in goodsList"
          :key="item.id"
          class="browse-goods-card"
          @click="goDetail(item.id)"
        >
          <image class="browse-goods-image" :src="item.picUrl" mode="aspectFill" />
          <view class="browse-goods-body">
            <text class="browse-goods-name">{{ item.name }}</text>
            <text class="browse-goods-brief">
              {{ resolveGoodsBrief(item.brief, '这件商品已经归入当前品牌，方便你顺着往下继续挑。') }}
            </text>
            <view class="browse-price-row">
              <text class="browse-price">¥ {{ item.retailPrice }}</text>
              <text class="browse-origin">¥ {{ item.counterPrice }}</text>
            </view>
          </view>
        </view>
      </view>

      <view v-else-if="!loading" class="browse-empty">
        <text class="browse-empty-title">{{ emptyState.title }}</text>
        <text class="browse-empty-desc">{{ emptyState.description }}</text>
      </view>
    </view>
  </view>
</template>

<script setup lang="ts">
import { computed, ref } from 'vue'
import { fetchBrandDetail, fetchBrandGoodsList, type BrandInfo, type GoodsListItem } from '@/entities/goods/api'
import {
  resolveGoodsBrief,
  resolveGoodsBrowseEmptyState,
  resolveGoodsBrowseHero,
  shouldRenderBrowseHeroDescription,
} from '@/features/goods/browse-display-utils'

type UniPageWithOptions = {
  options?: Record<string, unknown>
}

const pageOptions = (getCurrentPages()[getCurrentPages().length - 1] as UniPageWithOptions | undefined)?.options || {}
const brandId = typeof pageOptions.brandId === 'string' ? pageOptions.brandId : ''

const loading = ref(false)
const brand = ref<BrandInfo | null>(null)
const goodsList = ref<GoodsListItem[]>([])

const hero = computed(() =>
  resolveGoodsBrowseHero('brand', {
    title: brand.value?.name || '品牌详情',
    description: brand.value?.desc || '先看品牌介绍，再顺着这组商品继续挑选。',
  }),
)
const emptyState = computed(() => resolveGoodsBrowseEmptyState('brand'))
const showHeroDescription = shouldRenderBrowseHeroDescription('brand')

bootstrap()

async function bootstrap() {
  if (!brandId) {
    return
  }

  loading.value = true
  try {
    const [brandResult, goodsResult] = await Promise.all([
      fetchBrandDetail(brandId),
      fetchBrandGoodsList(brandId),
    ])
    brand.value = brandResult
    goodsList.value = goodsResult.list || []
  } catch (error) {
    console.error(error)
    goodsList.value = []
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

<style scoped lang="scss" src="../browse-page.scss"></style>
