import type { ApiEnvelope } from '@/shared/utils/contracts'
import { getApiClient } from '@/shared/request'

export interface CartItem {
  id: number
  goodsId: number
  productId: number
  goodsName: string
  picUrl: string
  number: number
  price: number
  checked: boolean
  addTime?: string
  specifications: string[]
}

export interface CartListPayload {
  cartList: CartItem[]
}

export interface CartActionPayload {
  goodsId: number
  productId: number
  number: number
}

export async function fetchCartList() {
  const response = await getApiClient().get<ApiEnvelope<CartListPayload>>('/cart/index')
  return response.data.data
}

export async function fetchCartGoodsCount() {
  const response = await getApiClient().get<ApiEnvelope<number>>('/cart/count')
  return response.data.data
}

export async function addCartItem(payload: CartActionPayload) {
  await getApiClient().post('/cart/add', payload)
}

export async function fastAddCartItem(payload: CartActionPayload) {
  const response = await getApiClient().post<ApiEnvelope<number>>('/cart/fast/add', payload)
  return response.data.data
}

export async function updateCartItem(payload: Pick<CartItem, 'goodsId' | 'id' | 'productId'> & { number: number }) {
  await getApiClient().post('/cart/update', payload)
}

export async function toggleCartItems(productIds: number[], checked: boolean) {
  await getApiClient().post('/cart/checked', {
    productIds,
    isChecked: checked ? 1 : 0,
  })
}

export async function removeCartItems(productIds: number[]) {
  const response = await getApiClient().post<ApiEnvelope<CartListPayload>>('/cart/delete', { productIds })
  return response.data.data
}
