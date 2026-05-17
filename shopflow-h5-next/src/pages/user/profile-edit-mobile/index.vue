<template>
  <view class="page">
    <view class="hero-card">
      <view class="hero-row">
        <view class="hero-avatar">编</view>
        <view class="hero-copy">
          <text class="eyebrow">{{ pageMeta.eyebrow }}</text>
          <text class="title">{{ pageMeta.title }}</text>
        </view>
      </view>
      <text class="desc">{{ pageMeta.description }}</text>
    </view>

    <view class="panel">
      <text class="field-label">手机号</text>
      <input
        v-model="mobile"
        class="field-input"
        type="number"
        maxlength="11"
        :placeholder="pageMeta.placeholder"
      />
    </view>

    <view class="footer">
      <view class="primary-btn" :class="{ 'primary-btn--disabled': !canSubmit || saving }" @click="submit">
        {{ saving ? '保存中...' : pageMeta.submitLabel }}
      </view>
    </view>
  </view>
</template>

<script setup lang="ts">
import { computed, ref } from 'vue'
import { getSessionSnapshot, persistLegacyProfile } from '@/shared/compat/session-adapter'
import { mobileReg } from '@/shared/utils/validate'
import { resolveProfileEditorMeta } from '../page-display-utils'

const mobile = ref(getSessionSnapshot().mobile || '')
const saving = ref(false)
const pageMeta = resolveProfileEditorMeta('mobile')
const canSubmit = computed(() => mobileReg.test(mobile.value.trim()))

bootstrap()

function bootstrap() {
  mobile.value = getSessionSnapshot().mobile || ''
}

async function submit() {
  if (!canSubmit.value || saving.value) {
    return
  }

  saving.value = true

  try {
    persistLegacyProfile({
      mobile: mobile.value.trim(),
    })
    uni.showToast({
      title: '手机号已保存',
      icon: 'none',
    })
    setTimeout(() => {
      uni.navigateBack({
        delta: 1,
      })
    }, 320)
  } catch (error) {
    console.error(error)
    uni.showToast({
      title: '保存手机号失败',
      icon: 'none',
    })
  } finally {
    saving.value = false
  }
}
</script>

<style scoped lang="scss">
.page {
  min-height: 100vh;
  padding: 14rpx 14rpx 124rpx;
  background: linear-gradient(180deg, rgb(var(--sf-color-brand-soft)) 0%, rgb(var(--sf-color-page)) 26%, #ffffff 100%);
}

.hero-card,
.panel {
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

.hero-row {
  display: flex;
  align-items: center;
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

.title {
  display: block;
  margin-top: 4rpx;
  font-size: 22rpx;
  line-height: 1.28;
  color: #ffffff;
}

.desc,
.field-label {
  display: block;
  margin-top: 8rpx;
  font-size: 18rpx;
  line-height: 1.42;
}

.desc {
  color: rgba(255, 255, 255, 0.88);
}

.field-label {
  margin-top: 0;
  color: rgb(var(--sf-color-text-secondary));
}

.panel {
  margin-top: 10rpx;
  padding: 12rpx 14rpx;
}

.field-input {
  width: 100%;
  height: 76rpx;
  margin-top: 12rpx;
  padding: 0 16rpx;
  border-radius: var(--sf-radius-card);
  background: rgb(var(--sf-color-page));
  font-size: 20rpx;
  color: rgb(var(--sf-color-ink));
  box-sizing: border-box;
}

.footer {
  position: fixed;
  right: 14rpx;
  bottom: 18rpx;
  left: 14rpx;
}

.primary-btn {
  display: flex;
  align-items: center;
  justify-content: center;
  height: 84rpx;
  border-radius: var(--sf-radius-card);
  background: linear-gradient(145deg, rgb(var(--sf-color-brand)) 0%, rgb(var(--sf-color-brand-light)) 100%);
  font-size: 22rpx;
  color: #ffffff;
  box-shadow: var(--sf-shadow-brand);
}

.primary-btn--disabled {
  opacity: 0.45;
}
</style>
