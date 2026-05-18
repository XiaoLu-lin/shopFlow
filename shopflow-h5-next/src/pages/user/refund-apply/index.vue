<template>
  <view class="page">
    <view class="hero-card">
      <view class="hero-row">
        <view class="hero-avatar">申</view>
        <view class="hero-copy">
          <text class="eyebrow">{{ hero.eyebrow }}</text>
          <text class="title">{{ hero.title }}</text>
        </view>
      </view>
      <text class="desc">{{ hero.description }}</text>
    </view>

    <view v-if="loading" class="section-list">
      <view v-for="index in 2" :key="index" class="section-card section-card--skeleton">
        <view class="skeleton-line skeleton-line--title"></view>
        <view class="skeleton-line"></view>
      </view>
    </view>

    <template v-else-if="orderInfo && orderGoods">
      <view class="section-list">
        <view class="section-card">
          <view class="goods-row">
            <image class="goods-image" :src="orderGoods.picUrl" mode="aspectFill" />
            <view class="goods-body">
              <text class="goods-title">{{ orderGoods.goodsName }}</text>
              <text class="goods-spec">{{ orderGoods.specifications.join(' / ') || '默认规格' }}</text>
              <view class="goods-foot">
                <text class="goods-meta">订单号 {{ orderInfo.orderSn }}</text>
                <text class="goods-price">¥ {{ orderInfo.actualPrice }}</text>
              </view>
            </view>
          </view>
        </view>

        <view class="section-card">
          <text class="section-title">售后类型</text>
          <scroll-view scroll-x class="chip-scroll">
            <view class="chip-row">
              <view class="chip" :class="{ 'chip--active': type === 0 }" @click="type = 0">未收货退款</view>
              <view class="chip" :class="{ 'chip--active': type === 1 }" @click="type = 1">已收货退款</view>
              <view class="chip" :class="{ 'chip--active': type === 2 }" @click="type = 2">退货退款</view>
            </view>
          </scroll-view>
        </view>

        <view class="section-card">
          <text class="field-label">退款原因</text>
          <input v-model="reason" class="field-input" maxlength="50" placeholder="请填写退款原因" />
        </view>

        <view class="section-card">
          <text class="field-label">退款金额</text>
          <input v-model="amount" class="field-input" type="digit" :placeholder="`最多 ¥ ${amountPlaceholder}`" />
          <text class="hint">默认按订单实付金额提交，也可以按实际情况调整。</text>
        </view>

        <view class="section-card">
          <text class="field-label">补充说明</text>
          <textarea v-model="comment" class="field-textarea" maxlength="120" placeholder="可补充描述问题，便于售后处理" />
          <text class="count">{{ comment.length }}/120</text>
        </view>

        <view class="section-card">
          <view class="upload-head">
            <text class="section-title">凭证图片</text>
            <view class="ghost-btn" @click="chooseProofImage">{{ uploading ? '上传中...' : '上传图片' }}</view>
          </view>
          <view v-if="pictures.length" class="proof-grid">
            <view v-for="(picture, index) in pictures" :key="picture" class="proof-card" @click="removePicture(index)">
              <image class="proof-image" :src="picture" mode="aspectFill" />
              <text class="proof-remove">删除</text>
            </view>
          </view>
          <text v-else class="hint">可补充商品问题截图或物流凭证。</text>
        </view>
      </view>

      <view class="footer">
        <view class="primary-btn" :class="{ 'primary-btn--disabled': saving || uploading }" @click="submit">
          {{ saving ? '提交中...' : '提交售后申请' }}
        </view>
      </view>
    </template>

    <view v-else class="empty-card">
      <text class="empty-title">未找到订单信息</text>
      <text class="empty-desc">请返回订单页重新发起售后申请。</text>
    </view>
  </view>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { fetchOrderDetail, type OrderDetailGoodsItem, type OrderDetailInfo } from '@/entities/order/api'
import { submitAftersale, uploadAftersaleProof } from '@/entities/user/api'
import { resolveUserPageHero } from '../page-display-utils'

type UniPageWithOptions = {
  options?: Record<string, unknown>
}

const pageOptions = (getCurrentPages()[getCurrentPages().length - 1] as UniPageWithOptions | undefined)?.options || {}
const orderId = typeof pageOptions.orderId === 'string' ? pageOptions.orderId : ''
const loading = ref(true)
const saving = ref(false)
const uploading = ref(false)
const type = ref(1)
const reason = ref('')
const comment = ref('')
const amount = ref('')
const pictures = ref<string[]>([])
const orderInfo = ref<OrderDetailInfo | null>(null)
const orderGoods = ref<OrderDetailGoodsItem | null>(null)
const amountPlaceholder = ref('0')
const hero = resolveUserPageHero('refund-apply')

bootstrap()

async function bootstrap() {
  if (!orderId) {
    loading.value = false
    return
  }

  loading.value = true

  try {
    const result = await fetchOrderDetail(orderId)
    orderInfo.value = result.orderInfo
    orderGoods.value = result.orderGoods?.[0] || null
    amountPlaceholder.value = String(result.orderInfo?.actualPrice || 0)
  } catch (error) {
    console.error(error)
  } finally {
    loading.value = false
  }
}

async function chooseProofImage() {
  if (uploading.value) {
    return
  }

  try {
    const chooseResult = await uni.chooseImage({
      count: 1,
      sizeType: ['compressed'],
      sourceType: ['album', 'camera'],
    })
    const filePath = chooseResult.tempFilePaths?.[0]

    if (!filePath) {
      return
    }

    uploading.value = true
    const result = await uploadAftersaleProof(filePath)
    pictures.value = [...pictures.value, result.url]
    toast('凭证上传成功')
  } catch (error) {
    console.error(error)
    toast('凭证上传失败')
  } finally {
    uploading.value = false
  }
}

function removePicture(index: number) {
  pictures.value = pictures.value.filter((_, currentIndex) => currentIndex !== index)
}

async function submit() {
  if (!orderId) {
    toast('缺少订单号')
    return
  }

  if (!reason.value.trim()) {
    toast('请输入退款原因')
    return
  }

  const parsedAmount = Number(amount.value || amountPlaceholder.value)
  if (!parsedAmount || Number.isNaN(parsedAmount)) {
    toast('请输入正确的退款金额')
    return
  }

  saving.value = true

  try {
    await submitAftersale({
      orderId,
      type: type.value,
      amount: parsedAmount,
      reason: reason.value.trim(),
      comment: comment.value.trim(),
      pictures: pictures.value,
    })
    toast('售后申请已提交')
    setTimeout(() => {
      uni.redirectTo({
        url: '/pages/user/refund/index?active=0',
      })
    }, 320)
  } catch (error) {
    console.error(error)
    toast('售后申请提交失败')
  } finally {
    saving.value = false
  }
}

function toast(title: string) {
  uni.showToast({
    title,
    icon: 'none',
  })
}
</script>

<style scoped lang="scss">
.page {
  min-height: 100vh;
  padding: 14rpx 14rpx 124rpx;
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
.goods-foot,
.upload-head {
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
.empty-title {
  display: block;
  margin-top: 4rpx;
  font-size: 22rpx;
  line-height: 1.28;
  color: inherit;
}

.desc,
.field-label,
.hint,
.count,
.empty-desc {
  display: block;
  margin-top: 8rpx;
  font-size: 18rpx;
  line-height: 1.42;
}

.desc {
  color: rgba(255, 255, 255, 0.88);
}

.field-label,
.hint,
.count,
.empty-desc,
.goods-spec,
.goods-meta {
  color: rgb(var(--sf-color-text-secondary));
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

.goods-row {
  display: flex;
  align-items: flex-start;
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

.goods-title,
.empty-title {
  display: block;
  font-size: 20rpx;
  line-height: 1.32;
  color: rgb(var(--sf-color-ink));
}

.goods-title {
  font-weight: 600;
}

.goods-spec,
.goods-meta {
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

.goods-price {
  font-size: 22rpx;
  color: rgb(var(--sf-color-ink));
}

.chip-scroll {
  margin-top: 10rpx;
  white-space: nowrap;
}

.chip-row {
  display: inline-flex;
  gap: 6rpx;
}

.chip {
  padding: 8rpx 14rpx;
  border-radius: 999px;
  background: rgb(var(--sf-color-page));
  font-size: 18rpx;
  color: rgb(var(--sf-color-text-secondary));
}

.chip--active {
  background: rgb(var(--sf-color-brand));
  color: #ffffff;
}

.field-input,
.field-textarea {
  width: 100%;
  margin-top: 12rpx;
  padding: 0 16rpx;
  border-radius: var(--sf-radius-card);
  background: rgb(var(--sf-color-page));
  font-size: 20rpx;
  color: rgb(var(--sf-color-ink));
  box-sizing: border-box;
}

.field-input {
  height: 76rpx;
}

.field-textarea {
  min-height: 200rpx;
  padding-top: 16rpx;
  padding-bottom: 16rpx;
  line-height: 1.5;
}

.count {
  margin-top: 10rpx;
  text-align: right;
}

.upload-head {
  justify-content: space-between;
  gap: 10rpx;
}

.ghost-btn {
  display: inline-flex;
  min-width: 152rpx;
  align-items: center;
  justify-content: center;
  height: 64rpx;
  border-radius: var(--sf-radius-card);
  background: rgb(var(--sf-color-brand-soft));
  font-size: 18rpx;
  color: rgb(var(--sf-color-brand));
}

.proof-grid {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 8rpx;
  margin-top: 10rpx;
}

.proof-card {
  position: relative;
  overflow: hidden;
  border-radius: var(--sf-radius-card);
  background: rgb(var(--sf-color-page));
}

.proof-image {
  width: 100%;
  height: 160rpx;
}

.proof-remove {
  position: absolute;
  top: 8rpx;
  right: 8rpx;
  padding: 4rpx 8rpx;
  border-radius: 999px;
  background: rgba(15, 23, 42, 0.62);
  font-size: 16rpx;
  color: #ffffff;
}

.footer {
  position: fixed;
  right: 14rpx;
  bottom: 18rpx;
  left: 14rpx;
}

.primary-btn {
  display: flex;
  align-items: center;
  justify-content: center;
  height: 84rpx;
  border-radius: var(--sf-radius-card);
  background: linear-gradient(145deg, rgb(var(--sf-color-brand)) 0%, rgb(var(--sf-color-brand-light)) 100%);
  font-size: 22rpx;
  color: #ffffff;
  box-shadow: var(--sf-shadow-brand);
}

.primary-btn--disabled {
  opacity: 0.45;
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
</style>
