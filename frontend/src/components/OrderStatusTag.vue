<template>
  <span class="status-tag" :style="tagStyle">{{ label }}</span>
</template>

<script setup>
import { computed } from 'vue'
import { ORDER_STATUS } from '../utils/enums'

const props = defineProps({
  status: { type: String, required: true }
})

const label = computed(() => ORDER_STATUS[props.status]?.label || props.status)
const tagStyle = computed(() => ({
  color: ORDER_STATUS[props.status]?.color || '#666',
  borderColor: ORDER_STATUS[props.status]?.color || '#ddd'
}))
</script>

<style scoped>
.status-tag {
  display: inline-block;
  border: 1px solid;
  border-radius: 12px;
  padding: 2px 10px;
  font-size: 12px;
}
</style>
