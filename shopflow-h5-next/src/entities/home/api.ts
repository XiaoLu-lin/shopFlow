import type { ApiEnvelope } from '@/shared/utils/contracts'
import { getApiClient } from '@/shared/request'
import { withAppIdParam } from '@/shared/compat/tenant'

export interface HomeBanner {
  url: string
}

export interface HomeChannel {
  id: number
  name: string
  iconUrl: string
}

export interface HomeCoupon {
  id: number
  name: string
  desc: string
  tag: string
  discount: number
  days: number
}

export interface HomeGoodsCard {
  id: number
  name: string
  brief: string
  picUrl: string
  retailPrice: number
  counterPrice?: number
  grouponPrice?: number
  grouponDiscount?: number
  grouponMember?: number
}

export interface HomeBrand {
  id: number
  name: string
  picUrl: string
}

export interface HomeTopic {
  id: number
  title: string
  subtitle: string
  picUrl: string
}

export interface HomePayload {
  banner: HomeBanner[]
  channel: HomeChannel[]
  couponList: HomeCoupon[]
  grouponList: HomeGoodsCard[]
  brandList: HomeBrand[]
  newGoodsList: HomeGoodsCard[]
  hotGoodsList: HomeGoodsCard[]
  topicList: HomeTopic[]
  tenantId?: string
}

export async function fetchHome() {
  const response = await getApiClient().get<ApiEnvelope<HomePayload>>('/home/index', {
    params: withAppIdParam(undefined),
  })

  return response.data.data
}
