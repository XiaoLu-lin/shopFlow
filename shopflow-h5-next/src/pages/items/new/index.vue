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
              {{ resolveGoodsBrief(item.brief, '这段时间刚刚上新的商品，已经先按顺序收在这里。') }}
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

      <view v-if="goodsList.length" class="browse-load-state">
        {{ loadingMore ? '正在继续加载...' : hasMore ? '上拉继续浏览更多商品' : '已经到底了' }}
      </view>
    </view>
  </view>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { onReachBottom } from '@dcloudio/uni-app'
import { fetchNewGoodsList, type GoodsListItem } from '@/entities/goods/api'
import { resolveBrowsePageState } from '@/features/goods/browse-pagination'
import {
  resolveGoodsBrief,
  resolveGoodsBrowseEmptyState,
  resolveGoodsBrowseHero,
  shouldRenderBrowseHeroDescription,
} from '@/features/goods/browse-display-utils'

const loading = ref(false)
const loadingMore = ref(false)
const goodsList = ref<GoodsListItem[]>([])
const page = ref(1)
const hasMore = ref(false)

const hero = resolveGoodsBrowseHero('new')
const emptyState = resolveGoodsBrowseEmptyState('new')
const showHeroDescription = shouldRenderBrowseHeroDescription('new')

bootstrap()
onReachBottom(() => {
  void loadMore()
})

async function bootstrap() {
  loading.value = true
  page.value = 1
  hasMore.value = false
  try {
    const result = await fetchNewGoodsList({ page: page.value, limit: 20 })
    const nextState = resolveBrowsePageState([], result)
    goodsList.value = nextState.list
    page.value = nextState.nextPage
    hasMore.value = nextState.hasMore
  } catch (error) {
    console.error(error)
    goodsList.value = []
    hasMore.value = false
  } finally {
    loading.value = false
  }
}

function goDetail(id: number) {
  uni.navigateTo({
    url: `/pages/items/detail/index?id=${id}`,
  })
}

async function loadMore() {
  if (!hasMore.value || loading.value || loadingMore.value) {
    return
  }

  loadingMore.value = true
  try {
    const result = await fetchNewGoodsList({ page: page.value, limit: 20 })
    const nextState = resolveBrowsePageState(goodsList.value, result)
    goodsList.value = nextState.list
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
