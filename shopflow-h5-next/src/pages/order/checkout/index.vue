<template>
  <view class="page">
    <view class="page-head">
      <view>
        <text class="eyebrow">ShopFlow Checkout</text>
        <text class="title">确认订单</text>
        <text class="desc">核对收货信息、优惠方案和商品明细后提交订单。</text>
      </view>
    </view>

    <view class="section-card address-card">
      <view class="section-head">
        <text class="section-title">配送地址</text>
        <text class="section-action" @click="goSelectAddress">选择地址</text>
      </view>
      <view class="address-body">
        <text class="address-name">{{ checkedAddress?.name || '未选择收货地址' }}</text>
        <text class="address-tel">{{ checkedAddress?.tel || '先去地址页补充收货信息' }}</text>
        <text class="address-copy">{{ checkedAddress?.addressDetail || '提交订单前请确认收货地址。' }}</text>
      </view>
    </view>

    <view v-if="checkedGoodsList.length" class="goods-section">
      <view v-for="item in checkedGoodsList" :key="item.id" class="goods-card">
        <image class="goods-image" :src="item.picUrl" mode="aspectFill" />
        <view class="goods-body">
          <text class="goods-name">{{ item.goodsName }}</text>
          <text class="goods-spec">{{ item.specifications.join(' / ') || '默认规格' }}</text>
          <view class="goods-bottom">
            <text class="goods-price">¥ {{ item.price }}</text>
            <text class="goods-count">x {{ item.number }}</text>
          </view>
        </view>
      </view>
    </view>

    <view class="section-card">
      <view class="section-head">
        <text class="section-title">优惠券</text>
        <text class="section-copy">{{ availableCoupons.length }} 张可用</text>
      </view>
      <scroll-view scroll-x class="coupon-scroll">
        <view class="coupon-row">
          <view
            class="coupon-chip"
            :class="{ 'coupon-chip--active': selectedCouponIndex === -1 }"
            @click="selectCoupon(-1)"
          >
            不使用
          </view>
          <view
            v-for="(coupon, index) in availableCoupons"
            :key="coupon.id"
            class="coupon-chip"
            :class="{ 'coupon-chip--active': selectedCouponIndex === index }"
            @click="selectCoupon(index)"
          >
            <text class="coupon-name">{{ coupon.name }}</text>
            <text class="coupon-discount">-¥{{ coupon.discount }}</text>
          </view>
        </view>
      </scroll-view>
    </view>

    <view class="section-card">
      <view class="section-head">
        <text class="section-title">订单备注</text>
        <text class="section-copy">{{ message.length }}/50</text>
      </view>
      <textarea
        v-model="message"
        maxlength="50"
        class="remark-input"
        placeholder="如有特殊需求，可以在这里补充说明"
      />
    </view>

    <view class="section-card amount-card">
      <view class="price-row">
        <text class="price-label">商品金额</text>
        <text class="price-value">¥ {{ checkoutData?.goodsTotalPrice || 0 }}</text>
      </view>
      <view class="price-row">
        <text class="price-label">运费</text>
        <text class="price-value">¥ {{ checkoutData?.freightPrice || 0 }}</text>
      </view>
      <view class="price-row">
        <text class="price-label">优惠券</text>
        <text class="price-value price-value--discount">- ¥ {{ checkoutData?.couponPrice || 0 }}</text>
      </view>
      <view class="price-row price-row--total">
        <text class="price-label">实付金额</text>
        <text class="pay-price">¥ {{ checkoutData?.actualPrice || 0 }}</text>
      </view>
    </view>

    <view class="footer-bar">
      <view>
        <text class="footer-label">应付合计</text>
        <text class="footer-price">¥ {{ checkoutData?.actualPrice || 0 }}</text>
      </view>
      <view
        class="submit-btn"
        :class="{ 'submit-btn--disabled': !canSubmit }"
        @click="submit"
      >
        提交订单
      </view>
    </view>
  </view>
</template>

<script setup lang="ts">
import { computed, ref } from 'vue'
import { onShow } from '@dcloudio/uni-app'
import type { CheckoutPayload, CouponOption } from '@/entities/order/api'
import { fetchCheckout, fetchSelectableCoupons, submitOrder } from '@/entities/order/api'
import { readFlowContext, writeFlowContext } from '@/shared/compat/session-adapter'

const message = ref('')
const selectedCouponIndex = ref(-1)
const checkoutData = ref<CheckoutPayload | null>(null)
const couponList = ref<CouponOption[]>([])
const flowContext = ref(readFlowContext())

const availableCoupons = computed(() => couponList.value.filter((item) => item.available))
const checkedAddress = computed(() => checkoutData.value?.checkedAddress)
const checkedGoodsList = computed(() => checkoutData.value?.checkedGoodsList || [])
const canSubmit = computed(() => Boolean(checkedAddress.value?.id) && checkedGoodsList.value.length > 0)

bootstrap()
onShow(() => {
  syncFlowContext()
  void bootstrap()
})

function syncFlowContext() {
  flowContext.value = readFlowContext()
}

async function bootstrap() {
  syncFlowContext()

  try {
    checkoutData.value = await fetchCheckout({
      cartId: flowContext.value.cartId || '0',
      addressId: flowContext.value.addressId || '0',
      couponId: flowContext.value.couponId || '0',
      userCouponId: flowContext.value.userCouponId || '0',
    })

    if ((flowContext.value.cartId || '0') !== '0') {
      const coupons = await fetchSelectableCoupons(flowContext.value.cartId || '0')
      couponList.value = coupons.list || []
      const selectedUserCouponId = flowContext.value.userCouponId || ''
      const available = couponList.value.filter((item) => item.available)
      selectedCouponIndex.value = available.findIndex((item) => String(item.id) === selectedUserCouponId)
      if (selectedCouponIndex.value < 0) {
        selectedCouponIndex.value = -1
      }
      return
    }

    couponList.value = []
    selectedCouponIndex.value = -1
  } catch (error) {
    console.error(error)
    uni.showToast({
      title: '加载结算信息失败',
      icon: 'none',
    })
  }
}

async function selectCoupon(index: number) {
  selectedCouponIndex.value = index

  if (index < 0) {
    writeFlowContext({
      CouponId: -1,
      UserCouponId: -1,
    })
    syncFlowContext()
    await bootstrap()
    return
  }

  const coupon = availableCoupons.value[index]
  if (!coupon) {
    return
  }

  writeFlowContext({
    CouponId: coupon.cid,
    UserCouponId: coupon.id,
  })
  syncFlowContext()
  await bootstrap()
}

function goSelectAddress() {
  uni.navigateTo({
    url: '/pages/user/address/index',
  })
}

async function submit() {
  if (!canSubmit.value) {
    uni.showToast({
      title: '请先设置收货地址',
      icon: 'none',
    })
    return
  }

  try {
    const order = await submitOrder({
      addressId: flowContext.value.addressId || String(checkedAddress.value?.id || ''),
      cartId: flowContext.value.cartId || '0',
      couponId: flowContext.value.couponId || '0',
      userCouponId: flowContext.value.userCouponId || '0',
      message: message.value,
    })

    writeFlowContext({
      AddressId: 0,
      CartId: 0,
      CouponId: 0,
      UserCouponId: 0,
    })
    syncFlowContext()

    uni.showToast({
      title: order.isPay ? '下单成功' : '订单已创建',
      icon: 'none',
    })

    const orderIds = order.orderIds || []
    const primaryOrderId = orderIds[0] || ''
    if (primaryOrderId) {
      const query = new URLSearchParams({
        orderId: primaryOrderId,
        orderIds: orderIds.join(','),
      }).toString()

      setTimeout(() => {
        uni.navigateTo({
          url: `/pages/order/payment/index?${query}`,
        })
      }, 180)
    }
  } catch (error) {
    console.error(error)
    uni.showToast({
      title: '提交订单失败',
      icon: 'none',
    })
  }
}
</script>

<style scoped lang="scss">
.page {
  min-height: 100vh;
  padding: 24rpx 20rpx calc(188rpx + env(safe-area-inset-bottom));
  background: rgb(var(--sf-color-page));
}

.page-head,
.section-card,
.goods-card {
  border: 2rpx solid rgb(var(--sf-color-line));
  border-radius: 16rpx;
  background: rgb(var(--sf-color-shell));
  box-shadow: var(--sf-shadow-card);
}

.page-head,
.section-card {
  padding: 24rpx;
}

.page-head {
  background: linear-gradient(135deg, rgb(var(--sf-color-brand-soft)) 0%, rgb(var(--sf-color-shell)) 72%);
}

.eyebrow {
  display: block;
  font-size: 18rpx;
  line-height: 1.2;
  letter-spacing: 2rpx;
  text-transform: uppercase;
  color: rgb(var(--sf-color-brand-deep));
}

.title {
  display: block;
  margin-top: 10rpx;
  font-size: 34rpx;
  line-height: 1.24;
  font-weight: 600;
  color: rgb(var(--sf-color-ink));
}

.desc,
.section-copy,
.address-tel,
.address-copy,
.goods-spec,
.goods-count,
.price-label,
.price-value,
.footer-label {
  display: block;
  margin-top: 8rpx;
  font-size: 21rpx;
  line-height: 1.45;
  color: rgb(var(--sf-color-text-secondary));
}

.desc {
  margin-top: 10rpx;
  font-size: 22rpx;
}

.section-card,
.goods-section {
  margin-top: 18rpx;
}

.section-head,
.goods-bottom,
.price-row,
.footer-bar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16rpx;
}

.section-title,
.address-name,
.goods-name {
  display: block;
  font-size: 26rpx;
  line-height: 1.35;
  color: rgb(var(--sf-color-ink));
}

.section-action {
  flex-shrink: 0;
  padding: 10rpx 18rpx;
  border-radius: 999px;
  background: rgb(var(--sf-color-brand-soft));
  font-size: 20rpx;
  line-height: 1.2;
  color: rgb(var(--sf-color-brand-deep));
}

.address-body {
  margin-top: 18rpx;
}

.address-copy {
  color: rgb(var(--sf-color-ink));
}

.goods-section {
  display: grid;
  gap: 14rpx;
}

.goods-card {
  display: flex;
  gap: 16rpx;
  padding: 18rpx;
}

.goods-image {
  width: 148rpx;
  height: 148rpx;
  flex-shrink: 0;
  border-radius: var(--sf-radius-image);
  background: rgb(var(--sf-color-mist));
}

.goods-body {
  min-width: 0;
  flex: 1;
}

.goods-bottom {
  margin-top: 16rpx;
}

.goods-price,
.pay-price,
.footer-price {
  font-size: 30rpx;
  line-height: 1.2;
  font-weight: 600;
  color: rgb(var(--sf-color-price));
}

.coupon-scroll {
  margin-top: 16rpx;
  white-space: nowrap;
}

.coupon-row {
  display: inline-flex;
  gap: 12rpx;
}

.coupon-chip {
  display: inline-flex;
  flex-direction: column;
  gap: 6rpx;
  padding: 14rpx 18rpx;
  border: 2rpx solid rgb(var(--sf-color-line));
  border-radius: 999px;
  background: rgb(var(--sf-color-shell));
  color: rgb(var(--sf-color-ink));
}

.coupon-chip--active {
  border-color: rgb(var(--sf-color-brand));
  background: rgb(var(--sf-color-brand-soft));
}

.coupon-name,
.coupon-discount {
  font-size: 20rpx;
  line-height: 1.2;
}

.coupon-discount {
  color: rgb(var(--sf-color-price));
}

.remark-input {
  width: 100%;
  min-height: 168rpx;
  margin-top: 16rpx;
  padding: 18rpx;
  border-radius: 16rpx;
  background: rgb(var(--sf-color-mist));
  font-size: 22rpx;
  line-height: 1.5;
  color: rgb(var(--sf-color-ink));
}

.amount-card {
  padding-bottom: 8rpx;
}

.price-row {
  margin-top: 12rpx;
}

.price-row:first-child {
  margin-top: 0;
}

.price-value {
  margin-top: 0;
  color: rgb(var(--sf-color-ink));
}

.price-value--discount {
  color: rgb(var(--sf-color-price));
}

.price-row--total {
  margin-top: 22rpx;
  padding-top: 20rpx;
  border-top: 2rpx solid rgb(var(--sf-color-divider));
}

.footer-bar {
  position: fixed;
  right: 20rpx;
  bottom: 20rpx;
  left: 20rpx;
  z-index: 40;
  padding: 18rpx 18rpx calc(18rpx + env(safe-area-inset-bottom));
  border: 2rpx solid rgb(var(--sf-color-line));
  border-radius: 20rpx;
  background: rgba(255, 255, 255, 0.96);
  box-shadow: var(--sf-shadow-soft);
  backdrop-filter: blur(12px);
}

.footer-price {
  display: block;
  margin-top: 6rpx;
}

.submit-btn {
  display: flex;
  flex-shrink: 0;
  align-items: center;
  justify-content: center;
  min-width: 220rpx;
  height: 86rpx;
  padding: 0 26rpx;
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
