<template>
  <view class="page">
    <view class="hero-card">
      <view class="hero-row">
        <view class="hero-avatar">编</view>
        <view class="hero-copy">
          <text class="eyebrow">{{ pageMeta.eyebrow }}</text>
          <text class="title">{{ pageMeta.title }}</text>
        </view>
      </view>
      <text class="desc">{{ pageMeta.description }}</text>
    </view>

    <view v-if="loading" class="panel">
      <view class="skeleton-line skeleton-line--title"></view>
      <view class="skeleton-line skeleton-line--field"></view>
      <view class="skeleton-line skeleton-line--field"></view>
      <view class="skeleton-line skeleton-line--field"></view>
      <view class="skeleton-line skeleton-line--area"></view>
    </view>

    <view v-else class="form-list">
      <view class="panel">
        <text class="field-label">收货人</text>
        <input
          v-model="form.name"
          class="field-input"
          maxlength="20"
          placeholder="请输入收货人姓名"
        />
      </view>

      <view class="panel">
        <text class="field-label">手机号</text>
        <input
          v-model="form.mobile"
          class="field-input"
          type="number"
          maxlength="11"
          placeholder="请输入手机号"
        />
      </view>

      <view class="panel">
        <text class="field-label">省 / 市 / 区</text>
        <view class="grid-row">
          <input v-model="form.province" class="field-input field-input--grid" placeholder="省" />
          <input v-model="form.city" class="field-input field-input--grid" placeholder="市" />
          <input v-model="form.county" class="field-input field-input--grid" placeholder="区" />
        </view>
      </view>

      <view class="panel">
        <text class="field-label">详细地址</text>
        <textarea
          v-model="form.addressDetail"
          class="field-textarea"
          maxlength="80"
          placeholder="街道、门牌号、楼栋信息"
        />
      </view>

      <view class="grid-double">
        <view class="panel">
          <text class="field-label">地区编码</text>
          <input
            v-model="form.areaCode"
            class="field-input"
            placeholder="例如 330106"
          />
        </view>
        <view class="panel">
          <text class="field-label">邮编</text>
          <input
            v-model="form.postalCode"
            class="field-input"
            placeholder="可选"
          />
        </view>
      </view>

      <view class="panel panel--summary">
        <label class="switch-row">
          <checkbox :checked="form.isDefault" color="#4a6fa5" @click="toggleDefault" />
          <text class="switch-label">设为默认地址</text>
        </label>
        <text class="summary-title">地址摘要</text>
        <text class="summary">{{ addressSummary }}</text>
      </view>
    </view>

    <view class="footer footer--double">
      <view
        class="ghost-btn"
        :class="{ 'ghost-btn--disabled': isCreateMode || deleting }"
        @click="remove"
      >
        {{ pageMeta.deleteLabel }}
      </view>
      <view class="primary-btn" :class="{ 'primary-btn--disabled': saving }" @click="submit">
        {{ pageMeta.submitLabel }}
      </view>
    </view>
  </view>
</template>

<script setup lang="ts">
import { computed, reactive, ref } from 'vue'
import type { AddressForm } from '@/entities/user/api'
import { deleteAddress, fetchAddressDetail, saveAddress } from '@/entities/user/api'
import { resolveAddressEditorMeta } from '../page-display-utils'

type UniPageWithOptions = {
  options?: Record<string, unknown>
}

const pageOptions = (getCurrentPages()[getCurrentPages().length - 1] as UniPageWithOptions | undefined)?.options || {}
const addressId = typeof pageOptions.addressId === 'string' ? pageOptions.addressId : '-1'

const isCreateMode = computed(() => !addressId || addressId === '-1' || addressId === '0')
const loading = ref(false)
const saving = ref(false)
const deleting = ref(false)
const form = reactive<AddressForm>({
  id: 0,
  name: '',
  mobile: '',
  province: '',
  city: '',
  county: '',
  addressDetail: '',
  areaCode: '',
  postalCode: '',
  addressAll: '',
  isDefault: true,
})

const addressSummary = computed(() => {
  const area = [form.province, form.city, form.county].filter(Boolean).join(' ')
  return `${area} ${form.addressDetail}`.trim() || '尚未填写完整'
})
const pageMeta = computed(() => resolveAddressEditorMeta(isCreateMode.value))

bootstrap()

async function bootstrap() {
  if (isCreateMode.value) {
    return
  }

  loading.value = true

  try {
    const detail = await fetchAddressDetail(addressId)
    patchForm(detail)
  } catch (error) {
    console.error(error)
  } finally {
    loading.value = false
  }
}

function patchForm(payload: AddressForm) {
  form.id = Number(payload.id || 0)
  form.name = payload.name || ''
  form.mobile = payload.mobile || ''
  form.province = payload.province || ''
  form.city = payload.city || ''
  form.county = payload.county || ''
  form.addressDetail = payload.addressDetail || ''
  form.areaCode = payload.areaCode || ''
  form.postalCode = payload.postalCode || ''
  form.addressAll = payload.addressAll || ''
  form.isDefault = payload.isDefault ?? true
}

function toggleDefault() {
  form.isDefault = !form.isDefault
}

function validate() {
  if (!form.name.trim()) {
    toast('请填写收货人')
    return false
  }

  if (!/^1\d{10}$/.test(form.mobile.trim())) {
    toast('请填写正确的手机号')
    return false
  }

  if (!form.province.trim() || !form.city.trim() || !form.county.trim()) {
    toast('请补全省市区')
    return false
  }

  if (!form.addressDetail.trim()) {
    toast('请填写详细地址')
    return false
  }

  if (!form.areaCode.trim()) {
    toast('请填写地区编码')
    return false
  }

  form.addressAll = addressSummary.value
  return true
}

async function submit() {
  if (saving.value || !validate()) {
    return
  }

  saving.value = true

  try {
    await saveAddress({
      ...form,
      id: isCreateMode.value ? 0 : form.id,
    })

    toast('地址已保存')

    setTimeout(() => {
      uni.navigateBack({
        delta: 1,
      })
    }, 320)
  } catch (error) {
    console.error(error)
    toast('保存地址失败')
  } finally {
    saving.value = false
  }
}

async function remove() {
  if (isCreateMode.value || !form.id || deleting.value) {
    return
  }

  deleting.value = true

  try {
    await deleteAddress(Number(form.id))
    toast('地址已删除')
    setTimeout(() => {
      uni.navigateBack({
        delta: 1,
      })
    }, 320)
  } catch (error) {
    console.error(error)
    toast('删除地址失败')
  } finally {
    deleting.value = false
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

.panel {
  border-radius: var(--sf-radius-card);
  background: #ffffff;
  border: 1px solid rgb(var(--sf-color-line));
  box-shadow: var(--sf-shadow-card);
}

.hero-card {
  border-radius: var(--sf-radius-card);
  color: #ffffff;
  background: linear-gradient(145deg, rgb(var(--sf-color-brand)) 0%, rgb(var(--sf-color-brand-light)) 100%);
  box-shadow: var(--sf-shadow-brand);
}

.hero-card,
.panel {
  padding: 14rpx 16rpx;
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
.summary,
.switch-label,
.summary-title {
  display: block;
  margin-top: 8rpx;
  font-size: 18rpx;
  line-height: 1.42;
}

.desc {
  color: rgba(255, 255, 255, 0.88);
}

.field-label,
.summary,
.switch-label,
.summary-title {
  color: rgb(var(--sf-color-text-secondary));
}

.form-list,
.grid-double {
  display: grid;
  gap: 8rpx;
  margin-top: 10rpx;
}

.grid-double {
  grid-template-columns: repeat(2, minmax(0, 1fr));
}

.grid-row {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 6rpx;
  margin-top: 8rpx;
}

.field-input,
.field-textarea {
  width: 100%;
  margin-top: 8rpx;
  border-radius: var(--sf-radius-card);
  background: rgb(var(--sf-color-page));
  padding: 0 14rpx;
  font-size: 20rpx;
  color: rgb(var(--sf-color-ink));
  box-sizing: border-box;
}

.field-input {
  height: 66rpx;
}

.field-input--grid {
  margin-top: 0;
}

.field-textarea {
  min-height: 124rpx;
  padding-top: 14rpx;
  padding-bottom: 14rpx;
  line-height: 1.45;
}

.switch-row {
  display: flex;
  align-items: center;
  gap: 8rpx;
}

.panel--summary {
  background: linear-gradient(180deg, #ffffff 0%, rgb(var(--sf-color-page)) 100%);
}

.summary-title {
  margin-top: 10rpx;
}

.summary {
  margin-top: 4rpx;
  color: rgb(var(--sf-color-ink));
}

.skeleton-line {
  height: 16rpx;
  margin-top: 8rpx;
  border-radius: 999px;
  background: rgb(var(--sf-color-divider));
}

.skeleton-line--title {
  width: 132rpx;
  margin-top: 0;
}

.skeleton-line--field {
  height: 66rpx;
}

.skeleton-line--area {
  height: 124rpx;
}

.footer {
  position: fixed;
  right: 14rpx;
  bottom: 16rpx;
  left: 14rpx;
}

.footer--double {
  display: grid;
  grid-template-columns: 1fr 1.15fr;
  gap: 8rpx;
}

.ghost-btn,
.primary-btn {
  display: flex;
  align-items: center;
  justify-content: center;
  height: 72rpx;
  border-radius: var(--sf-radius-card);
  font-size: 20rpx;
}

.ghost-btn {
  background: #ffffff;
  color: rgb(var(--sf-color-text-secondary));
  border: 1px solid rgb(var(--sf-color-line));
  box-shadow: var(--sf-shadow-soft);
}

.primary-btn {
  background: linear-gradient(135deg, rgb(var(--sf-color-brand)) 0%, rgb(var(--sf-color-brand-light)) 100%);
  color: #ffffff;
  box-shadow: var(--sf-shadow-brand);
}

.ghost-btn--disabled,
.primary-btn--disabled {
  opacity: 0.45;
}
</style>
