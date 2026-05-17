<template>
  <view class="page">
    <view class="hero-card">
      <view class="hero-row">
        <view class="hero-avatar">售</view>
        <view class="hero-copy">
          <text class="eyebrow">{{ hero.eyebrow }}</text>
          <text class="title">{{ hero.title }}</text>
        </view>
      </view>
      <text class="desc">{{ hero.description }}</text>
    </view>

    <scroll-view scroll-x class="tab-scroll">
      <view class="tab-row">
        <view
          v-for="(tab, index) in AFTERSALE_TABS"
          :key="tab.label"
          class="tab-chip"
          :class="{ 'tab-chip--active': index === activeTab }"
          @click="switchTab(index)"
        >
          {{ tab.label }}
        </view>
      </view>
    </scroll-view>

    <view v-if="loading" class="record-list">
      <view v-for="index in 2" :key="index" class="record-card record-card--skeleton">
        <view class="skeleton-line skeleton-line--title"></view>
        <view class="skeleton-line skeleton-line--short"></view>
        <view class="skeleton-box"></view>
      </view>
    </view>

    <view v-else-if="records.length" class="record-list">
      <view v-for="record in records" :key="record.id" class="record-card">
        <view class="record-head">
          <view class="record-meta">
            <text class="record-sn">售后单 {{ record.aftersaleSn }}</text>
            <text class="record-order">订单号 {{ record.orderId }}</text>
          </view>
          <text class="status-tag" :class="resolveAftersaleStatusClass(record.statusText)">{{ record.statusText }}</text>
        </view>

        <view class="goods-card" @click="goAftersaleDetail(record.orderId)">
          <image class="goods-image" :src="record.picUrl" mode="aspectFill" />
          <view class="goods-body">
            <text class="goods-title">{{ record.goodsName }}</text>
            <text class="goods-spec">{{ record.specificationsText || '默认规格' }}</text>
            <view class="goods-foot">
              <text class="goods-count">x {{ record.goodsCount }}</text>
              <text class="goods-price">¥ {{ record.goodsPrice }}</text>
            </view>
          </view>
        </view>

        <view class="record-info">
          <text class="record-copy">退款原因：{{ record.reason }}</text>
          <text class="record-copy">退款金额：¥ {{ record.amount }}</text>
          <text class="record-copy">申请时间：{{ record.addTime || '暂无记录' }}</text>
          <text v-if="record.comment" class="record-copy">补充说明：{{ record.comment }}</text>
        </view>

        <view class="action-row">
          <view class="ghost-btn" @click="goAftersaleDetail(record.orderId)">查看售后</view>
          <view class="ghost-btn" @click="goOrderDetail(record.orderId)">查看订单</view>
          <view v-if="canCancelAftersale(record.status)" class="primary-inline-btn" @click="handleCancel(record.id)">撤销售后</view>
        </view>
      </view>
    </view>

    <view v-else class="empty-card">
      <text class="empty-title">当前状态下还没有售后记录</text>
      <text class="empty-desc">等你提交新的售后申请后，这里会同步出现真实记录。</text>
    </view>
  </view>
</template>

<script setup lang="ts">
import { onShow } from '@dcloudio/uni-app'
import { ref } from 'vue'
import { cancelAftersale, fetchAftersaleList, type AftersaleListCard } from '@/entities/user/api'
import { AFTERSALE_TABS, canCancelAftersale, resolveAftersaleStatusClass } from '../aftersale-utils'
import { resolveUserPageHero } from '../page-display-utils'
import { normalizeListTab } from '../user-list-utils'

type UniPageWithOptions = {
  options?: Record<string, unknown>
}

const pageOptions = (getCurrentPages()[getCurrentPages().length - 1] as UniPageWithOptions | undefined)?.options || {}
const activeTab = ref(normalizeListTab(typeof pageOptions.active === 'string' ? pageOptions.active : undefined, AFTERSALE_TABS.length))
const loading = ref(true)
const records = ref<AftersaleListCard[]>([])
const hero = resolveUserPageHero('refund')

bootstrap()
onShow(() => {
  void bootstrap()
})

async function bootstrap() {
  loading.value = true

  try {
    const result = await fetchAftersaleList({
      status: AFTERSALE_TABS[activeTab.value]?.status || 0,
      page: 1,
      limit: 10,
    })
    records.value = result.list || []
  } catch (error) {
    console.error(error)
  } finally {
    loading.value = false
  }
}

async function switchTab(index: number) {
  if (index === activeTab.value) {
    return
  }

  activeTab.value = index
  await bootstrap()
}

function goOrderDetail(orderId: string) {
  uni.navigateTo({
    url: `/pages/order/detail/index?orderId=${orderId}`,
  })
}

function goAftersaleDetail(orderId: string) {
  uni.navigateTo({
    url: `/pages/user/refund-detail/index?orderId=${orderId}`,
  })
}

async function handleCancel(id: string) {
  try {
    await cancelAftersale(id)
    uni.showToast({
      title: '已撤销售后申请',
      icon: 'none',
    })
    await bootstrap()
  } catch (error) {
    console.error(error)
    uni.showToast({
      title: '撤销售后失败',
      icon: 'none',
    })
  }
}
</script>

<style scoped lang="scss">
.page {
  min-height: 100vh;
  padding: 14rpx 14rpx 32rpx;
  background: linear-gradient(180deg, rgb(var(--sf-color-brand-soft)) 0%, rgb(var(--sf-color-page)) 26%, #ffffff 100%);
}

.hero-card,
.record-card,
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
.record-head,
.goods-foot,
.action-row {
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
.record-copy {
  display: block;
  margin-top: 8rpx;
  font-size: 18rpx;
  line-height: 1.42;
}

.desc {
  color: rgba(255, 255, 255, 0.88);
}

.tab-scroll {
  margin-top: 10rpx;
  white-space: nowrap;
}

.tab-row {
  display: inline-flex;
  gap: 6rpx;
}

.tab-chip {
  padding: 8rpx 14rpx;
  border-radius: 999px;
  background: rgba(255, 255, 255, 0.94);
  border: 1px solid rgb(var(--sf-color-line));
  font-size: 18rpx;
  color: rgb(var(--sf-color-text-secondary));
  box-shadow: var(--sf-shadow-card);
}

.tab-chip--active {
  background: rgb(var(--sf-color-brand));
  border-color: rgb(var(--sf-color-brand));
  color: #ffffff;
}

.record-list {
  display: grid;
  gap: 8rpx;
  margin-top: 10rpx;
}

.record-card,
.empty-card {
  padding: 12rpx 14rpx;
}

.record-head {
  justify-content: space-between;
  gap: 10rpx;
}

.record-meta {
  min-width: 0;
  flex: 1;
}

.record-sn,
.goods-title,
.empty-title {
  display: block;
  font-size: 20rpx;
  line-height: 1.32;
  color: rgb(var(--sf-color-ink));
}

.record-sn,
.goods-title {
  font-weight: 600;
}

.record-order,
.goods-spec,
.goods-count,
.empty-desc,
.record-copy {
  display: block;
  margin-top: 6rpx;
  font-size: 17rpx;
  line-height: 1.4;
  color: rgb(var(--sf-color-text-secondary));
}

.status-tag {
  flex-shrink: 0;
  padding: 4rpx 10rpx;
  border-radius: 999px;
  font-size: 16rpx;
  line-height: 1.2;
}

.status-tag--brand {
  background: rgb(var(--sf-color-brand-soft));
  color: rgb(var(--sf-color-brand));
}

.status-tag--success {
  background: rgba(var(--sf-color-success), 0.12);
  color: rgb(var(--sf-color-success));
}

.status-tag--muted {
  background: rgb(var(--sf-color-divider));
  color: rgba(var(--sf-color-ink), 0.55);
}

.status-tag--plain {
  background: rgb(var(--sf-color-page));
  color: rgba(var(--sf-color-ink), 0.72);
}

.goods-card {
  display: flex;
  gap: 10rpx;
  margin-top: 10rpx;
  padding: 10rpx;
  border-radius: var(--sf-radius-card);
  background: rgb(var(--sf-color-page));
}

.goods-image {
  width: 104rpx;
  height: 104rpx;
  flex-shrink: 0;
  border-radius: var(--sf-radius-card);
  background: #ffffff;
}

.goods-body {
  min-width: 0;
  flex: 1;
}

.goods-foot {
  justify-content: space-between;
  gap: 8rpx;
  margin-top: 8rpx;
}

.goods-price {
  font-size: 22rpx;
  color: rgb(var(--sf-color-ink));
}

.record-info {
  margin-top: 8rpx;
}

.record-copy {
  margin-top: 4rpx;
}

.action-row {
  justify-content: flex-end;
  gap: 8rpx;
  margin-top: 10rpx;
}

.ghost-btn,
.primary-inline-btn {
  padding: 8rpx 14rpx;
  border-radius: var(--sf-radius-card);
  font-size: 18rpx;
  line-height: 1.2;
}

.ghost-btn {
  background: #ffffff;
  border: 1px solid rgb(var(--sf-color-line));
  color: rgb(var(--sf-color-text-secondary));
}

.primary-inline-btn {
  background: rgb(var(--sf-color-brand));
  color: #ffffff;
}

.record-card--skeleton {
  padding: 12rpx 14rpx;
}

.skeleton-line {
  height: 16rpx;
  margin-top: 10rpx;
  border-radius: 999px;
  background: rgb(var(--sf-color-divider));
}

.skeleton-line--title {
  width: 180rpx;
  margin-top: 0;
}

.skeleton-line--short {
  width: 240rpx;
}

.skeleton-box {
  height: 110rpx;
  margin-top: 10rpx;
  border-radius: var(--sf-radius-card);
  background: rgb(var(--sf-color-divider));
}
</style>
