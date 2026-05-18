<template>
  <view class="page">
    <view class="hero-card">
      <view class="hero-row">
        <view class="hero-avatar">服</view>
        <view class="hero-copy">
          <text class="eyebrow">{{ hero.eyebrow }}</text>
          <text class="title">{{ hero.title }}</text>
        </view>
      </view>
      <text class="desc">{{ hero.description }}</text>
    </view>

    <view class="service-panel">
      <view class="service-list">
        <view class="service-row" @click="showContact = true">
          <view class="service-copy">
            <text class="service-title">在线支持</text>
            <text class="service-desc">问题咨询与售后协助</text>
          </view>
          <text class="service-arrow">›</text>
        </view>

        <view class="service-row" @click="goHelp">
          <view class="service-copy">
            <text class="service-title">帮助说明</text>
            <text class="service-desc">下单、发货、退款规则</text>
          </view>
          <text class="service-arrow">›</text>
        </view>

        <view class="service-row" @click="goFeedback">
          <view class="service-copy">
            <text class="service-title">意见反馈</text>
            <text class="service-desc">告诉我们哪里还可以更好</text>
          </view>
          <text class="service-arrow">›</text>
        </view>
      </view>
    </view>

    <view v-if="showContact" class="mask" @click="showContact = false">
      <view class="sheet" @click.stop>
        <text class="sheet-title">在线支持</text>
        <text class="sheet-desc">目前先保留项目联系信息，后续可以继续接到真实客服渠道。</text>
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
import { resolveUserPageHero } from '../page-display-utils'

const showContact = ref(false)
const hero = resolveUserPageHero('service')

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
  padding: 14rpx 14rpx 32rpx;
  background: linear-gradient(180deg, rgb(var(--sf-color-brand-soft)) 0%, rgb(var(--sf-color-page)) 26%, #ffffff 100%);
}

.hero-card,
.service-panel,
.sheet {
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

.hero-row,
.service-row {
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
.sheet-title {
  display: block;
  margin-top: 4rpx;
  font-size: 22rpx;
  line-height: 1.28;
  color: inherit;
}

.desc,
.sheet-desc,
.service-desc {
  display: block;
  margin-top: 8rpx;
  font-size: 18rpx;
  line-height: 1.45;
}

.desc {
  color: rgba(255, 255, 255, 0.88);
}

.service-panel,
.sheet {
  margin-top: 10rpx;
  padding: 12rpx 14rpx;
}

.service-row {
  justify-content: space-between;
  gap: 10rpx;
}

.service-row + .service-row {
  margin-top: 12rpx;
  padding-top: 12rpx;
  border-top: 1px solid rgb(var(--sf-color-divider));
}

.service-copy {
  min-width: 0;
  flex: 1;
}

.service-title {
  display: block;
  font-size: 20rpx;
  font-weight: 600;
  line-height: 1.32;
  color: rgb(var(--sf-color-ink));
}

.service-desc,
.sheet-desc {
  color: rgb(var(--sf-color-text-secondary));
}

.service-arrow {
  font-size: 24rpx;
  line-height: 1;
  color: rgb(var(--sf-color-text-hint));
}

.mask {
  position: fixed;
  inset: 0;
  display: flex;
  align-items: flex-end;
  justify-content: center;
  background: rgba(15, 23, 42, 0.26);
  padding: 14rpx;
}

.contact-card {
  margin-top: 10rpx;
  padding: 12rpx 14rpx;
  border-radius: var(--sf-radius-card);
  background: rgb(var(--sf-color-page));
  font-size: 18rpx;
  line-height: 1.42;
  color: rgb(var(--sf-color-text-secondary));
}

.sheet-btn {
  display: flex;
  align-items: center;
  justify-content: center;
  height: 76rpx;
  margin-top: 12rpx;
  border-radius: var(--sf-radius-card);
  background: linear-gradient(145deg, rgb(var(--sf-color-brand)) 0%, rgb(var(--sf-color-brand-light)) 100%);
  font-size: 20rpx;
  color: #ffffff;
}
</style>
