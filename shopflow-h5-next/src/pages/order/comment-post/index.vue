<template>
  <view class="page" role="main">
    <view class="hero-card">
      <text class="title" role="heading" aria-level="1">订单评价</text>
      <text class="desc">分享你的真实使用感受，帮助其他用户做决定。</text>
    </view>

    <view v-if="loading" class="section-list">
      <view v-for="index in 2" :key="index" class="section-card section-card--skeleton">
        <view class="skeleton-line skeleton-line--title"></view>
        <view class="skeleton-line"></view>
      </view>
    </view>

    <template v-else-if="goodsInfo">
      <view class="section-list">
        <view class="section-card">
          <view class="goods-row">
            <image class="goods-image" :src="goodsInfo.picUrl" mode="aspectFill" />
            <view class="goods-body">
              <text class="goods-title">{{ goodsInfo.goodsName }}</text>
              <text class="goods-spec">{{ goodsInfo.specifications.join(' / ') || '默认规格' }}</text>
              <text class="goods-meta">x {{ goodsInfo.number }} / ¥ {{ goodsInfo.price }}</text>
            </view>
          </view>
        </view>

        <view class="section-card">
          <text class="section-title">评分</text>
          <view class="star-row">
            <view
              v-for="value in [1, 2, 3, 4, 5]"
              :key="value"
              class="star-chip"
              :class="{ 'star-chip--active': star >= value }"
              @click="star = value"
            >
              ★
            </view>
          </view>
          <text class="hint">{{ starText }}</text>
        </view>

        <view class="section-card">
          <text class="field-label">评价内容</text>
          <textarea
            v-model="content"
            class="field-textarea"
            maxlength="140"
            placeholder="留言经过筛选后，对所有人可见"
          />
          <text class="count">{{ content.length }}/140</text>
        </view>

        <view class="section-card">
          <view class="upload-head">
            <text class="section-title">评价图片</text>
            <view class="ghost-btn" role="button" @click="chooseCommentImage">
              {{ uploading ? '上传中...' : '上传图片' }}
            </view>
          </view>
          <view v-if="pictures.length" class="proof-grid">
            <view
              v-for="(picture, index) in pictures"
              :key="picture"
              class="proof-card"
              role="button"
              @click="removePicture(index)"
            >
              <image class="proof-image" :src="picture" mode="aspectFill" />
              <text class="proof-remove">删除</text>
            </view>
          </view>
          <text v-else class="hint">可补充实拍图或使用场景，最多上传 3 张。</text>
        </view>

        <view class="footer">
          <view
            class="primary-btn"
            :class="{ 'primary-btn--disabled': submitting || uploading }"
            role="button"
            @click="submit"
          >
            {{ submitting ? '提交中...' : '发表评价' }}
          </view>
        </view>
      </view>
    </template>

    <view v-else class="empty-card">
      <text class="empty-title">未找到待评价商品</text>
      <text class="empty-desc">请返回订单页重新发起评价。</text>
    </view>
  </view>
</template>

<script setup lang="ts">
import { computed, ref } from 'vue'
import { onShow } from '@dcloudio/uni-app'
import { fetchOrderCommentGoods, submitOrderComment, type OrderCommentGoodsItem } from '@/entities/order/api'
import { uploadAftersaleProof } from '@/entities/user/api'
import { buildOrderCommentPayload, getCommentStarText, validateOrderCommentForm } from '@/pages/order/comment-utils'

type UniPageWithOptions = {
  options?: Record<string, unknown>
}

const pageOptions = (getCurrentPages()[getCurrentPages().length - 1] as UniPageWithOptions | undefined)?.options || {}
const routeGoodsId = typeof pageOptions.goodsId === 'string' ? pageOptions.goodsId : ''
const loading = ref(true)
const submitting = ref(false)
const uploading = ref(false)
const goodsInfo = ref<OrderCommentGoodsItem | null>(null)
const star = ref(5)
const content = ref('')
const pictures = ref<string[]>([])
const starText = computed(() => getCommentStarText(star.value))

bootstrap()
onShow(() => {
  void bootstrap()
})

async function bootstrap() {
  if (!routeGoodsId) {
    goodsInfo.value = null
    loading.value = false
    return
  }

  loading.value = true

  try {
    goodsInfo.value = await fetchOrderCommentGoods(routeGoodsId)
  } catch (error) {
    console.error(error)
    goodsInfo.value = null
  } finally {
    loading.value = false
  }
}

async function chooseCommentImage() {
  if (uploading.value || pictures.value.length >= 3) {
    if (pictures.value.length >= 3) {
      toast('最多上传 3 张图片')
    }
    return
  }

  try {
    const chooseResult = await uni.chooseImage({
      count: 3 - pictures.value.length,
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
    toast('图片上传成功')
  } catch (error) {
    console.error(error)
    toast('图片上传失败')
  } finally {
    uploading.value = false
  }
}

function removePicture(index: number) {
  pictures.value = pictures.value.filter((_, currentIndex) => currentIndex !== index)
}

async function submit() {
  if (submitting.value || uploading.value) {
    return
  }

  const goodsId = Number(routeGoodsId)
  if (!goodsId || Number.isNaN(goodsId)) {
    toast('缺少评价商品')
    return
  }

  const errorMessage = validateOrderCommentForm({
    star: star.value,
    content: content.value,
    picUrls: pictures.value,
  })

  if (errorMessage) {
    toast(errorMessage)
    return
  }

  submitting.value = true

  try {
    await submitOrderComment(
      buildOrderCommentPayload({
        goodsId,
        star: star.value,
        content: content.value,
        picUrls: pictures.value,
      }),
    )
    toast('评价成功')
    setTimeout(() => {
      uni.navigateBack({
        delta: 1,
      })
    }, 320)
  } catch (error) {
    console.error(error)
    toast('评价提交失败')
  } finally {
    submitting.value = false
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
.empty-title,
.goods-title {
  display: block;
  font-size: 28rpx;
  line-height: 1.3;
  color: #172033;
}

.desc,
.field-label,
.hint,
.count,
.empty-desc,
.goods-spec,
.goods-meta {
  display: block;
  margin-top: 8rpx;
  font-size: 22rpx;
  line-height: 1.4;
  color: #748194;
}

.section-list {
  display: grid;
  gap: 16rpx;
  margin-top: 16rpx;
}

.section-card--skeleton {
  min-height: 152rpx;
}

.skeleton-line {
  height: 22rpx;
  margin-top: 14rpx;
  border-radius: 999px;
  background: linear-gradient(90deg, #edf2f8 0%, #f6f8fb 100%);
}

.skeleton-line--title {
  width: 45%;
  height: 28rpx;
  margin-top: 0;
}

.goods-row,
.upload-head,
.footer {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16rpx;
}

.goods-image {
  width: 176rpx;
  height: 176rpx;
  border-radius: 12rpx;
  background: #f3f6fb;
}

.goods-body {
  min-width: 0;
  flex: 1;
}

.star-row {
  display: flex;
  gap: 12rpx;
  margin-top: 18rpx;
}

.star-chip {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 72rpx;
  height: 72rpx;
  border-radius: 12rpx;
  background: #eef3f9;
  font-size: 34rpx;
  color: #a8b3c3;
}

.star-chip--active {
  background: rgba(250, 173, 20, 0.16);
  color: #faad14;
}

.field-textarea {
  width: 100%;
  min-height: 240rpx;
  margin-top: 14rpx;
  padding: 18rpx;
  border-radius: 12rpx;
  background: #f7faff;
  font-size: 24rpx;
  line-height: 1.5;
  color: #172033;
  box-sizing: border-box;
}

.count {
  text-align: right;
}

.ghost-btn,
.primary-btn {
  display: flex;
  align-items: center;
  justify-content: center;
  height: 76rpx;
  padding: 0 22rpx;
  border-radius: 12rpx;
  font-size: 22rpx;
}

.ghost-btn {
  flex-shrink: 0;
  background: #eef3f9;
  color: #1677ff;
}

.proof-grid {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 12rpx;
  margin-top: 16rpx;
}

.proof-card {
  overflow: hidden;
  border-radius: 12rpx;
  background: #f7faff;
}

.proof-image {
  width: 100%;
  height: 180rpx;
  background: #eef3f9;
}

.proof-remove {
  display: block;
  padding: 10rpx 0;
  font-size: 21rpx;
  text-align: center;
  color: #5f6b7c;
}

.footer {
  margin-top: 8rpx;
}

.primary-btn {
  width: 100%;
  background: #1677ff;
  color: #ffffff;
}

.primary-btn--disabled {
  opacity: 0.65;
}

.empty-card {
  margin-top: 16rpx;
}
</style>
