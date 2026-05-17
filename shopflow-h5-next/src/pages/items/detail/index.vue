<template>
  <view class="page">
    <view class="detail-shell">
      <scroll-view scroll-y class="detail-scroll">
        <view class="detail-scroll-inner">
          <swiper
            v-if="gallery.length"
            class="detail-swiper"
            indicator-dots
            circular
            autoplay
            :interval="3200"
            indicator-color="rgba(74,111,165,0.22)"
            indicator-active-color="#4A6FA5"
          >
            <swiper-item v-for="(item, index) in gallery" :key="`${item}-${index}`">
              <image class="detail-swiper-image" :src="item" mode="aspectFill" />
            </swiper-item>
          </swiper>
          <view v-else class="detail-swiper-placeholder">
            <text>商品图片加载中</text>
          </view>

          <view class="detail-hero">
            <text class="detail-hero-eyebrow">ShopFlow Detail</text>
            <text class="detail-hero-title">{{ detail?.info.name || '商品详情' }}</text>
            <text class="detail-hero-desc">
              {{ resolveGoodsBrief(detail?.info.brief, '把价格、规格选择和购买动作收成更顺手的单页节奏。') }}
            </text>
          </view>

          <view class="detail-card detail-card--summary">
            <view class="detail-price-row">
              <text class="detail-price">¥{{ matchedPrice }}</text>
              <text v-if="detail?.info.counterPrice" class="detail-origin-price">¥{{ detail.info.counterPrice }}</text>
              <text v-if="savingsCopy" class="detail-soft-tag">{{ savingsCopy }}</text>
              <text v-else-if="discountLabel" class="detail-soft-tag">{{ discountLabel }}</text>
            </view>
            <text class="detail-selection-copy">{{ selectionCopy }}</text>
          </view>

          <view class="detail-card">
            <view class="detail-row">
              <text class="detail-row-label">优惠说明</text>
              <text class="detail-row-value">实际可用优惠以下单结算页为准</text>
            </view>
            <view class="detail-service-list">
              <text>正品保障</text>
              <text>7天无理由</text>
              <text>极速退款</text>
            </view>
          </view>

          <view v-if="specList.length" class="detail-card">
            <text class="detail-card-title">选择规格</text>
            <view v-for="(item, groupIndex) in specList" :key="item.name" class="detail-spec-group">
              <text class="detail-spec-name">{{ item.name }}</text>
              <view class="detail-spec-values">
                <view
                  v-for="value in item.valueList"
                  :key="value.id"
                  class="detail-spec-chip"
                  :class="{ 'detail-spec-chip--active': selectedValueIds.includes(value.id) }"
                  @click="selectSku(groupIndex, value.id)"
                >
                  {{ value.value }}
                </view>
              </view>
            </view>
          </view>

          <view class="detail-card">
            <view class="detail-row detail-row--quantity">
              <text class="detail-card-title">购买数量</text>
              <view class="detail-quantity">
                <view class="detail-quantity-btn" @click="changeQuantity(-1)">-</view>
                <text class="detail-quantity-value">{{ quantity }}</text>
                <view class="detail-quantity-btn" @click="changeQuantity(1)">+</view>
              </view>
            </view>
          </view>

          <view class="detail-card">
            <text class="detail-card-title">商品详情</text>
            <rich-text v-if="hasDetailContent" class="detail-rich-text" :nodes="detail?.info.detail || ''" />
            <view v-else class="detail-empty">
              <text>详情信息整理中</text>
            </view>
          </view>

          <view v-if="attributeList.length" class="detail-card">
            <text class="detail-card-title">商品属性</text>
            <view class="detail-attr-list">
              <view v-for="item in attributeList" :key="`${item.attribute}-${item.value}`" class="detail-attr-row">
                <text class="detail-attr-label">{{ item.attribute }}</text>
                <text class="detail-attr-value">{{ item.value }}</text>
              </view>
            </view>
          </view>
        </view>
      </scroll-view>

      <view class="detail-bottom-bar">
        <view class="detail-bottom-tools">
          <view class="detail-tool" @click="contactService">客服</view>
          <view class="detail-tool" :class="{ 'detail-tool--active': collected }" @click="toggleCollectState">
            收藏
          </view>
          <view class="detail-tool" @click="goCart">购物车</view>
        </view>
        <view class="detail-bottom-actions">
          <view class="detail-btn detail-btn--ghost" @click="addToCart">加入购物车</view>
          <view class="detail-btn detail-btn--primary" @click="buyNow">立即购买</view>
        </view>
      </view>
    </view>
  </view>
</template>

<script setup lang="ts">
import { computed, ref } from 'vue'
import { addCartItem, fastAddCartItem } from '@/entities/cart/api'
import { fetchGoodsDetail } from '@/entities/goods/api'
import { toggleCollect } from '@/entities/user/api'
import type { GoodsDetailPayload } from '@/entities/goods/api'
import {
  formatDiscountLabel,
  formatSavingsCopy,
  hasRenderableDetail,
  resolveDetailSelectionCopy,
} from '@/features/goods/detail-display-utils'
import { resolveGoodsBrief } from '@/features/goods/browse-display-utils'
import {
  buildCartPayload,
  clampSkuQuantity,
  hasCompletedSkuSelection,
  resolveMatchedProduct,
  resolveSelectedValueNames,
} from '@/features/goods/sku-utils'
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
const selectedValueNames = computed(() => (
  detail.value ? resolveSelectedValueNames(detail.value.specificationList, selectedValueIds.value) : []
))
const matchedPrice = computed(() => matchedProduct.value?.price ?? detail.value?.info.retailPrice ?? '--')
const matchedStock = computed(() => matchedProduct.value?.number ?? 0)
const discountLabel = computed(() => formatDiscountLabel(matchedPrice.value, detail.value?.info.counterPrice))
const savingsCopy = computed(() => formatSavingsCopy(matchedPrice.value, detail.value?.info.counterPrice))
const selectionCopy = computed(() => resolveDetailSelectionCopy({
  completedSelection: completedSelection.value,
  stock: matchedStock.value,
  selectedValues: selectedValueNames.value,
}))
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
  uni.switchTab({
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
  display: flex;
  flex-direction: column;
  min-height: 0;
  height: calc(100vh - var(--window-top, 0px) - var(--window-bottom, 0px));
  background:
    radial-gradient(circle at top left, rgba(109, 140, 184, 0.14), rgba(109, 140, 184, 0) 24%),
    linear-gradient(180deg, rgba(244, 247, 251, 0.98) 0%, rgb(var(--sf-color-page)) 100%);
  overflow: hidden;
}

.detail-shell {
  display: flex;
  flex: 1;
  min-height: 0;
  flex-direction: column;
  padding: 12rpx 18rpx 12rpx;
}

.detail-hero,
.detail-card,
.detail-bottom-bar {
  border: 2rpx solid rgb(var(--sf-color-line));
  border-radius: 8px;
  background: rgba(255, 255, 255, 0.98);
  box-shadow: var(--sf-shadow-soft);
}

.detail-scroll {
  flex: 1;
  min-height: 0;
}

.detail-scroll-inner {
  display: grid;
  gap: 12rpx;
  padding-bottom: 12rpx;
}

.detail-swiper,
.detail-swiper-placeholder {
  height: 560rpx;
  overflow: hidden;
  border-radius: 8px;
  background: rgb(var(--sf-color-mist));
}

.detail-swiper-image {
  width: 100%;
  height: 100%;
}

.detail-swiper-placeholder {
  display: flex;
  align-items: center;
  justify-content: center;
  color: rgb(var(--sf-color-text-secondary));
  font-size: 22rpx;
}

.detail-hero,
.detail-card {
  padding: 18rpx;
}

.detail-hero {
  background: linear-gradient(145deg, rgb(var(--sf-color-brand)) 0%, rgb(var(--sf-color-brand-light)) 100%);
  box-shadow: var(--sf-shadow-brand);
}

.detail-hero-eyebrow,
.detail-hero-title,
.detail-hero-desc {
  display: block;
  color: #ffffff;
}

.detail-hero-eyebrow {
  font-size: 18rpx;
  letter-spacing: 0.08em;
  text-transform: uppercase;
  opacity: 0.82;
}

.detail-hero-title {
  margin-top: 4rpx;
  font-size: 30rpx;
  line-height: 1.3;
  font-weight: 700;
}

.detail-hero-desc {
  margin-top: 8rpx;
  font-size: 22rpx;
  line-height: 1.45;
  opacity: 0.92;
}

.detail-card--summary {
  background:
    radial-gradient(circle at top right, rgba(209, 223, 241, 0.72) 0%, rgba(209, 223, 241, 0) 38%),
    linear-gradient(145deg, rgba(251, 252, 254, 0.98) 0%, rgba(238, 244, 251, 0.98) 100%);
}

.detail-price-row,
.detail-row,
.detail-bottom-bar,
.detail-bottom-tools,
.detail-bottom-actions,
.detail-quantity,
.detail-attr-row {
  display: flex;
  align-items: center;
}

.detail-price-row {
  flex-wrap: wrap;
  gap: 10rpx;
}

.detail-price {
  color: rgb(var(--sf-color-price));
  font-size: 48rpx;
  line-height: 1;
  font-weight: 800;
}

.detail-origin-price {
  color: rgb(var(--sf-color-text-hint));
  font-size: 22rpx;
  line-height: 1.2;
  text-decoration: line-through;
}

.detail-soft-tag {
  padding: 4rpx 10rpx;
  border-radius: 999px;
  background: rgb(var(--sf-color-price-soft));
  color: rgb(var(--sf-color-price));
  font-size: 18rpx;
  line-height: 1.2;
  font-weight: 700;
}

.detail-selection-copy {
  display: block;
  margin-top: 12rpx;
  color: rgb(var(--sf-color-brand));
  font-size: 22rpx;
  line-height: 1.45;
  font-weight: 600;
}

.detail-row {
  justify-content: space-between;
  gap: 16rpx;
}

.detail-row-label,
.detail-card-title,
.detail-spec-name,
.detail-attr-label {
  color: rgb(var(--sf-color-ink));
  font-size: 24rpx;
  line-height: 1.35;
  font-weight: 700;
}

.detail-row-value,
.detail-service-list,
.detail-attr-value {
  color: rgb(var(--sf-color-text-secondary));
  font-size: 21rpx;
  line-height: 1.45;
}

.detail-service-list {
  display: flex;
  flex-wrap: wrap;
  gap: 10rpx 16rpx;
  margin-top: 12rpx;
}

.detail-service-list text::before {
  display: inline-block;
  width: 6rpx;
  height: 6rpx;
  margin-right: 8rpx;
  border-radius: 999px;
  background: rgb(var(--sf-color-brand));
  content: '';
  vertical-align: middle;
}

.detail-spec-group {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 24rpx;
  margin-top: 14rpx;
}

.detail-spec-values {
  display: flex;
  flex-wrap: wrap;
  justify-content: flex-end;
  gap: 10rpx;
  flex: 1;
}

.detail-spec-chip {
  padding: 10rpx 16rpx;
  border-radius: 8px;
  background: rgb(var(--sf-color-mist));
  color: rgb(var(--sf-color-text-secondary));
  font-size: 21rpx;
  line-height: 1.2;
}

.detail-spec-chip--active {
  background: linear-gradient(145deg, rgb(var(--sf-color-brand-soft)) 0%, rgba(223, 231, 241, 0.98) 100%);
  color: rgb(var(--sf-color-brand));
  font-weight: 700;
}

.detail-row--quantity {
  justify-content: space-between;
}

.detail-quantity {
  gap: 10rpx;
}

.detail-quantity-btn,
.detail-quantity-value {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 56rpx;
  height: 56rpx;
  border-radius: 8px;
  background: rgb(var(--sf-color-mist));
  color: rgb(var(--sf-color-ink));
  font-size: 24rpx;
  font-weight: 700;
}

.detail-quantity-value {
  width: 72rpx;
  background: rgba(255, 255, 255, 0.98);
  border: 2rpx solid rgb(var(--sf-color-line));
}

.detail-rich-text {
  display: block;
  margin-top: 12rpx;
  color: rgb(var(--sf-color-text-secondary));
  font-size: 22rpx;
  line-height: 1.6;
}

.detail-empty {
  margin-top: 12rpx;
  padding: 22rpx 18rpx;
  border-radius: 8px;
  background: linear-gradient(180deg, rgba(247, 250, 253, 0.98) 0%, rgba(255, 255, 255, 0.98) 100%);
  color: rgb(var(--sf-color-text-secondary));
  font-size: 21rpx;
  text-align: center;
}

.detail-attr-list {
  display: grid;
  gap: 12rpx;
  margin-top: 12rpx;
}

.detail-attr-row {
  justify-content: space-between;
  gap: 16rpx;
  padding: 12rpx 14rpx;
  border-radius: 8px;
  background: linear-gradient(180deg, rgba(255, 255, 255, 0.98) 0%, rgba(247, 250, 253, 0.98) 100%);
}

.detail-attr-label {
  font-size: 21rpx;
  font-weight: 600;
}

.detail-attr-value {
  text-align: right;
}

.detail-bottom-bar {
  justify-content: space-between;
  gap: 12rpx;
  padding: 12rpx;
}

.detail-bottom-tools {
  gap: 8rpx;
}

.detail-tool {
  display: flex;
  align-items: center;
  justify-content: center;
  min-width: 74rpx;
  height: 72rpx;
  padding: 0 10rpx;
  border-radius: 8px;
  background: rgb(var(--sf-color-mist));
  color: rgb(var(--sf-color-text-secondary));
  font-size: 20rpx;
  font-weight: 600;
}

.detail-tool--active {
  background: rgb(var(--sf-color-brand-soft));
  color: rgb(var(--sf-color-brand));
}

.detail-bottom-actions {
  min-width: 0;
  flex: 1;
  gap: 8rpx;
}

.detail-btn {
  display: flex;
  align-items: center;
  justify-content: center;
  height: 72rpx;
  border-radius: 8px;
  font-size: 22rpx;
  font-weight: 700;
}

.detail-btn--ghost,
.detail-btn--primary {
  flex: 1;
}

.detail-btn--ghost {
  border: 2rpx solid rgb(var(--sf-color-line));
  background: rgba(255, 255, 255, 0.98);
  color: rgb(var(--sf-color-brand));
}

.detail-btn--primary {
  background: linear-gradient(145deg, rgb(var(--sf-color-brand)) 0%, rgb(var(--sf-color-brand-light)) 100%);
  color: #ffffff;
  box-shadow: var(--sf-shadow-brand);
}
</style>
