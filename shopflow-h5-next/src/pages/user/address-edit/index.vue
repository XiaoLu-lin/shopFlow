<template>
  <view class="page">
    <view class="hero-card">
      <text class="title">{{ isCreateMode ? '新增地址' : '编辑地址' }}</text>
      <text class="desc">继续沿用旧站地址字段结构，先保持轻量表单，后面再接地区选择器。</text>
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

      <view class="panel">
        <label class="switch-row">
          <checkbox :checked="form.isDefault" color="#1677ff" @click="toggleDefault" />
          <text class="switch-label">设为默认地址</text>
        </label>
        <text class="summary">当前地址摘要：{{ addressSummary }}</text>
      </view>
    </view>

    <view class="footer footer--double">
      <view
        class="ghost-btn"
        :class="{ 'ghost-btn--disabled': isCreateMode || deleting }"
        @click="remove"
      >
        删除地址
      </view>
      <view class="primary-btn" :class="{ 'primary-btn--disabled': saving }" @click="submit">
        保存地址
      </view>
    </view>
  </view>
</template>

<script setup lang="ts">
import { computed, reactive, ref } from 'vue'
import type { AddressForm } from '@/entities/user/api'
import { deleteAddress, fetchAddressDetail, saveAddress } from '@/entities/user/api'

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

  if (!/^1\\d{10}$/.test(form.mobile.trim())) {
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
.summary,
.switch-label {
  display: block;
  margin-top: 8rpx;
  font-size: 22rpx;
  line-height: 1.4;
  color: #748194;
}

.form-list,
.grid-double {
  display: grid;
  gap: 14rpx;
  margin-top: 16rpx;
}

.grid-double {
  grid-template-columns: repeat(2, minmax(0, 1fr));
}

.grid-row {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 12rpx;
  margin-top: 14rpx;
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

.field-input--grid {
  margin-top: 0;
}

.field-textarea {
  min-height: 152rpx;
  padding-top: 18rpx;
  padding-bottom: 18rpx;
  line-height: 1.45;
}

.switch-row {
  display: flex;
  align-items: center;
  gap: 12rpx;
}

.summary {
  margin-top: 14rpx;
}

.skeleton-line {
  height: 20rpx;
  margin-top: 12rpx;
  border-radius: 999px;
  background: #edf1f6;
}

.skeleton-line--title {
  width: 180rpx;
  margin-top: 0;
}

.skeleton-line--field {
  height: 78rpx;
}

.skeleton-line--area {
  height: 152rpx;
}

.footer {
  position: fixed;
  right: 20rpx;
  bottom: 24rpx;
  left: 20rpx;
}

.footer--double {
  display: grid;
  grid-template-columns: 1fr 1.2fr;
  gap: 12rpx;
}

.ghost-btn,
.primary-btn {
  display: flex;
  align-items: center;
  justify-content: center;
  height: 84rpx;
  border-radius: 12rpx;
  font-size: 24rpx;
}

.ghost-btn {
  background: #ffffff;
  color: #5f6b7c;
  box-shadow: 0 10rpx 24rpx rgba(23, 32, 51, 0.06);
}

.primary-btn {
  background: #1677ff;
  color: #ffffff;
  box-shadow: 0 14rpx 30rpx rgba(22, 119, 255, 0.2);
}

.ghost-btn--disabled,
.primary-btn--disabled {
  opacity: 0.45;
}
</style>
