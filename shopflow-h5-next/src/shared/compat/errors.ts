export function resolveErrorMessage(error: unknown): string {
  if (typeof error === 'object' && error !== null) {
    const response = (error as { data?: { errmsg?: string } }).data
    if (response?.errmsg) {
      return response.errmsg
    }

    const axiosLike = error as { response?: { data?: { errmsg?: string } } }
    if (axiosLike.response?.data?.errmsg) {
      return axiosLike.response.data.errmsg
    }
  }

  if (error instanceof Error && error.message) {
    return error.message
  }

  return '请求失败，请稍后再试'
}
