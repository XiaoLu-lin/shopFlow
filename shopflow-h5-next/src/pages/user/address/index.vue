<template>
  <view class="page">
    <view class="hero-card">
      <view class="hero-row">
        <view class="hero-avatar">地</view>
        <view class="hero-copy">
          <text class="eyebrow">{{ hero.eyebrow }}</text>
          <text class="title">{{ hero.title }}</text>
        </view>
      </view>
      <text class="desc">{{ hero.description }}</text>
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
            <text class="name">{{ item.name }} · {{ item.tel }}</text>
            <view class="address-actions">
              <text v-if="item.isDefault" class="default-tag">默认</text>
              <view class="edit-chip" @click.stop="editAddress(item.id)">编辑</view>
            </view>
          </view>
          <text class="address">{{ item.address }}</text>
        </view>
      </view>
    </view>

    <view v-else class="empty-card">
      <view class="empty-head">
        <text class="empty-title">还没有收货地址</text>
        <text class="default-tag">待新增</text>
      </view>
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
import { resolveUserPageHero } from '../page-display-utils'

const loading = ref(true)
const addresses = ref<AddressCard[]>([])
const hero = resolveUserPageHero('address')

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
  padding: 14rpx 14rpx 124rpx;
  background: linear-gradient(180deg, rgb(var(--sf-color-brand-soft)) 0%, rgb(var(--sf-color-page)) 26%, #ffffff 100%);
}

.hero-card,
.address-card,
.empty-card {
  border-radius: var(--sf-radius-card);
  background: #ffffff;
  border: 1px solid rgb(var(--sf-color-line));
  box-shadow: var(--sf-shadow-soft);
}

.hero-card {
  padding: 14rpx 16rpx 16rpx;
  color: #ffffff;
  background: linear-gradient(145deg, rgb(var(--sf-color-brand)) 0%, rgb(var(--sf-color-brand-light)) 100%);
  box-shadow: var(--sf-shadow-brand);
}

.empty-card {
  padding: 12rpx 14rpx;
}

.hero-row,
.address-head,
.address-actions,
.empty-head {
  display: flex;
  align-items: center;
}

.hero-row {
  gap: 10rpx;
}

.hero-avatar {
  display: flex;
  width: 56rpx;
  height: 56rpx;
  flex-shrink: 0;
  align-items: center;
  justify-content: center;
  border-radius: var(--sf-radius-card);
  background: linear-gradient(145deg, rgba(255, 255, 255, 0.96), rgba(229, 237, 246, 0.92));
  font-size: 20rpx;
  font-weight: 700;
  color: rgb(var(--sf-color-brand));
}

.hero-copy {
  min-width: 0;
  flex: 1;
}

.eyebrow {
  display: block;
  font-size: 16rpx;
  letter-spacing: 0.12em;
  text-transform: uppercase;
  color: rgba(255, 255, 255, 0.78);
}

.title,
.empty-title {
  display: block;
  margin-top: 4rpx;
  font-size: 22rpx;
  line-height: 1.28;
  color: inherit;
}

.desc,
.empty-desc {
  display: block;
  margin-top: 8rpx;
  font-size: 18rpx;
  line-height: 1.42;
}

.desc {
  color: rgba(255, 255, 255, 0.88);
}

.empty-title,
.name {
  color: rgb(var(--sf-color-ink));
}

.empty-desc,
.address {
  color: rgb(var(--sf-color-text-secondary));
}

.address-list {
  display: grid;
  gap: 8rpx;
  margin-top: 10rpx;
}

.address-card {
  padding: 0;
}

.address-card--skeleton {
  padding: 12rpx 14rpx;
}

.address-body {
  padding: 12rpx 14rpx;
}

.address-head,
.empty-head {
  justify-content: space-between;
  gap: 8rpx;
}

.address-actions {
  flex-shrink: 0;
  gap: 8rpx;
}

.name {
  font-size: 20rpx;
  font-weight: 600;
  line-height: 1.3;
}

.default-tag {
  padding: 4rpx 10rpx;
  border-radius: var(--sf-radius-card);
  background: rgb(var(--sf-color-brand-soft));
  font-size: 16rpx;
  line-height: 1.2;
  color: rgb(var(--sf-color-brand));
}

.edit-chip {
  padding: 4rpx 10rpx;
  border-radius: var(--sf-radius-card);
  background: rgba(255, 255, 255, 0.92);
  border: 1px solid rgb(var(--sf-color-line));
  font-size: 16rpx;
  line-height: 1.2;
  color: rgb(var(--sf-color-brand));
}

.address {
  display: block;
  margin-top: 6rpx;
  font-size: 18rpx;
  line-height: 1.45;
}

.skeleton-line {
  height: 16rpx;
  margin-top: 8rpx;
  border-radius: 999px;
  background: rgb(var(--sf-color-divider));
}

.skeleton-line--title {
  width: 132rpx;
  margin-top: 0;
}

.skeleton-line--short {
  width: 184rpx;
}

.empty-card {
  margin-top: 10rpx;
  padding-top: 12rpx;
  padding-bottom: 12rpx;
}

.footer {
  position: fixed;
  right: 14rpx;
  bottom: 16rpx;
  left: 14rpx;
}

.primary-btn {
  display: flex;
  align-items: center;
  justify-content: center;
  height: 72rpx;
  border-radius: var(--sf-radius-card);
  background: linear-gradient(135deg, rgb(var(--sf-color-brand)) 0%, rgb(var(--sf-color-brand-light)) 100%);
  font-size: 20rpx;
  color: #ffffff;
  box-shadow: var(--sf-shadow-brand);
}
</style>
