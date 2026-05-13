<template>
  <view class="page">
    <view class="search-bar">
      <input
        v-model="keyword"
        class="search-input"
        placeholder="搜索商品名称"
        placeholder-class="search-placeholder"
        confirm-type="search"
        @confirm="submitSearch()"
      />
      <text class="search-action" @click="submitSearch()">搜索</text>
    </view>

    <view v-if="!submittedKeyword" class="panel">
      <view class="panel-head">
        <text class="panel-title">热门搜索</text>
      </view>
      <view class="tag-list">
        <view
          v-for="item in hotKeywords"
          :key="item.keyword"
          class="tag"
          @click="submitSearch(item.keyword)"
        >
          {{ item.keyword }}
        </view>
      </view>
    </view>

    <view v-if="historyKeywords.length && !submittedKeyword" class="panel">
      <view class="panel-head">
        <text class="panel-title">历史搜索</text>
        <text class="panel-link" @click="clearHistory">清空</text>
      </view>
      <view class="tag-list">
        <view
          v-for="item in historyKeywords"
          :key="item"
          class="tag tag--light"
          @click="submitSearch(item)"
        >
          {{ item }}
        </view>
      </view>
    </view>

    <view v-if="submittedKeyword" class="result-head">
      <text class="result-title">“{{ submittedKeyword }}” 的结果</text>
      <text class="panel-link" @click="submittedKeyword = ''">返回</text>
    </view>

    <view v-if="resultList.length" class="goods-list">
      <view
        v-for="item in resultList"
        :key="item.id"
        class="goods-card"
        @click="goDetail(item.id)"
      >
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

    <view v-else-if="submittedKeyword && !loading" class="empty-card">
      <text class="empty-title">没有找到相关商品</text>
      <text class="empty-desc">可以换个关键词再试试。</text>
    </view>
  </view>
</template>

<script setup lang="ts">
import { computed, ref } from 'vue'
import { clearSearchHistory, fetchSearchIndex, fetchSearchResult } from '@/entities/goods/api'
import type { GoodsListItem, SearchIndexPayload } from '@/entities/goods/api'

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

const hotKeywords = computed(() => searchIndex.value?.hotKeywordList || [])
const historyKeywords = computed(() => searchIndex.value?.historyKeywordList || [])

bootstrap()

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
  loading.value = true

  try {
    const result = await fetchSearchResult({
      keyword: finalKeyword,
      page: 1,
      limit: 20,
      categoryId: categoryId || undefined,
    })
    resultList.value = result.list || []
  } catch (error) {
    console.error(error)
    resultList.value = []
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

.search-bar {
  display: flex;
  align-items: center;
  gap: 14rpx;
  padding: 14rpx 18rpx;
  border-radius: 12rpx;
  background: #ffffff;
  box-shadow: 0 10rpx 24rpx rgba(23, 32, 51, 0.06);
}

.search-input {
  min-width: 0;
  flex: 1;
  height: 64rpx;
  font-size: 25rpx;
  line-height: 64rpx;
  color: #172033;
}

.search-placeholder {
  color: #96a2b4;
}

.search-action {
  font-size: 24rpx;
  color: #1677ff;
}

.panel {
  margin-top: 16rpx;
  padding: 20rpx;
  border-radius: 12rpx;
  background: #ffffff;
  box-shadow: 0 10rpx 24rpx rgba(23, 32, 51, 0.05);
}

.panel-head,
.result-head {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.panel-title,
.result-title {
  font-size: 25rpx;
  line-height: 1.3;
  color: #172033;
}

.panel-link {
  font-size: 22rpx;
  color: #1677ff;
}

.tag-list {
  display: flex;
  flex-wrap: wrap;
  gap: 12rpx;
  margin-top: 14rpx;
}

.tag {
  padding: 10rpx 18rpx;
  border-radius: 999px;
  background: #edf5ff;
  font-size: 22rpx;
  line-height: 1.2;
  color: #1677ff;
}

.tag--light {
  background: #f4f7fb;
  color: #556276;
}

.result-head {
  margin-top: 16rpx;
  padding: 0 4rpx;
}

.goods-list {
  display: grid;
  gap: 14rpx;
  margin-top: 14rpx;
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

.goods-price-row {
  display: flex;
  align-items: flex-end;
  gap: 10rpx;
  margin-top: 14rpx;
}

.goods-price {
  font-size: 28rpx;
  line-height: 1.2;
  font-weight: 600;
  color: #172033;
}

.goods-origin {
  font-size: 21rpx;
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
