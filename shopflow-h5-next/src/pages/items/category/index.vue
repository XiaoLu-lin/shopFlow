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

      <view v-if="categoryPills.length" class="browse-panel">
        <view class="browse-pill-list">
          <view
            v-for="item in categoryPills"
            :key="item.id"
            class="browse-pill"
            :class="{ 'browse-pill--active': Number(item.id) === currentCategoryId }"
            @click="switchCategory(item.id, item.name)"
          >
            {{ item.name }}
          </view>
        </view>
      </view>

      <view v-if="goodsList.length" class="browse-goods-list">
        <view v-for="item in goodsList" :key="item.id" class="browse-goods-card" @click="goDetail(item.id)">
          <image class="browse-goods-image" :src="item.picUrl" mode="aspectFill" />
          <view class="browse-goods-body">
            <text class="browse-goods-name">{{ item.name }}</text>
            <text class="browse-goods-brief">
              {{ resolveGoodsBrief(item.brief, '把同一分类下的商品先收在这里，方便你继续往下挑选。') }}
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
import { computed, ref } from 'vue'
import { onReachBottom } from '@dcloudio/uni-app'
import { fetchGoodsCategory, fetchGoodsList, type GoodsCategoryPayload, type GoodsListItem } from '@/entities/goods/api'
import { resolveBrowsePageState } from '@/features/goods/browse-pagination'
import {
  resolveCategoryBrowseTabs,
  resolveGoodsBrief,
  resolveGoodsBrowseEmptyState,
  resolveGoodsBrowseHero,
  shouldRenderBrowseHeroDescription,
} from '@/features/goods/browse-display-utils'

type UniPageWithOptions = {
  options?: Record<string, unknown>
}

const pageOptions = (getCurrentPages()[getCurrentPages().length - 1] as UniPageWithOptions | undefined)?.options || {}
const categoryId = typeof pageOptions.categoryId === 'string' ? pageOptions.categoryId : ''
const categoryName = typeof pageOptions.title === 'string' ? decodeURIComponent(pageOptions.title) : ''

const loading = ref(false)
const loadingMore = ref(false)
const categoryInfo = ref<GoodsCategoryPayload | null>(null)
const goodsList = ref<GoodsListItem[]>([])
const page = ref(1)
const hasMore = ref(false)

const currentCategoryId = computed(() => Number(categoryId))
const pageTitle = computed(() => categoryName || categoryInfo.value?.currentCategory?.name || '分类商品')
const categoryDesc = computed(
  () => categoryInfo.value?.currentCategory?.desc || '当前分类下的商品已经集中收在这里，方便你继续浏览。',
)
const hero = computed(() =>
  resolveGoodsBrowseHero('category', {
    title: pageTitle.value,
    description: categoryDesc.value,
  }),
)
const showHeroDescription = shouldRenderBrowseHeroDescription('category')
const categoryPills = computed(() => {
  const current = categoryInfo.value?.currentCategory || null
  const brothers = categoryInfo.value?.brotherCategory || []
  return resolveCategoryBrowseTabs(current, brothers)
})
const emptyState = computed(() =>
  resolveGoodsBrowseEmptyState('category', {
    title: pageTitle.value,
  }),
)

bootstrap()
onReachBottom(() => {
  void loadMore()
})

async function bootstrap() {
  if (!categoryId) {
    return
  }

  loading.value = true
  page.value = 1
  hasMore.value = false
  try {
    const [categoryResult, goodsResult] = await Promise.all([
      fetchGoodsCategory(categoryId),
      fetchGoodsList({ categoryId, page: page.value, limit: 20 }),
    ])
    const nextState = resolveBrowsePageState([], goodsResult)
    categoryInfo.value = categoryResult
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

function switchCategory(nextCategoryId: number, nextTitle: string) {
  uni.redirectTo({
    url: `/pages/items/category/index?categoryId=${nextCategoryId}&title=${encodeURIComponent(nextTitle)}`,
  })
}

function goDetail(id: number) {
  uni.navigateTo({
    url: `/pages/items/detail/index?id=${id}`,
  })
}

async function loadMore() {
  if (!categoryId || !hasMore.value || loading.value || loadingMore.value) {
    return
  }

  loadingMore.value = true
  try {
    const result = await fetchGoodsList({ categoryId, page: page.value, limit: 20 })
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

<style scoped lang="scss">
.browse-pill--active {
  background: rgb(var(--sf-color-brand-soft));
  color: rgb(var(--sf-color-brand));
}
</style>
