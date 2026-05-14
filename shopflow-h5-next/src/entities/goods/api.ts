import type { ApiEnvelope } from '@/shared/utils/contracts'
import { getApiClient } from '@/shared/request'

export interface GoodsListItem {
  id: number
  name: string
  brief: string
  picUrl: string
  retailPrice: number
  counterPrice: number
}

export interface GoodsListPayload {
  list: GoodsListItem[]
  page: number
  pages: number
}

export interface BrandInfo {
  id: number
  name: string
  desc: string
  picUrl: string
}

export interface BrandListPayload {
  list: BrandInfo[]
  page: number
  pages: number
}

export interface GoodsCategoryInfo {
  id: number
  name: string
  desc: string
}

export interface GoodsCategoryPayload {
  currentCategory: GoodsCategoryInfo
  brotherCategory: GoodsCategoryInfo[]
  parentCategory: GoodsCategoryInfo
}

export interface GoodsAttribute {
  attribute: string
  value: string
}

export interface GoodsSpecificationValue {
  id: number
  value: string
  picUrl?: string
}

export interface GoodsSpecification {
  name: string
  valueList: GoodsSpecificationValue[]
}

export interface GoodsProduct {
  id: number
  specifications: string[]
  price: number
  number: number
}

export interface GoodsDetailPayload {
  info: {
    id: number
    name: string
    brief: string
    detail: string
    picUrl: string
    retailPrice: number
    counterPrice: number
    gallery: string[]
  }
  attribute: GoodsAttribute[]
  specificationList: GoodsSpecification[]
  productList: GoodsProduct[]
  userHasCollect: number
  comment?: {
    allCount: number
    hasPicCount: number
  }
}

export interface GoodsCommentCountPayload {
  allCount: number
  hasPicCount: number
}

export interface GoodsCommentItem {
  id: number | string
  nickName?: string
  avatarUrl?: string
  addTime?: string
  content?: string
  star?: number
  picUrls?: string[]
  adminContent?: string
}

export interface GoodsCommentListPayload {
  list: GoodsCommentItem[]
  page: number
  pages: number
}

export interface TopicInfo {
  id: number
  title: string
  subtitle?: string
  priceInfo?: string
  picUrl?: string
  content?: string
}

export interface TopicListPayload {
  list: TopicInfo[]
  page: number
  pages: number
}

export interface TopicDetailPayload {
  topic: TopicInfo
  goods: GoodsListItem[]
}

export interface GrouponListItem extends GoodsListItem {
  grouponMember: number
  grouponDiscount: number
}

export interface GrouponListPayload {
  list: GrouponListItem[]
  page: number
  pages: number
}

export interface SearchIndexPayload {
  defaultKeyword?: {
    keyword?: string
    isHot?: boolean
    isDefault?: boolean
  } | null
  historyKeywordList: string[]
  hotKeywordList: Array<{
    keyword: string
    isHot?: boolean
    isDefault?: boolean
  }>
}

export interface SearchHelperItem {
  keyword: string
}


export async function fetchGoodsCategory(id: number | string) {
  const response = await getApiClient().get<ApiEnvelope<GoodsCategoryPayload>>('/goods/category', {
    params: { id },
  })
  return response.data.data
}

export async function fetchGoodsList(params: { categoryId: number | string; page: number; limit: number }) {
  const response = await getApiClient().get<ApiEnvelope<GoodsListPayload>>('/goods/list', {
    params,
  })
  return response.data.data
}

export async function fetchHotGoodsList(params: { page: number; limit: number }) {
  const response = await getApiClient().get<ApiEnvelope<GoodsListPayload>>('/goods/list', {
    params: {
      isHot: true,
      ...params,
    },
  })
  return response.data.data
}

export async function fetchNewGoodsList(params: { page: number; limit: number }) {
  const response = await getApiClient().get<ApiEnvelope<GoodsListPayload>>('/goods/list', {
    params: {
      isNew: true,
      ...params,
    },
  })
  return response.data.data
}

export async function fetchGoodsDetail(id: number | string) {
  const response = await getApiClient().get<ApiEnvelope<GoodsDetailPayload>>('/goods/detail', {
    params: { id },
  })
  return response.data.data
}

export async function fetchGoodsCommentCount(goodsId: number | string) {
  const response = await getApiClient().get<ApiEnvelope<GoodsCommentCountPayload>>('/goods/comment/count', {
    params: { goodsId },
  })
  return response.data.data
}

export async function fetchGoodsCommentList(params: {
  goodsId: number | string
  page: number
  limit: number
  hasPicture: boolean
}) {
  const response = await getApiClient().get<ApiEnvelope<GoodsCommentListPayload>>('/goods/comment/list', {
    params,
  })
  return response.data.data
}

export async function fetchBrandList(params: { page: number; limit: number }) {
  const response = await getApiClient().get<ApiEnvelope<BrandListPayload>>('/brand/list', {
    params,
  })
  return response.data.data
}

export async function fetchBrandDetail(id: number | string) {
  const response = await getApiClient().get<ApiEnvelope<BrandInfo>>('/brand/detail', {
    params: { id },
  })
  return response.data.data
}

export async function fetchBrandGoodsList(brandId: number | string) {
  const response = await getApiClient().get<ApiEnvelope<GoodsListPayload>>('/goods/list', {
    params: {
      brandId,
      page: 1,
      limit: 20,
    },
  })
  return response.data.data
}

export async function fetchTopicList(params: { page: number; limit: number }) {
  const response = await getApiClient().get<ApiEnvelope<TopicListPayload>>('/topic/list', {
    params,
  })
  return response.data.data
}

export async function fetchTopicDetail(id: number | string) {
  const response = await getApiClient().get<ApiEnvelope<TopicDetailPayload>>('/topic/detail', {
    params: { id },
  })
  return response.data.data
}

export async function fetchGrouponList(params: { page: number; limit: number }) {
  const response = await getApiClient().get<ApiEnvelope<GrouponListPayload>>('/groupon/list', {
    params,
  })
  return response.data.data
}

export async function fetchSearchIndex() {
  const response = await getApiClient().get<ApiEnvelope<SearchIndexPayload>>('/search/index')
  return response.data.data
}

export async function fetchSearchHelper(keyword: string) {
  const response = await getApiClient().get<ApiEnvelope<SearchHelperItem[]>>('/search/helper', {
    params: { keyword },
  })
  return response.data.data
}

export async function fetchSearchResult(params: {
  keyword: string
  page: number
  limit: number
  categoryId?: number | string
}) {
  const response = await getApiClient().get<ApiEnvelope<GoodsListPayload & { limit?: number }>>('/goods/list', {
    params: {
      categoryId: 0,
      ...params,
    },
  })
  return response.data.data
}

export async function clearSearchHistory() {
  await getApiClient().post('/search/clearhistory')
}
