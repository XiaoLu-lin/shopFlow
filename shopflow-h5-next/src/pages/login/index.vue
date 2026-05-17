<template>
  <view class="page">
    <view class="auth-shell">
      <view class="auth-hero">
        <text class="auth-eyebrow">{{ hero.eyebrow }}</text>
        <text class="auth-title">{{ hero.title }}</text>
        <text v-if="hero.description" class="auth-desc">{{ hero.description }}</text>
      </view>

      <view class="auth-card">
        <view class="auth-field">
          <text class="auth-field-label">账号</text>
          <input
            v-model="account"
            class="auth-field-input"
            type="text"
            placeholder="请输入用户名、手机号或邮箱"
            placeholder-class="auth-placeholder"
          />
        </view>

        <view class="auth-field">
          <view class="auth-field-head">
            <text class="auth-field-label">密码</text>
            <text class="auth-toggle" @click="visiblePassword = !visiblePassword">
              {{ visiblePassword ? '隐藏' : '显示' }}
            </text>
          </view>
          <input
            v-model="password"
            class="auth-field-input"
            :password="!visiblePassword"
            placeholder="请输入密码"
            placeholder-class="auth-placeholder"
          />
        </view>

        <view class="auth-submit" :class="{ 'auth-submit--disabled': !canSubmit || submitting }" @click="submit">
          {{ submitting ? '登录中...' : '登录' }}
        </view>

        <view class="auth-action-row">
          <view class="auth-ghost" @click="go('/pages/login/register-get-code/index')">免费注册</view>
          <view class="auth-ghost" @click="go('/pages/login/forget/index')">找回密码</view>
        </view>
      </view>
    </view>
  </view>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { resolveAuthActionHero } from '@/features/auth/auth-form-display-utils'
import { useUniLoginForm } from '@/features/auth/use-uni-login-form'

const redirect = computed(() => {
  const pages = getCurrentPages()
  const currentPage = pages[pages.length - 1] as { options?: Record<string, unknown> } | undefined
  const value = currentPage?.options?.redirect
  return typeof value === 'string' ? value : ''
})

const hero = resolveAuthActionHero('login')
const { account, password, visiblePassword, submitting, canSubmit, submit } = useUniLoginForm(redirect.value)

function go(url: string) {
  uni.navigateTo({ url })
}
</script>
<style scoped lang="scss" src="./auth-page.scss"></style>
