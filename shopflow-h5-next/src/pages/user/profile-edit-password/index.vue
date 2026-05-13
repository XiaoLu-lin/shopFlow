<template>
  <view class="page">
    <view class="hero-card">
      <text class="title">重置密码</text>
      <text class="desc">复用旧站短信重置协议 `/auth/reset`，先保留手机号 + 验证码 + 新密码流程。</text>
    </view>

    <view class="panel">
      <text class="field-label">手机号</text>
      <input v-model="mobile" class="field-input" type="number" maxlength="11" placeholder="请输入手机号" />
    </view>

    <view class="panel">
      <text class="field-label">验证码</text>
      <view class="inline-row">
        <input v-model="code" class="field-input field-input--inline" type="number" maxlength="6" placeholder="请输入验证码" />
        <view class="ghost-btn" @click="requestCode">{{ countdown > 0 ? `${countdown}s` : '获取验证码' }}</view>
      </view>
    </view>

    <view class="panel">
      <text class="field-label">新密码</text>
      <input v-model="password" class="field-input" password maxlength="20" placeholder="请输入新密码" />
    </view>

    <view class="footer">
      <view class="primary-btn" :class="{ 'primary-btn--disabled': !canSubmit || saving }" @click="submit">
        {{ saving ? '提交中...' : '确认重置' }}
      </view>
    </view>
  </view>
</template>

<script setup lang="ts">
import { computed, ref } from 'vue'
import { requestResetCaptcha, submitPasswordReset } from '@/entities/auth/api'
import { getSessionSnapshot } from '@/shared/compat/session-adapter'
import { mobileReg } from '@/shared/utils/validate'

const mobile = ref(getSessionSnapshot().mobile || '')
const code = ref('')
const password = ref('')
const saving = ref(false)
const counting = ref(false)
const countdown = ref(0)
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
  padding: 20rpx 20rpx 148rpx;
  background: linear-gradient(180deg, #ffffff 0%, #f6f8fb 100%);
}

.hero-card,
.panel {
  border-radius: 12rpx;
  background: #ffffff;
  box-shadow: 0 10rpx 24rpx rgba(23, 32, 51, 0.06);
  padding: 22rpx;
}

.title {
  display: block;
  font-size: 28rpx;
  line-height: 1.3;
  color: #172033;
}

.desc,
.field-label {
  display: block;
  margin-top: 8rpx;
  font-size: 22rpx;
  line-height: 1.4;
  color: #748194;
}

.panel {
  margin-top: 16rpx;
}

.inline-row {
  display: flex;
  gap: 12rpx;
  margin-top: 14rpx;
}

.field-input {
  width: 100%;
  height: 78rpx;
  margin-top: 14rpx;
  border-radius: 10rpx;
  background: #f6f8fb;
  padding: 0 18rpx;
  font-size: 24rpx;
  color: #172033;
  box-sizing: border-box;
}

.field-input--inline {
  margin-top: 0;
  flex: 1;
}

.ghost-btn {
  display: flex;
  min-width: 180rpx;
  align-items: center;
  justify-content: center;
  border-radius: 10rpx;
  background: #ffffff;
  font-size: 22rpx;
  color: #5f6b7c;
  box-shadow: inset 0 0 0 1px #dbe3ef;
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

.primary-btn--disabled {
  opacity: 0.45;
}
</style>
