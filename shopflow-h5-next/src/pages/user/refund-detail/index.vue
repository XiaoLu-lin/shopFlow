<template>
  <view class="page" role="main">
    <view class="hero-card">
      <text class="title" role="heading" aria-level="1">售后详情</text>
      <text class="desc">已接回真实售后详情接口，当前展示售后状态、商品、退款原因和金额信息。</text>
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
          <view class="head-row">
            <view>
              <text class="meta">售后单号 {{ detail.aftersale.aftersaleSn }}</text>
              <text class="meta meta--muted">订单号 {{ detail.order.orderSn }}</text>
            </view>
            <text class="status" :class="resolveAftersaleStatusClass(detail.aftersale.statusText || '')">
              {{ detail.aftersale.statusText || '处理中' }}
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
            <view v-for="picture in detail.aftersale.pictures" :key="picture" class="proof-card" role="button" @click="previewImage(picture)">
              <image class="proof-image" :src="picture" mode="aspectFill" />
            </view>
          </view>
        </view>

        <view class="action-row">
          <view class="ghost-btn" role="button" @click="goOrderDetail">查看订单</view>
          <view v-if="canCancelAftersale(detail.aftersale.status)" class="dark-btn" role="button" @click="requestCancel">撤销售后</view>
        </view>
      </view>
    </template>

    <view v-else class="empty-card">
      <text class="empty-title">未找到售后详情</text>
      <text class="empty-desc">请返回售后列表重试。</text>
    </view>

    <view v-if="showCancelDialog" class="dialog-mask">
      <view class="dialog-card">
        <text class="dialog-title">确认撤销售后</text>
        <text class="dialog-desc">撤销后将返回售后列表，当前申请无法继续处理。</text>
        <view class="dialog-actions">
          <view class="ghost-btn" role="button" @click="showCancelDialog = false">取消</view>
          <view class="dark-btn" role="button" @click="handleCancel">确认</view>
        </view>
      </view>
    </view>
  </view>
</template>

<script setup lang="ts">
import { onShow } from '@dcloudio/uni-app'
import { ref } from 'vue'
import { cancelAftersale, fetchAftersaleDetail, type AftersaleDetailPayload } from '@/entities/user/api'
import { canCancelAftersale, resolveAftersaleStatusClass } from '../aftersale-utils'

type UniPageWithOptions = {
  options?: Record<string, unknown>
}

const pageOptions = (getCurrentPages()[getCurrentPages().length - 1] as UniPageWithOptions | undefined)?.options || {}
const orderId = typeof pageOptions.orderId === 'string' ? pageOptions.orderId : ''
const loading = ref(true)
const detail = ref<AftersaleDetailPayload | null>(null)
const showCancelDialog = ref(false)

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
    showCancelDialog.value = false
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

function requestCancel() {
  showCancelDialog.value = true
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
.empty-title,
.section-title {
  display: block;
  font-size: 28rpx;
  line-height: 1.3;
  color: #172033;
}

.desc,
.empty-desc,
.meta,
.comment {
  display: block;
  margin-top: 8rpx;
  font-size: 22rpx;
  line-height: 1.4;
  color: #748194;
}

.section-list {
  display: grid;
  gap: 14rpx;
  margin-top: 16rpx;
}

.head-row,
.goods-foot,
.action-row,
.info-row {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16rpx;
}

.meta--muted {
  font-size: 20rpx;
  color: #a0aaba;
}

.status {
  font-size: 22rpx;
}

.goods-row {
  display: flex;
  gap: 14rpx;
}

.goods-image {
  width: 112rpx;
  height: 112rpx;
  flex-shrink: 0;
  border-radius: 10rpx;
  background: #f6f8fb;
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

.info-list {
  margin-top: 14rpx;
}

.info-row + .info-row {
  margin-top: 12rpx;
}

.info-label {
  font-size: 22rpx;
  color: #748194;
}

.info-value {
  max-width: 60%;
  text-align: right;
  font-size: 22rpx;
  color: #172033;
}

.comment {
  margin-top: 14rpx;
  border-radius: 10rpx;
  background: #f6f8fb;
  padding: 16rpx;
  color: #5f6b7c;
}

.proof-grid {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 12rpx;
  margin-top: 14rpx;
}

.proof-card {
  overflow: hidden;
  border-radius: 10rpx;
  background: #f6f8fb;
}

.proof-image {
  width: 100%;
  height: 160rpx;
}

.dialog-mask {
  position: fixed;
  inset: 0;
  display: flex;
  align-items: center;
  justify-content: center;
  background: rgba(23, 32, 51, 0.28);
  padding: 32rpx;
}

.dialog-card {
  width: 100%;
  max-width: 560rpx;
  border-radius: 16rpx;
  background: #ffffff;
  padding: 28rpx 24rpx;
  box-shadow: 0 20rpx 40rpx rgba(23, 32, 51, 0.12);
}

.dialog-title {
  display: block;
  font-size: 28rpx;
  line-height: 1.4;
  color: #172033;
}

.dialog-desc {
  display: block;
  margin-top: 12rpx;
  font-size: 22rpx;
  line-height: 1.5;
  color: #748194;
}

.dialog-actions {
  display: flex;
  justify-content: flex-end;
  gap: 16rpx;
  margin-top: 24rpx;
}

.action-row {
  justify-content: flex-end;
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

.section-card--skeleton {
  min-height: 108rpx;
}

.skeleton-line {
  height: 20rpx;
  margin-top: 12rpx;
  border-radius: 999px;
  background: #edf1f6;
}

.skeleton-line--title {
  width: 220rpx;
  margin-top: 0;
}

.skeleton-line--short {
  width: 140rpx;
}

.empty-card {
  margin-top: 16rpx;
  text-align: center;
}
</style>
