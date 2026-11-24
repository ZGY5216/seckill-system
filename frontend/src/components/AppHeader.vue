<template>
  <header class="header">
    <div class="header-inner">
      <router-link to="/" class="logo">⚡ 在线秒杀</router-link>
      <nav class="nav">
        <template v-if="user.isLoggedIn && user.isUser">
          <router-link to="/home" class="nav-link">秒杀大厅</router-link>
          <router-link to="/orders" class="nav-link">我的订单</router-link>
        </template>
        <template v-if="user.isLoggedIn && user.isAdmin">
          <router-link to="/admin/activities" class="nav-link">活动管理</router-link>
        </template>
      </nav>
      <div class="header-right">
        <template v-if="user.isLoggedIn">
          <span class="username">{{ user.realName || user.username }}</span>
          <button class="btn btn-outline" @click="onLogout">退出登录</button>
        </template>
        <template v-else>
          <router-link to="/login" class="btn btn-primary">登录</router-link>
          <router-link to="/register" class="btn btn-outline">注册</router-link>
        </template>
      </div>
    </div>
  </header>
</template>

<script setup>
import { useRouter } from 'vue-router'
import { useUserStore } from '../store'
import { logout as logoutApi } from '../api/auth'

const router = useRouter()
const user = useUserStore()

async function onLogout() {
  try {
    await logoutApi()
  } catch (e) {
    // 忽略后端退出异常，本地清理即可
  }
  user.logout()
  router.push('/login')
}
</script>

<style scoped>
.header {
  background: #fff;
  border-bottom: 1px solid var(--border);
  position: sticky;
  top: 0;
  z-index: 100;
}

.header-inner {
  max-width: 1180px;
  margin: 0 auto;
  padding: 0 20px;
  height: 60px;
  display: flex;
  align-items: center;
  gap: 28px;
}

.logo {
  font-size: 18px;
  font-weight: 700;
  color: var(--primary);
}

.nav {
  display: flex;
  gap: 20px;
  flex: 1;
}

.nav-link {
  font-size: 14px;
  color: var(--text-secondary);
}

.nav-link:hover {
  color: var(--primary);
}

.header-right {
  display: flex;
  align-items: center;
  gap: 12px;
}

.username {
  font-size: 14px;
}
</style>
