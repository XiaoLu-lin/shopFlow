import { mobileReg } from '@/shared/utils/validate'

export type AuthActionType = 'login' | 'register' | 'reset'

export interface AuthActionFormState {
  mobile: string
  code: string
  username?: string
  password: string
  confirmPassword?: string
}

export function resolveAuthActionHero(type: AuthActionType) {
  if (type === 'register') {
    return {
      eyebrow: 'ShopFlow',
      title: '注册账号',
      description: '用手机号完成注册，流程会压成一页，体验更连贯。',
    }
  }

  if (type === 'reset') {
    return {
      eyebrow: 'ShopFlow',
      title: '找回密码',
      description: '在同一页完成验证码校验和新密码设置，不再拆成迁移期多页面。',
    }
  }

  return {
    eyebrow: 'ShopFlow',
    title: '账号登录',
    description: '',
  }
}

export function validateAuthActionForm(type: Exclude<AuthActionType, 'login'>, state: AuthActionFormState) {
  if (type === 'register') {
    if (!state.mobile || !state.code || !state.username || !state.password || !state.confirmPassword) {
      return '请完整填写注册信息'
    }
  } else if (!state.mobile || !state.code || !state.password || !state.confirmPassword) {
    return '请完整填写重置信息'
  }

  if (!mobileReg.test(state.mobile)) {
    return '请输入正确的手机号'
  }

  if (state.password !== state.confirmPassword) {
    return '两次密码输入不一致'
  }

  return ''
}

export function getAuthActionSuccessMessage(type: Exclude<AuthActionType, 'login'>) {
  return type === 'register' ? '注册成功，请登录' : '密码重置成功，请登录'
}
