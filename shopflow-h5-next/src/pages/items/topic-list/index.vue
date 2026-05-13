<template>
  <view class="page">
    <view class="hero-card">
      <text class="hero-title">专题内容</text>
      <text class="hero-desc">先把专题列表迁到新工程，支持继续进入专题详情。</text>
    </view>

    <view class="topic-list">
      <view v-for="item in topicList" :key="item.id" class="topic-card" @click="goTopic(item.id)">
        <image v-if="item.picUrl" class="topic-image" :src="item.picUrl" mode="aspectFill" />
        <view v-else class="topic-image topic-image--empty" />
        <text class="topic-title">{{ item.title }}</text>
        <text class="topic-desc">{{ item.subtitle || '专题内容整理中。' }}</text>
      </view>
    </view>
  </view>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { fetchTopicList, type TopicInfo } from '@/entities/goods/api'

const topicList = ref<TopicInfo[]>([])

bootstrap()

async function bootstrap() {
  try {
    const result = await fetchTopicList({ page: 1, limit: 20 })
    topicList.value = result.list || []
  } catch (error) {
    console.error(error)
  }
}

function goTopic(id: number) {
  uni.navigateTo({
    url: `/pages/items/topic/index?topicId=${id}`,
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

.topic-list {
  display: grid;
  gap: 14rpx;
  margin-top: 16rpx;
}

.topic-card {
  overflow: hidden;
  border-radius: 12rpx;
  background: #ffffff;
  box-shadow: 0 10rpx 24rpx rgba(23, 32, 51, 0.06);
}

.topic-image {
  width: 100%;
  height: 260rpx;
  background: #f3f6fb;
}

.topic-image--empty {
  background: #edf4ff;
}

.topic-title {
  display: block;
  padding: 18rpx 18rpx 0;
  font-size: 26rpx;
  line-height: 1.3;
  color: #172033;
}

.topic-desc {
  display: block;
  padding: 8rpx 18rpx 18rpx;
  font-size: 22rpx;
  line-height: 1.4;
  color: #748194;
}
</style>
