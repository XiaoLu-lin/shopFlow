<template>
  <view class="page">
    <view class="detail-header">
      <view class="header-icon" @click="goBack">‹</view>
      <view class="header-tabs">
        <text class="header-tab header-tab--active">商品</text>
        <text class="header-tab">评价</text>
        <text class="header-tab">详情</text>
      </view>
      <view class="header-icon">⋯</view>
    </view>

    <swiper
      v-if="gallery.length"
      class="hero-swiper"
      indicator-dots
      circular
      autoplay
      :interval="3200"
      indicator-color="rgba(74,111,165,0.22)"
      indicator-active-color="#4A6FA5"
    >
      <swiper-item v-for="(item, index) in gallery" :key="`${item}-${index}`">
        <image class="hero-image" :src="item" mode="aspectFill" />
      </swiper-item>
    </swiper>
    <view v-else class="hero-placeholder">
      <text>商品图片加载中</text>
    </view>

    <view class="summary-card">
      <view class="price-row">
        <text class="price">¥{{ matchedPrice }}</text>
        <text v-if="detail?.info.counterPrice" class="origin-price">¥{{ detail.info.counterPrice }}</text>
        <text v-if="discountLabel" class="discount-tag">{{ discountLabel }}</text>
      </view>
      <view class="summary-head">
        <view class="summary-main">
          <text class="goods-name">{{ detail?.info.name || '商品详情' }}</text>
          <text class="goods-brief">{{ detail?.info.brief || '正在加载商品基础信息。' }}</text>
        </view>
      </view>
      <text class="stock-copy">{{ completedSelection ? `库存 ${matchedStock}` : '请选择完整规格后再购买' }}</text>
    </view>

    <view class="service-card">
      <view class="promo-row">
        <text class="promo-label">券</text>
        <text class="promo-copy">优惠以结算页可用券为准</text>
      </view>
      <view class="service-row">
        <text>正品保障</text>
        <text>7天无理由</text>
        <text>极速退款</text>
      </view>
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

    <view class="panel">
      <text class="panel-title">购买数量</text>
      <view class="quantity-row">
        <view class="quantity-btn" @click="changeQuantity(-1)">-</view>
        <text class="quantity-value">{{ quantity }}</text>
        <view class="quantity-btn" @click="changeQuantity(1)">+</view>
      </view>
    </view>

    <view class="panel">
      <text class="panel-title">商品详情</text>
      <rich-text v-if="hasDetailContent" class="detail-content" :nodes="detail?.info.detail || ''" />
      <view v-else class="detail-empty">
        <text>详情信息整理中</text>
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

    <view class="action-bar">
      <view class="action-icon" @click="contactService">客服</view>
      <view class="action-icon" :class="{ 'action-icon--active': collected }" @click="toggleCollectState">
        收藏
      </view>
      <view class="action-icon" @click="goCart">购物车</view>
      <view class="action-btn action-btn--primary" @click="addToCart">加购</view>
      <view class="action-btn action-btn--buy" @click="buyNow">购买</view>
    </view>
  </view>
</template>

<script setup lang="ts">
import { computed, ref } from 'vue'
import { addCartItem, fastAddCartItem } from '@/entities/cart/api'
import { fetchGoodsDetail } from '@/entities/goods/api'
import { toggleCollect } from '@/entities/user/api'
import type { GoodsDetailPayload } from '@/entities/goods/api'
import { formatDiscountLabel, hasRenderableDetail } from '@/features/goods/detail-display-utils'
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
const discountLabel = computed(() => formatDiscountLabel(matchedPrice.value, detail.value?.info.counterPrice))
const hasDetailContent = computed(() => hasRenderableDetail(detail.value?.info.detail))

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

function goBack() {
  uni.navigateBack({
    delta: 1,
  })
}

function goCart() {
  uni.navigateTo({
    url: '/pages/order/cart/index',
  })
}

function contactService() {
  uni.showToast({
    title: '客服入口待接入',
    icon: 'none',
  })
}
</script>

<style scoped lang="scss">
.page {
  min-height: 100vh;
  padding: 16rpx 18rpx 128rpx;
  background: rgb(var(--sf-color-page));
}

.detail-header {
  position: sticky;
  top: 0;
  z-index: 10;
  display: flex;
  align-items: center;
  justify-content: space-between;
  height: 76rpx;
  padding: 0 12rpx;
  border: 2rpx solid rgb(var(--sf-color-line));
  border-radius: 8px;
  background: rgba(255, 255, 255, 0.94);
  box-shadow: 0 4rpx 12rpx rgba(44, 62, 80, 0.04);
}

.header-icon {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 52rpx;
  height: 52rpx;
  color: rgb(var(--sf-color-text-secondary));
  font-size: 34rpx;
}

.header-tabs {
  display: flex;
  align-items: center;
  gap: 32rpx;
}

.header-tab {
  color: rgb(var(--sf-color-text-secondary));
  font-size: 24rpx;
  font-weight: 700;
  line-height: 52rpx;
}

.header-tab--active {
  color: rgb(var(--sf-color-brand));
  border-bottom: 4rpx solid rgb(var(--sf-color-brand));
}

.hero-swiper,
.hero-placeholder {
  height: 670rpx;
  margin-top: 16rpx;
  overflow: hidden;
  border: 2rpx solid rgb(var(--sf-color-line));
  border-radius: 8px;
  background: rgb(var(--sf-color-mist));
}

.hero-placeholder {
  display: flex;
  align-items: center;
  justify-content: center;
  color: rgb(var(--sf-color-text-secondary));
  font-size: 24rpx;
}

.hero-image {
  width: 100%;
  height: 100%;
}

.summary-card,
.service-card,
.panel {
  margin-top: 12rpx;
  padding: 20rpx 22rpx;
  border: 1rpx solid rgba(var(--sf-color-line), 0.86);
  border-radius: 8px;
  background: rgb(var(--sf-color-shell));
  box-shadow: 0 2rpx 8rpx rgba(44, 62, 80, 0.025);
}

.price-row {
  display: flex;
  align-items: flex-end;
  gap: 9rpx;
}

.price {
  color: rgb(var(--sf-color-price));
  font-size: 48rpx;
  font-weight: 800;
  line-height: 1;
}

.origin-price {
  color: rgb(var(--sf-color-text-hint));
  font-size: 23rpx;
  line-height: 1.1;
  text-decoration: line-through;
}

.discount-tag,
.promo-label {
  border-radius: 6rpx;
  background: rgb(var(--sf-color-price-soft));
  color: rgb(var(--sf-color-price));
}

.discount-tag {
  padding: 3rpx 7rpx;
  border-radius: 4px;
  font-size: 18rpx;
  font-weight: 700;
}

.summary-head {
  display: flex;
  align-items: flex-start;
  margin-top: 16rpx;
}

.summary-main {
  min-width: 0;
  flex: 1;
}

.goods-name,
.panel-title {
  display: block;
  color: rgb(var(--sf-color-ink));
  font-size: 28rpx;
  font-weight: 800;
  line-height: 1.3;
}

.goods-brief,
.stock-copy {
  display: block;
  color: rgb(var(--sf-color-text-secondary));
  font-size: 21rpx;
  line-height: 1.45;
}

.goods-brief {
  margin-top: 8rpx;
}

.stock-copy {
  margin-top: 12rpx;
  color: rgb(var(--sf-color-brand));
  font-size: 22rpx;
  font-weight: 600;
}

.action-icon--active {
  background: rgb(var(--sf-color-brand-soft));
  color: rgb(var(--sf-color-brand));
}

.promo-row,
.service-row {
  display: flex;
  align-items: center;
}

.promo-row {
  gap: 12rpx;
}

.promo-label {
  padding: 3rpx 8rpx;
  border-radius: 4px;
  font-size: 18rpx;
  font-weight: 700;
}

.promo-copy {
  color: rgb(var(--sf-color-price));
  font-size: 21rpx;
  font-weight: 600;
}

.service-row {
  flex-wrap: wrap;
  gap: 8rpx 16rpx;
  margin-top: 12rpx;
  color: rgb(var(--sf-color-brand));
  font-size: 20rpx;
  font-weight: 600;
}

.service-row text::before {
  display: inline-block;
  width: 6rpx;
  height: 6rpx;
  margin-right: 7rpx;
  border-radius: 999px;
  background: rgb(var(--sf-color-brand));
  content: '';
  vertical-align: middle;
}

.sku-group {
  margin-top: 14rpx;
}

.spec-name,
.attr-label {
  display: block;
  color: rgb(var(--sf-color-text-secondary));
  font-size: 20rpx;
  font-weight: 600;
  line-height: 1.3;
}

.sku-values {
  display: flex;
  flex-wrap: wrap;
  gap: 8rpx 10rpx;
  margin-top: 10rpx;
}

.sku-chip {
  padding: 8rpx 14rpx;
  border: 2rpx solid transparent;
  border-radius: 8px;
  background: rgb(var(--sf-color-mist));
  color: rgb(var(--sf-color-text-secondary));
  font-size: 20rpx;
  line-height: 1.2;
}

.sku-chip--active {
  border-color: rgba(var(--sf-color-brand), 0.28);
  background: rgb(var(--sf-color-brand-soft));
  color: rgb(var(--sf-color-brand));
  font-weight: 700;
}

.quantity-row {
  display: flex;
  align-items: center;
  gap: 8rpx;
  margin-top: 14rpx;
}

.quantity-btn,
.quantity-value {
  display: flex;
  align-items: center;
  justify-content: center;
  min-width: 48rpx;
  height: 46rpx;
  border-radius: 8px;
  background: rgb(var(--sf-color-mist));
  color: rgb(var(--sf-color-ink));
  font-size: 22rpx;
}

.quantity-value {
  padding: 0 18rpx;
  background: rgb(var(--sf-color-shell));
  border: 2rpx solid rgb(var(--sf-color-line));
}

.detail-content {
  display: block;
  margin-top: 16rpx;
  color: rgb(var(--sf-color-ink));
  font-size: 24rpx;
  line-height: 1.6;
}

.detail-empty {
  margin-top: 16rpx;
  padding: 32rpx 20rpx;
  border-radius: 8px;
  background: rgb(var(--sf-color-mist));
  color: rgb(var(--sf-color-text-secondary));
  font-size: 23rpx;
  text-align: center;
}

.attr-list {
  display: grid;
  gap: 12rpx;
  margin-top: 16rpx;
}

.attr-row {
  padding: 15rpx 16rpx;
  border-radius: 8px;
  background: rgb(var(--sf-color-mist));
}

.attr-value {
  display: block;
  margin-top: 6rpx;
  color: rgb(var(--sf-color-ink));
  font-size: 23rpx;
  line-height: 1.35;
}

.action-bar {
  position: fixed;
  left: 18rpx;
  right: 18rpx;
  bottom: 18rpx;
  z-index: 20;
  display: grid;
  grid-template-columns: 84rpx 84rpx 84rpx 1fr 1fr;
  gap: 8rpx;
  padding: 9rpx;
  border: 2rpx solid rgb(var(--sf-color-line));
  border-radius: 8px;
  background: rgba(255, 255, 255, 0.96);
  box-shadow: var(--sf-shadow-soft);
}

.action-icon,
.action-btn {
  display: flex;
  align-items: center;
  justify-content: center;
  height: 68rpx;
  border-radius: 8px;
  font-size: 21rpx;
  line-height: 1.2;
}

.action-icon {
  background: rgb(var(--sf-color-mist));
  color: rgb(var(--sf-color-text-secondary));
}

.action-btn {
  color: #ffffff;
  font-weight: 700;
}

.action-btn--primary {
  background: linear-gradient(135deg, rgb(var(--sf-color-brand)) 0%, rgb(var(--sf-color-brand-light)) 100%);
}

.action-btn--buy {
  background: linear-gradient(135deg, rgb(var(--sf-color-price)) 0%, #f06b55 100%);
}
</style>
