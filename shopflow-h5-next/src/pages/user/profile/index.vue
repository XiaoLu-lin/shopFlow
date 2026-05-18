<template>
  <view class="page">
    <view class="hero-card">
      <view class="hero-row">
        <view class="hero-avatar">
          <image v-if="avatarUrl" class="avatar-image" :src="avatarUrl" mode="aspectFill" />
          <text v-else class="avatar-fallback">{{ avatarFallback }}</text>
        </view>
        <view class="hero-copy">
          <text class="eyebrow">{{ hero.eyebrow }}</text>
          <text class="title">{{ hero.title }}</text>
        </view>
      </view>
      <text class="desc">{{ hero.description }}</text>
    </view>

    <view v-if="loading" class="profile-list">
      <view v-for="index in 4" :key="index" class="profile-card profile-card--skeleton">
        <view class="skeleton-line skeleton-line--title"></view>
        <view class="skeleton-line skeleton-line--short"></view>
      </view>
    </view>

    <view v-else class="profile-list">
      <view class="profile-card" @click="goEditNickname">
        <view class="profile-row">
          <view class="profile-copy">
            <text class="profile-label">昵称</text>
            <text class="profile-hint">更新展示名</text>
          </view>
          <view class="profile-meta">
            <text class="profile-value">{{ profile?.nickName || session.nickName || '未设置' }}</text>
            <text class="profile-arrow">›</text>
          </view>
        </view>
      </view>

      <view class="profile-card" @click="cycleGender">
        <view class="profile-row">
          <view class="profile-copy">
            <text class="profile-label">性别</text>
            <text class="profile-hint">点击切换当前资料项</text>
          </view>
          <view class="profile-meta">
            <text class="profile-value profile-value--accent">{{ genderText }}</text>
            <text class="profile-arrow">›</text>
          </view>
        </view>
      </view>

      <view class="profile-card" @click="goEditMobile">
        <view class="profile-row">
          <view class="profile-copy">
            <text class="profile-label">手机号</text>
            <text class="profile-hint">保持登录信息一致</text>
          </view>
          <view class="profile-meta">
            <text class="profile-value">{{ profile?.mobile || session.mobile || '未设置' }}</text>
            <text class="profile-arrow">›</text>
          </view>
        </view>
      </view>

      <view class="profile-card" @click="goEditPassword">
        <view class="profile-row">
          <view class="profile-copy">
            <text class="profile-label">密码设置</text>
            <text class="profile-hint">短信重置流程</text>
          </view>
          <view class="profile-meta">
            <text class="profile-value">去设置</text>
            <text class="profile-arrow">›</text>
          </view>
        </view>
      </view>
    </view>

    <view class="footer">
      <view class="primary-btn primary-btn--quiet" @click="logout">退出当前账号</view>
    </view>
  </view>
</template>

<script setup lang="ts">
import { onShow } from '@dcloudio/uni-app'
import { computed, ref } from 'vue'
import { fetchUserProfile, updateUserProfile, type UserProfilePayload } from '@/entities/user/api'
import { clearSession, getSessionSnapshot } from '@/shared/compat/session-adapter'
import { resolveUserPageHero } from '../page-display-utils'

const session = ref(getSessionSnapshot())
const loading = ref(true)
const updating = ref(false)
const profile = ref<UserProfilePayload | null>(null)
const hero = resolveUserPageHero('profile')
const genderOptions = [
  { label: '保密', value: 0 },
  { label: '男', value: 1 },
  { label: '女', value: 2 },
]

const avatarUrl = computed(() => profile.value?.avatar || session.value.avatar)
const avatarFallback = computed(() => (profile.value?.nickName || session.value.nickName || '我').slice(0, 1))
const genderText = computed(() => genderOptions.find((item) => item.value === (profile.value?.gender ?? 0))?.label || '保密')

bootstrap()
onShow(() => {
  session.value = getSessionSnapshot()
  void bootstrap()
})

async function bootstrap() {
  loading.value = true

  try {
    profile.value = await fetchUserProfile()
  } catch (error) {
    console.error(error)
  } finally {
    loading.value = false
  }
}

async function cycleGender() {
  if (!profile.value || updating.value) {
    return
  }

  const currentIndex = genderOptions.findIndex((item) => item.value === (profile.value?.gender ?? 0))
  const next = genderOptions[(currentIndex + 1) % genderOptions.length]

  updating.value = true

  try {
    await updateUserProfile({ gender: next.value })
    profile.value = {
      ...profile.value,
      gender: next.value,
    }
    uni.showToast({
      title: `已切换为${next.label}`,
      icon: 'none',
    })
  } catch (error) {
    console.error(error)
    uni.showToast({
      title: '更新性别失败',
      icon: 'none',
    })
  } finally {
    updating.value = false
  }
}

function logout() {
  clearSession()
  uni.showToast({
    title: '已退出登录',
    icon: 'none',
  })
  setTimeout(() => {
    uni.reLaunch({
      url: '/pages/home/index',
    })
  }, 320)
}

function goEditNickname() {
  uni.navigateTo({
    url: '/pages/user/profile-edit-nickname/index',
  })
}

function goEditMobile() {
  uni.navigateTo({
    url: '/pages/user/profile-edit-mobile/index',
  })
}

function goEditPassword() {
  uni.navigateTo({
    url: '/pages/user/profile-edit-password/index',
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
.profile-card {
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
.profile-row,
.profile-meta {
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
  overflow: hidden;
  border-radius: var(--sf-radius-card);
  background: linear-gradient(145deg, rgba(255, 255, 255, 0.96), rgba(229, 237, 246, 0.92));
}

.avatar-image {
  width: 100%;
  height: 100%;
}

.avatar-fallback {
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

.desc {
  display: block;
  margin-top: 8rpx;
  font-size: 18rpx;
  line-height: 1.42;
  color: rgba(255, 255, 255, 0.88);
}

.profile-list {
  display: grid;
  gap: 8rpx;
  margin-top: 10rpx;
}

.profile-card {
  padding: 12rpx 14rpx;
}

.profile-row {
  justify-content: space-between;
  gap: 12rpx;
}

.profile-copy {
  min-width: 0;
  flex: 1;
}

.profile-meta {
  flex-shrink: 0;
  gap: 8rpx;
}

.profile-label,
.profile-value {
  display: block;
  font-size: 20rpx;
  line-height: 1.3;
  color: rgb(var(--sf-color-ink));
}

.profile-label {
  font-weight: 600;
}

.profile-hint {
  display: block;
  margin-top: 6rpx;
  font-size: 17rpx;
  line-height: 1.38;
  color: rgb(var(--sf-color-text-secondary));
}

.profile-value {
  max-width: 220rpx;
  text-align: right;
  color: rgb(var(--sf-color-text-secondary));
}

.profile-value--accent {
  color: rgb(var(--sf-color-brand));
}

.profile-arrow {
  font-size: 24rpx;
  line-height: 1;
  color: rgb(var(--sf-color-text-hint));
}

.profile-card--skeleton {
  padding: 12rpx 14rpx;
}

.skeleton-line {
  height: 16rpx;
  margin-top: 10rpx;
  border-radius: 999px;
  background: rgb(var(--sf-color-divider));
}

.skeleton-line--title {
  width: 168rpx;
  margin-top: 0;
}

.skeleton-line--short {
  width: 240rpx;
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

.primary-btn--quiet {
  background: linear-gradient(145deg, rgb(var(--sf-color-brand-deep)) 0%, rgb(var(--sf-color-brand)) 100%);
}
</style>
