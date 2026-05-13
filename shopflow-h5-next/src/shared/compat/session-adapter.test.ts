import { describe, expect, test } from 'vitest'
import { createMemoryStorage } from '@/shared/platform/storage'
import { clearSession, getSessionSnapshot, normalizeLoginPayload, persistLegacyLoginSession, persistLegacyProfile, persistTenantToken, writeFlowContext } from './session-adapter'

describe('session-adapter', () => {
  test('persists legacy token and profile fields from userToken payload', () => {
    const storage = createMemoryStorage()
    const snapshot = persistLegacyLoginSession({
      data: {
        userToken: 'token-1',
        userInfo: {
          avatarUrl: 'avatar.png',
          nickName: 'shopflow',
          mobile: '13800000000',
        },
      },
    }, storage)

    expect(snapshot.token).toBe('token-1')
    expect(storage.getItem('Authorization')).toBe('token-1')
    expect(storage.getItem('avatar')).toBe('avatar.png')
    expect(storage.getItem('nickName')).toBe('shopflow')
    expect(storage.getItem('mobile')).toBe('13800000000')
  })

  test('normalizes token fallback from legacy token field', () => {
    expect(
      normalizeLoginPayload({
        data: {
          token: 'legacy-token',
        },
      }).token,
    ).toBe('legacy-token')
  })

  test('reads persisted tenant token from storage snapshot', () => {
    const storage = createMemoryStorage()
    persistTenantToken('tenant-1', storage)
    const snapshot = getSessionSnapshot(storage)

    expect(snapshot.tenantToken).toBe('tenant-1')
  })

  test('clears legacy session fields including mobile', () => {
    const storage = createMemoryStorage({
      Authorization: 'token-1',
      avatar: 'avatar.png',
      nickName: 'shopflow',
      mobile: '13800000000',
    })

    clearSession(storage)

    expect(storage.getItem('Authorization')).toBeNull()
    expect(storage.getItem('avatar')).toBeNull()
    expect(storage.getItem('nickName')).toBeNull()
    expect(storage.getItem('mobile')).toBeNull()
  })

  test('writes checkout-related legacy flow context keys', () => {
    const storage = createMemoryStorage()
    writeFlowContext({
      AddressId: 8,
      CartId: 11,
      CouponId: 12,
      UserCouponId: 13,
    }, storage)

    expect(storage.getItem('AddressId')).toBe('8')
    expect(storage.getItem('CartId')).toBe('11')
    expect(storage.getItem('CouponId')).toBe('12')
    expect(storage.getItem('UserCouponId')).toBe('13')
  })

  test('persists edited legacy profile fields for immediate ui sync', () => {
    const storage = createMemoryStorage({
      nickName: '旧昵称',
      mobile: '13800000000',
    })

    const snapshot = persistLegacyProfile({
      nickName: '新昵称',
      mobile: '13900000000',
    }, storage)

    expect(storage.getItem('nickName')).toBe('新昵称')
    expect(storage.getItem('mobile')).toBe('13900000000')
    expect(snapshot.nickName).toBe('新昵称')
    expect(snapshot.mobile).toBe('13900000000')
  })
})
