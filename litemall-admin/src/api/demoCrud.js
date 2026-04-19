import request from '@/utils/request'

export function listDemoCrud(query) {
  return request({
    url: '/demo-crud/list',
    method: 'get',
    params: query
  })
}

export function readDemoCrud(id) {
  return request({
    url: '/demo-crud/read',
    method: 'get',
    params: { id }
  })
}

export function createDemoCrud(data) {
  return request({
    url: '/demo-crud/create',
    method: 'post',
    data
  })
}

export function updateDemoCrud(data) {
  return request({
    url: '/demo-crud/update',
    method: 'post',
    data
  })
}

export function deleteDemoCrud(id) {
  return request({
    url: '/demo-crud/delete',
    method: 'post',
    params: { id }
  })
}
