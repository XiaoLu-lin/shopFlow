<template>
  <view class="page">
    <view class="hero-card">
      <view class="hero-row">
        <view class="hero-avatar">反</view>
        <view class="hero-copy">
          <text class="eyebrow">{{ hero.eyebrow }}</text>
          <text class="title">{{ hero.title }}</text>
        </view>
      </view>
      <text class="desc">{{ hero.description }}</text>
    </view>

    <view class="panel">
      <text class="field-label">问题类型</text>
      <view class="type-row">
        <view
          v-for="type in types"
          :key="type"
          class="type-chip"
          :class="{ 'type-chip--active': type === feedType }"
          @click="feedType = type"
        >
          {{ type }}
        </view>
      </view>
    </view>

    <view class="panel">
      <text class="field-label">反馈内容</text>
      <textarea
        v-model="content"
        class="field-textarea"
        maxlength="200"
        placeholder="对商品、页面、服务或流程的建议都可以写在这里"
      />
      <text class="count">{{ content.length }}/200</text>
    </view>

    <view class="panel">
      <text class="field-label">联系方式</text>
      <input
        v-model="mobile"
        class="field-input"
        type="number"
        maxlength="11"
        placeholder="请输入联系电话"
      />
    </view>

    <view class="footer">
      <view class="primary-btn" :class="{ 'primary-btn--disabled': !canSubmit || submitting }" @click="submit">
        {{ submitting ? '提交中...' : '提交反馈' }}
      </view>
    </view>
  </view>
</template>

<script setup lang="ts">
import { computed, ref } from 'vue'
import { submitUserFeedback } from '@/entities/user/api'
import { getSessionSnapshot } from '@/shared/compat/session-adapter'
import { mobileReg } from '@/shared/utils/validate'
import { resolveUserPageHero } from '../page-display-utils'

const session = getSessionSnapshot()
const mobile = ref(session.mobile || '')
const content = ref('')
const feedType = ref('商品相关')
const submitting = ref(false)
const types = ['商品相关', '功能异常', '优化建议', '其他']
const hero = resolveUserPageHero('feedback')

const canSubmit = computed(() => mobileReg.test(mobile.value.trim()) && content.value.trim().length > 0)

async function submit() {
  if (submitting.value) {
    return
  }

  if (!mobileReg.test(mobile.value.trim())) {
    toast('请输入正确的联系电话')
    return
  }

  if (!content.value.trim()) {
    toast('请输入反馈内容')
    return
  }

  submitting.value = true

  try {
    await submitUserFeedback({
      mobile: mobile.value.trim(),
      feedType: feedType.value,
      content: content.value.trim(),
    })

    toast('感谢你的反馈')
    setTimeout(() => {
      uni.navigateBack({
        delta: 1,
      })
    }, 320)
  } catch (error) {
    console.error(error)
    toast('提交反馈失败')
  } finally {
    submitting.value = false
  }
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
.field-label,
.count {
  display: block;
  margin-top: 8rpx;
  font-size: 18rpx;
  line-height: 1.42;
}

.desc {
  color: rgba(255, 255, 255, 0.88);
}

.field-label,
.count {
  color: rgb(var(--sf-color-text-secondary));
}

.panel {
  margin-top: 10rpx;
  padding: 12rpx 14rpx;
}

.type-row {
  display: flex;
  flex-wrap: wrap;
  gap: 8rpx;
  margin-top: 12rpx;
}

.type-chip {
  padding: 8rpx 14rpx;
  border-radius: 999px;
  background: rgb(var(--sf-color-page));
  font-size: 18rpx;
  color: rgb(var(--sf-color-text-secondary));
}

.type-chip--active {
  background: rgb(var(--sf-color-brand));
  color: #ffffff;
}

.field-input,
.field-textarea {
  width: 100%;
  margin-top: 12rpx;
  padding: 0 16rpx;
  border-radius: var(--sf-radius-card);
  background: rgb(var(--sf-color-page));
  font-size: 20rpx;
  color: rgb(var(--sf-color-ink));
  box-sizing: border-box;
}

.field-input {
  height: 76rpx;
}

.field-textarea {
  min-height: 220rpx;
  padding-top: 16rpx;
  padding-bottom: 16rpx;
  line-height: 1.5;
}

.count {
  margin-top: 10rpx;
  text-align: right;
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
