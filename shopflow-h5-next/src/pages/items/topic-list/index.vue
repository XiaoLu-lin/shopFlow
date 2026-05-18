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

      <view v-if="topicList.length" class="browse-topic-list">
        <view
          v-for="item in topicList"
          :key="item.id"
          class="browse-topic-card"
          @click="goTopic(item.id)"
        >
          <image v-if="item.picUrl" class="browse-topic-image" :src="item.picUrl" mode="aspectFill" />
          <view v-else class="browse-topic-image" />
          <view class="browse-topic-body">
            <text class="browse-topic-title">{{ item.title }}</text>
            <text class="browse-topic-desc">
              {{ resolveGoodsBrief(item.subtitle, '专题内容会以更轻的阅读节奏收在这里。') }}
            </text>
          </view>
        </view>
      </view>

      <view v-else-if="!loading" class="browse-empty">
        <text class="browse-empty-title">{{ emptyState.title }}</text>
        <text class="browse-empty-desc">{{ emptyState.description }}</text>
      </view>

      <view v-if="topicList.length" class="browse-load-state">
        {{ loadingMore ? '正在继续加载...' : hasMore ? '上拉继续浏览更多专题' : '已经到底了' }}
      </view>
    </view>
  </view>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { onReachBottom } from '@dcloudio/uni-app'
import { fetchTopicList, type TopicInfo } from '@/entities/goods/api'
import { resolveBrowsePageState } from '@/features/goods/browse-pagination'
import {
  resolveGoodsBrief,
  resolveGoodsBrowseEmptyState,
  resolveGoodsBrowseHero,
  shouldRenderBrowseHeroDescription,
} from '@/features/goods/browse-display-utils'

const loading = ref(false)
const loadingMore = ref(false)
const topicList = ref<TopicInfo[]>([])
const page = ref(1)
const hasMore = ref(false)

const hero = resolveGoodsBrowseHero('topic-list')
const emptyState = resolveGoodsBrowseEmptyState('topic-list')
const showHeroDescription = shouldRenderBrowseHeroDescription('topic-list')

bootstrap()
onReachBottom(() => {
  void loadMore()
})

async function bootstrap() {
  loading.value = true
  page.value = 1
  hasMore.value = false
  try {
    const result = await fetchTopicList({ page: page.value, limit: 20 })
    const nextState = resolveBrowsePageState([], result)
    topicList.value = nextState.list
    page.value = nextState.nextPage
    hasMore.value = nextState.hasMore
  } catch (error) {
    console.error(error)
    topicList.value = []
    hasMore.value = false
  } finally {
    loading.value = false
  }
}

function goTopic(id: number) {
  uni.navigateTo({
    url: `/pages/items/topic/index?topicId=${id}`,
  })
}

async function loadMore() {
  if (!hasMore.value || loading.value || loadingMore.value) {
    return
  }

  loadingMore.value = true
  try {
    const result = await fetchTopicList({ page: page.value, limit: 20 })
    const nextState = resolveBrowsePageState(topicList.value, result)
    topicList.value = nextState.list
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
