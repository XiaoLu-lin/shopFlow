<template>
  <view class="page">
    <scroll-view scroll-y class="page-scroll">
      <view class="page-scroll-inner">
        <view v-if="cartItems.length" class="cart-toolbar">
          <view class="toolbar-copy">
            <text class="toolbar-title">{{ toolbarTitle }}</text>
            <text class="toolbar-desc">{{ toolbarDesc }}</text>
          </view>
          <view class="toolbar-action" @click="editing = !editing">
            {{ editing ? '完成' : '编辑' }}
          </view>
        </view>

        <view v-if="cartItems.length" class="cart-list">
          <view v-for="item in cartItems" :key="item.id" class="cart-card">
            <view
              class="check-dot"
              :class="{ 'check-dot--active': checkedIds.includes(item.id) }"
              @click="toggleChecked(item.id)"
            >
              <text v-if="checkedIds.includes(item.id)" class="check-mark">✓</text>
            </view>
            <image class="goods-image" :src="item.picUrl" mode="aspectFill" />
            <view class="goods-body">
              <view class="goods-top">
                <text class="goods-name">{{ item.goodsName }}</text>
                <text class="goods-state">{{ checkedIds.includes(item.id) ? '已选' : '可选' }}</text>
              </view>
              <text class="goods-spec">{{ item.specifications.join(' / ') || '默认规格' }}</text>
              <text class="goods-copy">{{ editing ? '调整数量或移除商品' : '确认后可一起进入结算页' }}</text>
              <view class="goods-bottom">
                <view class="price-block">
                  <text class="goods-price">¥ {{ item.price }}</text>
                  <text v-if="!editing" class="goods-count">x {{ item.number }}</text>
                </view>
                <view v-if="editing" class="count-row">
                  <view class="count-btn" @click="updateQuantity(item.id, -1)">-</view>
                  <text class="count-value">{{ item.number }}</text>
                  <view class="count-btn" @click="updateQuantity(item.id, 1)">+</view>
                </view>
              </view>
              <text v-if="editing" class="delete-link" @click="removeItems([item.productId])">删除商品</text>
            </view>
          </view>
        </view>

        <view v-else class="empty-card">
          <text class="empty-title">购物车还没有商品</text>
          <text class="empty-desc">去商品详情挑几件喜欢的商品，再回来一起结算。</text>
        </view>
      </view>
    </scroll-view>

    <view class="footer-bar">
      <view class="footer-main">
        <view class="footer-check" @click="toggleAll">
          <view class="check-dot" :class="{ 'check-dot--active': allChecked }">
            <text v-if="allChecked" class="check-mark">✓</text>
          </view>
          <view>
            <text class="footer-label">全选</text>
            <text class="footer-copy">{{ selectionCopy }}</text>
          </view>
        </view>
        <view class="footer-total">
          <text class="footer-label">合计</text>
          <text class="footer-price">¥ {{ totalPrice }}</text>
        </view>
      </view>
      <view
        class="submit-btn"
        :class="{ 'submit-btn--disabled': submitMeta.disabled }"
        @click="submitAction"
      >
        {{ submitMeta.label }}
      </view>
    </view>
  </view>
</template>

<script setup lang="ts">
import { onShow } from '@dcloudio/uni-app'
import { computed, ref, watch } from 'vue'
import type { CartItem } from '@/entities/cart/api'
import { fetchCartList, removeCartItems, toggleCartItems, updateCartItem } from '@/entities/cart/api'
import { resolveCartSelectionCopy, resolveCartSubmitMeta } from '@/features/cart/cart-display-utils'
import { calculateCartTotal, collectAllIds, collectCheckedIds, collectCheckedProductIds, isAllChecked } from '@/features/cart/cart-utils'
import { writeFlowContext } from '@/shared/compat/session-adapter'

const editing = ref(false)
const checkedIds = ref<number[]>([])
const cartItems = ref<CartItem[]>([])

const allIds = computed(() => collectAllIds(cartItems.value))
const checkedCount = computed(() => checkedIds.value.length)
const totalPrice = computed(() => calculateCartTotal(cartItems.value, checkedIds.value))
const checkedProductIds = computed(() => collectCheckedProductIds(cartItems.value, checkedIds.value))
const allChecked = computed(() => isAllChecked(cartItems.value, checkedIds.value))
const selectionCopy = computed(() => resolveCartSelectionCopy(checkedCount.value))
const toolbarTitle = computed(() => `共 ${cartItems.value.length} 件商品`)
const toolbarDesc = computed(() => {
  if (editing.value) {
    return '勾选后可批量删除，数量也可以直接调整。'
  }

  if (checkedCount.value > 0) {
    return `已选 ${checkedCount.value} 件，合计 ¥ ${totalPrice.value}`
  }

  return '先勾选商品，再一起进入结算页。'
})
const submitMeta = computed(() =>
  resolveCartSubmitMeta({
    editing: editing.value,
    checkedCount: checkedCount.value,
  }),
)

watch(cartItems, (items) => {
  checkedIds.value = collectCheckedIds(items)
}, { immediate: true })

bootstrap()
onShow(() => {
  void bootstrap()
})

async function bootstrap() {
  try {
    const result = await fetchCartList()
    cartItems.value = result.cartList || []
  } catch (error) {
    console.error(error)
    uni.showToast({
      title: '购物车加载失败',
      icon: 'none',
    })
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
    uni.showToast({
      title: '更新勾选状态失败',
      icon: 'none',
    })
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
    uni.showToast({
      title: '更新勾选状态失败',
      icon: 'none',
    })
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
    uni.showToast({
      title: '更新数量失败',
      icon: 'none',
    })
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
    uni.showToast({
      title: '删除商品失败',
      icon: 'none',
    })
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
  if (submitMeta.value.disabled) {
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
  display: flex;
  flex-direction: column;
  min-height: 0;
  height: calc(100vh - var(--window-top, 0px) - var(--window-bottom, 0px));
  background: rgb(var(--sf-color-page));
  overflow: hidden;
}

.page-scroll {
  flex: 1;
  min-height: 0;
}

.page-scroll-inner {
  padding: 18rpx 20rpx 24rpx;
}

.cart-toolbar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16rpx;
  margin-bottom: 16rpx;
  padding: 8rpx 4rpx 2rpx;
}

.toolbar-copy {
  min-width: 0;
  flex: 1;
}

.toolbar-title,
.toolbar-desc {
  display: block;
}

.toolbar-title {
  font-size: 24rpx;
  line-height: 1.3;
  font-weight: 600;
  color: rgb(var(--sf-color-ink));
}

.toolbar-desc {
  margin-top: 6rpx;
  font-size: 20rpx;
  line-height: 1.38;
  color: rgb(var(--sf-color-text-secondary));
}

.toolbar-action {
  flex-shrink: 0;
  padding: 10rpx 18rpx;
  border: 2rpx solid rgb(var(--sf-color-line));
  border-radius: 999px;
  background: rgb(var(--sf-color-shell));
  font-size: 22rpx;
  line-height: 1.2;
  color: rgb(var(--sf-color-brand));
  box-shadow: var(--sf-shadow-card);
}

.cart-list {
  display: grid;
  gap: 16rpx;
}

.cart-card,
.empty-card {
  display: flex;
  gap: 16rpx;
  padding: 20rpx;
  border: 2rpx solid rgb(var(--sf-color-line));
  border-radius: 16rpx;
  background: rgb(var(--sf-color-shell));
  box-shadow: var(--sf-shadow-card);
}

.check-dot {
  display: flex;
  flex-shrink: 0;
  align-items: center;
  justify-content: center;
  width: 40rpx;
  height: 40rpx;
  margin-top: 16rpx;
  border: 2rpx solid rgb(var(--sf-color-line));
  border-radius: 999px;
  background: rgb(var(--sf-color-shell));
  transition: transform 150ms ease, background-color 150ms ease, border-color 150ms ease;
}

.check-dot--active {
  border-color: rgb(var(--sf-color-brand));
  background: rgb(var(--sf-color-brand));
}

.check-mark {
  font-size: 22rpx;
  line-height: 1;
  color: rgb(var(--sf-color-shell));
}

.goods-image {
  width: 152rpx;
  height: 152rpx;
  flex-shrink: 0;
  border-radius: var(--sf-radius-image);
  background: rgb(var(--sf-color-mist));
}

.goods-body {
  min-width: 0;
  flex: 1;
}

.goods-top,
.goods-bottom,
.price-block,
.footer-main,
.footer-check {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 14rpx;
}

.goods-name {
  min-width: 0;
  flex: 1;
  font-size: 26rpx;
  line-height: 1.38;
  color: rgb(var(--sf-color-ink));
}

.goods-state {
  flex-shrink: 0;
  padding: 8rpx 14rpx;
  border-radius: 999px;
  background: rgb(var(--sf-color-brand-soft));
  font-size: 19rpx;
  line-height: 1.2;
  color: rgb(var(--sf-color-brand-deep));
}

.goods-spec,
.goods-copy,
.goods-count,
.footer-label,
.footer-copy,
.empty-desc {
  display: block;
  margin-top: 10rpx;
  font-size: 21rpx;
  line-height: 1.42;
  color: rgb(var(--sf-color-text-secondary));
}

.goods-copy {
  color: rgb(var(--sf-color-text-hint));
}

.goods-bottom {
  margin-top: 18rpx;
}

.goods-price,
.footer-price,
.empty-title {
  font-size: 30rpx;
  line-height: 1.2;
  font-weight: 600;
  color: rgb(var(--sf-color-price));
}

.goods-count {
  margin-top: 0;
}

.count-row {
  display: inline-flex;
  align-items: center;
  gap: 12rpx;
  padding: 8rpx 10rpx;
  border-radius: 999px;
  background: rgb(var(--sf-color-mist));
}

.count-btn {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 42rpx;
  height: 42rpx;
  border-radius: 999px;
  background: rgb(var(--sf-color-shell));
  font-size: 24rpx;
  line-height: 1;
  color: rgb(var(--sf-color-ink));
}

.count-value {
  min-width: 32rpx;
  font-size: 22rpx;
  line-height: 1.2;
  text-align: center;
  color: rgb(var(--sf-color-ink));
}

.delete-link {
  display: inline-flex;
  margin-top: 16rpx;
  font-size: 21rpx;
  line-height: 1.3;
  color: rgb(var(--sf-color-price));
}

.empty-card {
  display: block;
  padding: 48rpx 28rpx;
  text-align: center;
}

.footer-bar {
  flex-shrink: 0;
  margin: 0 20rpx 20rpx;
  padding: 18rpx;
  border: 2rpx solid rgb(var(--sf-color-line));
  border-radius: 20rpx;
  background: rgba(255, 255, 255, 0.96);
  box-shadow: var(--sf-shadow-soft);
  backdrop-filter: blur(12px);
}

.footer-main {
  align-items: flex-end;
}

.footer-copy {
  margin-top: 6rpx;
}

.footer-total {
  text-align: right;
}

.footer-price {
  display: block;
  margin-top: 6rpx;
}

.submit-btn {
  display: flex;
  align-items: center;
  justify-content: center;
  height: 88rpx;
  margin-top: 18rpx;
  border-radius: 999px;
  background: linear-gradient(135deg, rgb(var(--sf-color-brand)) 0%, rgb(var(--sf-color-brand-light)) 100%);
  font-size: 24rpx;
  font-weight: 600;
  color: rgb(var(--sf-color-shell));
}

.submit-btn--disabled {
  background: rgb(var(--sf-color-line));
  color: rgb(var(--sf-color-text-secondary));
}
</style>
