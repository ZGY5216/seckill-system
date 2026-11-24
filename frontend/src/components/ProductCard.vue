<template>
  <div class="card" @click="goDetail">
    <div class="card-img">
      <img v-if="product.imageUrl" :src="product.imageUrl" :alt="product.name" />
      <span v-else class="img-placeholder">🍊</span>
      <span class="badge" :style="badgeStyle">{{ statusLabel }}</span>
    </div>
    <div class="card-body">
      <h3 class="card-title">{{ product.name }}</h3>
      <div class="card-price">
        <span class="price">{{ formatPrice(product.seckillPrice) }}</span>
        <span class="original">{{ formatPrice(product.originalPrice) }}</span>
      </div>
      <div class="card-stock text-muted">剩余库存：{{ product.stock }}</div>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useRouter } from 'vue-router'
import { formatPrice } from '../utils/format'
import { ACTIVITY_STATUS } from '../utils/enums'

const props = defineProps({
  product: { type: Object, required: true }
})

const router = useRouter()

const statusLabel = computed(() => ACTIVITY_STATUS[props.product.activityStatus]?.label || '秒杀中')
const badgeStyle = computed(() => ({
  background: ACTIVITY_STATUS[props.product.activityStatus]?.color || '#2e9e5b'
}))

function goDetail() {
  router.push(`/products/${props.product.id}`)
}
</script>

<style scoped>
.card {
  background: #fff;
  border-radius: var(--radius);
  overflow: hidden;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.06);
  cursor: pointer;
  transition: transform 0.2s, box-shadow 0.2s;
}

.card:hover {
  transform: translateY(-3px);
  box-shadow: 0 6px 16px rgba(0, 0, 0, 0.12);
}

.card-img {
  position: relative;
  height: 160px;
  background: #f0f1f3;
  display: flex;
  align-items: center;
  justify-content: center;
}

.card-img img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.img-placeholder {
  font-size: 48px;
}

.badge {
  position: absolute;
  top: 10px;
  left: 10px;
  color: #fff;
  font-size: 12px;
  padding: 3px 10px;
  border-radius: 12px;
}

.card-body {
  padding: 14px;
}

.card-title {
  font-size: 15px;
  margin-bottom: 8px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.card-price {
  display: flex;
  align-items: baseline;
  gap: 10px;
  margin-bottom: 6px;
}

.original {
  color: #999;
  font-size: 13px;
  text-decoration: line-through;
}
</style>
