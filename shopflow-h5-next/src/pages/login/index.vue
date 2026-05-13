<template>
  <view class="page">
    <view class="hero">
      <text class="eyebrow">ShopFlow Account</text>
      <text class="title">账号登录</text>
      <text class="desc">
        登录后会继续沿用旧 `Authorization`、`avatar`、`nickName` 与 redirect 语义。
      </text>
    </view>

    <view class="card">
      <view class="tip">
        支持用户名、手机号或邮箱登录。当前正在迁移为 uni-app 多端登录页，这里已经接入真实登录 API。
      </view>

      <view class="field-group">
        <view class="field-item">
          <text class="field-label">账号</text>
          <input
            v-model="account"
            class="field-input"
            type="text"
            placeholder="请输入用户名、手机号或邮箱"
            placeholder-class="field-placeholder"
          />
        </view>

        <view class="field-item">
          <view class="field-row">
            <text class="field-label">密码</text>
            <text class="toggle" @click="visiblePassword = !visiblePassword">
              {{ visiblePassword ? '隐藏' : '显示' }}
            </text>
          </view>
          <input
            v-model="password"
            class="field-input"
            :password="!visiblePassword"
            placeholder="请输入密码"
            placeholder-class="field-placeholder"
          />
        </view>
      </view>

      <view class="meta">
        <text>登录后将跳转到：{{ redirectLabel }}</text>
      </view>

      <button class="submit" :disabled="!canSubmit || submitting" @click="submit">
        {{ submitting ? '登录中...' : '登录' }}
      </button>

      <view class="actions">
        <button class="ghost" @click="go('/pages/login/register-get-code/index')">免费注册</button>
        <button class="ghost" @click="go('/pages/login/forget/index')">找回密码</button>
      </view>
    </view>
  </view>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { useUniLoginForm } from '@/features/auth/use-uni-login-form'

const redirect = computed(() => {
  const pages = getCurrentPages()
  const currentPage = pages[pages.length - 1] as { options?: Record<string, unknown> } | undefined
  const value = currentPage?.options?.redirect
  return typeof value === 'string' ? value : ''
})

const redirectLabel = computed(() => redirect.value || 'home')
const { account, password, visiblePassword, submitting, canSubmit, submit } = useUniLoginForm(redirect.value)

function go(url: string) {
  uni.navigateTo({ url })
}
</script>

<style scoped lang="scss">
.page {
  min-height: 100vh;
  padding: 24rpx;
  background: linear-gradient(180deg, #ffffff 0%, #f6f8fb 100%);
}

.hero {
  padding: 28rpx;
  border-radius: 24rpx;
  background: linear-gradient(180deg, #ffffff 0%, #edf4ff 100%);
  box-shadow: 0 12rpx 34rpx rgba(23, 32, 51, 0.08);
}

.eyebrow {
  display: block;
  font-size: 22rpx;
  line-height: 1.4;
  color: #1677ff;
}

.title {
  display: block;
  margin-top: 8rpx;
  font-size: 40rpx;
  line-height: 1.25;
  font-weight: 600;
  color: #172033;
}

.desc {
  display: block;
  margin-top: 14rpx;
  font-size: 25rpx;
  line-height: 1.5;
  color: #66758a;
}

.card {
  margin-top: 20rpx;
  padding: 24rpx;
  border-radius: 20rpx;
  background: #ffffff;
  box-shadow: 0 10rpx 26rpx rgba(23, 32, 51, 0.06);
}

.tip {
  margin-bottom: 16rpx;
  padding: 16rpx 18rpx;
  border-radius: 16rpx;
  background: #f3f8ff;
  font-size: 22rpx;
  line-height: 1.45;
  color: #5f6b7c;
}

.field-group {
  display: grid;
  gap: 16rpx;
}

.field-item {
  padding: 20rpx;
  border-radius: 16rpx;
  background: #f9fbff;
  border: 1rpx solid #e8edf5;
}

.field-row {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12rpx;
}

.field-label {
  display: block;
  margin-bottom: 10rpx;
  font-size: 24rpx;
  line-height: 1.4;
  color: #4f5c72;
}

.field-input {
  width: 100%;
  height: 72rpx;
  font-size: 28rpx;
  line-height: 72rpx;
  color: #172033;
}

.field-placeholder {
  color: #96a2b4;
}

.toggle {
  font-size: 24rpx;
  color: #1677ff;
}

.meta {
  margin: 16rpx 0 18rpx;
  font-size: 22rpx;
  line-height: 1.45;
  color: #738095;
}

.submit {
  width: 100%;
  height: 84rpx;
  border: 0;
  border-radius: 18rpx;
  background: #1677ff;
  color: #ffffff;
  font-size: 28rpx;
  font-weight: 600;
  box-shadow: 0 12rpx 28rpx rgba(22, 119, 255, 0.22);
}

.submit[disabled] {
  opacity: 0.5;
}

.actions {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 16rpx;
  margin-top: 18rpx;
}

.ghost {
  display: flex;
  align-items: center;
  justify-content: center;
  height: 76rpx;
  border: 0;
  border-radius: 16rpx;
  background: #f7faff;
  color: #4f5c72;
  font-size: 24rpx;
}
</style>
