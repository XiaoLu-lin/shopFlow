<template>
  <view class="page">
    <view class="hero-card">
      <text class="title">申请售后</text>
      <text class="desc">当前先接通退款原因、金额、说明和凭证上传流程。</text>
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
          <text class="hint">默认按订单实付金额提交，可手动调整。</text>
        </view>

        <view class="section-card">
          <text class="field-label">补充说明</text>
          <textarea v-model="comment" class="field-textarea" maxlength="120" placeholder="可补充描述问题，便于售后处理" />
          <text class="count">{{ comment.length }}/120</text>
        </view>

        <view class="section-card">
          <view class="upload-head">
            <text class="section-title">凭证图片</text>
            <view class="ghost-btn" @click="chooseProofImage">
              {{ uploading ? '上传中...' : '上传图片' }}
            </view>
          </view>
          <view v-if="pictures.length" class="proof-grid">
            <view v-for="(picture, index) in pictures" :key="picture" class="proof-card" @click="removePicture(index)">
              <image class="proof-image" :src="picture" mode="aspectFill" />
              <text class="proof-remove">删除</text>
            </view>
          </view>
          <text v-else class="hint">可补充商品问题截图或物流凭证。</text>
        </view>

        <view class="footer">
          <view class="primary-btn" :class="{ 'primary-btn--disabled': saving || uploading }" @click="submit">
            {{ saving ? '提交中...' : '提交售后申请' }}
          </view>
        </view>
      </view>
    </template>

    <view v-else class="empty-card">
      <text class="empty-title">未找到订单信息</text>
      <text class="empty-desc">请返回订单页重试。</text>
    </view>
  </view>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { fetchOrderDetail, type OrderDetailInfo, type OrderDetailGoodsItem } from '@/entities/order/api'
import { submitAftersale, uploadAftersaleProof } from '@/entities/user/api'

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
  padding: 20rpx 20rpx 148rpx;
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
.section-title,
.empty-title {
  display: block;
  font-size: 28rpx;
  line-height: 1.3;
  color: #172033;
}

.desc,
.field-label,
.hint,
.count,
.empty-desc {
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

.goods-row,
.goods-foot,
.upload-head {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16rpx;
}

.goods-row {
  align-items: flex-start;
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
.goods-meta {
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

.chip-scroll {
  margin-top: 14rpx;
  white-space: nowrap;
}

.chip-row {
  display: inline-flex;
  gap: 12rpx;
}

.chip {
  padding: 12rpx 18rpx;
  border-radius: 999px;
  background: #f6f8fb;
  font-size: 22rpx;
  color: #5f6b7c;
}

.chip--active {
  background: #1677ff;
  color: #ffffff;
}

.field-input,
.field-textarea {
  width: 100%;
  margin-top: 14rpx;
  border-radius: 10rpx;
  background: #f6f8fb;
  padding: 0 18rpx;
  font-size: 24rpx;
  color: #172033;
  box-sizing: border-box;
}

.field-input {
  height: 78rpx;
}

.field-textarea {
  min-height: 200rpx;
  padding-top: 18rpx;
  padding-bottom: 18rpx;
  line-height: 1.5;
}

.count {
  margin-top: 10rpx;
  text-align: right;
}

.ghost-btn {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  min-width: 160rpx;
  height: 64rpx;
  border-radius: 10rpx;
  background: #ffffff;
  font-size: 22rpx;
  color: #5f6b7c;
  box-shadow: inset 0 0 0 1px #dbe3ef;
}

.proof-grid {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 12rpx;
  margin-top: 14rpx;
}

.proof-card {
  position: relative;
  overflow: hidden;
  border-radius: 10rpx;
  background: #f6f8fb;
}

.proof-image {
  width: 100%;
  height: 160rpx;
}

.proof-remove {
  position: absolute;
  right: 8rpx;
  top: 8rpx;
  border-radius: 999px;
  background: rgba(23, 32, 51, 0.66);
  padding: 4rpx 8rpx;
  font-size: 18rpx;
  color: #ffffff;
}

.footer {
  position: fixed;
  right: 20rpx;
  bottom: 24rpx;
  left: 20rpx;
}

.primary-btn {
  display: flex;
  align-items: center;
  justify-content: center;
  height: 84rpx;
  border-radius: 12rpx;
  background: #1677ff;
  font-size: 24rpx;
  color: #ffffff;
  box-shadow: 0 14rpx 30rpx rgba(22, 119, 255, 0.2);
}

.primary-btn--disabled {
  opacity: 0.45;
}

.section-card--skeleton {
  min-height: 96rpx;
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

.empty-card {
  margin-top: 16rpx;
  text-align: center;
}
</style>
