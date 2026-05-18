<template>
  <view class="page">
    <view class="hero-card">
      <view class="hero-row">
        <view class="hero-avatar">详</view>
        <view class="hero-copy">
          <text class="eyebrow">{{ hero.eyebrow }}</text>
          <text class="title">{{ hero.title }}</text>
        </view>
      </view>
      <text class="desc">{{ hero.description }}</text>
    </view>

    <view v-if="loading" class="section-list">
      <view v-for="index in 3" :key="index" class="section-card section-card--skeleton">
        <view class="skeleton-line skeleton-line--title"></view>
        <view class="skeleton-line"></view>
        <view class="skeleton-line skeleton-line--short"></view>
      </view>
    </view>

    <template v-else-if="detail">
      <view class="section-list">
        <view class="section-card">
          <view class="summary-head">
            <view class="summary-copy">
              <text class="summary-title">售后单 {{ detail.aftersale.aftersaleSn }}</text>
              <text class="summary-meta">订单号 {{ detail.order.orderSn }}</text>
            </view>
            <text class="status-tag" :class="resolveAftersaleStatusClass(statusText)">
              {{ statusText }}
            </text>
          </view>
        </view>

        <view class="section-card">
          <view class="goods-row">
            <image class="goods-image" :src="detail.orderGoods.picUrl" mode="aspectFill" />
            <view class="goods-body">
              <text class="goods-title">{{ detail.orderGoods.goodsName }}</text>
              <text class="goods-spec">{{ detail.orderGoods.specifications?.join(' / ') || '默认规格' }}</text>
              <view class="goods-foot">
                <text class="goods-count">x {{ detail.orderGoods.number }}</text>
                <text class="goods-price">¥ {{ detail.orderGoods.price }}</text>
              </view>
            </view>
          </view>
        </view>

        <view class="section-card">
          <text class="section-title">售后信息</text>
          <view class="info-list">
            <view class="info-row"><text class="info-label">退款原因</text><text class="info-value">{{ detail.aftersale.reason }}</text></view>
            <view class="info-row"><text class="info-label">退款金额</text><text class="info-value">¥ {{ detail.aftersale.amount }}</text></view>
            <view class="info-row"><text class="info-label">申请时间</text><text class="info-value">{{ detail.aftersale.addTime || '--' }}</text></view>
            <view class="info-row">
              <text class="info-label">售后类型</text>
              <text class="info-value">{{ detail.aftersale.type === 0 ? '未收货退款' : detail.aftersale.type === 1 ? '已收货退款' : '退货退款' }}</text>
            </view>
          </view>
          <text v-if="detail.aftersale.comment" class="comment">{{ detail.aftersale.comment }}</text>
        </view>

        <view v-if="detail.aftersale.pictures?.length" class="section-card">
          <text class="section-title">凭证图片</text>
          <view class="proof-grid">
            <view v-for="picture in detail.aftersale.pictures" :key="picture" class="proof-card" @click="previewImage(picture)">
              <image class="proof-image" :src="picture" mode="aspectFill" />
            </view>
          </view>
        </view>

        <view class="action-row">
          <view class="ghost-btn" @click="goOrderDetail">查看订单</view>
          <view v-if="canCancelAftersale(detail.aftersale.status)" class="primary-inline-btn" @click="handleCancel">撤销售后</view>
        </view>
      </view>
    </template>

    <view v-else class="empty-card">
      <text class="empty-title">未找到售后详情</text>
      <text class="empty-desc">请返回售后列表重新查看。</text>
    </view>
  </view>
</template>

<script setup lang="ts">
import { onShow } from '@dcloudio/uni-app'
import { computed, ref } from 'vue'
import { cancelAftersale, fetchAftersaleDetail, type AftersaleDetailPayload } from '@/entities/user/api'
import { canCancelAftersale, resolveAftersaleStatusClass, resolveAftersaleStatusText } from '../aftersale-utils'
import { resolveUserPageHero } from '../page-display-utils'

type UniPageWithOptions = {
  options?: Record<string, unknown>
}

const pageOptions = (getCurrentPages()[getCurrentPages().length - 1] as UniPageWithOptions | undefined)?.options || {}
const orderId = typeof pageOptions.orderId === 'string' ? pageOptions.orderId : ''
const loading = ref(true)
const detail = ref<AftersaleDetailPayload | null>(null)
const hero = resolveUserPageHero('refund-detail')
const statusText = computed(() => {
  const aftersale = detail.value?.aftersale
  if (!aftersale) {
    return '处理中'
  }

  return aftersale.statusText || resolveAftersaleStatusText(aftersale.status)
})

bootstrap()
onShow(() => {
  if (orderId) {
    void bootstrap()
  }
})

async function bootstrap() {
  if (!orderId) {
    loading.value = false
    return
  }

  loading.value = true

  try {
    detail.value = await fetchAftersaleDetail(orderId)
  } catch (error) {
    console.error(error)
  } finally {
    loading.value = false
  }
}

function goOrderDetail() {
  uni.navigateTo({
    url: `/pages/order/detail/index?orderId=${orderId}`,
  })
}

async function handleCancel() {
  const aftersaleId = detail.value?.aftersale.id
  if (!aftersaleId) {
    return
  }

  try {
    await cancelAftersale(aftersaleId)
    uni.showToast({
      title: '已撤销售后申请',
      icon: 'none',
    })
    setTimeout(() => {
      uni.redirectTo({
        url: '/pages/user/refund/index?active=0',
      })
    }, 320)
  } catch (error) {
    console.error(error)
    uni.showToast({
      title: '撤销售后失败',
      icon: 'none',
    })
  }
}

function previewImage(current: string) {
  const urls = detail.value?.aftersale.pictures || []
  uni.previewImage({
    current,
    urls,
  })
}
</script>

<style scoped lang="scss">
.page {
  min-height: 100vh;
  padding: 14rpx 14rpx 32rpx;
  background: linear-gradient(180deg, rgb(var(--sf-color-brand-soft)) 0%, rgb(var(--sf-color-page)) 26%, #ffffff 100%);
}

.hero-card,
.section-card,
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
.summary-head,
.goods-foot,
.action-row,
.info-row {
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
.section-title,
.empty-title,
.summary-title {
  display: block;
  margin-top: 4rpx;
  font-size: 22rpx;
  line-height: 1.28;
  color: inherit;
}

.desc,
.empty-desc,
.summary-meta,
.comment {
  display: block;
  margin-top: 8rpx;
  font-size: 18rpx;
  line-height: 1.42;
}

.desc {
  color: rgba(255, 255, 255, 0.88);
}

.section-list {
  display: grid;
  gap: 8rpx;
  margin-top: 10rpx;
}

.section-card,
.empty-card {
  padding: 12rpx 14rpx;
}

.summary-head,
.action-row,
.info-row {
  justify-content: space-between;
  gap: 10rpx;
}

.summary-copy {
  min-width: 0;
  flex: 1;
}

.summary-title,
.goods-title,
.empty-title {
  color: rgb(var(--sf-color-ink));
}

.summary-title,
.goods-title {
  font-weight: 600;
}

.summary-meta,
.goods-spec,
.goods-count,
.empty-desc,
.comment,
.info-label {
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

.goods-row {
  display: flex;
  gap: 10rpx;
}

.goods-image {
  width: 104rpx;
  height: 104rpx;
  flex-shrink: 0;
  border-radius: var(--sf-radius-card);
  background: rgb(var(--sf-color-page));
}

.goods-body {
  min-width: 0;
  flex: 1;
}

.goods-spec,
.goods-count,
.info-label,
.info-value {
  display: block;
  margin-top: 6rpx;
  font-size: 17rpx;
  line-height: 1.4;
}

.goods-foot {
  justify-content: space-between;
  gap: 8rpx;
  margin-top: 8rpx;
}

.goods-price,
.info-value {
  color: rgb(var(--sf-color-ink));
}

.info-list {
  margin-top: 8rpx;
}

.info-row + .info-row {
  margin-top: 10rpx;
}

.info-value {
  max-width: 62%;
  text-align: right;
}

.comment {
  margin-top: 10rpx;
  padding: 10rpx 12rpx;
  border-radius: var(--sf-radius-card);
  background: rgb(var(--sf-color-page));
}

.proof-grid {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 8rpx;
  margin-top: 10rpx;
}

.proof-card {
  overflow: hidden;
  border-radius: var(--sf-radius-card);
  background: rgb(var(--sf-color-page));
}

.proof-image {
  width: 100%;
  height: 160rpx;
}

.action-row {
  justify-content: flex-end;
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

.section-card--skeleton {
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
  width: 120rpx;
}
</style>
