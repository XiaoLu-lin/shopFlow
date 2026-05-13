import type { ApiEnvelope } from '@/shared/utils/contracts'
import { getApiClient } from '@/shared/request'

export interface LegacyLoginPayload {
  username?: string
  mobile?: string
  email?: string
  password: string
}

export interface LoginUserInfo {
  nickName?: string
  avatarUrl?: string
}

export interface LoginResponse {
  token?: string
  userToken?: string
  userInfo?: LoginUserInfo
}

export interface AuthCaptchaPayload {
  mobile: string
}

export interface RegisterPayload {
  code: string
  username: string
  mobile: string
  password: string
}

export interface ResetPasswordPayload {
  mobile: string
  code: string
  password: string
}

export interface AuthProfilePayload {
  nickname?: string
  gender?: number
  avatar?: string
}

export async function loginByLegacyAccount(payload: LegacyLoginPayload) {
  const response = await getApiClient().post<ApiEnvelope<LoginResponse>>('/auth/login_legacy', payload)
  return response.data
}

export async function requestRegisterCaptcha(payload: AuthCaptchaPayload) {
  await getApiClient().post('/auth/captcha/mobile', payload)
}

export async function submitRegister(payload: RegisterPayload) {
  await getApiClient().post('/auth/register', payload)
}

export async function requestResetCaptcha(payload: AuthCaptchaPayload) {
  await getApiClient().post('/auth/captcha/mobile', payload)
}

export async function submitPasswordReset(payload: ResetPasswordPayload) {
  await getApiClient().post('/auth/reset', payload)
}

export async function requestSceneCaptcha(payload: AuthCaptchaPayload & { type: string }) {
  await getApiClient().post('/auth/captcha/mobile', payload)
}

export async function updateAuthProfile(payload: AuthProfilePayload) {
  await getApiClient().post('/auth/profile', payload)
}
