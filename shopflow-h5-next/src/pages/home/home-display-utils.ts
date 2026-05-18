import type { HomeChannel, HomeGoodsCard, HomePayload } from '@/entities/home/api'

export type HomeFeedKey = 'recommend' | 'new' | 'hot'

const HOME_CHANNEL_TARGETS = [
  '/pages/items/catalog/index',
  '/pages/items/hot/index',
  '/pages/items/new/index',
  '/pages/items/groupon/index',
  '/pages/items/brand-list/index',
  '/pages/items/topic-list/index',
  '/pages/items/search/index',
  '/pages/login/index',
]

export function resolveHomeFeed(feed: HomeFeedKey, payload: HomePayload | null): HomeGoodsCard[] {
  if (!payload) {
    return []
  }

  if (feed === 'new') {
    return payload.newGoodsList || []
  }

  if (feed === 'hot') {
    return payload.hotGoodsList || []
  }

  return [...(payload.hotGoodsList || []), ...(payload.newGoodsList || [])]
}

export function shouldShowCoupons(payload: HomePayload | null): boolean {
  return Boolean(payload?.couponList?.length)
}

export function shouldShowFlashSale(payload: HomePayload | null): boolean {
  return Boolean(payload?.grouponList?.length)
}

export function resolveHomeHeroCopy() {
  return {
    eyebrow: 'ShopFlow',
    title: '今日精选',
  }
}

export function resolveHomeChannelEntries(channels: HomeChannel[]) {
  return channels.map((item, index) => ({
    ...item,
    target: HOME_CHANNEL_TARGETS[index] || HOME_CHANNEL_TARGETS[0],
  }))
}

export function createFirstTriggerSkipper() {
  let isFirstTrigger = true

  return function shouldSkipCurrentTrigger() {
    if (isFirstTrigger) {
      isFirstTrigger = false
      return true
    }

    return false
  }
}
