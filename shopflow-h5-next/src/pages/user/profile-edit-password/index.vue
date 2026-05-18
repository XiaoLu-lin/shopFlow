<template>
  <view class="page">
    <view class="hero-card">
      <view class="hero-row">
        <view class="hero-avatar">密</view>
        <view class="hero-copy">
          <text class="eyebrow">{{ pageMeta.eyebrow }}</text>
          <text class="title">{{ pageMeta.title }}</text>
        </view>
      </view>
      <text class="desc">{{ pageMeta.description }}</text>
    </view>

    <view class="form-list">
      <view class="panel">
        <text class="field-label">手机号</text>
        <input v-model="mobile" class="field-input" type="number" maxlength="11" placeholder="请输入手机号" />
      </view>

      <view class="panel">
        <text class="field-label">验证码</text>
        <view class="inline-row">
          <input
            v-model="code"
            class="field-input field-input--inline"
            type="number"
            maxlength="6"
            placeholder="请输入验证码"
          />
          <view class="ghost-btn" @click="requestCode">
            {{ countdown > 0 ? `${countdown}s` : pageMeta.captchaLabel }}
          </view>
        </view>
      </view>

      <view class="panel">
        <text class="field-label">新密码</text>
        <input
          v-model="password"
          class="field-input"
          password
          maxlength="20"
          :placeholder="pageMeta.placeholder"
        />
      </view>
    </view>

    <view class="footer">
      <view class="primary-btn" :class="{ 'primary-btn--disabled': !canSubmit || saving }" @click="submit">
        {{ saving ? '提交中...' : pageMeta.submitLabel }}
      </view>
    </view>
  </view>
</template>

<script setup lang="ts">
import { computed, ref } from 'vue'
import { requestResetCaptcha, submitPasswordReset } from '@/entities/auth/api'
import { getSessionSnapshot } from '@/shared/compat/session-adapter'
import { mobileReg } from '@/shared/utils/validate'
import { resolveProfileEditorMeta } from '../page-display-utils'

const mobile = ref(getSessionSnapshot().mobile || '')
const code = ref('')
const password = ref('')
const saving = ref(false)
const counting = ref(false)
const countdown = ref(0)
const pageMeta = resolveProfileEditorMeta('password')
const canSubmit = computed(() => mobileReg.test(mobile.value.trim()) && code.value.trim().length >= 4 && password.value.trim().length >= 6)

async function requestCode() {
  if (countdown.value > 0 || counting.value) {
    return
  }

  if (!mobileReg.test(mobile.value.trim())) {
    toast('请输入正确的手机号')
    return
  }

  counting.value = true

  try {
    await requestResetCaptcha({
      mobile: mobile.value.trim(),
    })
    toast('验证码已发送')
    startCountdown()
  } catch (error) {
    console.error(error)
    toast('验证码发送失败')
  } finally {
    counting.value = false
  }
}

async function submit() {
  if (!canSubmit.value || saving.value) {
    return
  }

  saving.value = true

  try {
    await submitPasswordReset({
      mobile: mobile.value.trim(),
      code: code.value.trim(),
      password: password.value.trim(),
    })
    toast('密码已重置')
    setTimeout(() => {
      uni.navigateBack({
        delta: 1,
      })
    }, 320)
  } catch (error) {
    console.error(error)
    toast('密码重置失败')
  } finally {
    saving.value = false
  }
}

function startCountdown() {
  countdown.value = 60
  const timer = setInterval(() => {
    countdown.value -= 1
    if (countdown.value <= 0) {
      clearInterval(timer)
      countdown.value = 0
    }
  }, 1000)
}

function toast(title: string) {
  uni.showToast({
    title,
    icon: 'none',
  })
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

.hero-row,
.inline-row {
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

.form-list {
  display: grid;
  gap: 8rpx;
  margin-top: 10rpx;
}

.panel {
  padding: 12rpx 14rpx;
}

.inline-row {
  gap: 8rpx;
  margin-top: 12rpx;
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

.field-input--inline {
  margin-top: 0;
  flex: 1;
}

.ghost-btn {
  display: flex;
  min-width: 168rpx;
  height: 76rpx;
  flex-shrink: 0;
  align-items: center;
  justify-content: center;
  border-radius: var(--sf-radius-card);
  background: rgb(var(--sf-color-brand-soft));
  font-size: 19rpx;
  color: rgb(var(--sf-color-brand));
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
