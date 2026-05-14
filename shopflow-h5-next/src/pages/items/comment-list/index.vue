<template>
  <view class="page" role="main">
    <view class="hero-card">
      <text class="title" role="heading" aria-level="1">商品评价</text>
      <text class="desc">查看真实买家反馈，帮助你更快判断商品是否适合自己。</text>
    </view>

    <view class="tab-row">
      <view
        class="tab-chip"
        :class="{ 'tab-chip--active': activeTab === 0 }"
        role="button"
        @click="switchTab(0)"
      >
        全部({{ allCount }})
      </view>
      <view
        class="tab-chip"
        :class="{ 'tab-chip--active': activeTab === 1 }"
        role="button"
        @click="switchTab(1)"
      >
        有图({{ hasPicCount }})
      </view>
    </view>

    <view v-if="loading && !comments.length" class="section-list">
      <view v-for="index in 2" :key="index" class="section-card section-card--skeleton">
        <view class="skeleton-line skeleton-line--title"></view>
        <view class="skeleton-line"></view>
      </view>
    </view>

    <view v-else-if="comments.length" class="section-list">
      <view v-for="comment in comments" :key="comment.id" class="section-card">
        <view class="comment-head">
          <image class="avatar" :src="comment.avatarUrl || defaultAvatar" mode="aspectFill" />
          <view class="comment-main">
            <text class="nickname">{{ comment.nickName || '匿名用户' }}</text>
            <text class="time-copy">{{ comment.addTime || '--' }}</text>
          </view>
          <text class="star-copy">{{ renderStars(comment.star || 0) }}</text>
        </view>

        <text class="content-copy">{{ comment.content || '用户未填写评价内容' }}</text>

        <view v-if="comment.picUrls?.length" class="picture-grid">
          <image
            v-for="picture in comment.picUrls"
            :key="picture"
            class="picture-item"
            :src="picture"
            mode="aspectFill"
            @click="previewImages(comment.picUrls || [], picture)"
          />
        </view>

        <view v-if="comment.adminContent" class="reply-card">
          <text class="reply-label">商家回复：</text>
          <text class="reply-copy">{{ comment.adminContent }}</text>
        </view>
      </view>
    </view>

    <view v-else class="empty-card">
      <text class="empty-title">{{ activeTab === 1 ? '当前没有带图评价' : '当前还没有商品评价' }}</text>
      <text class="empty-desc">等更多用户下单使用后，这里会陆续出现真实反馈。</text>
    </view>
  </view>
</template>

<script setup lang="ts">
import { onReachBottom, onShow } from '@dcloudio/uni-app'
import { ref } from 'vue'
import {
  fetchGoodsCommentCount,
  fetchGoodsCommentList,
  type GoodsCommentItem,
} from '@/entities/goods/api'
import { createGoodsCommentQuery, normalizeCommentGoodsId, shouldFinishCommentPaging } from '../comment-utils'

type UniPageWithOptions = {
  options?: Record<string, unknown>
}

const DEFAULT_LIMIT = 20
const pageOptions = (getCurrentPages()[getCurrentPages().length - 1] as UniPageWithOptions | undefined)?.options || {}
const goodsId = normalizeCommentGoodsId(typeof pageOptions.goodsId === 'string' ? pageOptions.goodsId : undefined)
const activeTab = ref(0)
const loading = ref(false)
const comments = ref<GoodsCommentItem[]>([])
const allCount = ref(0)
const hasPicCount = ref(0)
const page = ref(1)
const pages = ref(1)
const finished = ref(false)
const defaultAvatar = ref('avatar.png')

bootstrap()
onShow(() => {
  void bootstrap()
})
onReachBottom(() => {
  void loadMore()
})

async function bootstrap() {
  if (!goodsId) {
    return
  }

  await Promise.all([fetchCount(), resetAndLoad()])
}

async function fetchCount() {
  try {
    const result = await fetchGoodsCommentCount(goodsId)
    allCount.value = result.allCount || 0
    hasPicCount.value = result.hasPicCount || 0
  } catch (error) {
    console.error(error)
  }
}

async function switchTab(nextTab: number) {
  if (nextTab === activeTab.value) {
    return
  }

  activeTab.value = nextTab
  await resetAndLoad()
}

async function resetAndLoad() {
  comments.value = []
  page.value = 1
  pages.value = 1
  finished.value = false
  await loadMore()
}

async function loadMore() {
  if (loading.value || finished.value || !goodsId) {
    return
  }

  loading.value = true

  try {
    const result = await fetchGoodsCommentList(
      createGoodsCommentQuery({
        goodsId,
        page: page.value,
        limit: DEFAULT_LIMIT,
        hasPicture: activeTab.value === 1,
      }),
    )
    const list = result.list || []

    comments.value = [...comments.value, ...list]
    pages.value = result.pages || 1
    finished.value = shouldFinishCommentPaging(list, result.page || page.value, result.pages || 1)
    page.value = (result.page || page.value) + 1
  } catch (error) {
    console.error(error)
    finished.value = true
  } finally {
    loading.value = false
  }
}

function previewImages(urls: string[], current: string) {
  if (!urls.length) {
    return
  }

  uni.previewImage({
    current,
    urls,
  })
}

function renderStars(star: number) {
  const normalized = Math.max(0, Math.min(5, Math.round(star)))
  return `${'★'.repeat(normalized)}${'☆'.repeat(5 - normalized)}`
}
</script>

<style scoped lang="scss">
.page {
  min-height: 100vh;
  padding: 20rpx 20rpx 40rpx;
  background: linear-gradient(180deg, #ffffff 0%, #f6f8fb 100%);
}

.hero-card,
.section-card,
.empty-card {
  border-radius: 12rpx;
  background: #ffffff;
  box-shadow: 0 10rpx 24rpx rgba(23, 32, 51, 0.06);
}

.hero-card,
.section-card,
.empty-card {
  padding: 22rpx;
}

.title,
.nickname,
.empty-title {
  display: block;
  font-size: 28rpx;
  line-height: 1.3;
  color: #172033;
}

.desc,
.time-copy,
.content-copy,
.empty-desc,
.reply-copy,
.reply-label {
  display: block;
  margin-top: 8rpx;
  font-size: 22rpx;
  line-height: 1.45;
  color: #748194;
}

.tab-row {
  display: flex;
  gap: 12rpx;
  margin-top: 16rpx;
}

.tab-chip {
  flex: 1;
  padding: 18rpx 20rpx;
  border-radius: 999px;
  background: #ffffff;
  font-size: 22rpx;
  text-align: center;
  color: #5f6b7c;
  box-shadow: 0 10rpx 24rpx rgba(23, 32, 51, 0.06);
}

.tab-chip--active {
  background: #1677ff;
  color: #ffffff;
}

.section-list {
  display: grid;
  gap: 16rpx;
  margin-top: 16rpx;
}

.section-card--skeleton {
  min-height: 180rpx;
}

.skeleton-line {
  height: 22rpx;
  margin-top: 14rpx;
  border-radius: 999px;
  background: linear-gradient(90deg, #edf2f8 0%, #f6f8fb 100%);
}

.skeleton-line--title {
  width: 40%;
  height: 28rpx;
  margin-top: 0;
}

.comment-head {
  display: flex;
  align-items: center;
  gap: 16rpx;
}

.avatar {
  width: 76rpx;
  height: 76rpx;
  border-radius: 999px;
  background: #edf2f8;
}

.comment-main {
  min-width: 0;
  flex: 1;
}

.star-copy {
  flex-shrink: 0;
  font-size: 24rpx;
  line-height: 1.2;
  color: #faad14;
}

.content-copy {
  color: #172033;
}

.picture-grid {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 12rpx;
  margin-top: 16rpx;
}

.picture-item {
  width: 100%;
  height: 180rpx;
  border-radius: 12rpx;
  background: #eef3f9;
}

.reply-card {
  margin-top: 16rpx;
  padding: 16rpx;
  border-radius: 12rpx;
  background: #f7faff;
}

.reply-label,
.reply-copy {
  display: inline;
}

.empty-card {
  margin-top: 16rpx;
}
</style>
