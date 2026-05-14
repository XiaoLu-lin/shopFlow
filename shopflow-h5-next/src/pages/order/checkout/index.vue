<template>
  <view class="page" role="main">
    <view class="hero-card">
      <text class="title" role="heading" aria-level="1">确认订单</text>
      <text class="desc">已接入结算接口，沿用旧站 `AddressId / CartId / CouponId / UserCouponId` 下单上下文。</text>
    </view>

    <view class="panel">
      <view class="panel-head">
        <view>
          <text class="panel-title">{{ checkedAddress?.name || '未选择地址' }}</text>
          <text class="panel-copy">{{ checkedAddress?.tel || '请先到地址页设置' }}</text>
          <text class="panel-copy">{{ checkedAddress?.addressDetail || '当前结算页先展示地址摘要' }}</text>
        </view>
        <view class="panel-action" role="button" @click="goSelectAddress">选择地址</view>
      </view>
    </view>

    <view class="goods-list">
      <view v-for="item in checkedGoodsList" :key="item.id" class="goods-card">
        <image class="goods-image" :src="item.picUrl" mode="aspectFill" />
        <view class="goods-body">
          <text class="goods-name">{{ item.goodsName }}</text>
          <text class="goods-spec">{{ item.specifications.join(' / ') }}</text>
          <view class="goods-foot">
            <text class="goods-price">¥ {{ item.price }}</text>
            <text class="goods-count">x {{ item.number }}</text>
          </view>
        </view>
      </view>
    </view>

    <view class="panel">
      <view class="panel-head">
        <text class="panel-title">优惠券</text>
        <text class="panel-copy">{{ availableCoupons.length }} 张可用</text>
      </view>
      <scroll-view scroll-x class="coupon-scroll">
        <view class="coupon-row">
          <view
            class="coupon-chip"
            :class="{ 'coupon-chip--active': selectedCouponIndex === -1 }"
            role="button"
            @click="selectCoupon(-1)"
          >
            不使用
          </view>
          <view
            v-for="(coupon, index) in availableCoupons"
            :key="coupon.id"
            class="coupon-chip"
            :class="{ 'coupon-chip--active': selectedCouponIndex === index }"
            role="button"
            @click="selectCoupon(index)"
          >
            {{ coupon.name }} - ¥{{ coupon.discount }}
          </view>
        </view>
      </scroll-view>
    </view>

    <view class="panel">
      <text class="panel-title">订单备注</text>
      <textarea
        v-model="message"
        maxlength="50"
        class="remark-input"
        placeholder="请输入备注"
      />
      <text class="remark-count">{{ message.length }}/50</text>
    </view>

    <view class="panel">
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
        <text class="price-value">- ¥ {{ checkoutData?.couponPrice || 0 }}</text>
      </view>
      <view class="submit-row">
        <view>
          <text class="price-label">实付</text>
          <text class="pay-price">¥ {{ checkoutData?.actualPrice || 0 }}</text>
        </view>
        <view class="submit-btn" role="button" @click="submit">提交订单</view>
      </view>
    </view>
  </view>
</template>

<script setup lang="ts">
import { computed, ref } from 'vue'
import { onShow } from '@dcloudio/uni-app'
import { fetchCheckout, fetchSelectableCoupons, submitOrder, type CheckoutPayload, type CouponOption } from '@/entities/order/api'
import { readFlowContext, writeFlowContext } from '@/shared/compat/session-adapter'

const message = ref('')
const selectedCouponIndex = ref(-1)
const checkoutData = ref<CheckoutPayload | null>(null)
const couponList = ref<CouponOption[]>([])

const flowContext = computed(() => readFlowContext())
const availableCoupons = computed(() => couponList.value.filter((item) => item.available))
const checkedAddress = computed(() => checkoutData.value?.checkedAddress)
const checkedGoodsList = computed(() => checkoutData.value?.checkedGoodsList || [])

bootstrap()
onShow(() => {
  void bootstrap()
})

async function bootstrap() {
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
    }
  } catch (error) {
    console.error(error)
  }
}

function selectCoupon(index: number) {
  selectedCouponIndex.value = index

  if (index < 0) {
    writeFlowContext({
      CouponId: -1,
      UserCouponId: -1,
    })
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
}

function goSelectAddress() {
  uni.navigateTo({
    url: '/pages/user/address/index',
  })
}

async function submit() {
  if (!checkedAddress.value?.id) {
    uni.showToast({
      title: '请先设置收货地址',
      icon: 'none',
    })
    return
  }

  try {
    const order = await submitOrder({
      addressId: flowContext.value.addressId || String(checkedAddress.value.id),
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

    uni.showToast({
      title: order.isPay ? '下单成功' : '订单已创建',
      icon: 'none',
    })
    uni.navigateTo({
      url: `/pages/order/payment/index?orderId=${order.orderIds[0] || ''}&orderIds=${(order.orderIds || []).join(',')}`,
    })
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
  padding: 20rpx 20rpx 40rpx;
  background: linear-gradient(180deg, #ffffff 0%, #f6f8fb 100%);
}

.hero-card,
.panel,
.goods-card {
  border-radius: 12rpx;
  background: #ffffff;
  box-shadow: 0 10rpx 24rpx rgba(23, 32, 51, 0.06);
}

.hero-card,
.panel {
  padding: 22rpx;
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

.panel,
.goods-list {
  margin-top: 16rpx;
}

.panel-head,
.goods-foot,
.price-row,
.submit-row {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16rpx;
}

.panel-title {
  display: block;
  font-size: 25rpx;
  line-height: 1.3;
  color: #172033;
}

.panel-copy,
.goods-spec,
.price-label {
  display: block;
  margin-top: 6rpx;
  font-size: 21rpx;
  line-height: 1.35;
  color: #748194;
}

.goods-list {
  display: grid;
  gap: 14rpx;
}

.goods-card {
  display: flex;
  gap: 16rpx;
  padding: 16rpx;
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
  display: block;
  font-size: 24rpx;
  line-height: 1.35;
  color: #172033;
}

.goods-price,
.footer-price,
.pay-price {
  font-size: 28rpx;
  line-height: 1.2;
  font-weight: 600;
  color: #172033;
}

.goods-count,
.price-value {
  font-size: 22rpx;
  line-height: 1.2;
  color: #172033;
}

.coupon-scroll {
  margin-top: 14rpx;
  white-space: nowrap;
}

.coupon-row {
  display: inline-flex;
  gap: 12rpx;
}

.coupon-chip {
  padding: 12rpx 18rpx;
  border-radius: 999px;
  background: #f3f6fb;
  font-size: 21rpx;
  line-height: 1.2;
  color: #5f6b7c;
}

.coupon-chip--active {
  background: #1677ff;
  color: #ffffff;
}

.remark-input {
  width: 100%;
  min-height: 160rpx;
  margin-top: 14rpx;
  padding: 18rpx;
  border-radius: 10rpx;
  background: #f7faff;
  font-size: 22rpx;
  line-height: 1.45;
  color: #172033;
}

.remark-count {
  display: block;
  margin-top: 8rpx;
  text-align: right;
  font-size: 20rpx;
  color: #94a0b0;
}

.submit-row {
  margin-top: 18rpx;
}

.submit-btn {
  display: flex;
  align-items: center;
  justify-content: center;
  height: 84rpx;
  padding: 0 28rpx;
  border-radius: 12rpx;
  background: #1677ff;
  font-size: 24rpx;
  color: #ffffff;
}
</style>
