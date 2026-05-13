export const mobileReg = /^1[0-9]{10}$/
export const emailReg = /^(\w)+(\.\w+)*@(\w)+((\.\w+)+)$/

export function validateAddress(value: string): boolean {
  const trimmed = value.trim()
  return trimmed.length >= 5 && trimmed.length <= 100
}
