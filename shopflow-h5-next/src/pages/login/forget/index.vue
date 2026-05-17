<template>
  <view class="page">
    <view class="auth-shell">
      <view class="auth-hero">
        <text class="auth-eyebrow">{{ hero.eyebrow }}</text>
        <text class="auth-title">{{ hero.title }}</text>
        <text class="auth-desc">{{ hero.description }}</text>
      </view>

      <view class="auth-card">
        <view class="auth-field">
          <text class="auth-field-label">手机号</text>
          <input
            v-model="mobile"
            class="auth-field-input"
            type="number"
            placeholder="请输入手机号"
            placeholder-class="auth-placeholder"
          />
        </view>

        <view class="auth-field">
          <view class="auth-field-head">
            <text class="auth-field-label">验证码</text>
            <text class="auth-captcha" @click="requestCaptcha">
              {{ counting ? `${countdown}s` : '获取验证码' }}
            </text>
          </view>
          <input
            v-model="code"
            class="auth-field-input"
            type="number"
            placeholder="请输入短信验证码"
            placeholder-class="auth-placeholder"
          />
        </view>

        <view class="auth-field">
          <text class="auth-field-label">新密码</text>
          <input
            v-model="password"
            class="auth-field-input"
            :password="!visiblePassword"
            placeholder="请输入新密码"
            placeholder-class="auth-placeholder"
          />
        </view>

        <view class="auth-field">
          <view class="auth-field-head">
            <text class="auth-field-label">确认密码</text>
            <text class="auth-toggle" @click="visiblePassword = !visiblePassword">
              {{ visiblePassword ? '隐藏' : '显示' }}
            </text>
          </view>
          <input
            v-model="confirmPassword"
            class="auth-field-input"
            :password="!visiblePassword"
            placeholder="请再次输入新密码"
            placeholder-class="auth-placeholder"
          />
        </view>

        <view class="auth-submit" :class="{ 'auth-submit--disabled': !canSubmit || submitting }" @click="submit">
          {{ submitting ? '提交中...' : '确认重置' }}
        </view>
      </view>
    </view>
  </view>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { resolveAuthActionHero } from '@/features/auth/auth-form-display-utils'
import { useUniAuthActionForm } from '@/features/auth/use-uni-auth-action-form'

const hero = resolveAuthActionHero('reset')
const visiblePassword = ref(false)
const {
  mobile,
  code,
  password,
  confirmPassword,
  counting,
  countdown,
  submitting,
  canSubmit,
  requestCaptcha,
  submit,
} = useUniAuthActionForm('reset')
</script>

<style scoped lang="scss" src="../auth-page.scss"></style>
