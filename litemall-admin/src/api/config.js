import request from '@/utils/request'


export function listConfig(query) {
  return request({
    url: '/config/list',
    method: 'get',
    params: query,
  })
}

export function updateConfig(data) {
  return request({
    url: '/config/update',
    method: 'post',
    data
  })
}
