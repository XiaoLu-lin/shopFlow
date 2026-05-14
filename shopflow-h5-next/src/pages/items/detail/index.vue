<template>
  <view class="page">
    <swiper
      v-if="gallery.length"
      class="hero-swiper"
      indicator-dots
      circular
      autoplay
      :interval="3200"
      indicator-color="rgba(255,255,255,0.5)"
      indicator-active-color="#1677ff"
    >
      <swiper-item v-for="(item, index) in gallery" :key="`${item}-${index}`">
        <image class="hero-image" :src="item" mode="aspectFill" />
      </swiper-item>
    </swiper>

    <view class="summary-card">
      <view class="summary-head">
        <view class="summary-main">
          <text class="goods-name">{{ detail?.info.name || '商品详情' }}</text>
          <text class="goods-brief">{{ detail?.info.brief || '正在加载商品基础信息。' }}</text>
        </view>
        <view class="collect-btn" :class="{ 'collect-btn--active': collected }" @click="toggleCollectState">
          {{ collected ? '已收藏' : '收藏' }}
        </view>
      </view>
      <view class="price-row">
        <text class="price">¥ {{ matchedPrice }}</text>
        <text class="origin-price">¥ {{ detail?.info.counterPrice ?? '--' }}</text>
      </view>
      <text class="stock-copy">{{ completedSelection ? `库存 ${matchedStock}` : '请选择完整规格后再购买' }}</text>
    </view>

    <view class="panel" role="button" @click="goCommentList">
      <view class="comment-head">
        <text class="panel-title">商品评价</text>
        <text class="panel-link">查看全部评论</text>
      </view>
      <text class="comment-copy">共 {{ commentCount }} 条评价，{{ hasPicCount }} 条带图</text>
    </view>

    <view v-if="specList.length" class="panel">
      <text class="panel-title">选择规格</text>
      <view class="sku-group" v-for="(item, groupIndex) in specList" :key="item.name">
        <text class="spec-name">{{ item.name }}</text>
        <view class="sku-values">
          <view
            v-for="value in item.valueList"
            :key="value.id"
            class="sku-chip"
            :class="{ 'sku-chip--active': selectedValueIds.includes(value.id) }"
            @click="selectSku(groupIndex, value.id)"
          >
            {{ value.value }}
          </view>
        </view>
      </view>
    </view>

    <view v-if="attributeList.length" class="panel">
      <text class="panel-title">商品属性</text>
      <view class="attr-list">
        <view v-for="item in attributeList" :key="`${item.attribute}-${item.value}`" class="attr-row">
          <text class="attr-label">{{ item.attribute }}</text>
          <text class="attr-value">{{ item.value }}</text>
        </view>
      </view>
    </view>

    <view class="panel">
      <text class="panel-title">购买数量</text>
      <view class="quantity-row">
        <view class="quantity-btn" @click="changeQuantity(-1)">-</view>
        <text class="quantity-value">{{ quantity }}</text>
        <view class="quantity-btn" @click="changeQuantity(1)">+</view>
      </view>
    </view>

    <view class="action-bar">
      <view class="action-btn action-btn--ghost" @click="goCart">购物车</view>
      <view class="action-btn action-btn--primary" @click="addToCart">加入购物车</view>
      <view class="action-btn action-btn--dark" @click="buyNow">立即购买</view>
    </view>
  </view>
</template>

<script setup lang="ts">
import { computed, ref } from 'vue'
import { addCartItem, fastAddCartItem } from '@/entities/cart/api'
import { fetchGoodsDetail } from '@/entities/goods/api'
import { toggleCollect } from '@/entities/user/api'
import type { GoodsDetailPayload } from '@/entities/goods/api'
import { buildCartPayload, clampSkuQuantity, hasCompletedSkuSelection, resolveMatchedProduct } from '@/features/goods/sku-utils'
import { writeFlowContext } from '@/shared/compat/session-adapter'

type UniPageWithOptions = {
  options?: Record<string, unknown>
}

const pageOptions = (getCurrentPages()[getCurrentPages().length - 1] as UniPageWithOptions | undefined)?.options || {}
const id = pageOptions.id
const detail = ref<GoodsDetailPayload | null>(null)
const selectedValueIds = ref<number[]>([])
const quantity = ref(1)
const collected = ref(false)

const gallery = computed(() => detail.value?.info.gallery || [])
const specList = computed(() => detail.value?.specificationList || [])
const attributeList = computed(() => detail.value?.attribute || [])
const matchedProduct = computed(() => (detail.value ? resolveMatchedProduct(detail.value, selectedValueIds.value) : null))
const completedSelection = computed(() => (detail.value ? hasCompletedSkuSelection(detail.value, selectedValueIds.value) : false))
const matchedPrice = computed(() => matchedProduct.value?.price ?? detail.value?.info.retailPrice ?? '--')
const matchedStock = computed(() => matchedProduct.value?.number ?? 0)
const commentCount = computed(() => detail.value?.comment?.allCount || 0)
const hasPicCount = computed(() => detail.value?.comment?.hasPicCount || 0)

bootstrap()

async function bootstrap() {
  if (!id || typeof id !== 'string') {
    return
  }

  try {
    detail.value = await fetchGoodsDetail(id)
    collected.value = detail.value.userHasCollect === 1
    selectedValueIds.value = detail.value.specificationList
      .map((group) => group.valueList[0]?.id)
      .filter((value): value is number => typeof value === 'number')
  } catch (error) {
    console.error(error)
  }
}

function selectSku(groupIndex: number, valueId: number) {
  const next = [...selectedValueIds.value]
  next[groupIndex] = valueId
  selectedValueIds.value = next
  quantity.value = clampSkuQuantity(quantity.value, matchedStock.value || 1)
}

function changeQuantity(step: 1 | -1) {
  quantity.value = clampSkuQuantity(quantity.value + step, matchedStock.value || 1)
}

async function toggleCollectState() {
  if (!detail.value) {
    return
  }

  try {
    await toggleCollect(detail.value.info.id)
    collected.value = !collected.value
    uni.showToast({
      title: collected.value ? '已收藏' : '已取消收藏',
      icon: 'none',
    })
  } catch (error) {
    console.error(error)
    uni.showToast({
      title: '收藏操作失败',
      icon: 'none',
    })
  }
}

async function addToCart() {
  if (!detail.value) {
    return
  }

  if (!completedSelection.value) {
    uni.showToast({
      title: '请先选完整规格',
      icon: 'none',
    })
    return
  }

  const payload = buildCartPayload(detail.value, {
    quantity: quantity.value,
    selectedValueIds: selectedValueIds.value,
  })

  if (!payload) {
    return
  }

  try {
    await addCartItem(payload)
    uni.showToast({
      title: '已加入购物车',
      icon: 'none',
    })
  } catch (error) {
    console.error(error)
    uni.showToast({
      title: '加购失败',
      icon: 'none',
    })
  }
}

async function buyNow() {
  if (!detail.value) {
    return
  }

  if (!completedSelection.value) {
    uni.showToast({
      title: '请先选完整规格',
      icon: 'none',
    })
    return
  }

  const payload = buildCartPayload(detail.value, {
    quantity: quantity.value,
    selectedValueIds: selectedValueIds.value,
  })

  if (!payload) {
    return
  }

  try {
    const cartId = await fastAddCartItem(payload)
    writeFlowContext({
      AddressId: 0,
      CartId: cartId,
      CouponId: 0,
      UserCouponId: 0,
    })
    uni.navigateTo({
      url: '/pages/order/checkout/index',
    })
  } catch (error) {
    console.error(error)
    uni.showToast({
      title: '立即购买失败',
      icon: 'none',
    })
  }
}

function goCart() {
  uni.navigateTo({
    url: '/pages/order/cart/index',
  })
}

function goCommentList() {
  if (!id || typeof id !== 'string') {
    return
  }

  uni.navigateTo({
    url: `/pages/items/comment-list/index?goodsId=${id}`,
  })
}
</script>

<style scoped lang="scss">
.page {
  min-height: 100vh;
  padding: 20rpx 20rpx 140rpx;
  background: linear-gradient(180deg, #ffffff 0%, #f6f8fb 100%);
}

.hero-swiper {
  height: 560rpx;
  border-radius: 12rpx;
  overflow: hidden;
  background: #f3f6fb;
}

.hero-image {
  width: 100%;
  height: 100%;
}

.summary-card,
.panel {
  margin-top: 16rpx;
  padding: 22rpx;
  border-radius: 12rpx;
  background: #ffffff;
  box-shadow: 0 10rpx 24rpx rgba(23, 32, 51, 0.06);
}

.summary-head {
  display: flex;
  align-items: flex-start;
  gap: 16rpx;
}

.summary-main {
  min-width: 0;
  flex: 1;
}

.collect-btn {
  flex-shrink: 0;
  padding: 10rpx 16rpx;
  border-radius: 999px;
  background: #f3f6fb;
  font-size: 21rpx;
  line-height: 1.2;
  color: #5f6b7c;
}

.collect-btn--active {
  background: #edf5ff;
  color: #1677ff;
}

.goods-name,
.panel-title {
  display: block;
  font-size: 28rpx;
  line-height: 1.3;
  color: #172033;
}

.goods-brief,
.tip-copy,
.comment-copy {
  display: block;
  margin-top: 8rpx;
  font-size: 22rpx;
  line-height: 1.45;
  color: #748194;
}

.comment-head {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16rpx;
}

.panel-link {
  flex-shrink: 0;
  font-size: 22rpx;
  line-height: 1.3;
  color: #1677ff;
}

.price-row {
  display: flex;
  align-items: flex-end;
  gap: 12rpx;
  margin-top: 16rpx;
}

.price {
  font-size: 34rpx;
  line-height: 1.2;
  font-weight: 600;
  color: #172033;
}

.origin-price {
  font-size: 22rpx;
  line-height: 1.2;
  color: #9aa5b5;
  text-decoration: line-through;
}

.stock-copy {
  display: block;
  margin-top: 8rpx;
  font-size: 21rpx;
  line-height: 1.35;
  color: #748194;
}

.sku-group {
  margin-top: 14rpx;
}

.spec-list,
.attr-list {
  display: grid;
  gap: 12rpx;
  margin-top: 14rpx;
}

.attr-row {
  padding: 16rpx;
  border-radius: 10rpx;
  background: #f7faff;
}

.spec-name,
.attr-label {
  display: block;
  font-size: 23rpx;
  line-height: 1.3;
  color: #516076;
}

.attr-value {
  display: block;
  margin-top: 6rpx;
  font-size: 23rpx;
  line-height: 1.35;
  color: #172033;
}

.sku-values {
  display: flex;
  flex-wrap: wrap;
  gap: 10rpx;
  margin-top: 10rpx;
}

.sku-chip {
  padding: 10rpx 16rpx;
  border-radius: 999px;
  background: #f3f6fb;
  font-size: 21rpx;
  line-height: 1.2;
  color: #5f6b7c;
}

.sku-chip--active {
  background: #edf5ff;
  color: #1677ff;
}

.quantity-row {
  display: flex;
  align-items: center;
  gap: 14rpx;
  margin-top: 14rpx;
}

.quantity-btn,
.quantity-value {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 68rpx;
  height: 68rpx;
  border-radius: 10rpx;
  background: #f3f6fb;
  font-size: 26rpx;
  color: #172033;
}

.action-bar {
  position: fixed;
  left: 20rpx;
  right: 20rpx;
  bottom: 20rpx;
  display: grid;
  grid-template-columns: 1fr 1fr 1fr;
  gap: 12rpx;
}

.action-btn {
  display: flex;
  align-items: center;
  justify-content: center;
  height: 84rpx;
  border-radius: 12rpx;
  font-size: 24rpx;
  line-height: 1.2;
}

.action-btn--ghost {
  background: #ffffff;
  color: #5f6b7c;
  box-shadow: 0 10rpx 24rpx rgba(23, 32, 51, 0.06);
}

.action-btn--primary {
  background: #1677ff;
  color: #ffffff;
}

.action-btn--dark {
  background: #172033;
  color: #ffffff;
}
</style>
