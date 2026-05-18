import type { ApiEnvelope } from '@/shared/utils/contracts'
import { getApiClient } from '@/shared/request'

export interface UserOrderStats {
  unpaid: number
  unship: number
  unrecv: number
  uncomment: number
}

export interface UserIndexPayload {
  order: UserOrderStats
}

export interface AddressItem {
  id: number
  name: string
  tel: string
  province: string
  city: string
  county: string
  addressDetail: string
  isDefault: boolean
}

export interface AddressListPayload {
  list: AddressItem[]
}

export interface AddressCard {
  id: number
  name: string
  tel: string
  address: string
  isDefault: boolean
}

export interface AddressForm {
  id?: number | string
  name: string
  mobile: string
  province: string
  city: string
  county: string
  addressDetail: string
  areaCode: string
  postalCode: string
  addressAll: string
  isDefault: boolean
}

export interface UserOrderGoodsItem {
  goodsName: string
  number: number
  picUrl: string
  specifications: string[]
}

export interface UserOrderHandleOption {
  cancel: boolean
  pay: boolean
  refund: boolean
  confirm: boolean
  delete: boolean
  comment: boolean
}

export interface UserOrderItem {
  id: number
  orderSn: string
  actualPrice: number
  freightPrice: number
  orderStatusText: string
  goodsList: UserOrderGoodsItem[]
  handleOption: UserOrderHandleOption
}

export interface UserOrderListPayload {
  list: UserOrderItem[]
  page: number
  pages: number
}

export interface UserCouponItem {
  id: number
  name: string
  desc: string
  tag: string
  discount: number
  endTime: string
}

export interface UserCouponListPayload {
  list: UserCouponItem[]
  page: number
  pages: number
}

export interface CollectItem {
  id: number
  valueId: number
  name: string
  brief: string
  picUrl: string
  retailPrice: number
  counterPrice: number
}

export interface CollectListPayload {
  list: CollectItem[]
  page: number
  pages: number
}

export interface IssueItem {
  id: number
  question: string
  answer: string
}

export interface UserProfilePayload {
  nickName: string
  avatar: string
  gender: number
  mobile: string
}

export interface UserProfileUpdatePayload {
  nickname?: string
  gender?: number
  avatar?: string
}

export interface UserFeedbackPayload {
  mobile: string
  feedType: string
  content: string
}

export interface AftersaleRecord {
  id: string
  orderId: string
  aftersaleSn: string
  status: number
  type: number
  reason: string
  amount: number
  comment?: string
  addTime?: string
  pictures?: string[]
  statusText?: string
}

export interface AftersaleGoodsItem {
  goodsName: string
  number: number
  price: number
  picUrl: string
  specifications: string[]
}

export interface AftersaleListItem {
  statusText: string
  aftersale: AftersaleRecord
  orderGoods: AftersaleGoodsItem
}

export interface AftersaleListCard {
  id: string
  orderId: string
  aftersaleSn: string
  status: number
  statusText: string
  reason: string
  amount: number
  comment: string
  addTime: string
  goodsName: string
  goodsCount: number
  goodsPrice: number
  picUrl: string
  specifications: string[]
  specificationsText: string
}

export interface AftersaleListPayload {
  list: AftersaleListCard[]
  page: number
  pages: number
}

export interface AftersaleDetailPayload {
  order: {
    id: string
    orderSn: string
    actualPrice: number
  }
  aftersale: AftersaleRecord
  orderGoods: AftersaleGoodsItem
}

export interface AftersaleSubmitPayload {
  orderId: string
  type: number
  amount: number
  reason: string
  comment: string
  pictures: string[]
}

export interface UploadProofPayload {
  url: string
}

export async function fetchUserIndex() {
  const response = await getApiClient().get<ApiEnvelope<UserIndexPayload>>('/user/index')
  return response.data.data
}

export async function fetchAddressList() {
  const response = await getApiClient().get<ApiEnvelope<AddressListPayload>>('/address/list')
  const data = response.data.data

  return {
    ...data,
    list: data.list.map((item) => ({
      id: item.id,
      name: item.name,
      tel: item.tel,
      address: `${item.province}${item.city}${item.county} ${item.addressDetail}`.trim(),
      isDefault: item.isDefault,
    })) satisfies AddressCard[],
  }
}

export async function fetchAddressDetail(id: number | string) {
  const response = await getApiClient().get<ApiEnvelope<AddressForm>>('/address/detail', {
    params: {
      id,
    },
  })
  return response.data.data
}

export async function saveAddress(payload: AddressForm) {
  const response = await getApiClient().post<ApiEnvelope<number>>('/address/save', payload)
  return response.data.data
}

export async function deleteAddress(id: number) {
  await getApiClient().post('/address/delete', { id })
}

export async function fetchUserOrderList(params: {
  showType: number
  page: number
  limit: number
}) {
  const response = await getApiClient().get<ApiEnvelope<UserOrderListPayload>>('/order/list', {
    params,
  })
  return response.data.data
}

export async function fetchUserCouponList(params: {
  status: number
  page: number
  limit: number
}) {
  const response = await getApiClient().get<ApiEnvelope<UserCouponListPayload>>('/coupon/user', {
    params,
  })
  return response.data.data
}

export async function fetchCollectList(params: {
  type: number
  page: number
  limit: number
}) {
  const response = await getApiClient().get<ApiEnvelope<CollectListPayload>>('/collect/list', {
    params,
  })
  return response.data.data
}

export async function fetchAftersaleList(params: {
  status: number
  page: number
  limit: number
}) {
  const response = await getApiClient().get<ApiEnvelope<{ list: AftersaleListItem[]; page: number; pages: number }>>('/aftersale/list', {
    params,
  })
  const data = response.data.data

  return {
    ...data,
    list: data.list.map((item) => ({
      id: item.aftersale.id,
      orderId: item.aftersale.orderId,
      aftersaleSn: item.aftersale.aftersaleSn,
      status: item.aftersale.status,
      statusText: item.statusText,
      reason: item.aftersale.reason,
      amount: item.aftersale.amount,
      comment: item.aftersale.comment || '',
      addTime: item.aftersale.addTime || '',
      goodsName: item.orderGoods.goodsName,
      goodsCount: item.orderGoods.number,
      goodsPrice: item.orderGoods.price,
      picUrl: item.orderGoods.picUrl,
      specifications: item.orderGoods.specifications || [],
      specificationsText: (item.orderGoods.specifications || []).join(' / '),
    })) satisfies AftersaleListCard[],
  }
}

export async function fetchAftersaleDetail(orderId: string) {
  const response = await getApiClient().get<ApiEnvelope<AftersaleDetailPayload>>('/aftersale/detail', {
    params: {
      orderId,
    },
  })
  const data = response.data.data

  return {
    ...data,
    aftersale: {
      ...data.aftersale,
      pictures: data.aftersale.pictures || [],
    },
  }
}

export async function cancelAftersale(id: string) {
  await getApiClient().post('/aftersale/cancel', { id })
}

export async function submitAftersale(payload: AftersaleSubmitPayload) {
  await getApiClient().post('/aftersale/submit', payload)
}

export async function uploadAftersaleProof(filePath: string) {
  const response = await getApiClient().upload<ApiEnvelope<UploadProofPayload>>('/storage/upload', {
    filePath,
    name: 'file',
  })
  return response.data.data
}

export async function toggleCollect(valueId: number, type = 0) {
  await getApiClient().post('/collect/addordelete', {
    valueId,
    type,
  })
}

export async function fetchIssueList() {
  const response = await getApiClient().get<ApiEnvelope<{ list: IssueItem[] }>>('/issue/list')
  return response.data.data.list
}

export async function fetchUserProfile() {
  const response = await getApiClient().get<ApiEnvelope<UserProfilePayload>>('/user/info')
  return response.data.data
}

export async function updateUserProfile(payload: UserProfileUpdatePayload) {
  await getApiClient().post('/auth/profile', payload)
}

export async function submitUserFeedback(payload: UserFeedbackPayload) {
  await getApiClient().post('/feedback/submit', payload)
}
