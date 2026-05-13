export type QuickLinkType =
  | 'order'
  | 'cart'
  | 'address'
  | 'collect'
  | 'profile'
  | 'help'
  | 'service'
  | 'feedback'
  | 'coupon'
  | 'refund'
  | 'placeholder'

export interface QuickLinkItem {
  label: string
  type: QuickLinkType
  copy: string
}

export function resolveQuickLinkAction(link: QuickLinkItem) {
  if (link.type === 'order') {
    return {
      action: 'navigateTo' as const,
      url: '/pages/user/order-list/index?active=0',
    }
  }

  if (link.type === 'cart') {
    return {
      action: 'switchTab' as const,
      url: '/pages/order/cart/index',
    }
  }

  if (link.type === 'address') {
    return {
      action: 'navigateTo' as const,
      url: '/pages/user/address/index',
    }
  }

  if (link.type === 'collect') {
    return {
      action: 'navigateTo' as const,
      url: '/pages/user/collect/index',
    }
  }

  if (link.type === 'profile') {
    return {
      action: 'navigateTo' as const,
      url: '/pages/user/profile/index',
    }
  }

  if (link.type === 'help') {
    return {
      action: 'navigateTo' as const,
      url: '/pages/user/help/index',
    }
  }

  if (link.type === 'service') {
    return {
      action: 'navigateTo' as const,
      url: '/pages/user/service/index',
    }
  }

  if (link.type === 'feedback') {
    return {
      action: 'navigateTo' as const,
      url: '/pages/user/feedback/index',
    }
  }

  if (link.type === 'coupon') {
    return {
      action: 'navigateTo' as const,
      url: '/pages/user/coupon/index?active=0',
    }
  }

  if (link.type === 'refund') {
    return {
      action: 'navigateTo' as const,
      url: '/pages/user/refund/index?active=0',
    }
  }

  return {
    action: 'toast' as const,
    title: `${link.label}下一批补齐`,
  }
}
