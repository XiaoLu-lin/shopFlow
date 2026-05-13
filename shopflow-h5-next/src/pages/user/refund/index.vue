<template>
  <view class="page">
    <view class="hero-card">
      <text class="title">退款 / 售后</text>
      <text class="desc">已接回真实售后列表接口，当前支持按状态查看与撤销申请中的售后单。</text>
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
      <view v-for="index in 2" :key="index" class="record-card">
        <view class="skeleton-row">
          <view class="skeleton-line skeleton-line--title"></view>
          <view class="skeleton-line skeleton-line--tag"></view>
        </view>
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
          <text class="record-status" :class="resolveAftersaleStatusClass(record.statusText)">{{ record.statusText }}</text>
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

        <view class="info-grid">
          <text>退款原因：{{ record.reason }}</text>
          <text class="text-right">退款金额：¥ {{ record.amount }}</text>
          <text class="info-wide">申请时间：{{ record.addTime || '暂无记录' }}</text>
          <text v-if="record.comment" class="info-wide">补充说明：{{ record.comment }}</text>
        </view>

        <view class="action-row">
          <view class="ghost-btn" @click="goAftersaleDetail(record.orderId)">查看售后</view>
          <view class="ghost-btn" @click="goOrderDetail(record.orderId)">查看订单</view>
          <view v-if="canCancelAftersale(record.status)" class="dark-btn" @click="handleCancel(record.id)">撤销售后</view>
        </view>
      </view>
    </view>

    <view v-else class="empty-card">
      <text class="empty-title">当前状态下还没有售后记录</text>
      <text class="empty-desc">等你提交退款或售后申请后，这里会出现真实记录。</text>
    </view>
  </view>
</template>

<script setup lang="ts">
import { onShow } from '@dcloudio/uni-app'
import { ref } from 'vue'
import { cancelAftersale, fetchAftersaleList, type AftersaleListCard } from '@/entities/user/api'
import { AFTERSALE_TABS, canCancelAftersale } from '../aftersale-utils'
import { resolveAftersaleStatusClass } from '../aftersale-utils'
import { normalizeListTab } from '../user-list-utils'

type UniPageWithOptions = {
  options?: Record<string, unknown>
}

const pageOptions = (getCurrentPages()[getCurrentPages().length - 1] as UniPageWithOptions | undefined)?.options || {}
const activeTab = ref(normalizeListTab(typeof pageOptions.active === 'string' ? pageOptions.active : undefined, AFTERSALE_TABS.length))
const loading = ref(true)
const records = ref<AftersaleListCard[]>([])

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
  padding: 20rpx 20rpx 40rpx;
  background: linear-gradient(180deg, #ffffff 0%, #f6f8fb 100%);
}

.hero-card,
.record-card,
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

.tab-scroll {
  margin-top: 16rpx;
  white-space: nowrap;
}

.tab-row {
  display: inline-flex;
  gap: 12rpx;
}

.tab-chip {
  padding: 12rpx 20rpx;
  border-radius: 999px;
  background: #ffffff;
  font-size: 22rpx;
  color: #5f6b7c;
  box-shadow: 0 10rpx 24rpx rgba(23, 32, 51, 0.06);
}

.tab-chip--active {
  background: #1677ff;
  color: #ffffff;
}

.record-list {
  display: grid;
  gap: 14rpx;
  margin-top: 16rpx;
}

.record-card {
  padding: 20rpx;
}

.record-head,
.goods-foot,
.action-row,
.skeleton-row {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16rpx;
}

.record-meta {
  min-width: 0;
  flex: 1;
}

.record-sn,
.record-order {
  display: block;
  font-size: 22rpx;
  line-height: 1.4;
  color: #748194;
}

.record-order {
  margin-top: 6rpx;
  font-size: 20rpx;
  color: #a0aaba;
}

.record-status {
  font-size: 22rpx;
}

.goods-card {
  display: flex;
  gap: 14rpx;
  margin-top: 16rpx;
  border-radius: 10rpx;
  background: #f6f8fb;
  padding: 16rpx;
}

.goods-image {
  width: 112rpx;
  height: 112rpx;
  flex-shrink: 0;
  border-radius: 10rpx;
  background: #ffffff;
}

.goods-body {
  min-width: 0;
  flex: 1;
}

.goods-title {
  display: block;
  font-size: 24rpx;
  line-height: 1.4;
  color: #172033;
}

.goods-spec,
.goods-count {
  display: block;
  margin-top: 8rpx;
  font-size: 22rpx;
  line-height: 1.4;
  color: #748194;
}

.goods-price {
  font-size: 26rpx;
  color: #172033;
}

.info-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 10rpx 14rpx;
  margin-top: 14rpx;
  font-size: 20rpx;
  line-height: 1.45;
  color: #5f6b7c;
}

.text-right {
  text-align: right;
}

.info-wide {
  grid-column: span 2;
}

.action-row {
  justify-content: flex-end;
  margin-top: 16rpx;
}

.ghost-btn,
.dark-btn {
  padding: 12rpx 16rpx;
  border-radius: 10rpx;
  font-size: 22rpx;
}

.ghost-btn {
  background: #ffffff;
  color: #5f6b7c;
  box-shadow: inset 0 0 0 1px #dbe3ef;
}

.dark-btn {
  background: #253044;
  color: #ffffff;
}

.skeleton-line {
  height: 20rpx;
  border-radius: 999px;
  background: #edf1f6;
}

.skeleton-line--title {
  width: 220rpx;
}

.skeleton-line--tag {
  width: 96rpx;
}

.skeleton-box {
  height: 132rpx;
  margin-top: 16rpx;
  border-radius: 10rpx;
  background: #edf1f6;
}

.empty-card {
  margin-top: 16rpx;
  text-align: center;
}
</style>
