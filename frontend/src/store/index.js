import { defineStore } from 'pinia'

function readJSON(key, fallback) {
  try {
    const raw = localStorage.getItem(key)
    return raw ? JSON.parse(raw) : fallback
  } catch (e) {
    return fallback
  }
}

// 用户认证状态：token / role / userId
export const useUserStore = defineStore('user', {
  state: () => ({
    token: '',
    userId: null,
    role: '',
    username: '',
    realName: '',
    userInfo: null
  }),
  getters: {
    isLoggedIn: (state) => !!state.token,
    isUser: (state) => state.role === 'USER',
    isAdmin: (state) => state.role === 'ADMIN'
  },
  actions: {
    setLogin(data) {
      this.token = data.token || ''
      this.userId = data.userId || null
      this.role = data.role || ''
      this.username = data.username || ''
      this.realName = data.realName || ''
      if (data.userInfo) this.userInfo = data.userInfo
      try {
        localStorage.setItem('sk_token', this.token)
        localStorage.setItem(
          'sk_user',
          JSON.stringify({
            userId: this.userId,
            role: this.role,
            username: this.username,
            realName: this.realName
          })
        )
      } catch (e) {
        // ignore
      }
    },
    restore() {
      try {
        this.token = localStorage.getItem('sk_token') || ''
        const u = readJSON('sk_user', null)
        if (u) {
          this.userId = u.userId || null
          this.role = u.role || ''
          this.username = u.username || ''
          this.realName = u.realName || ''
        }
      } catch (e) {
        // ignore
      }
    },
    logout() {
      this.token = ''
      this.userId = null
      this.role = ''
      this.username = ''
      this.realName = ''
      this.userInfo = null
      try {
        localStorage.removeItem('sk_token')
        localStorage.removeItem('sk_user')
      } catch (e) {
        // ignore
      }
    }
  }
})
