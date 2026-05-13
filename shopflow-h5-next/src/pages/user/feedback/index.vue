<template>
  <view class="page">
    <view class="hero-card">
      <text class="title">意见反馈</text>
      <text class="desc">保留旧站反馈类型、内容和联系方式三段式提交。</text>
    </view>

    <view class="panel">
      <text class="field-label">反馈类型</text>
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

const session = getSessionSnapshot()
const mobile = ref(session.mobile || '')
const content = ref('')
const feedType = ref('商品相关')
const submitting = ref(false)
const types = ['商品相关', '功能异常', '优化建议', '其他']

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

.title {
  display: block;
  font-size: 28rpx;
  line-height: 1.3;
  color: #172033;
}

.desc,
.field-label,
.count {
  display: block;
  margin-top: 8rpx;
  font-size: 22rpx;
  line-height: 1.4;
  color: #748194;
}

.panel {
  margin-top: 16rpx;
}

.type-row {
  display: flex;
  flex-wrap: wrap;
  gap: 12rpx;
  margin-top: 14rpx;
}

.type-chip {
  padding: 12rpx 18rpx;
  border-radius: 10rpx;
  background: #f6f8fb;
  font-size: 22rpx;
  color: #5f6b7c;
}

.type-chip--active {
  background: #1677ff;
  color: #ffffff;
}

.field-input,
.field-textarea {
  width: 100%;
  margin-top: 14rpx;
  border-radius: 10rpx;
  background: #f6f8fb;
  padding: 0 18rpx;
  font-size: 24rpx;
  color: #172033;
  box-sizing: border-box;
}

.field-input {
  height: 78rpx;
}

.field-textarea {
  min-height: 220rpx;
  padding-top: 18rpx;
  padding-bottom: 18rpx;
  line-height: 1.5;
}

.count {
  margin-top: 10rpx;
  text-align: right;
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
