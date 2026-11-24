<template>
  <div v-if="order" class="container-card">
    <div class="detail-head">
      <h2>订单详情</h2>
      <OrderStatusTag :status="order.status" />
    </div>

    <div class="info-grid">
      <div><span class="text-muted">订单编号：</span>{{ order.orderNo }}</div>
      <div><span class="text-muted">下单时间：</span>{{ formatDateTime(order.createTime) }}</div>
      <div><span class="text-muted">商品：</span>{{ order.productName }}</div>
      <div><span class="text-muted">数量：</span>×{{ order.quantity }}</div>
      <div><span class="text-muted">订单金额：</span><span class="price">{{ formatPrice(order.totalAmount) }}</span></div>
    </div>

    <table v-if="order.items?.length" class="items-table">
      <thead>
        <tr>
          <th>商品</th>
          <th>单价</th>
          <th>数量</th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="item in order.items" :key="item.id">
          <td>{{ item.productName }}</td>
          <td>{{ formatPrice(item.price) }}</td>
          <td>×{{ item.quantity }}</td>
        </tr>
      </tbody>
    </table>

    <div class="actions">
      <button v-if="order.status === 'PENDING_PAYMENT'" class="btn btn-primary" @click="onPay">立即支付</button>
      <button v-if="order.status === 'PENDING_PAYMENT'" class="btn btn-danger" @click="onCancel">取消订单</button>
      <button v-if="order.status === 'PAID'" class="btn btn-outline" @click="onComplete">确认收货</button>
      <router-link to="/orders" class="btn btn-outline">返回列表</router-link>
    </div>
  </div>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { getOrderDetail, cancelOrder, payOrder, completeOrder } from '../../api/order'
import OrderStatusTag from '../../components/OrderStatusTag.vue'
import { formatPrice, formatDateTime } from '../../utils/format'
import { notifySuccess } from '../../utils/request'

const route = useRoute()
const router = useRouter()
const order = ref(null)

async function load() {
  try {
    order.value = await getOrderDetail(route.params.id)
  } catch (e) {
    // 拦截器已提示
  }
}

async function onPay() {
  await payOrder(order.value.id)
  notifySuccess('支付成功')
  load()
}

async function onCancel() {
  if (!window.confirm('确定取消该订单吗？取消后库存将回补。')) return
  await cancelOrder(order.value.id)
  notifySuccess('订单已取消')
  load()
}

async function onComplete() {
  await completeOrder(order.value.id)
  notifySuccess('已确认收货')
  load()
}

onMounted(load)
</script>

<style scoped>
.detail-head {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 20px;
}

.info-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 12px;
  margin-bottom: 20px;
}

.items-table {
  width: 100%;
  border-collapse: collapse;
  margin-bottom: 20px;
}

.items-table th,
.items-table td {
  border-bottom: 1px solid var(--border);
  padding: 10px;
  text-align: left;
}

.actions {
  display: flex;
  gap: 12px;
}
</style>
