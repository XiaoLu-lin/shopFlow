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

      <view v-if="brandList.length" class="browse-brand-list">
        <view
          v-for="item in brandList"
          :key="item.id"
          class="browse-brand-card"
          @click="goBrand(item.id)"
        >
          <image v-if="item.picUrl" class="browse-brand-image" :src="item.picUrl" mode="aspectFill" />
          <view v-else class="browse-brand-image" />
          <view class="browse-brand-body">
            <text class="browse-brand-name">{{ item.name }}</text>
            <text class="browse-brand-brief">
              {{ resolveGoodsBrief(item.desc, '先把品牌介绍收轻一点，进入详情后再继续看商品。') }}
            </text>
            <view class="browse-info-tags">
              <text class="browse-info-tag browse-info-tag--light">进入品牌详情</text>
            </view>
          </view>
        </view>
      </view>

      <view v-else-if="!loading" class="browse-empty">
        <text class="browse-empty-title">{{ emptyState.title }}</text>
        <text class="browse-empty-desc">{{ emptyState.description }}</text>
      </view>

      <view v-if="brandList.length" class="browse-load-state">
        {{ loadingMore ? '正在继续加载...' : hasMore ? '上拉继续浏览更多品牌' : '已经到底了' }}
      </view>
    </view>
  </view>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { onReachBottom } from '@dcloudio/uni-app'
import { fetchBrandList, type BrandInfo } from '@/entities/goods/api'
import { resolveBrowsePageState } from '@/features/goods/browse-pagination'
import {
  resolveGoodsBrief,
  resolveGoodsBrowseEmptyState,
  resolveGoodsBrowseHero,
  shouldRenderBrowseHeroDescription,
} from '@/features/goods/browse-display-utils'

const loading = ref(false)
const loadingMore = ref(false)
const brandList = ref<BrandInfo[]>([])
const page = ref(1)
const hasMore = ref(false)

const hero = resolveGoodsBrowseHero('brand-list')
const emptyState = resolveGoodsBrowseEmptyState('brand-list')
const showHeroDescription = shouldRenderBrowseHeroDescription('brand-list')

bootstrap()
onReachBottom(() => {
  void loadMore()
})

async function bootstrap() {
  loading.value = true
  page.value = 1
  hasMore.value = false
  try {
    const result = await fetchBrandList({ page: page.value, limit: 20 })
    const nextState = resolveBrowsePageState([], result)
    brandList.value = nextState.list
    page.value = nextState.nextPage
    hasMore.value = nextState.hasMore
  } catch (error) {
    console.error(error)
    brandList.value = []
    hasMore.value = false
  } finally {
    loading.value = false
  }
}

function goBrand(id: number) {
  uni.navigateTo({
    url: `/pages/items/brand/index?brandId=${id}`,
  })
}

async function loadMore() {
  if (!hasMore.value || loading.value || loadingMore.value) {
    return
  }

  loadingMore.value = true
  try {
    const result = await fetchBrandList({ page: page.value, limit: 20 })
    const nextState = resolveBrowsePageState(brandList.value, result)
    brandList.value = nextState.list
    page.value = nextState.nextPage
    hasMore.value = nextState.hasMore
  } catch (error) {
    console.error(error)
  } finally {
    loadingMore.value = false
  }
}
</script>

<style scoped lang="scss" src="../browse-page.scss"></style>
