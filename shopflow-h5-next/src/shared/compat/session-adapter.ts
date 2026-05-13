import { SESSION_KEYS } from './session-keys'
import { resolveStorage, type StorageLike } from '@/shared/platform/storage'

export interface SessionSnapshot {
  token: string
  tenantToken: string
  avatar: string
  nickName: string
  mobile: string
}

function read(storage: Storage, key: string): string {
  return storage.getItem(key) || ''
}

function readValue(storage: StorageLike, key: string): string {
  return storage.getItem(key) || ''
}

export function getSessionSnapshot(storage: StorageLike = resolveStorage()): SessionSnapshot {
  return {
    token: readValue(storage, SESSION_KEYS.authorization),
    tenantToken: readValue(storage, SESSION_KEYS.tenantToken),
    avatar: readValue(storage, SESSION_KEYS.avatar),
    nickName: readValue(storage, SESSION_KEYS.nickName),
    mobile: readValue(storage, SESSION_KEYS.mobile),
  }
}

export function persistLegacyLoginSession(
  payload: unknown,
  storage: StorageLike = resolveStorage(),
): SessionSnapshot {
  const normalized = normalizeLoginPayload(payload)

  if (normalized.token) {
    storage.setItem(SESSION_KEYS.authorization, normalized.token)
  }
  if (normalized.avatar) {
    storage.setItem(SESSION_KEYS.avatar, normalized.avatar)
  }
  if (normalized.nickName) {
    storage.setItem(SESSION_KEYS.nickName, normalized.nickName)
  }
  if (normalized.mobile) {
    storage.setItem(SESSION_KEYS.mobile, normalized.mobile)
  }

  return {
    token: normalized.token,
    tenantToken: readValue(storage, SESSION_KEYS.tenantToken),
    avatar: normalized.avatar,
    nickName: normalized.nickName,
    mobile: normalized.mobile,
  }
}

export function persistTenantToken(token: string, storage: StorageLike = resolveStorage()): string {
  const nextToken = token.trim()
  if (nextToken) {
    storage.setItem(SESSION_KEYS.tenantToken, nextToken)
  }
  return nextToken
}

export function persistLegacyProfile(
  payload: {
    nickName?: string
    avatar?: string
    mobile?: string
  },
  storage: StorageLike = resolveStorage(),
) {
  const nextNickName = payload.nickName?.trim()
  const nextAvatar = payload.avatar?.trim()
  const nextMobile = payload.mobile?.trim()

  if (nextNickName) {
    storage.setItem(SESSION_KEYS.nickName, nextNickName)
  }

  if (nextAvatar) {
    storage.setItem(SESSION_KEYS.avatar, nextAvatar)
  }

  if (nextMobile) {
    storage.setItem(SESSION_KEYS.mobile, nextMobile)
  }

  return getSessionSnapshot(storage)
}

export function clearSession(storage: StorageLike = resolveStorage()) {
  storage.removeItem(SESSION_KEYS.authorization)
  storage.removeItem(SESSION_KEYS.avatar)
  storage.removeItem(SESSION_KEYS.nickName)
  storage.removeItem(SESSION_KEYS.mobile)
}

export function readFlowContext(storage: StorageLike = resolveStorage()) {
  return {
    addressId: readValue(storage, SESSION_KEYS.addressId),
    cartId: readValue(storage, SESSION_KEYS.cartId),
    couponId: readValue(storage, SESSION_KEYS.couponId),
    userCouponId: readValue(storage, SESSION_KEYS.userCouponId),
  }
}

export function writeFlowContext(
  payload: Partial<Record<(typeof SESSION_KEYS)[keyof typeof SESSION_KEYS], string | number>>,
  storage: StorageLike = resolveStorage(),
) {
  Object.entries(payload).forEach(([key, value]) => {
    storage.setItem(key, String(value))
  })
}

export function normalizeLoginPayload(payload: unknown) {
  const container = asRecord(payload)
  const data = asRecord(container?.data) ?? container
  const userInfo = asRecord(data?.userInfo)

  return {
    token: normalizeText(data?.userToken) || normalizeText(data?.token),
    avatar: normalizeText(userInfo?.avatarUrl),
    nickName: normalizeText(userInfo?.nickName),
    mobile: normalizeText(userInfo?.mobile) || normalizeText(data?.mobile),
  }
}

function asRecord(value: unknown): Record<string, unknown> | null {
  return typeof value === 'object' && value !== null ? (value as Record<string, unknown>) : null
}

function normalizeText(value: unknown): string {
  return typeof value === 'string' ? value.trim() : ''
}
