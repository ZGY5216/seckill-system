<template>
  <div>
    <h2 class="page-title">我的订单</h2>

    <EmptyState v-if="!loading && orders.length === 0" text="暂无订单，去秒杀大厅看看吧">
      <router-link to="/home" class="btn btn-primary">去秒杀</router-link>
    </EmptyState>

    <div v-else class="order-list">
      <div v-for="order in orders" :key="order.id" class="order-item container-card">
        <div class="order-main">
          <div class="order-head">
            <span class="order-no">单号：{{ order.orderNo }}</span>
            <OrderStatusTag :status="order.status" />
          </div>
          <div class="order-body">
            <div class="product">
              <strong>{{ order.productName }}</strong>
              <span class="text-muted">×{{ order.quantity }}</span>
            </div>
            <div class="amount price">{{ formatPrice(order.totalAmount) }}</div>
          </div>
          <div class="text-muted time">下单时间：{{ formatDateTime(order.createTime) }}</div>
        </div>
        <div class="order-actions">
          <button class="btn btn-outline" @click="goDetail(order.id)">查看详情</button>
          <button
            v-if="order.status === 'PENDING_PAYMENT'"
            class="btn btn-primary"
            @click="onPay(order.id)"
          >
            立即支付
          </button>
          <button
            v-if="order.status === 'PENDING_PAYMENT'"
            class="btn btn-danger"
            @click="onCancel(order.id)"
          >
            取消订单
          </button>
          <button
            v-if="order.status === 'PAID'"
            class="btn btn-outline"
            @click="onComplete(order.id)"
          >
            确认收货
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import { listOrders, cancelOrder, payOrder, completeOrder } from '../../api/order'
import OrderStatusTag from '../../components/OrderStatusTag.vue'
import EmptyState from '../../components/EmptyState.vue'
import { formatPrice, formatDateTime } from '../../utils/format'
import { notifySuccess, notifyError } from '../../utils/request'

const router = useRouter()
const orders = ref([])
const loading = ref(false)

async function load() {
  loading.value = true
  try {
    const data = await listOrders({ pageNum: 1, pageSize: 20 })
    orders.value = data.records || []
  } catch (e) {
    // 拦截器已提示
  } finally {
    loading.value = false
  }
}

function goDetail(id) {
  router.push(`/orders/${id}`)
}

async function onPay(id) {
  try {
    await payOrder(id)
    notifySuccess('支付成功')
    load()
  } catch (e) {
    // 拦截器已提示
  }
}

async function onCancel(id) {
  if (!window.confirm('确定取消该订单吗？取消后库存将回补。')) return
  try {
    await cancelOrder(id)
    notifySuccess('订单已取消')
    load()
  } catch (e) {
    // 拦截器已提示
  }
}

async function onComplete(id) {
  try {
    await completeOrder(id)
    notifySuccess('已确认收货')
    load()
  } catch (e) {
    // 拦截器已提示
  }
}

onMounted(load)
</script>

<style scoped>
.page-title {
  margin-bottom: 20px;
}

.order-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.order-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 16px;
}

.order-head {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 8px;
}

.order-no {
  font-size: 13px;
  color: var(--text-secondary);
}

.order-body {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 6px;
}

.amount {
  font-size: 18px;
}

.time {
  font-size: 12px;
}

.order-actions {
  display: flex;
  gap: 10px;
  flex-shrink: 0;
}
</style>
