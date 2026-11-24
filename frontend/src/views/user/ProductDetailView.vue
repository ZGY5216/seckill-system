<template>
  <div v-if="detail" class="detail container-card">
    <div class="detail-img">
      <img v-if="detail.imageUrl" :src="detail.imageUrl" :alt="detail.name" />
      <span v-else class="img-placeholder">🍊</span>
    </div>
    <div class="detail-info">
      <h2>{{ detail.name }}</h2>
      <div class="price-row">
        <span class="price big">{{ formatPrice(detail.seckillPrice) }}</span>
        <span class="original">{{ formatPrice(detail.originalPrice) }}</span>
      </div>
      <p class="text-muted">{{ detail.description || '暂无商品描述' }}</p>
      <div class="meta">
        <p>活动状态：
          <span class="activity-badge" :style="badgeStyle">{{ activityStatusLabel(detail.activityStatus) }}</span>
        </p>
        <p>开始时间：{{ formatDateTime(detail.startAt) }}</p>
        <p>结束时间：{{ formatDateTime(detail.endAt) }}</p>
        <p>剩余库存：{{ detail.stock }}</p>
      </div>
      <div class="actions">
        <button
          class="btn btn-primary seckill-btn"
          :disabled="!canSeckill || submitting"
          @click="onSeckill"
        >
          {{ buttonText }}
        </button>
        <router-link to="/home" class="btn btn-outline">返回大厅</router-link>
      </div>
    </div>
  </div>
</template>

<script setup>
import { computed, onMounted, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { getProductDetail } from '../../api/product'
import { createOrder } from '../../api/order'
import { formatPrice, formatDateTime } from '../../utils/format'
import { ACTIVITY_STATUS, activityStatusLabel } from '../../utils/enums'
import { notifySuccess, notifyError } from '../../utils/request'

const route = useRoute()
const router = useRouter()
const detail = ref(null)
const submitting = ref(false)

const badgeStyle = computed(() => ({
  color: ACTIVITY_STATUS[detail.value?.activityStatus]?.color || '#666',
  borderColor: ACTIVITY_STATUS[detail.value?.activityStatus]?.color || '#ddd'
}))

const canSeckill = computed(() =>
  detail.value && detail.value.activityStatus === 'ONGOING' && detail.value.stock > 0
)

const buttonText = computed(() => {
  if (!detail.value) return '立即秒杀'
  if (detail.value.activityStatus === 'NOT_STARTED') return '活动未开始'
  if (detail.value.activityStatus === 'ENDED') return '活动已结束'
  if (detail.value.stock <= 0) return '已抢光'
  return '立即秒杀'
})

async function load() {
  try {
    detail.value = await getProductDetail(route.params.id)
  } catch (e) {
    // 拦截器已提示
  }
}

async function onSeckill() {
  if (!detail.value?.activityId) {
    notifyError('当前商品无可参与的秒杀活动')
    return
  }
  submitting.value = true
  try {
    const order = await createOrder({ activityId: detail.value.activityId, quantity: 1 })
    notifySuccess('下单成功，请尽快支付')
    router.push(`/orders/${order.id}`)
  } catch (e) {
    // 拦截器已提示
  } finally {
    submitting.value = false
  }
}

onMounted(load)
</script>

<style scoped>
.detail {
  display: flex;
  gap: 32px;
}

.detail-img {
  width: 340px;
  height: 300px;
  border-radius: var(--radius);
  background: #f0f1f3;
  display: flex;
  align-items: center;
  justify-content: center;
  overflow: hidden;
  flex-shrink: 0;
}

.detail-img img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.img-placeholder {
  font-size: 72px;
}

.detail-info {
  flex: 1;
}

.price-row {
  margin: 16px 0;
  display: flex;
  align-items: baseline;
  gap: 12px;
}

.price.big {
  font-size: 28px;
}

.original {
  color: #999;
  text-decoration: line-through;
}

.meta {
  margin: 16px 0;
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.actions {
  display: flex;
  gap: 12px;
  margin-top: 24px;
}

.activity-badge {
  display: inline-block;
  border: 1px solid;
  border-radius: 12px;
  padding: 2px 10px;
  font-size: 12px;
}

.seckill-btn {
  min-width: 140px;
}
</style>
