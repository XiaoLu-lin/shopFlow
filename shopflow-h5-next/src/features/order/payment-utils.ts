import { SESSION_KEYS } from '@/shared/compat/session-keys'
import { resolveStorage, type StorageLike } from '@/shared/platform/storage'

export interface JsapiPrepayPayload {
  appId?: string
  timeStamp?: string
  nonceStr?: string
  packageValue?: string
  paySign?: string
}

export interface JsapiInvokePayload {
  appId: string
  timeStamp: string
  nonceStr: string
  package: string
  signType: 'MD5'
  paySign: string
}

export function buildJsapiInvokePayload(payload: JsapiPrepayPayload): JsapiInvokePayload | null {
  if (!payload.appId || !payload.timeStamp || !payload.nonceStr || !payload.packageValue || !payload.paySign) {
    return null
  }

  return {
    appId: payload.appId,
    timeStamp: payload.timeStamp,
    nonceStr: payload.nonceStr,
    package: payload.packageValue,
    signType: 'MD5',
    paySign: payload.paySign,
  }
}

export function persistPrepayData(payload: JsapiPrepayPayload, storage: StorageLike = resolveStorage()) {
  const invokePayload = buildJsapiInvokePayload(payload)
  if (!invokePayload) {
    return null
  }

  storage.setItem(SESSION_KEYS.prepayData, JSON.stringify(invokePayload))
  return invokePayload
}

export function readPrepayData(storage: StorageLike = resolveStorage()): JsapiInvokePayload | null {
  const raw = storage.getItem(SESSION_KEYS.prepayData)
  if (!raw) {
    return null
  }

  try {
    const parsed = JSON.parse(raw) as JsapiInvokePayload
    if (!parsed?.appId || !parsed?.timeStamp || !parsed?.nonceStr || !parsed?.package || !parsed?.paySign) {
      return null
    }
    return parsed
  } catch {
    return null
  }
}

export function clearPrepayData(storage: StorageLike = resolveStorage()) {
  storage.removeItem(SESSION_KEYS.prepayData)
}

export function resolvePaymentStatus(errMsg: string) {
  if (errMsg === 'get_brand_wcpay_request:ok') {
    return 'success'
  }

  if (errMsg === 'get_brand_wcpay_request:cancel') {
    return 'cancel'
  }

  return 'failed'
}

export function buildH5RedirectUrl(baseOrigin: string, basePathname: string, status = 'success', orderIds: string[] = []) {
  const routeQuery = buildPaymentStatusRouteQuery(orderIds)

  if (!routeQuery.orderId || !routeQuery.orderIds) {
    return `${baseOrigin}${basePathname}#/order/payment/${status}`
  }

  const search = new URLSearchParams({
    orderId: routeQuery.orderId,
    orderIds: routeQuery.orderIds,
  }).toString()
  const suffix = search ? `?${search}` : ''
  return `${baseOrigin}${basePathname}#/order/payment/${status}${suffix}`
}

export function buildPaymentStatusRouteQuery(orderIds: string[]): Record<string, string> {
  const normalizedOrderIds = orderIds.map((item) => item.trim()).filter(Boolean)

  if (!normalizedOrderIds.length) {
    return {}
  }

  return {
    orderId: normalizedOrderIds[0],
    orderIds: normalizedOrderIds.join(','),
  }
}

export function resolvePaymentResultCopy(input: {
  routeStatus?: string
  orderStatusText?: string
}) {
  if (input.routeStatus === 'cancel') {
    return {
      title: '支付已取消',
      description: '你已取消本次支付，可以返回订单列表后重新发起支付。',
      accent: 'warning' as const,
      autoRedirect: false,
    }
  }

  if (input.routeStatus !== 'success') {
    return {
      title: '支付失败',
      description: '系统繁忙或支付未完成，请稍后重试。',
      accent: 'error' as const,
      autoRedirect: false,
    }
  }

  if (input.orderStatusText?.trim()) {
    if (input.orderStatusText.includes('待付款')) {
      return {
        title: '支付结果确认中',
        description: '支付请求已返回成功，但订单状态仍显示待付款，请稍后刷新订单列表确认。',
        accent: 'pending' as const,
        autoRedirect: false,
      }
    }

    return {
      title: '支付成功',
      description: `订单已支付，当前状态：${input.orderStatusText}。`,
      accent: 'success' as const,
      autoRedirect: true,
    }
  }

  return {
    title: '支付成功',
    description: '订单支付已完成，稍后将自动回到订单列表。',
    accent: 'success' as const,
    autoRedirect: true,
  }
}

export function createTimeoutController() {
  let timer: ReturnType<typeof setTimeout> | null = null

  return {
    schedule(callback: () => void, delay: number) {
      if (timer) {
        clearTimeout(timer)
      }

      timer = setTimeout(() => {
        timer = null
        callback()
      }, delay)
    },
    clear() {
      if (!timer) {
        return
      }

      clearTimeout(timer)
      timer = null
    },
  }
}
