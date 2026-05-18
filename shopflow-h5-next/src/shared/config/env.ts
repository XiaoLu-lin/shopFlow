export const DEFAULT_SHOPFLOW_APP_ID = '1649067'
export const DEFAULT_MP_WEIXIN_BASE_API = 'https://manager.enshipeixue.com/wx-api'
export const DEFAULT_H5_BASE_API = '/wx-api'
export const DEFAULT_MP_WEIXIN_DEV_BASE_API = 'http://127.0.0.1:6914/wx'

export function resolveBaseApi(): string {
  const configured = import.meta.env.VITE_APP_BASE_API
  if (typeof configured === 'string' && configured.trim().length > 0) {
    const normalized = configured.trim()

    // #ifdef MP-WEIXIN
    if (normalized.startsWith('http://') || normalized.startsWith('https://')) {
      return normalized
    }
    return resolveMiniProgramBaseApi()
    // #endif

    return normalized
  }

  // #ifdef MP-WEIXIN
  return resolveMiniProgramBaseApi()
  // #endif

  return DEFAULT_H5_BASE_API
}

export function resolveShopFlowAppId(): string {
  const appId = import.meta.env.VITE_APP_SHOPFLOW_APPID
  return typeof appId === 'string' && appId.trim().length > 0
    ? appId.trim()
    : DEFAULT_SHOPFLOW_APP_ID
}

function resolveMiniProgramBaseApi() {
  try {
    const accountInfo = uni.getAccountInfoSync?.()
    const envVersion = accountInfo?.miniProgram?.envVersion || 'develop'

    if (envVersion === 'trial' || envVersion === 'release') {
      return DEFAULT_MP_WEIXIN_BASE_API
    }
  } catch {
    return DEFAULT_MP_WEIXIN_DEV_BASE_API
  }

  return DEFAULT_MP_WEIXIN_DEV_BASE_API
}
