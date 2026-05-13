export interface ApiEnvelope<T = unknown> {
  errno: string | number
  errmsg?: string
  data: T
}

export function isSuccessResponse(payload: Pick<ApiEnvelope, 'errno'> | null | undefined): boolean {
  if (!payload) {
    return false
  }
  return payload.errno === 0 || payload.errno === '0' || payload.errno === 'success'
}
