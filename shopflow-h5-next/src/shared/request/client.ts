import { resolveBaseApi } from '@/shared/config/env'
import { getSessionSnapshot } from '@/shared/compat/session-adapter'
import { SHOPFLOW_HEADERS } from '@/shared/compat/session-keys'
import { TENANT_PATHS, handleTenantPayload, normalizeWxPath, shouldBootstrapTenant, withAppIdParam } from '@/shared/compat/tenant'
import { resolveStorage, type StorageLike } from '@/shared/platform/storage'
import { isSuccessResponse, type ApiEnvelope } from '@/shared/utils/contracts'

export interface RequestRuntime {
  storage?: StorageLike
  onUnauthorized?: () => void
  onBusinessError?: (message: string) => void
}

export interface RequestConfig {
  params?: Record<string, unknown>
  headers?: Record<string, string>
  meta?: {
    persistTenantToken?: boolean
  }
}

export interface ApiResponse<T = unknown> {
  data: T
  statusCode: number
  header: Record<string, string>
  config: RequestConfig
}

type UniRequestData = string | Record<string, unknown> | ArrayBuffer | undefined

export interface ApiClientLike {
  get<T = unknown>(url: string, config?: RequestConfig): Promise<ApiResponse<T>>
  post<T = unknown>(url: string, data?: unknown, config?: RequestConfig): Promise<ApiResponse<T>>
  upload<T = unknown>(url: string, options: {
    file?: File
    filePath?: string
    files?: Array<{
      name?: string
      file?: File
      uri?: string
    }>
    name: string
    formData?: Record<string, unknown>
    headers?: Record<string, string>
  }): Promise<ApiResponse<T>>
}

export function createApiClient(runtime: RequestRuntime = {}): ApiClientLike {
  const storage = runtime.storage ?? resolveStorage()
  const baseURL = resolveBaseApi()

  let bootstrapPromise: Promise<string> | null = null

  async function bootstrapTenant(userToken: string): Promise<string> {
    if (bootstrapPromise) {
      return bootstrapPromise
    }

    bootstrapPromise = request<ApiEnvelope<string | { tenantId?: string }>>(
      TENANT_PATHS.homeAuth,
      {
        method: 'POST',
        data: {
          appid: withAppIdParam(undefined).appid,
        },
        headers: createHeaders(userToken, ''),
      },
    ).then((response) => {
      if (!isSuccessResponse(response.data)) {
        throw new Error(response.data.errmsg || '租户预热失败')
      }

      const token = handleTenantPayload(response.data, storage)
      if (!token) {
        throw new Error('未获取到租户授权')
      }

      return token
    }).finally(() => {
      bootstrapPromise = null
    })

    return bootstrapPromise
  }

  async function withResolvedConfig(url: string, config: RequestConfig = {}) {
    const requestPath = normalizeWxPath(url)
    const session = getSessionSnapshot(storage)
    const nextConfig: RequestConfig = {
      ...config,
      params: config.params ? { ...config.params } : undefined,
      headers: {
        ...(config.headers || {}),
      },
      meta: config.meta ? { ...config.meta } : undefined,
    }

    if (requestPath === TENANT_PATHS.homeIndex) {
      nextConfig.params = withAppIdParam(nextConfig.params)
      nextConfig.meta = {
        ...(nextConfig.meta || {}),
        persistTenantToken: true,
      }
    }

    if (shouldBootstrapTenant(requestPath, session.tenantToken)) {
      const tenantToken = await bootstrapTenant(session.token)
      nextConfig.headers = {
        ...nextConfig.headers,
        ...createHeaders(session.token, tenantToken),
      }
      return nextConfig
    }

    nextConfig.headers = {
      ...nextConfig.headers,
      ...createHeaders(session.token, session.tenantToken),
    }
    return nextConfig
  }

  async function request<T>(
    url: string,
    options: {
      method: 'GET' | 'POST'
      data?: unknown
      headers?: Record<string, string>
      params?: Record<string, unknown>
      meta?: RequestConfig['meta']
    },
  ): Promise<ApiResponse<T>> {
    const resolvedConfig = await withResolvedConfig(url, {
      params: options.params,
      headers: options.headers,
      meta: options.meta,
    })

    const response = await uni.request({
      url: buildUrl(baseURL, url, resolvedConfig.params),
      method: options.method,
      data: normalizeRequestData(options.data),
      timeout: 5000,
      header: sanitizeHeaders(resolvedConfig.headers),
    })

    const normalizedHeader = normalizeHeaders(response.header)
    const payload = response.data as ApiEnvelope

    if (resolvedConfig.meta?.persistTenantToken) {
      handleTenantPayload(payload, storage)
    }

    if (!isSuccessResponse(payload)) {
      if (payload?.errno === 'A0223' || payload?.errno === 501) {
        runtime.onUnauthorized?.()
      }

      const message = payload?.errmsg || '请求失败'
      runtime.onBusinessError?.(message)
      throw new Error(message)
    }

    return {
      data: response.data as T,
      statusCode: response.statusCode,
      header: normalizedHeader,
      config: resolvedConfig,
    }
  }

  return {
    get(url, config) {
      return request(url, {
        method: 'GET',
        params: config?.params,
        headers: config?.headers,
        meta: config?.meta,
      })
    },
    post(url, data, config) {
      return request(url, {
        method: 'POST',
        data,
        params: config?.params,
        headers: config?.headers,
        meta: config?.meta,
      })
    },
    async upload<T = unknown>(url: string, options: {
      file?: File
      filePath?: string
      files?: Array<{
        name?: string
        file?: File
        uri?: string
      }>
      name: string
      formData?: Record<string, unknown>
      headers?: Record<string, string>
    }) {
      const resolvedConfig = await withResolvedConfig(url, {
        headers: options.headers,
      })

      const response = await uni.uploadFile({
        url: buildUrl(baseURL, url),
        file: options.file,
        filePath: options.filePath,
        files: options.files,
        name: options.name,
        formData: normalizeFormData(options.formData),
        header: sanitizeHeaders(resolvedConfig.headers),
        timeout: 5000,
      })

      const normalizedHeader = normalizeHeaders((response as typeof response & { header?: Record<string, string> }).header)
      const payload = parseUploadPayload(response.data)

      if (!isSuccessResponse(payload)) {
        if (payload?.errno === 'A0223' || payload?.errno === 501) {
          runtime.onUnauthorized?.()
        }

        const message = payload?.errmsg || '上传失败'
        runtime.onBusinessError?.(message)
        throw new Error(message)
      }

      return {
        data: payload as T,
        statusCode: response.statusCode,
        header: normalizedHeader,
        config: resolvedConfig,
      }
    },
  }
}

function createHeaders(userToken: string, tenantToken: string) {
  const headers: Record<string, string> = {}

  if (userToken.trim()) {
    headers[SHOPFLOW_HEADERS.userToken] = userToken.trim()
  }

  if (tenantToken.trim()) {
    headers[SHOPFLOW_HEADERS.tenantToken] = tenantToken.trim()
  }

  return headers
}

function sanitizeHeaders(headers?: Record<string, string>) {
  if (!headers) {
    return {}
  }

  return Object.entries(headers).reduce<Record<string, string>>((result, [key, value]) => {
    const nextKey = String(key || '').trim()
    const nextValue = String(value || '').trim()

    if (!nextKey || !nextValue) {
      return result
    }

    if (!isLatin1String(nextValue)) {
      return result
    }

    result[nextKey] = nextValue
    return result
  }, {})
}

function buildUrl(baseURL: string, url: string, params?: Record<string, unknown>) {
  const normalizedBase = baseURL.endsWith('/') ? baseURL.slice(0, -1) : baseURL
  const normalizedPath = url.startsWith('/') ? url : `/${url}`
  const query = toQueryString(params)

  return `${normalizedBase}${normalizedPath}${query ? `?${query}` : ''}`
}

function toQueryString(params?: Record<string, unknown>) {
  if (!params) {
    return ''
  }

  const pairs: string[] = []

  Object.entries(params).forEach(([key, value]) => {
    if (value === undefined || value === null || value === '') {
      return
    }

    if (Array.isArray(value)) {
      value.forEach((item) => {
        if (item !== undefined && item !== null) {
          pairs.push(`${encodeURIComponent(key)}=${encodeURIComponent(String(item))}`)
        }
      })
      return
    }

    pairs.push(`${encodeURIComponent(key)}=${encodeURIComponent(String(value))}`)
  })

  return pairs.join('&')
}

function normalizeHeaders(header: unknown) {
  if (!header || typeof header !== 'object') {
    return {}
  }

  return Object.entries(header as Record<string, unknown>).reduce<Record<string, string>>((result, [key, value]) => {
    result[key] = String(value)
    return result
  }, {})
}

function normalizeRequestData(data: unknown): UniRequestData {
  if (data === undefined || data === null) {
    return undefined
  }

  if (typeof data === 'string' || data instanceof ArrayBuffer) {
    return data
  }

  if (typeof FormData !== 'undefined' && data instanceof FormData) {
    const payload: Record<string, unknown> = {}
    data.forEach((value, key) => {
      payload[key] = value
    })
    return payload
  }

  if (typeof data === 'object') {
    return data as Record<string, unknown>
  }

  return String(data)
}

function normalizeFormData(data?: Record<string, unknown>) {
  if (!data) {
    return {}
  }

  return Object.entries(data).reduce<Record<string, string>>((result, [key, value]) => {
    if (value === undefined || value === null) {
      return result
    }

    result[key] = String(value)
    return result
  }, {})
}

function parseUploadPayload(data: string | Record<string, unknown>): ApiEnvelope {
  if (typeof data === 'string') {
    try {
      return JSON.parse(data) as ApiEnvelope
    } catch {
      return {
        errno: -1,
        errmsg: '上传返回解析失败',
        data: null,
      } satisfies ApiEnvelope
    }
  }

  return data as unknown as ApiEnvelope
}

function isLatin1String(value: string) {
  for (let index = 0; index < value.length; index += 1) {
    if (value.charCodeAt(index) > 255) {
      return false
    }
  }

  return true
}
