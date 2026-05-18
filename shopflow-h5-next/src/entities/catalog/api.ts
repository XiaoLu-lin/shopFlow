import type { ApiEnvelope } from '@/shared/utils/contracts'
import { getApiClient } from '@/shared/request'

export interface CatalogCategory {
  id: number
  name: string
  desc: string
  picUrl: string
  iconUrl?: string
}

export interface CatalogListPayload {
  categoryList: CatalogCategory[]
  currentCategory: CatalogCategory
  currentSubCategory: CatalogCategory[]
}

export interface CatalogCurrentPayload {
  currentCategory: CatalogCategory
  currentSubCategory: CatalogCategory[]
}

export async function fetchCatalogList() {
  const response = await getApiClient().get<ApiEnvelope<CatalogListPayload>>('/catalog/index')
  return response.data.data
}

export async function fetchCurrentCatalog(id: number) {
  const response = await getApiClient().get<ApiEnvelope<CatalogCurrentPayload>>('/catalog/current', {
    params: { id },
  })
  return response.data.data
}
