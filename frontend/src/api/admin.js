import request from './index'

export function listActivities() {
  return request.get('/admin/activities')
}

export function createActivity(data) {
  return request.post('/admin/activities', data)
}

export function updateActivity(data) {
  return request.put('/admin/activities', data)
}
