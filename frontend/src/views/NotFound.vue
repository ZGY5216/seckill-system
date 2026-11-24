<template>
  <div class="container-card not-found">
    <div class="icon">🔍</div>
    <h2>页面不存在</h2>
    <p class="text-muted">您访问的页面已被移除或地址有误</p>
    <router-link to="/" class="btn btn-primary">返回首页</router-link>
  </div>
</template>

<style scoped>
.not-found {
  text-align: center;
  padding: 80px 20px;
}

.icon {
  font-size: 48px;
  margin-bottom: 16px;
}

.not-found h2 {
  margin-bottom: 10px;
}

.not-found .btn {
  margin-top: 20px;
}
</style>
