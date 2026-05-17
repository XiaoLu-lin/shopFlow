const LEGACY_STATIC_ROUTE_MAP: Record<string, string> = {
  '/': '/pages/home/index',
  '/items': '/pages/items/catalog/index',
  '/items/search': '/pages/items/search/index',
  '/items/hot': '/pages/items/hot/index',
  '/items/new': '/pages/items/new/index',
  '/items/groupon': '/pages/items/groupon/index',
  '/items/brand-list': '/pages/items/brand-list/index',
  '/items/topic-list': '/pages/items/topic-list/index',
  '/login': '/pages/login/index',
  '/login/forget': '/pages/login/forget/index',
  '/login/registerGetCode': '/pages/login/register-get-code/index',
  '/order': '/pages/order/cart/index',
  '/order/checkout': '/pages/order/checkout/index',
  '/user': '/pages/user/index',
  '/user/server': '/pages/user/service/index',
  '/user/address': '/pages/user/address/index',
  '/user/collect': '/pages/user/collect/index',
  '/user/help': '/pages/user/help/index',
  '/user/feedback': '/pages/user/feedback/index',
  '/user/information': '/pages/user/profile/index',
}

function trimHashPrefix(input: string) {
  const normalized = input.trim()
  if (!normalized) {
    return '/'
  }

  if (normalized.startsWith('#')) {
    return normalized.slice(1) || '/'
  }

  const hashIndex = normalized.indexOf('#')
  return hashIndex >= 0 ? normalized.slice(hashIndex + 1) || '/' : normalized
}

export function splitLegacyHashRoute(input: string) {
  const normalized = trimHashPrefix(input)
  const queryIndex = normalized.indexOf('?')

  if (queryIndex < 0) {
    return {
      path: normalized || '/',
      query: '',
    }
  }

  return {
    path: normalized.slice(0, queryIndex) || '/',
    query: normalized.slice(queryIndex + 1),
  }
}

export function resolveLegacyHashRoute(input: string) {
  const { path, query } = splitLegacyHashRoute(input)

  if (LEGACY_STATIC_ROUTE_MAP[path]) {
    const suffix = query ? `?${query}` : ''
    return `${LEGACY_STATIC_ROUTE_MAP[path]}${suffix}`
  }

  const detailMatch = path.match(/^\/items\/detail\/([^/]+)$/)
  if (detailMatch) {
    const suffix = query ? `&${query}` : ''
    return `/pages/items/detail/index?id=${detailMatch[1]}${suffix ? suffix : ''}`
  }

  const brandMatch = path.match(/^\/items\/brand\/([^/]+)$/)
  if (brandMatch) {
    const suffix = query ? `&${query}` : ''
    return `/pages/items/brand/index?brandId=${brandMatch[1]}${suffix ? suffix : ''}`
  }

  const topicMatch = path.match(/^\/items\/topic\/([^/]+)$/)
  if (topicMatch) {
    const suffix = query ? `&${query}` : ''
    return `/pages/items/topic/index?topicId=${topicMatch[1]}${suffix ? suffix : ''}`
  }

  const paymentStatusMatch = path.match(/^\/order\/payment\/([^/]+)$/)
  if (paymentStatusMatch) {
    const suffix = query ? `&${query}` : ''
    return `/pages/order/payment-status/index?status=${paymentStatusMatch[1]}${suffix ? suffix : ''}`
  }

  const orderListMatch = path.match(/^\/user\/order\/list\/([^/]+)$/)
  if (orderListMatch) {
    const active = orderListMatch[1]
    const suffix = query ? `&${query}` : ''
    return `/pages/user/order-list/index?active=${active}${suffix ? suffix : ''}`
  }

  const couponListMatch = path.match(/^\/user\/coupon\/list\/([^/]+)$/)
  if (couponListMatch) {
    const active = couponListMatch[1]
    const suffix = query ? `&${query}` : ''
    return `/pages/user/coupon/index?active=${active}${suffix ? suffix : ''}`
  }

  const infoEditMatch = path.match(/^\/user\/information\/(setMobile|setNickname|setPassword)$/)
  if (infoEditMatch) {
    const routeMap: Record<string, string> = {
      setMobile: '/pages/user/profile-edit-mobile/index',
      setNickname: '/pages/user/profile-edit-nickname/index',
      setPassword: '/pages/user/profile-edit-password/index',
    }
    const suffix = query ? `?${query}` : ''
    return `${routeMap[infoEditMatch[1]]}${suffix}`
  }

  const orderDetailMatch = path.match(/^\/order\/order-detail$/)
  if (orderDetailMatch) {
    const suffix = query ? `?${query}` : ''
    return `/pages/order/detail/index${suffix}`
  }

  const orderPaymentMatch = path.match(/^\/order\/payment$/)
  if (orderPaymentMatch) {
    const suffix = query ? `?${query}` : ''
    return `/pages/order/payment/index${suffix}`
  }

  const refundListMatch = path.match(/^\/user\/refund\/list$/)
  if (refundListMatch) {
    const suffix = query ? `&${query}` : ''
    return `/pages/user/refund/index?active=0${suffix ? suffix : ''}`
  }

  return ''
}
