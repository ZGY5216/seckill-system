<template>
  <div class="auth-wrap">
    <div class="container-card auth-card">
      <h2 class="auth-title">登录</h2>
      <form @submit.prevent="onSubmit">
        <div class="form-item">
          <label>账号</label>
          <input v-model="form.username" placeholder="请输入账号" />
        </div>
        <div class="form-item">
          <label>密码</label>
          <input v-model="form.password" type="password" placeholder="请输入密码" />
        </div>
        <button type="submit" class="btn btn-primary auth-submit" :disabled="loading">
          {{ loading ? '登录中…' : '登录' }}
        </button>
      </form>
      <p class="text-muted auth-tip">
        还没有账号？<router-link to="/register" class="link">立即注册</router-link>
      </p>
      <p class="text-muted auth-tip">演示账号：buyer / 123456（用户），admin / 123456（管理员）</p>
    </div>
  </div>
</template>

<script setup>
import { reactive, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { login } from '../../api/auth'
import { useUserStore } from '../../store'
import { notifyError } from '../../utils/request'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

const form = reactive({ username: '', password: '' })
const loading = ref(false)

async function onSubmit() {
  if (!form.username || !form.password) {
    notifyError('请输入账号和密码')
    return
  }
  loading.value = true
  try {
    const data = await login(form)
    userStore.setLogin(data)
    router.push(route.query.redirect || (data.role === 'ADMIN' ? '/admin/activities' : '/home'))
  } catch (e) {
    // 拦截器已提示
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.auth-wrap {
  max-width: 420px;
  margin: 60px auto;
}

.auth-title {
  margin-bottom: 24px;
  text-align: center;
}

.auth-submit {
  width: 100%;
}

.auth-tip {
  text-align: center;
  margin-top: 16px;
}

.link {
  color: var(--primary);
}
</style>
