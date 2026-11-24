<template>
  <div class="empty">
    <div class="empty-icon">📭</div>
    <p class="text-muted">{{ text }}</p>
    <slot />
  </div>
</template>

<script setup>
defineProps({
  text: { type: String, default: '暂无数据' }
})
</script>

<style scoped>
.empty {
  text-align: center;
  padding: 60px 0;
}

.empty-icon {
  font-size: 44px;
  margin-bottom: 12px;
}
</style>
