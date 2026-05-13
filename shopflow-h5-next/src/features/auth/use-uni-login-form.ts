import { computed, ref } from 'vue'
import { loginByLegacyAccount } from '@/entities/auth/api'
import { persistLegacyLoginSession } from '@/shared/compat/session-adapter'
import { resolveErrorMessage } from '@/shared/compat/errors'
import { redirectAfterLogin } from '@/shared/platform/navigation'
import { emailReg, mobileReg } from '@/shared/utils/validate'

export function useUniLoginForm(redirect?: string) {
  const account = ref('')
  const password = ref('')
  const visiblePassword = ref(false)
  const submitting = ref(false)

  const canSubmit = computed(() => account.value.trim().length > 0 && password.value.trim().length > 0)

  async function submit() {
    if (!canSubmit.value || submitting.value) {
      return
    }

    submitting.value = true
    try {
      const response = await loginByLegacyAccount(resolvePayload())
      persistLegacyLoginSession(response)
      uni.showToast({
        title: '登录成功',
        icon: 'success',
      })
      redirectAfterLogin(redirect)
    } catch (error) {
      uni.showToast({
        title: resolveErrorMessage(error),
        icon: 'none',
      })
    } finally {
      submitting.value = false
    }
  }

  function resolvePayload() {
    const payload: {
      username?: string
      mobile?: string
      email?: string
      password: string
    } = {
      password: password.value,
    }

    if (mobileReg.test(account.value)) {
      payload.mobile = account.value
    } else if (emailReg.test(account.value)) {
      payload.email = account.value
    } else {
      payload.username = account.value
    }

    return payload
  }

  return {
    account,
    password,
    visiblePassword,
    submitting,
    canSubmit,
    submit,
  }
}
