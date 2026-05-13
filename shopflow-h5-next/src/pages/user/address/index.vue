<template>
  <view class="page">
    <view class="hero-card">
      <text class="title">收货地址</text>
      <text class="desc">沿用旧站 AddressId 选择语义，结算页会继续读取当前选中的地址。</text>
    </view>

    <view v-if="loading" class="address-list">
      <view v-for="index in 3" :key="index" class="address-card address-card--skeleton">
        <view class="skeleton-line skeleton-line--title"></view>
        <view class="skeleton-line skeleton-line--short"></view>
        <view class="skeleton-line"></view>
      </view>
    </view>

    <view v-else-if="addresses.length" class="address-list">
      <view v-for="item in addresses" :key="item.id" class="address-card">
        <view class="address-body" @click="selectAddress(item.id)">
          <view class="address-head">
            <view class="name-row">
              <text class="name">{{ item.name }}</text>
              <text v-if="item.isDefault" class="default-tag">默认</text>
            </view>
            <text class="mobile">{{ item.tel }}</text>
          </view>
          <text class="address">{{ item.address }}</text>
        </view>
        <view class="edit-btn" @click="editAddress(item.id)">编辑</view>
      </view>
    </view>

    <view v-else class="empty-card">
      <text class="empty-title">还没有收货地址</text>
      <text class="empty-desc">先新增一个常用地址，后面结算时就能直接带出来。</text>
    </view>

    <view class="footer">
      <view class="primary-btn" @click="addAddress">新增地址</view>
    </view>
  </view>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { onShow } from '@dcloudio/uni-app'
import { fetchAddressList, type AddressCard } from '@/entities/user/api'
import { writeFlowContext } from '@/shared/compat/session-adapter'

const loading = ref(true)
const addresses = ref<AddressCard[]>([])

bootstrap()
onShow(() => {
  void bootstrap()
})

async function bootstrap() {
  loading.value = true

  try {
    const result = await fetchAddressList()
    addresses.value = result.list || []
  } catch (error) {
    console.error(error)
  } finally {
    loading.value = false
  }
}

function addAddress() {
  uni.navigateTo({
    url: '/pages/user/address-edit/index?addressId=-1',
  })
}

function editAddress(id: number) {
  uni.navigateTo({
    url: `/pages/user/address-edit/index?addressId=${id}`,
  })
}

function selectAddress(id: number) {
  writeFlowContext({
    AddressId: id,
  })

  uni.showToast({
    title: '已选中该地址',
    icon: 'none',
  })

  setTimeout(() => {
    uni.navigateBack({
      delta: 1,
    })
  }, 320)
}
</script>

<style scoped lang="scss">
.page {
  min-height: 100vh;
  padding: 20rpx 20rpx 148rpx;
  background: linear-gradient(180deg, #ffffff 0%, #f6f8fb 100%);
}

.hero-card,
.address-card,
.empty-card {
  border-radius: 12rpx;
  background: #ffffff;
  box-shadow: 0 10rpx 24rpx rgba(23, 32, 51, 0.06);
}

.hero-card,
.empty-card {
  padding: 22rpx;
}

.title,
.empty-title {
  display: block;
  font-size: 28rpx;
  line-height: 1.3;
  color: #172033;
}

.desc,
.empty-desc,
.mobile {
  display: block;
  margin-top: 8rpx;
  font-size: 22rpx;
  line-height: 1.4;
  color: #748194;
}

.address-list {
  display: grid;
  gap: 14rpx;
  margin-top: 16rpx;
}

.address-card {
  display: flex;
  align-items: flex-start;
  gap: 16rpx;
  padding: 20rpx;
}

.address-card--skeleton {
  display: block;
}

.address-body {
  min-width: 0;
  flex: 1;
}

.address-head,
.name-row {
  display: flex;
  align-items: center;
  gap: 12rpx;
}

.address-head {
  justify-content: space-between;
}

.name {
  font-size: 26rpx;
  line-height: 1.3;
  color: #172033;
}

.default-tag {
  padding: 4rpx 10rpx;
  border-radius: 8rpx;
  background: #edf5ff;
  font-size: 18rpx;
  line-height: 1.2;
  color: #1677ff;
}

.address {
  display: block;
  margin-top: 12rpx;
  font-size: 22rpx;
  line-height: 1.5;
  color: #4f5d72;
}

.edit-btn {
  flex-shrink: 0;
  padding: 12rpx 16rpx;
  border-radius: 10rpx;
  background: #f6f8fb;
  font-size: 22rpx;
  line-height: 1.2;
  color: #5f6b7c;
}

.skeleton-line {
  height: 20rpx;
  margin-top: 12rpx;
  border-radius: 999px;
  background: #edf1f6;
}

.skeleton-line--title {
  width: 180rpx;
  margin-top: 0;
}

.skeleton-line--short {
  width: 240rpx;
}

.empty-card {
  margin-top: 16rpx;
  text-align: center;
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
</style>
