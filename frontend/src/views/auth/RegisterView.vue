<template>
  <div class="auth-wrap">
    <div class="container-card auth-card">
      <h2 class="auth-title">注册</h2>
      <form @submit.prevent="onSubmit">
        <div class="form-item">
          <label>账号</label>
          <input v-model="form.username" placeholder="4-20 位账号" />
        </div>
        <div class="form-item">
          <label>密码</label>
          <input v-model="form.password" type="password" placeholder="6-20 位密码" />
        </div>
        <div class="form-item">
          <label>真实姓名（选填）</label>
          <input v-model="form.realName" placeholder="请输入真实姓名" />
        </div>
        <div class="form-item">
          <label>手机号（选填）</label>
          <input v-model="form.phone" placeholder="请输入手机号" />
        </div>
        <button type="submit" class="btn btn-primary auth-submit" :disabled="loading">
          {{ loading ? '提交中…' : '注册' }}
        </button>
      </form>
      <p class="text-muted auth-tip">
        已有账号？<router-link to="/login" class="link">返回登录</router-link>
      </p>
    </div>
  </div>
</template>

<script setup>
import { reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { register } from '../../api/auth'
import { notifyError, notifySuccess } from '../../utils/request'

const router = useRouter()
const form = reactive({ username: '', password: '', realName: '', phone: '', email: '' })
const loading = ref(false)

async function onSubmit() {
  if (!form.username || !form.password) {
    notifyError('请输入账号和密码')
    return
  }
  loading.value = true
  try {
    await register(form)
    notifySuccess('注册成功，请登录')
    router.push('/login')
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
