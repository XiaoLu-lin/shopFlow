import type { HomeGoodsCard, HomePayload } from '@/entities/home/api'

export type HomeFeedKey = 'recommend' | 'new' | 'hot'

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
