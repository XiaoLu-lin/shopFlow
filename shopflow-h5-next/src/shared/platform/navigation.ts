export interface LoginRedirectOptions {
  redirect?: string
}

const ROUTE_NAME_TO_PAGE: Record<string, string> = {
  home: '/pages/home/index',
  user: '/pages/user/index',
  cart: '/pages/order/cart/index',
  orderCheckout: '/pages/order/checkout/index',
  search: '/pages/items/search/index',
  login: '/pages/login/index',
}

const TAB_PAGES = new Set([
  ROUTE_NAME_TO_PAGE.home,
  ROUTE_NAME_TO_PAGE.user,
  ROUTE_NAME_TO_PAGE.cart,
])

export function normalizeRedirectTarget(target?: string) {
  const trimmed = (target || '').trim()
  return trimmed || 'home'
}

export function resolveNamedPage(name?: string) {
  return ROUTE_NAME_TO_PAGE[normalizeRedirectTarget(name)] || ROUTE_NAME_TO_PAGE.home
}

export function buildLoginUrl(options: LoginRedirectOptions = {}) {
  const redirect = encodeURIComponent(normalizeRedirectTarget(options.redirect))
  return `/pages/login/index?redirect=${redirect}`
}

export function redirectToLogin(options: LoginRedirectOptions = {}) {
  uni.navigateTo({
    url: buildLoginUrl(options),
  })
}

export function navigateBack(delta = 1) {
  uni.navigateBack({ delta })
}

export function redirectAfterLogin(name?: string) {
  const url = resolveNamedPage(name)

  if (TAB_PAGES.has(url)) {
    uni.switchTab({ url })
    return
  }

  uni.redirectTo({ url })
}
