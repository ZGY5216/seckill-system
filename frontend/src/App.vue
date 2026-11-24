<template>
  <div class="app-shell">
    <AppHeader />
    <main class="app-main">
      <router-view />
    </main>
    <footer class="app-footer">在线秒杀系统 · 软件工程与 UML 课程实践</footer>
  </div>
</template>

<script setup>
import AppHeader from './components/AppHeader.vue'
</script>
