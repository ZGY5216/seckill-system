import request from './index'

export function register(data) {
  return request.post('/auth/register', data)
}

export function login(data) {
  return request.post('/auth/login', data)
}

export function currentUser() {
  return request.get('/auth/me')
}

export function logout() {
  return request.post('/auth/logout')
}
