<template>
  <view class="page">
    <view class="hero-card">
      <text class="title">服务中心</text>
      <text class="desc">保留旧站服务入口结构，先聚合客服、反馈和常见问题三个高频入口。</text>
    </view>

    <view class="service-list">
      <view class="service-card" @click="showContact = true">
        <view>
          <text class="service-title">联系客服</text>
          <text class="service-desc">查看当前项目与联系信息</text>
        </view>
        <text class="service-arrow">></text>
      </view>

      <view class="service-card" @click="goFeedback">
        <view>
          <text class="service-title">意见反馈</text>
          <text class="service-desc">提交页面、商品或服务建议</text>
        </view>
        <text class="service-arrow">></text>
      </view>

      <view class="service-card" @click="goHelp">
        <view>
          <text class="service-title">常见问题</text>
          <text class="service-desc">快速查看帮助中心 FAQ</text>
        </view>
        <text class="service-arrow">></text>
      </view>
    </view>

    <view v-if="showContact" class="mask" @click="showContact = false">
      <view class="sheet" @click.stop>
        <text class="sheet-title">联系客服</text>
        <text class="sheet-desc">当前先保留项目级联系信息，后续可切到真实客服渠道。</text>
        <view class="contact-card">项目名称：ShopFlow H5</view>
        <view class="contact-card">联系电话：021-xxxx-xxxx</view>
        <view class="contact-card">联系 QQ：738696120</view>
        <view class="contact-card">版本：Modern Uni Preview</view>
        <view class="sheet-btn" @click="showContact = false">我知道了</view>
      </view>
    </view>
  </view>
</template>

<script setup lang="ts">
import { ref } from 'vue'

const showContact = ref(false)

function goFeedback() {
  uni.navigateTo({
    url: '/pages/user/feedback/index',
  })
}

function goHelp() {
  uni.navigateTo({
    url: '/pages/user/help/index',
  })
}
</script>

<style scoped lang="scss">
.page {
  min-height: 100vh;
  padding: 20rpx 20rpx 40rpx;
  background: linear-gradient(180deg, #ffffff 0%, #f6f8fb 100%);
}

.hero-card,
.service-card,
.sheet {
  border-radius: 12rpx;
  background: #ffffff;
  box-shadow: 0 10rpx 24rpx rgba(23, 32, 51, 0.06);
}

.hero-card,
.sheet {
  padding: 22rpx;
}

.title,
.sheet-title {
  display: block;
  font-size: 28rpx;
  line-height: 1.3;
  color: #172033;
}

.desc,
.sheet-desc {
  display: block;
  margin-top: 8rpx;
  font-size: 22rpx;
  line-height: 1.4;
  color: #748194;
}

.service-list {
  display: grid;
  gap: 14rpx;
  margin-top: 16rpx;
}

.service-card {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16rpx;
  padding: 20rpx;
}

.service-title {
  display: block;
  font-size: 24rpx;
  line-height: 1.35;
  color: #172033;
}

.service-desc {
  display: block;
  margin-top: 8rpx;
  font-size: 22rpx;
  line-height: 1.4;
  color: #748194;
}

.service-arrow {
  font-size: 24rpx;
  color: #a0aaba;
}

.mask {
  position: fixed;
  inset: 0;
  display: flex;
  align-items: flex-end;
  justify-content: center;
  background: rgba(15, 23, 42, 0.3);
  padding: 20rpx;
}

.sheet {
  width: 100%;
}

.contact-card {
  margin-top: 12rpx;
  border-radius: 10rpx;
  background: #f6f8fb;
  padding: 18rpx;
  font-size: 22rpx;
  line-height: 1.4;
  color: #4f5d72;
}

.sheet-btn {
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
</style>
