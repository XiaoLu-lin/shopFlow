import { computed, ref } from 'vue'
import {
  getAuthActionSuccessMessage,
  type AuthActionFormState,
  type AuthActionType,
  validateAuthActionForm,
} from './auth-form-display-utils'
import {
  requestRegisterCaptcha,
  requestResetCaptcha,
  submitPasswordReset,
  submitRegister,
} from '@/entities/auth/api'
import { navigateBack } from '@/shared/platform/navigation'
import { resolveErrorMessage } from '@/shared/compat/errors'

export function useUniAuthActionForm(type: Exclude<AuthActionType, 'login'>) {
  const mobile = ref('')
  const code = ref('')
  const username = ref('')
  const password = ref('')
  const confirmPassword = ref('')
  const counting = ref(false)
  const countdown = ref(0)
  const submitting = ref(false)
  let timer: ReturnType<typeof setInterval> | null = null

  const isRegister = computed(() => type === 'register')
  const canSubmit = computed(() => {
    if (isRegister.value) {
      return Boolean(
        mobile.value.trim() &&
        code.value.trim() &&
        username.value.trim() &&
        password.value.trim() &&
        confirmPassword.value.trim(),
      )
    }

    return Boolean(
      mobile.value.trim() &&
      code.value.trim() &&
      password.value.trim() &&
      confirmPassword.value.trim(),
    )
  })

  function buildState(): AuthActionFormState {
    return {
      mobile: mobile.value.trim(),
      code: code.value.trim(),
      username: username.value.trim(),
      password: password.value,
      confirmPassword: confirmPassword.value,
    }
  }

  async function requestCaptcha() {
    const trimmedMobile = mobile.value.trim()
    if (!trimmedMobile) {
      uni.showToast({
        title: '请输入手机号',
        icon: 'none',
      })
      return
    }

    const validationMessage = validateAuthActionForm(type, {
      mobile: trimmedMobile,
      code: 'placeholder',
      username: isRegister.value ? 'placeholder' : undefined,
      password: 'placeholder',
      confirmPassword: 'placeholder',
    })

    if (validationMessage && validationMessage !== '两次密码输入不一致') {
      uni.showToast({
        title: validationMessage,
        icon: 'none',
      })
      return
    }

    if (counting.value) {
      return
    }

    try {
      if (type === 'register') {
        await requestRegisterCaptcha({ mobile: trimmedMobile })
      } else {
        await requestResetCaptcha({ mobile: trimmedMobile })
      }

      startCountdown()
      uni.showToast({
        title: '发送成功',
        icon: 'success',
      })
    } catch (error) {
      uni.showToast({
        title: resolveErrorMessage(error),
        icon: 'none',
      })
    }
  }

  async function submit() {
    if (!canSubmit.value || submitting.value) {
      return
    }

    const validationMessage = validateAuthActionForm(type, buildState())
    if (validationMessage) {
      uni.showToast({
        title: validationMessage,
        icon: 'none',
      })
      return
    }

    submitting.value = true
    try {
      if (type === 'register') {
        await submitRegister({
          mobile: mobile.value.trim(),
          code: code.value.trim(),
          username: username.value.trim(),
          password: password.value,
        })
      } else {
        await submitPasswordReset({
          mobile: mobile.value.trim(),
          code: code.value.trim(),
          password: password.value,
        })
      }

      uni.showToast({
        title: getAuthActionSuccessMessage(type),
        icon: 'success',
      })

      stopCountdown()
      setTimeout(() => {
        navigateBack()
      }, 180)
    } catch (error) {
      uni.showToast({
        title: resolveErrorMessage(error),
        icon: 'none',
      })
    } finally {
      submitting.value = false
    }
  }

  function startCountdown() {
    stopCountdown()
    counting.value = true
    countdown.value = 60
    timer = setInterval(() => {
      countdown.value -= 1
      if (countdown.value <= 0) {
        stopCountdown()
      }
    }, 1000)
  }

  function stopCountdown() {
    if (timer) {
      clearInterval(timer)
      timer = null
    }
    counting.value = false
    countdown.value = 0
  }

  return {
    mobile,
    code,
    username,
    password,
    confirmPassword,
    counting,
    countdown,
    submitting,
    canSubmit,
    isRegister,
    requestCaptcha,
    submit,
  }
}
