<template>
  <view class="page">
    <view class="hero">
      <text class="eyebrow">ShopFlow</text>
      <text class="title">uni-app 多端基线已启动</text>
      <text class="desc">当前阶段先完成 H5 与微信小程序的统一工程壳，后续再按领域迁移业务页。</text>
    </view>

    <view class="panel">
      <text class="panel-title">当前基线</text>
      <text class="panel-item">技术栈：uni-app + Vue 3 + TypeScript + Pinia</text>
      <text class="panel-item">组件基座：uView Plus + sf-* 业务组件</text>
      <text class="panel-item">样式策略：SCSS + H5 Tailwind 增强</text>
      <text class="panel-item">兼容目标：/wx/*、tenant 预热、旧登录态键</text>
    </view>
  </view>
</template>

<script setup lang="ts">
</script>

<style scoped lang="scss">
.page {
  min-height: 100vh;
  padding: 24rpx;
  background: #f6f8fb;
  color: #172033;
}

.hero {
  padding: 28rpx;
  border-radius: 24rpx;
  background: linear-gradient(180deg, #ffffff 0%, #eef5ff 100%);
  box-shadow: 0 12rpx 36rpx rgba(23, 32, 51, 0.08);
}

.eyebrow {
  display: block;
  font-size: 22rpx;
  line-height: 1.4;
  color: #1677ff;
}

.title {
  display: block;
  margin-top: 10rpx;
  font-size: 40rpx;
  line-height: 1.3;
  font-weight: 600;
}

.desc {
  display: block;
  margin-top: 14rpx;
  font-size: 26rpx;
  line-height: 1.5;
  color: #6f7a8a;
}

.panel {
  margin-top: 20rpx;
  padding: 24rpx;
  border-radius: 20rpx;
  background: #ffffff;
  box-shadow: 0 10rpx 28rpx rgba(23, 32, 51, 0.06);
}

.panel-title {
  display: block;
  font-size: 28rpx;
  line-height: 1.4;
  font-weight: 600;
}

.panel-item {
  display: block;
  margin-top: 12rpx;
  font-size: 24rpx;
  line-height: 1.5;
  color: #5f6b7c;
}
</style>
