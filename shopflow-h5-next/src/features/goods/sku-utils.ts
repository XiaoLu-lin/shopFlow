import type { GoodsDetailPayload, GoodsProduct, GoodsSpecification } from '@/entities/goods/api'

export interface SkuChoiceState {
  quantity: number
  selectedValueIds: number[]
}

export interface SkuOptionGroup {
  name: string
  values: Array<{
    id: number
    value: string
    picUrl?: string
  }>
}

export function buildSkuOptionGroups(detail: GoodsDetailPayload): SkuOptionGroup[] {
  return detail.specificationList.map((group) => ({
    name: group.name,
    values: group.valueList.map((item) => ({
      id: item.id,
      value: item.value,
      picUrl: item.picUrl,
    })),
  }))
}

export function resolveSelectedValueNames(
  specificationList: GoodsSpecification[],
  selectedValueIds: number[],
) {
  return specificationList.flatMap((group) =>
    group.valueList
      .filter((item) => selectedValueIds.includes(item.id))
      .map((item) => item.value),
  )
}

export function resolveMatchedProduct(
  detail: GoodsDetailPayload,
  selectedValueIds: number[],
): GoodsProduct | null {
  const selectedNames = resolveSelectedValueNames(detail.specificationList, selectedValueIds)

  if (!selectedNames.length) {
    return detail.productList[0] || null
  }

  return (
    detail.productList.find((product) => (
      product.specifications.length === selectedNames.length
      && product.specifications.every((item) => selectedNames.includes(item))
    )) || null
  )
}

export function hasCompletedSkuSelection(detail: GoodsDetailPayload, selectedValueIds: number[]) {
  if (!detail.specificationList.length) {
    return true
  }

  return detail.specificationList.every((group) =>
    group.valueList.some((item) => selectedValueIds.includes(item.id)),
  )
}

export function buildCartPayload(detail: GoodsDetailPayload, state: SkuChoiceState) {
  const matchedProduct = resolveMatchedProduct(detail, state.selectedValueIds)

  if (!matchedProduct) {
    return null
  }

  return {
    goodsId: detail.info.id,
    productId: matchedProduct.id,
    number: state.quantity,
  }
}

export function clampSkuQuantity(quantity: number, stock: number) {
  const normalizedStock = stock > 0 ? stock : 1
  return Math.max(1, Math.min(normalizedStock, quantity))
}
