import request from './index'

export function listProducts(params) {
  return request.get('/products', { params })
}

export function getProductDetail(id) {
  return request.get(`/products/${id}`)
}
