<template>
  <view class="page">
    <view class="hero-card">
      <view class="hero-main">
        <view class="avatar">
          <image v-if="avatarUrl" class="avatar-image" :src="avatarUrl" mode="aspectFill" />
          <text v-else class="avatar-fallback">{{ avatarFallback }}</text>
        </view>
        <view class="hero-copy">
          <text class="title">{{ profile?.nickName || session.nickName || '未设置昵称' }}</text>
          <text class="desc">资料页继续兼容旧站昵称、头像和手机号键。</text>
        </view>
      </view>
    </view>

    <view v-if="loading" class="panel-list">
      <view v-for="index in 4" :key="index" class="panel panel--skeleton">
        <view class="skeleton-line skeleton-line--title"></view>
      </view>
    </view>

    <view v-else class="panel-list">
      <view class="panel" @click="goEditNickname">
        <view class="panel-row">
          <text class="label">昵称</text>
          <text class="value">{{ profile?.nickName || session.nickName || '未设置' }}</text>
        </view>
        <text class="hint">点击修改昵称</text>
      </view>

      <view class="panel" @click="cycleGender">
        <view class="panel-row">
          <text class="label">性别</text>
          <text class="value value--accent">{{ genderText }}</text>
        </view>
        <text class="hint">点击切换，先保留轻量编辑方式</text>
      </view>

      <view class="panel" @click="goEditMobile">
        <view class="panel-row">
          <text class="label">手机号</text>
          <text class="value">{{ profile?.mobile || session.mobile || '未设置' }}</text>
        </view>
        <text class="hint">点击修改手机号</text>
      </view>

      <view class="panel" @click="goEditPassword">
        <view class="panel-row">
          <text class="label">密码设置</text>
          <text class="value">后续补短信重置</text>
        </view>
        <text class="hint">点击进入短信重置流程</text>
      </view>
    </view>

    <view class="footer">
      <view class="logout-btn" @click="logout">退出当前账号</view>
    </view>
  </view>
</template>

<script setup lang="ts">
import { onShow } from '@dcloudio/uni-app'
import { computed, ref } from 'vue'
import { fetchUserProfile, updateUserProfile, type UserProfilePayload } from '@/entities/user/api'
import { clearSession, getSessionSnapshot } from '@/shared/compat/session-adapter'

const session = ref(getSessionSnapshot())
const loading = ref(true)
const updating = ref(false)
const profile = ref<UserProfilePayload | null>(null)
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
  padding: 20rpx 20rpx 148rpx;
  background: linear-gradient(180deg, #ffffff 0%, #f6f8fb 100%);
}

.hero-card,
.panel {
  border-radius: 12rpx;
  background: #ffffff;
  box-shadow: 0 10rpx 24rpx rgba(23, 32, 51, 0.06);
}

.hero-card,
.panel {
  padding: 22rpx;
}

.hero-main,
.panel-row {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16rpx;
}

.avatar {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 96rpx;
  height: 96rpx;
  border-radius: 999px;
  background: #edf5ff;
  overflow: hidden;
}

.avatar-image {
  width: 100%;
  height: 100%;
}

.avatar-fallback {
  font-size: 30rpx;
  color: #1677ff;
}

.hero-copy {
  min-width: 0;
  flex: 1;
}

.title {
  display: block;
  font-size: 28rpx;
  line-height: 1.3;
  color: #172033;
}

.desc,
.hint {
  display: block;
  margin-top: 8rpx;
  font-size: 22rpx;
  line-height: 1.4;
  color: #748194;
}

.panel-list {
  display: grid;
  gap: 14rpx;
  margin-top: 16rpx;
}

.label,
.value {
  font-size: 24rpx;
  line-height: 1.35;
  color: #172033;
}

.value {
  text-align: right;
  color: #5f6b7c;
}

.value--accent {
  color: #1677ff;
}

.panel--skeleton {
  min-height: 72rpx;
}

.skeleton-line {
  height: 20rpx;
  border-radius: 999px;
  background: #edf1f6;
}

.skeleton-line--title {
  width: 220rpx;
}

.footer {
  position: fixed;
  right: 20rpx;
  bottom: 24rpx;
  left: 20rpx;
}

.logout-btn {
  display: flex;
  align-items: center;
  justify-content: center;
  height: 84rpx;
  border-radius: 12rpx;
  background: #ffffff;
  font-size: 24rpx;
  color: #5f6b7c;
  box-shadow: 0 10rpx 24rpx rgba(23, 32, 51, 0.06);
}
</style>
