import type { ApiEnvelope } from '@/shared/utils/contracts'
import { getApiClient } from '@/shared/request'

export interface CheckoutAddress {
  id: number
  name: string
  tel: string
  addressDetail: string
}

export interface CheckoutGoodsItem {
  id: number
  goodsName: string
  number: number
  price: number
  picUrl: string
  specifications: string[]
}

export interface CheckoutPayload {
  checkedGoodsList: CheckoutGoodsItem[]
  checkedAddress: CheckoutAddress
  availableCouponLength: number
  goodsTotalPrice: number
  freightPrice: number
  couponPrice: number
  grouponPrice: number
  orderTotalPrice: number
  actualPrice: number
  addressId: number
  cartId: number
  couponId: number
  userCouponId: number
}

export interface CouponOption {
  id: number
  cid: number
  name: string
  min: number
  discount: number
  desc: string
  tag: string
  startTime: string
  endTime: string
  available: boolean
}

export interface CouponSelectPayload {
  list: CouponOption[]
}

export interface SubmitOrderPayload {
  isPay: boolean
  orderIds: string[]
  orderId?: number | string
}

export interface OrderDetailHandleOption {
  cancel: boolean
  pay: boolean
  refund: boolean
  aftersale?: boolean
  confirm: boolean
  delete: boolean
  comment: boolean
}

export interface OrderDetailInfo {
  id: string
  orderSn: string
  consignee: string
  mobile: string
  address: string
  message?: string
  goodsPrice: number
  freightPrice: number
  couponPrice?: number
  actualPrice: number
  orderStatusText: string
  addTime?: string
  expCode?: string
  expName?: string
  expNo?: string
  handleOption: OrderDetailHandleOption
}

export interface OrderDetailGoodsItem {
  id: number
  goodsName: string
  number: number
  price: number
  picUrl: string
  specifications: string[]
  comment?: number
}

export interface OrderDetailPayload {
  orderInfo: OrderDetailInfo
  orderGoods: OrderDetailGoodsItem[]
}

export interface OrderCommentGoodsItem {
  id: number
  goodsId?: number
  goodsName: string
  number: number
  price: number
  picUrl: string
  specifications: string[]
  comment?: number
}

export interface OrderCommentPayload {
  goodsId: number
  content: string
  star: number
  hasPicture: boolean
  picUrls: string[]
}

export interface OrderPrepayPayload {
  appId?: string
  timeStamp?: string
  nonceStr?: string
  packageValue?: string
  signType?: string
  paySign?: string
  mwebUrl?: string
}

export interface OrderH5PayPayload {
  mwebUrl: string
}

export async function fetchCheckout(params: {
  cartId: string
  addressId: string
  couponId: string
  userCouponId: string
}) {
  const response = await getApiClient().get<ApiEnvelope<CheckoutPayload>>('/cart/checkout', {
    params: {
      ...params,
      grouponRulesId: 0,
    },
  })
  return response.data.data
}

export async function fetchSelectableCoupons(cartId: string) {
  const response = await getApiClient().get<ApiEnvelope<CouponSelectPayload>>('/coupon/selectlist', {
    params: {
      cartId,
      grouponRulesId: 0,
    },
  })
  return response.data.data
}

export async function submitOrder(payload: {
  addressId: string
  cartId: string
  couponId: string
  userCouponId: string
  message: string
}) {
  const response = await getApiClient().post<ApiEnvelope<SubmitOrderPayload>>('/order/submit', {
    ...payload,
    grouponLinkId: 0,
    grouponRulesId: 0,
  })
  const data = response.data.data

  if (Array.isArray(data.orderIds) && data.orderIds.length > 0) {
    return data
  }

  return {
    ...data,
    orderIds: data.orderId !== undefined && data.orderId !== null ? [String(data.orderId)] : [],
    isPay: data.isPay ?? false,
  }
}

export async function fetchOrderDetail(orderId: string) {
  const response = await getApiClient().get<ApiEnvelope<OrderDetailPayload>>('/order/detail', {
    params: {
      orderId,
    },
  })
  const data = response.data.data as OrderDetailPayload & {
    orderGoods?: OrderDetailGoodsItem[] | OrderDetailGoodsItem
  }

  return {
    ...data,
    orderGoods: Array.isArray(data.orderGoods) ? data.orderGoods : data.orderGoods ? [data.orderGoods] : [],
  }
}

export async function fetchOrderCommentGoods(goodsId: number | string) {
  const response = await getApiClient().get<ApiEnvelope<OrderCommentGoodsItem>>('/order/goods', {
    params: {
      goodsId,
    },
  })
  return response.data.data
}

export async function submitOrderComment(payload: OrderCommentPayload) {
  await getApiClient().post('/order/comment', payload)
}

export async function submitOrderPrepay(orderIds: string[]) {
  const response = await getApiClient().post<ApiEnvelope<OrderPrepayPayload>>('/order/prepay', orderIds)
  return response.data.data
}

export async function submitOrderH5Pay(orderIds: string[]) {
  const primaryOrderId = orderIds[0] || ''
  const response = await getApiClient().post<ApiEnvelope<OrderH5PayPayload>>('/order/h5pay', {
    orderId: primaryOrderId,
  })
  return response.data.data
}

export async function cancelUserOrder(orderId: number) {
  await getApiClient().post('/order/cancel', { orderId })
}

export async function refundUserOrder(orderId: number) {
  await getApiClient().post('/order/refund', { orderId })
}

export async function deleteUserOrder(orderId: number) {
  await getApiClient().post('/order/delete', { orderId })
}

export async function confirmUserOrder(orderId: number) {
  await getApiClient().post('/order/confirm', { orderId })
}
