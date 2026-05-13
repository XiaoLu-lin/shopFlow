<template>
  <view class="page">
    <view class="hero-card">
      <text class="title">修改昵称</text>
      <text class="desc">继续沿用旧站昵称字段，保存后资料页会读取最新昵称。</text>
    </view>

    <view class="panel">
      <text class="field-label">昵称</text>
      <input
        v-model="nickname"
        class="field-input"
        maxlength="20"
        placeholder="请输入昵称"
      />
    </view>

    <view class="footer">
      <view class="primary-btn" :class="{ 'primary-btn--disabled': !canSubmit || saving }" @click="submit">
        {{ saving ? '保存中...' : '保存昵称' }}
      </view>
    </view>
  </view>
</template>

<script setup lang="ts">
import { computed, ref } from 'vue'
import { updateAuthProfile } from '@/entities/auth/api'
import { fetchUserProfile } from '@/entities/user/api'
import { getSessionSnapshot, persistLegacyProfile } from '@/shared/compat/session-adapter'

const session = getSessionSnapshot()
const nickname = ref(session.nickName || '')
const saving = ref(false)
const canSubmit = computed(() => nickname.value.trim().length > 0)

bootstrap()

async function bootstrap() {
  try {
    const profile = await fetchUserProfile()
    nickname.value = profile.nickName || session.nickName || ''
  } catch (error) {
    console.error(error)
  }
}

async function submit() {
  if (!canSubmit.value || saving.value) {
    return
  }

  saving.value = true

  try {
    await updateAuthProfile({
      nickname: nickname.value.trim(),
    })
    persistLegacyProfile({
      nickName: nickname.value.trim(),
    })
    uni.showToast({
      title: '昵称已保存',
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
      title: '保存昵称失败',
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
