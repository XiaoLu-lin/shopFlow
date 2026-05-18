import type { GoodsDetailPayload } from '@/entities/goods/api'

export function buildAttributePairs(detail: GoodsDetailPayload) {
  return detail.attribute.map((item) => [item.attribute, item.value] as const)
}

export function buildSpecSummary(detail: GoodsDetailPayload) {
  return detail.specificationList.map((item) => ({
    name: item.name,
    values: item.valueList.map((value) => value.value),
  }))
}
