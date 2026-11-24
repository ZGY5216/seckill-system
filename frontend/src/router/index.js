import { createRouter, createWebHistory } from 'vue-router'
import { useUserStore } from '../store'

const routes = [
  {
    path: '/',
    redirect: () => {
      const user = useUserStore()
      if (!user.token) return '/login'
      if (user.role === 'ADMIN') return '/admin/activities'
      return '/home'
    }
  },
  {
    path: '/login',
    name: 'Login',
    component: () => import('../views/auth/LoginView.vue'),
    meta: { public: true, title: '登录' }
  },
  {
    path: '/register',
    name: 'Register',
    component: () => import('../views/auth/RegisterView.vue'),
    meta: { public: true, title: '注册' }
  },

  // 用户端
  {
    path: '/home',
    name: 'Home',
    component: () => import('../views/user/HomeView.vue'),
    meta: { role: 'USER', title: '秒杀大厅' }
  },
  {
    path: '/products/:id',
    name: 'ProductDetail',
    component: () => import('../views/user/ProductDetailView.vue'),
    meta: { role: 'USER', title: '商品详情' }
  },
  {
    path: '/orders',
    name: 'OrderList',
    component: () => import('../views/user/OrderListView.vue'),
    meta: { role: 'USER', title: '我的订单' }
  },
  {
    path: '/orders/:id',
    name: 'OrderDetail',
    component: () => import('../views/user/OrderDetailView.vue'),
    meta: { role: 'USER', title: '订单详情' }
  },

  // 管理端
  {
    path: '/admin/activities',
    name: 'AdminActivities',
    component: () => import('../views/admin/ActivityView.vue'),
    meta: { role: 'ADMIN', title: '活动管理' }
  },

  {
    path: '/:pathMatch(.*)*',
    name: 'NotFound',
    component: () => import('../views/NotFound.vue'),
    meta: { public: true, title: '页面不存在' }
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes,
  scrollBehavior() {
    return { top: 0 }
  }
})

router.beforeEach((to, from, next) => {
  const user = useUserStore()
  if (!user.token) user.restore()

  document.title = to.meta.title ? to.meta.title + ' - 在线秒杀系统' : '在线秒杀系统'

  if (to.meta.public) {
    next()
    return
  }
  if (!user.isLoggedIn) {
    next({ path: '/login', query: { redirect: to.fullPath } })
    return
  }
  const needRole = to.meta.role
  if (needRole && user.role !== needRole) {
    if (user.role === 'ADMIN') next('/admin/activities')
    else next('/home')
    return
  }
  next()
})

export default router
