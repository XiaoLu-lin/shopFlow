<template>
  <view class="page">
    <scroll-view scroll-y class="page-scroll">
      <view class="page-scroll-inner">
        <view class="hero-card">
          <text class="hero-eyebrow">ShopFlow</text>
          <text class="hero-title">确认订单</text>
        </view>

        <view class="field-card address-card">
          <view class="section-head">
            <text class="section-title">配送地址</text>
            <text class="section-action" @click="goSelectAddress">选择地址</text>
          </view>
          <text class="section-copy section-copy--strong">{{ checkedAddress?.name || '未选择收货地址' }}</text>
          <text class="section-copy">{{ checkedAddress?.tel || '先去地址页补充收货信息' }}</text>
          <text class="section-copy">{{ checkedAddress?.addressDetail || '提交订单前请确认收货地址。' }}</text>
        </view>

        <view v-if="checkedGoodsList.length" class="goods-list">
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

        <view class="field-card">
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
                <text class="coupon-value">-¥{{ coupon.discount }}</text>
              </view>
            </view>
          </scroll-view>
        </view>

        <view class="field-card">
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

        <view class="field-card">
          <view class="info-row">
            <text class="info-label">商品金额</text>
            <text class="info-value">¥ {{ checkoutData?.goodsTotalPrice || 0 }}</text>
          </view>
          <view class="info-row">
            <text class="info-label">运费</text>
            <text class="info-value">¥ {{ checkoutData?.freightPrice || 0 }}</text>
          </view>
          <view class="info-row">
            <text class="info-label">优惠券</text>
            <text class="info-value">- ¥ {{ checkoutData?.couponPrice || 0 }}</text>
          </view>
          <view class="info-row info-row--strong">
            <text class="info-label">实付金额</text>
            <text class="pay-price">¥ {{ checkoutData?.actualPrice || 0 }}</text>
          </view>
        </view>
      </view>
    </scroll-view>

    <view class="footer-bar">
      <view class="footer-main">
        <text class="footer-label">应付合计</text>
        <text class="footer-price">¥ {{ checkoutData?.actualPrice || 0 }}</text>
      </view>
      <view class="submit-btn" :class="{ 'submit-btn--disabled': !canSubmit }" @click="submit">
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
  display: flex;
  flex-direction: column;
  min-height: 0;
  height: calc(100vh - var(--window-top, 0px) - var(--window-bottom, 0px));
  background:
    radial-gradient(circle at top left, rgba(109, 140, 184, 0.14), rgba(109, 140, 184, 0) 24%),
    linear-gradient(180deg, rgba(244, 247, 251, 0.96) 0%, rgb(var(--sf-color-page)) 100%);
  overflow: hidden;
}

.page-scroll {
  flex: 1;
  min-height: 0;
}

.page-scroll-inner {
  padding: 18rpx 20rpx calc(176rpx + env(safe-area-inset-bottom));
}

.hero-card,
.field-card,
.goods-card,
.footer-bar,
.coupon-chip {
  border: 2rpx solid rgb(var(--sf-color-line));
  border-radius: var(--sf-radius-card);
  background: rgb(var(--sf-color-shell));
  box-shadow: var(--sf-shadow-soft);
}

.hero-card,
.field-card {
  padding: 18rpx;
}

.hero-card {
  background:
    radial-gradient(circle at top right, rgba(209, 223, 241, 0.72) 0%, rgba(209, 223, 241, 0) 38%),
    linear-gradient(145deg, rgba(251, 252, 254, 0.98) 0%, rgba(238, 244, 251, 0.98) 100%);
}

.hero-eyebrow {
  display: block;
  font-size: 18rpx;
  line-height: 1.2;
  letter-spacing: 0.12em;
  text-transform: uppercase;
  color: rgb(var(--sf-color-brand));
}

.hero-title,
.section-title,
.goods-name {
  display: block;
  color: rgb(var(--sf-color-ink));
}

.hero-title {
  margin-top: 6rpx;
  font-size: 30rpx;
  line-height: 1.3;
  font-weight: 700;
}

.field-card,
.goods-list {
  margin-top: 14rpx;
}

.section-head,
.goods-bottom,
.info-row,
.footer-bar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16rpx;
}

.section-title {
  font-size: 24rpx;
  line-height: 1.35;
  font-weight: 600;
}

.section-copy,
.info-label,
.info-value,
.goods-spec,
.goods-count,
.footer-label,
.coupon-name,
.coupon-value {
  display: block;
  color: rgb(var(--sf-color-text-secondary));
}

.section-copy,
.goods-spec,
.info-label,
.info-value {
  margin-top: 6rpx;
  font-size: 20rpx;
  line-height: 1.45;
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

.section-copy--strong {
  color: rgb(var(--sf-color-ink));
}

.goods-list {
  display: grid;
  gap: 12rpx;
}

.goods-card {
  display: flex;
  align-items: flex-start;
  gap: 16rpx;
  padding: 14rpx;
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

.goods-name {
  display: -webkit-box;
  overflow: hidden;
  font-size: 23rpx;
  line-height: 1.4;
  font-weight: 600;
  -webkit-box-orient: vertical;
  -webkit-line-clamp: 2;
}

.goods-bottom {
  margin-top: 12rpx;
}

.goods-price,
.pay-price,
.footer-price {
  font-size: 28rpx;
  line-height: 1.2;
  font-weight: 700;
  color: rgb(var(--sf-color-price));
}

.coupon-scroll {
  margin-top: 14rpx;
  white-space: nowrap;
}

.coupon-row {
  display: inline-flex;
  gap: 10rpx;
}

.coupon-chip {
  display: flex;
  flex-direction: column;
  justify-content: center;
  min-width: 148rpx;
  padding: 12rpx 16rpx;
}

.coupon-chip--active {
  background: linear-gradient(145deg, rgba(237, 243, 248, 0.98) 0%, rgba(223, 231, 241, 0.98) 100%);
  border-color: rgba(var(--sf-color-brand), 0.18);
}

.coupon-name {
  font-size: 20rpx;
  line-height: 1.35;
  color: rgb(var(--sf-color-ink));
}

.coupon-value {
  margin-top: 6rpx;
  font-size: 19rpx;
  line-height: 1.3;
}

.remark-input {
  width: 100%;
  min-height: 104rpx;
  margin-top: 14rpx;
  padding: 14rpx;
  border-radius: var(--sf-radius-card);
  background: linear-gradient(180deg, rgba(247, 250, 253, 0.98) 0%, rgba(255, 255, 255, 0.98) 100%);
  font-size: 21rpx;
  line-height: 1.5;
  color: rgb(var(--sf-color-ink));
}

.info-row {
  align-items: flex-start;
}

.info-row + .info-row {
  margin-top: 14rpx;
}

.info-label {
  min-width: 120rpx;
  margin-top: 0;
}

.info-value {
  min-width: 0;
  flex: 1;
  margin-top: 0;
  text-align: right;
}

.info-row--strong {
  margin-top: 18rpx;
  padding-top: 18rpx;
  border-top: 2rpx solid rgb(var(--sf-color-divider));
}

.footer-bar {
  margin: 0 20rpx 20rpx;
  padding: 16rpx 18rpx calc(16rpx + env(safe-area-inset-bottom));
}

.footer-main {
  min-width: 0;
  flex: 1;
}

.footer-label {
  font-size: 20rpx;
  line-height: 1.35;
}

.footer-price {
  display: block;
  margin-top: 6rpx;
}

.submit-btn {
  display: flex;
  align-items: center;
  justify-content: center;
  min-width: 148rpx;
  height: 72rpx;
  padding: 0 24rpx;
  border-radius: 999px;
  background: linear-gradient(135deg, rgb(var(--sf-color-brand)) 0%, rgb(var(--sf-color-brand-light)) 100%);
  font-size: 22rpx;
  line-height: 1.2;
  font-weight: 600;
  color: rgb(var(--sf-color-shell));
}

.submit-btn--disabled {
  opacity: 0.45;
}
</style>
