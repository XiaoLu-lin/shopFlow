import { redirectToLogin } from '@/shared/platform/navigation'
import { createApiClient, type ApiClientLike } from './client'

let apiClient: ApiClientLike | null = null

export function getApiClient(): ApiClientLike {
  if (apiClient) {
    return apiClient
  }

  apiClient = createApiClient({
    onUnauthorized() {
      redirectToLogin()
    },
    onBusinessError(message) {
      uni.showToast({
        title: message,
        icon: 'none',
      })
    },
  })

  return apiClient
}
