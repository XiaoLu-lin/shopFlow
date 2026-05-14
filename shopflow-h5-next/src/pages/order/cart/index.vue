<template>
  <view class="page" role="main">
    <view class="hero-card">
      <view class="hero-head">
        <view>
          <text class="title" role="heading" aria-level="1">购物车</text>
          <text class="desc">已接回旧站勾选、编辑、删除和数量修改语义。</text>
        </view>
        <view v-if="cartItems.length" class="edit-btn" role="button" @click="editing = !editing">
          {{ editing ? '完成' : '编辑' }}
        </view>
      </view>
    </view>

    <view v-if="cartItems.length" class="cart-list">
      <view v-for="item in cartItems" :key="item.id" class="cart-card">
        <view class="cart-row">
          <view
            class="check-dot"
            :class="{ 'check-dot--active': checkedIds.includes(item.id) }"
            role="button"
            @click="toggleChecked(item.id)"
          >
            <text v-if="checkedIds.includes(item.id)" class="check-mark">✓</text>
          </view>
          <image class="goods-image" :src="item.picUrl" mode="aspectFill" />
          <view class="goods-body">
            <view class="goods-head">
              <text class="goods-name">{{ item.goodsName }}</text>
              <text class="goods-badge">{{ checkedIds.includes(item.id) ? '已选中' : '未选中' }}</text>
            </view>
            <text class="goods-spec">{{ item.specifications.join(' / ') }}</text>
            <view class="goods-foot">
              <text class="goods-price">¥ {{ item.price }}</text>
              <view v-if="editing" class="count-row">
                <view class="count-btn" role="button" @click="updateQuantity(item.id, -1)">-</view>
                <text class="count-value">{{ item.number }}</text>
                <view class="count-btn" role="button" @click="updateQuantity(item.id, 1)">+</view>
              </view>
              <text v-else class="goods-count">x {{ item.number }}</text>
            </view>
            <text v-if="editing" class="delete-btn" role="button" @click="removeItems([item.productId])">删除</text>
          </view>
        </view>
      </view>
    </view>

    <view v-else class="empty-card">
      <text class="empty-title">你的购物车还空着</text>
      <text class="empty-desc">先去商品详情里加购一些商品吧。</text>
    </view>

    <view class="footer-bar">
      <view class="footer-meta">
        <view class="footer-check" role="button" @click="toggleAll">
          <view class="check-dot" :class="{ 'check-dot--active': allChecked }">
            <text v-if="allChecked" class="check-mark">✓</text>
          </view>
          <view class="footer-copy">
            <text class="footer-label">全选</text>
            <text class="footer-count">{{ checkedIds.length }} 件</text>
          </view>
        </view>
        <view class="footer-total">
          <text class="footer-label">合计</text>
          <text class="footer-price">¥ {{ totalPrice }}</text>
        </view>
      </view>
      <view class="submit-btn" :class="{ 'submit-btn--disabled': !checkedIds.length }" role="button" @click="submitAction">
        {{ editing ? '删除所选商品' : '去结算' }}
      </view>
    </view>
  </view>
</template>

<script setup lang="ts">
import { computed, ref, watch } from 'vue'
import { fetchCartList, removeCartItems, toggleCartItems, updateCartItem } from '@/entities/cart/api'
import { calculateCartTotal, collectAllIds, collectCheckedIds, collectCheckedProductIds, isAllChecked } from '@/features/cart/cart-utils'
import { writeFlowContext } from '@/shared/compat/session-adapter'

const editing = ref(false)
const checkedIds = ref<number[]>([])
const cartItems = ref<Awaited<ReturnType<typeof fetchCartList>>['cartList']>([])

const allIds = computed(() => collectAllIds(cartItems.value))
const totalPrice = computed(() => calculateCartTotal(cartItems.value, checkedIds.value))
const checkedProductIds = computed(() => collectCheckedProductIds(cartItems.value, checkedIds.value))
const allChecked = computed(() => isAllChecked(cartItems.value, checkedIds.value))

watch(cartItems, (items) => {
  checkedIds.value = collectCheckedIds(items)
}, { immediate: true })

bootstrap()

async function bootstrap() {
  try {
    const result = await fetchCartList()
    cartItems.value = result.cartList || []
  } catch (error) {
    console.error(error)
  }
}

async function toggleChecked(itemId: number) {
  const item = cartItems.value.find((current) => current.id === itemId)
  if (!item) {
    return
  }

  const nextCheckedIds = checkedIds.value.includes(itemId)
    ? checkedIds.value.filter((id) => id !== itemId)
    : [...checkedIds.value, itemId]

  checkedIds.value = nextCheckedIds

  try {
    await toggleCartItems([item.productId], nextCheckedIds.includes(itemId))
    await bootstrap()
  } catch (error) {
    console.error(error)
  }
}

async function toggleAll() {
  const nextChecked = !allChecked.value
  checkedIds.value = nextChecked ? [...allIds.value] : []

  try {
    await toggleCartItems(cartItems.value.map((item) => item.productId), nextChecked)
    await bootstrap()
  } catch (error) {
    console.error(error)
  }
}

async function updateQuantity(itemId: number, step: 1 | -1) {
  const item = cartItems.value.find((current) => current.id === itemId)
  if (!item) {
    return
  }

  const nextNumber = Math.max(1, item.number + step)
  if (nextNumber === item.number) {
    return
  }

  try {
    await updateCartItem({
      id: item.id,
      goodsId: item.goodsId,
      productId: item.productId,
      number: nextNumber,
    })
    await bootstrap()
  } catch (error) {
    console.error(error)
  }
}

async function removeItems(productIds: number[]) {
  if (!productIds.length) {
    return
  }

  try {
    await removeCartItems(productIds)
    await bootstrap()
  } catch (error) {
    console.error(error)
  }
}

function goCheckout() {
  writeFlowContext({
    AddressId: 0,
    CartId: 0,
    CouponId: 0,
    UserCouponId: 0,
  })
  uni.navigateTo({
    url: '/pages/order/checkout/index',
  })
}

async function submitAction() {
  if (!checkedIds.value.length) {
    uni.showToast({
      title: '请先选择商品',
      icon: 'none',
    })
    return
  }

  if (editing.value) {
    await removeItems(checkedProductIds.value)
    return
  }

  goCheckout()
}
</script>

<style scoped lang="scss">
.page {
  min-height: 100vh;
  padding: 20rpx 20rpx 240rpx;
  background: linear-gradient(180deg, #ffffff 0%, #f6f8fb 100%);
}

.hero-card,
.cart-card,
.empty-card {
  border-radius: 12rpx;
  background: #ffffff;
  box-shadow: 0 10rpx 24rpx rgba(23, 32, 51, 0.06);
}

.hero-card {
  padding: 22rpx;
}

.hero-head,
.goods-head,
.goods-foot,
.footer-meta {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16rpx;
}

.title {
  display: block;
  font-size: 28rpx;
  line-height: 1.3;
  color: #172033;
}

.desc {
  display: block;
  margin-top: 8rpx;
  font-size: 22rpx;
  line-height: 1.4;
  color: #748194;
}

.edit-btn,
.goods-badge {
  padding: 10rpx 16rpx;
  border-radius: 999px;
  background: #edf5ff;
  font-size: 20rpx;
  line-height: 1.2;
  color: #1677ff;
}

.cart-list {
  display: grid;
  gap: 14rpx;
  margin-top: 16rpx;
}

.cart-card {
  padding: 16rpx;
}

.cart-row {
  display: flex;
  gap: 16rpx;
}

.check-dot {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 32rpx;
  height: 32rpx;
  margin-top: 8rpx;
  border: 2rpx solid #d5ddea;
  border-radius: 999px;
  background: #ffffff;
}

.check-dot--active {
  border-color: #1677ff;
  background: #1677ff;
}

.check-mark {
  font-size: 20rpx;
  line-height: 1;
  color: #ffffff;
}

.goods-image {
  width: 176rpx;
  height: 176rpx;
  border-radius: 10rpx;
  background: #f3f6fb;
}

.goods-body {
  min-width: 0;
  flex: 1;
}

.goods-name {
  min-width: 0;
  flex: 1;
  font-size: 24rpx;
  line-height: 1.35;
  color: #172033;
}

.goods-spec,
.goods-count {
  display: block;
  margin-top: 8rpx;
  font-size: 21rpx;
  line-height: 1.35;
  color: #748194;
}

.goods-price {
  font-size: 27rpx;
  line-height: 1.2;
  font-weight: 600;
  color: #172033;
}

.count-row {
  display: flex;
  align-items: center;
  gap: 10rpx;
}

.count-btn,
.count-value {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 56rpx;
  height: 56rpx;
  border-radius: 10rpx;
  background: #f3f6fb;
  font-size: 24rpx;
  color: #172033;
}

.delete-btn {
  display: inline-block;
  margin-top: 10rpx;
  font-size: 20rpx;
  line-height: 1.2;
  color: #ef4444;
}

.empty-card {
  margin-top: 16rpx;
  padding: 32rpx 24rpx;
  text-align: center;
}

.empty-title {
  display: block;
  font-size: 25rpx;
  line-height: 1.3;
  color: #172033;
}

.empty-desc {
  display: block;
  margin-top: 8rpx;
  font-size: 22rpx;
  line-height: 1.35;
  color: #748194;
}

.footer-bar {
  position: fixed;
  left: 20rpx;
  right: 20rpx;
  bottom: calc(112rpx + env(safe-area-inset-bottom));
  padding: 18rpx;
  border-radius: 12rpx;
  background: #ffffff;
  box-shadow: 0 10rpx 24rpx rgba(23, 32, 51, 0.06);
  z-index: 20;
}

.footer-check {
  display: flex;
  align-items: center;
  gap: 12rpx;
}

.footer-copy,
.footer-total {
  display: flex;
  flex-direction: column;
  gap: 4rpx;
}

.footer-label {
  font-size: 20rpx;
  line-height: 1.2;
  color: #748194;
}

.footer-count,
.footer-price {
  font-size: 26rpx;
  line-height: 1.2;
  color: #172033;
}

.submit-btn {
  display: flex;
  align-items: center;
  justify-content: center;
  height: 84rpx;
  margin-top: 16rpx;
  border-radius: 12rpx;
  background: #1677ff;
  font-size: 24rpx;
  color: #ffffff;
}

.submit-btn--disabled {
  opacity: 0.45;
}
</style>
