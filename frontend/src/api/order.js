import request from './index'

export function createOrder(data) {
  return request.post('/orders', data)
}

export function listOrders(params) {
  return request.get('/orders', { params })
}

export function getOrderDetail(id) {
  return request.get(`/orders/${id}`)
}

export function cancelOrder(id) {
  return request.post(`/orders/${id}/cancel`)
}

export function payOrder(id) {
  return request.post(`/orders/${id}/pay`)
}

export function completeOrder(id) {
  return request.post(`/orders/${id}/complete`)
}
