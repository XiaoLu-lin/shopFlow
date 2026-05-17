<template>
  <view class="page">
    <view class="hero-card">
      <view class="hero-row">
        <view class="hero-avatar">帮</view>
        <view class="hero-copy">
          <text class="eyebrow">{{ hero.eyebrow }}</text>
          <text class="title">{{ hero.title }}</text>
        </view>
      </view>
      <text class="desc">{{ hero.description }}</text>
    </view>

    <view v-if="loading" class="faq-list">
      <view v-for="index in 4" :key="index" class="faq-card faq-card--skeleton">
        <view class="skeleton-line skeleton-line--title"></view>
        <view class="skeleton-line"></view>
      </view>
    </view>

    <view v-else-if="issues.length" class="faq-list">
      <view v-for="item in issues" :key="item.id" class="faq-card" @click="toggle(item.id)">
        <view class="faq-head">
          <text class="question">{{ item.question }}</text>
          <text class="faq-tag">{{ expandedId === item.id ? '收起' : '展开' }}</text>
        </view>
        <text v-if="expandedId === item.id" class="answer">{{ item.answer }}</text>
      </view>
    </view>

    <view v-else class="empty-card">
      <text class="empty-title">暂无帮助内容</text>
      <text class="empty-desc">常见问题更新后，会继续收在这里方便你查看。</text>
    </view>
  </view>
</template>

<script setup lang="ts">
import { onShow } from '@dcloudio/uni-app'
import { ref } from 'vue'
import { fetchIssueList, type IssueItem } from '@/entities/user/api'
import { resolveUserPageHero } from '../page-display-utils'

const loading = ref(true)
const issues = ref<IssueItem[]>([])
const expandedId = ref<number | null>(null)
const hero = resolveUserPageHero('help')

bootstrap()
onShow(() => {
  void bootstrap()
})

async function bootstrap() {
  loading.value = true

  try {
    issues.value = await fetchIssueList()
    if (!issues.value.find((item) => item.id === expandedId.value)) {
      expandedId.value = issues.value[0]?.id ?? null
    }
  } catch (error) {
    console.error(error)
  } finally {
    loading.value = false
  }
}

function toggle(id: number) {
  expandedId.value = expandedId.value === id ? null : id
}
</script>

<style scoped lang="scss">
.page {
  min-height: 100vh;
  padding: 14rpx 14rpx 32rpx;
  background: linear-gradient(180deg, rgb(var(--sf-color-brand-soft)) 0%, rgb(var(--sf-color-page)) 26%, #ffffff 100%);
}

.hero-card,
.faq-card,
.empty-card {
  border-radius: var(--sf-radius-card);
  background: #ffffff;
  border: 1px solid rgb(var(--sf-color-line));
  box-shadow: var(--sf-shadow-soft);
}

.hero-card {
  padding: 14rpx 16rpx 16rpx;
  color: #ffffff;
  background: linear-gradient(145deg, rgb(var(--sf-color-brand)) 0%, rgb(var(--sf-color-brand-light)) 100%);
  box-shadow: var(--sf-shadow-brand);
}

.hero-row,
.faq-head {
  display: flex;
  align-items: center;
}

.hero-row {
  gap: 10rpx;
}

.hero-avatar {
  display: flex;
  width: 56rpx;
  height: 56rpx;
  flex-shrink: 0;
  align-items: center;
  justify-content: center;
  border-radius: var(--sf-radius-card);
  background: linear-gradient(145deg, rgba(255, 255, 255, 0.96), rgba(229, 237, 246, 0.92));
  font-size: 20rpx;
  font-weight: 700;
  color: rgb(var(--sf-color-brand));
}

.hero-copy {
  min-width: 0;
  flex: 1;
}

.eyebrow {
  display: block;
  font-size: 16rpx;
  letter-spacing: 0.12em;
  text-transform: uppercase;
  color: rgba(255, 255, 255, 0.78);
}

.title,
.empty-title {
  display: block;
  margin-top: 4rpx;
  font-size: 22rpx;
  line-height: 1.28;
  color: inherit;
}

.desc,
.empty-desc,
.answer {
  display: block;
  margin-top: 8rpx;
  font-size: 18rpx;
  line-height: 1.45;
}

.desc {
  color: rgba(255, 255, 255, 0.88);
}

.faq-list {
  display: grid;
  gap: 8rpx;
  margin-top: 10rpx;
}

.faq-card,
.empty-card {
  padding: 12rpx 14rpx;
}

.faq-head {
  justify-content: space-between;
  gap: 10rpx;
}

.question,
.empty-title {
  flex: 1;
  font-size: 20rpx;
  line-height: 1.38;
  color: rgb(var(--sf-color-ink));
}

.faq-tag {
  flex-shrink: 0;
  padding: 4rpx 10rpx;
  border-radius: var(--sf-radius-card);
  background: rgb(var(--sf-color-brand-soft));
  font-size: 16rpx;
  line-height: 1.2;
  color: rgb(var(--sf-color-brand));
}

.answer,
.empty-desc {
  color: rgb(var(--sf-color-text-secondary));
}

.faq-card--skeleton {
  padding: 12rpx 14rpx;
}

.skeleton-line {
  height: 16rpx;
  margin-top: 10rpx;
  border-radius: 999px;
  background: rgb(var(--sf-color-divider));
}

.skeleton-line--title {
  width: 240rpx;
  margin-top: 0;
}
</style>
