import request from '@/utils/request'

export function getEnums(enumType) {
  return request({
    url: `/enums/${enumType}`,
    method: 'get'
  })
}
