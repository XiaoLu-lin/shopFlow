<template>
  <view class="page">
    <view class="browse-shell">
      <view class="browse-search-bar">
        <input
          v-model="keyword"
          class="browse-search-input"
          placeholder="搜索商品名称"
          placeholder-class="browse-search-placeholder"
          confirm-type="search"
          @confirm="submitSearch()"
        />
        <text class="browse-search-action" @click="submitSearch()">搜索</text>
      </view>

      <view
        v-if="submittedKeyword"
        class="browse-hero"
        :class="resultHero.tone === 'brand' ? 'browse-hero--brand' : 'browse-hero--soft'"
      >
        <view class="browse-hero-head">
          <view class="browse-hero-copy">
            <text class="browse-hero-title">{{ resultHero.title }}</text>
            <text class="browse-hero-desc">{{ resultHero.description }}</text>
          </view>
          <text class="browse-hero-action" @click="clearSearchResult">返回</text>
        </view>
      </view>

      <view v-if="!submittedKeyword" class="browse-panel">
        <view class="browse-panel-head browse-panel-head--compact">
          <text class="browse-panel-title">热门搜索</text>
        </view>
        <view class="browse-tag-list">
          <view
            v-for="item in hotKeywords"
            :key="item.keyword"
            class="browse-tag"
            @click="submitSearch(item.keyword)"
          >
            {{ item.keyword }}
          </view>
        </view>
      </view>

      <view v-if="historyKeywords.length && !submittedKeyword" class="browse-panel">
        <view class="browse-panel-head">
          <text class="browse-panel-title">最近浏览</text>
          <text class="browse-panel-link" @click="clearHistory">清空</text>
        </view>
        <view class="browse-tag-list">
          <view
            v-for="item in historyKeywords"
            :key="item"
            class="browse-tag browse-tag--light"
            @click="submitSearch(item)"
          >
            {{ item }}
          </view>
        </view>
      </view>

      <view v-if="submittedKeyword && resultList.length" class="browse-goods-list">
        <view
          v-for="item in resultList"
          :key="item.id"
          class="browse-goods-card"
          @click="goDetail(item.id)"
        >
          <image class="browse-goods-image" :src="item.picUrl" mode="aspectFill" />
          <view class="browse-goods-body">
            <text class="browse-goods-name">{{ item.name }}</text>
            <text class="browse-goods-brief">
              {{ resolveGoodsBrief(item.brief, '和当前关键词相关的商品已经先收在这里，方便你继续往下看。') }}
            </text>
            <view class="browse-price-row">
              <text class="browse-price">¥ {{ item.retailPrice }}</text>
              <text class="browse-origin">¥ {{ item.counterPrice }}</text>
            </view>
          </view>
        </view>
      </view>

      <view v-else-if="submittedKeyword && !loading" class="browse-empty">
        <text class="browse-empty-title">{{ emptyState.title }}</text>
        <text class="browse-empty-desc">{{ emptyState.description }}</text>
      </view>

      <view v-if="submittedKeyword && resultList.length" class="browse-load-state">
        {{ loadingMore ? '正在继续加载...' : hasMore ? '上拉继续浏览更多商品' : '已经到底了' }}
      </view>
    </view>
  </view>
</template>

<script setup lang="ts">
import { computed, ref } from 'vue'
import { onReachBottom } from '@dcloudio/uni-app'
import { clearSearchHistory, fetchSearchIndex, fetchSearchResult } from '@/entities/goods/api'
import type { GoodsListItem, SearchIndexPayload } from '@/entities/goods/api'
import { resolveBrowsePageState } from '@/features/goods/browse-pagination'
import {
  resolveGoodsBrief,
  resolveGoodsBrowseEmptyState,
  resolveGoodsBrowseHero,
} from '@/features/goods/browse-display-utils'

type UniPageWithOptions = {
  options?: Record<string, unknown>
}

const pageOptions = ((getCurrentPages()[getCurrentPages().length - 1] as UniPageWithOptions | undefined)?.options || {})
const defaultKeyword = typeof pageOptions.keyword === 'string' ? pageOptions.keyword : ''
const categoryId = typeof pageOptions.categoryId === 'string' ? pageOptions.categoryId : ''
const title = typeof pageOptions.title === 'string' ? decodeURIComponent(pageOptions.title) : ''

const keyword = ref(defaultKeyword || title)
const submittedKeyword = ref('')
const searchIndex = ref<SearchIndexPayload | null>(null)
const resultList = ref<GoodsListItem[]>([])
const loading = ref(false)
const loadingMore = ref(false)
const page = ref(1)
const hasMore = ref(false)

const hotKeywords = computed(() => searchIndex.value?.hotKeywordList || [])
const historyKeywords = computed(() => searchIndex.value?.historyKeywordList || [])
const resultHero = computed(() =>
  resolveGoodsBrowseHero('search-result', {
    title: submittedKeyword.value || title,
  }),
)
const emptyState = computed(() => resolveGoodsBrowseEmptyState('search-result'))

bootstrap()
onReachBottom(() => {
  void loadMore()
})

async function bootstrap() {
  try {
    searchIndex.value = await fetchSearchIndex()
  } catch (error) {
    console.error(error)
  }

  if (keyword.value.trim() || categoryId) {
    await submitSearch(keyword.value)
  }
}

async function submitSearch(nextKeyword?: string) {
  const finalKeyword = (nextKeyword ?? keyword.value).trim()
  keyword.value = finalKeyword
  submittedKeyword.value = finalKeyword || title || '分类商品'
  page.value = 1
  hasMore.value = false
  loading.value = true

  try {
    const result = await fetchSearchResult({
      keyword: finalKeyword,
      page: page.value,
      limit: 20,
      categoryId: categoryId || undefined,
    })
    const nextState = resolveBrowsePageState([], result)
    resultList.value = nextState.list
    page.value = nextState.nextPage
    hasMore.value = nextState.hasMore
  } catch (error) {
    console.error(error)
    resultList.value = []
    hasMore.value = false
  } finally {
    loading.value = false
  }
}

async function clearHistory() {
  try {
    await clearSearchHistory()
    searchIndex.value = {
      ...(searchIndex.value || {
        hotKeywordList: [],
        historyKeywordList: [],
      }),
      historyKeywordList: [],
    }
  } catch (error) {
    console.error(error)
  }
}

function clearSearchResult() {
  submittedKeyword.value = ''
  resultList.value = []
  page.value = 1
  hasMore.value = false
  loadingMore.value = false
}

function goDetail(id: number) {
  uni.navigateTo({
    url: `/pages/items/detail/index?id=${id}`,
  })
}

async function loadMore() {
  if (!submittedKeyword.value || !hasMore.value || loading.value || loadingMore.value) {
    return
  }

  loadingMore.value = true
  try {
    const result = await fetchSearchResult({
      keyword: keyword.value,
      page: page.value,
      limit: 20,
      categoryId: categoryId || undefined,
    })
    const nextState = resolveBrowsePageState(resultList.value, result)
    resultList.value = nextState.list
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
