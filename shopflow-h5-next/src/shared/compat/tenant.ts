import { resolveShopFlowAppId } from '@/shared/config/env'
import { resolveStorage, type StorageLike } from '@/shared/platform/storage'
import { persistTenantToken } from './session-adapter'

export const TENANT_PATHS = {
  homeIndex: '/home/index',
  homeAuth: '/home/auth',
}

export function withAppIdParam<T extends Record<string, unknown> | undefined>(params: T) {
  return {
    ...(params || {}),
    appid: ((params as Record<string, unknown> | undefined)?.appid as string | undefined) ?? resolveShopFlowAppId(),
  } as Record<string, unknown> & { appid: string }
}

export function normalizeWxPath(url: string): string {
  const normalizedUrl = url || ''
  const wxIndex = normalizedUrl.indexOf('/wx')
  const path = wxIndex >= 0 ? normalizedUrl.slice(wxIndex + 3) : normalizedUrl
  const queryIndex = path.indexOf('?')
  return queryIndex >= 0 ? path.slice(0, queryIndex) : path
}

export function shouldBootstrapTenant(path: string, tenantToken: string): boolean {
  if (tenantToken.trim()) {
    return false
  }

  const normalizedPath = normalizeWxPath(path)
  return normalizedPath !== TENANT_PATHS.homeIndex && normalizedPath !== TENANT_PATHS.homeAuth
}

export function extractTenantToken(payload: unknown): string {
  if (typeof payload === 'string') {
    return payload.trim()
  }

  if (typeof payload === 'object' && payload !== null) {
    const record = payload as Record<string, unknown>
    if (typeof record.tenantId === 'string' && record.tenantId.trim()) {
      return record.tenantId.trim()
    }
    if ('data' in record) {
      return extractTenantToken(record.data)
    }
  }

  return ''
}

export function handleTenantPayload(payload: unknown, storage: StorageLike = resolveStorage()): string {
  const tenantToken = extractTenantToken(payload)
  return tenantToken ? persistTenantToken(tenantToken, storage) : ''
}
