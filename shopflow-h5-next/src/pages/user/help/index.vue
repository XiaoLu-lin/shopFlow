<template>
  <view class="page">
    <view class="hero-card">
      <text class="title">帮助中心</text>
      <text class="desc">常见问题直接复用旧站 `/issue/list`，先提供一版简洁 FAQ 列表。</text>
    </view>

    <view v-if="loading" class="faq-list">
      <view v-for="index in 4" :key="index" class="faq-card faq-card--skeleton">
        <view class="skeleton-line skeleton-line--title"></view>
        <view class="skeleton-line"></view>
      </view>
    </view>

    <view v-else-if="issues.length" class="faq-list">
      <view
        v-for="item in issues"
        :key="item.id"
        class="faq-card"
        @click="toggle(item.id)"
      >
        <view class="faq-head">
          <text class="question">{{ item.question }}</text>
          <text class="arrow">{{ expandedId === item.id ? '−' : '+' }}</text>
        </view>
        <text v-if="expandedId === item.id" class="answer">{{ item.answer }}</text>
      </view>
    </view>

    <view v-else class="empty-card">
      <text class="empty-title">暂无帮助内容</text>
      <text class="empty-desc">后端返回为空时，这里会保持轻量空态。</text>
    </view>
  </view>
</template>

<script setup lang="ts">
import { onShow } from '@dcloudio/uni-app'
import { ref } from 'vue'
import { fetchIssueList, type IssueItem } from '@/entities/user/api'

const loading = ref(true)
const issues = ref<IssueItem[]>([])
const expandedId = ref<number | null>(null)

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
  padding: 20rpx 20rpx 40rpx;
  background: linear-gradient(180deg, #ffffff 0%, #f6f8fb 100%);
}

.hero-card,
.faq-card,
.empty-card {
  border-radius: 12rpx;
  background: #ffffff;
  box-shadow: 0 10rpx 24rpx rgba(23, 32, 51, 0.06);
}

.hero-card,
.empty-card {
  padding: 22rpx;
}

.title,
.empty-title {
  display: block;
  font-size: 28rpx;
  line-height: 1.3;
  color: #172033;
}

.desc,
.empty-desc {
  display: block;
  margin-top: 8rpx;
  font-size: 22rpx;
  line-height: 1.4;
  color: #748194;
}

.faq-list {
  display: grid;
  gap: 14rpx;
  margin-top: 16rpx;
}

.faq-card {
  padding: 20rpx;
}

.faq-head {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 16rpx;
}

.question {
  flex: 1;
  font-size: 24rpx;
  line-height: 1.45;
  color: #172033;
}

.arrow {
  font-size: 28rpx;
  line-height: 1.1;
  color: #1677ff;
}

.answer {
  display: block;
  margin-top: 14rpx;
  font-size: 22rpx;
  line-height: 1.6;
  color: #5f6b7c;
}

.faq-card--skeleton {
  min-height: 92rpx;
}

.skeleton-line {
  height: 20rpx;
  margin-top: 12rpx;
  border-radius: 999px;
  background: #edf1f6;
}

.skeleton-line--title {
  width: 280rpx;
  margin-top: 0;
}

.empty-card {
  margin-top: 16rpx;
  text-align: center;
}
</style>
