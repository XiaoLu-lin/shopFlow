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

      <view class="browse-rich-card">
        <text class="browse-card-title">专题正文</text>
        <rich-text class="browse-rich-copy browse-rich-content" :nodes="topicContent" />
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
              {{ resolveGoodsBrief(item.brief, '专题相关商品已经整理在下方，方便你顺着继续浏览。') }}
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
import { fetchTopicDetail, type GoodsListItem, type TopicInfo } from '@/entities/goods/api'
import {
  resolveGoodsBrief,
  resolveGoodsBrowseEmptyState,
  resolveGoodsBrowseHero,
  resolveTopicContent,
  shouldRenderBrowseHeroDescription,
} from '@/features/goods/browse-display-utils'

type UniPageWithOptions = {
  options?: Record<string, unknown>
}

const pageOptions = (getCurrentPages()[getCurrentPages().length - 1] as UniPageWithOptions | undefined)?.options || {}
const topicId = typeof pageOptions.topicId === 'string' ? pageOptions.topicId : ''

const loading = ref(false)
const topic = ref<TopicInfo | null>(null)
const goodsList = ref<GoodsListItem[]>([])

const hero = computed(() =>
  resolveGoodsBrowseHero('topic', {
    title: topic.value?.title || '专题详情',
    description: topic.value?.subtitle || '先看专题内容，再顺着下面的商品继续往下浏览。',
  }),
)
const emptyState = computed(() => resolveGoodsBrowseEmptyState('topic'))
const topicContent = computed(() => resolveTopicContent(topic.value?.content))
const showHeroDescription = shouldRenderBrowseHeroDescription('topic')

bootstrap()

async function bootstrap() {
  if (!topicId) {
    return
  }

  loading.value = true
  try {
    const result = await fetchTopicDetail(topicId)
    topic.value = result.topic
    goodsList.value = result.goods || []
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
