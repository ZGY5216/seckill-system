<template>
  <div>
    <h2 class="page-title">秒杀活动管理</h2>

    <div class="container-card form-card">
      <h3>{{ form.id ? '编辑活动' : '新建活动' }}</h3>
      <div class="form-grid">
        <div class="form-item">
          <label>活动标题</label>
          <input v-model="form.title" placeholder="如：鲜橙限时秒杀专场" />
        </div>
        <div class="form-item">
          <label>关联商品 ID</label>
          <input v-model.number="form.productId" type="number" placeholder="商品表中的 ID" />
        </div>
        <div class="form-item">
          <label>秒杀价（元）</label>
          <input v-model="form.seckillPrice" type="number" step="0.01" placeholder="0.00" />
        </div>
        <div class="form-item">
          <label>活动库存</label>
          <input v-model.number="form.activityStock" type="number" placeholder="库存数量" />
        </div>
        <div class="form-item">
          <label>开始时间</label>
          <input v-model="form.startAt" type="datetime-local" />
        </div>
        <div class="form-item">
          <label>结束时间</label>
          <input v-model="form.endAt" type="datetime-local" />
        </div>
      </div>
      <div class="actions">
        <button class="btn btn-primary" :disabled="submitting" @click="onSave">
          {{ submitting ? '保存中…' : (form.id ? '保存修改' : '创建活动') }}
        </button>
        <button v-if="form.id" class="btn btn-outline" @click="resetForm">取消编辑</button>
      </div>
    </div>

    <div class="container-card table-card">
      <table>
        <thead>
          <tr>
            <th>ID</th>
            <th>标题</th>
            <th>商品ID</th>
            <th>秒杀价</th>
            <th>库存</th>
            <th>开始时间</th>
            <th>结束时间</th>
            <th>状态</th>
            <th>操作</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="a in activities" :key="a.id">
            <td>{{ a.id }}</td>
            <td>{{ a.title }}</td>
            <td>{{ a.productId }}</td>
            <td>{{ formatPrice(a.seckillPrice) }}</td>
            <td>{{ a.activityStock }}</td>
            <td>{{ formatDateTime(a.startAt) }}</td>
            <td>{{ formatDateTime(a.endAt) }}</td>
            <td>{{ activityStatusLabel(a.status) }}</td>
            <td><button class="btn btn-outline" @click="onEdit(a)">编辑</button></td>
          </tr>
        </tbody>
      </table>
    </div>
  </div>
</template>

<script setup>
import { onMounted, reactive, ref } from 'vue'
import { listActivities, createActivity, updateActivity } from '../../api/admin'
import { formatPrice, formatDateTime } from '../../utils/format'
import { activityStatusLabel } from '../../utils/enums'
import { notifySuccess, notifyError } from '../../utils/request'

const activities = ref([])
const submitting = ref(false)

const emptyForm = () => ({
  id: null,
  title: '',
  productId: null,
  seckillPrice: '',
  activityStock: null,
  startAt: '',
  endAt: ''
})

const form = reactive(emptyForm())

async function load() {
  try {
    activities.value = await listActivities()
  } catch (e) {
    // 拦截器已提示
  }
}

function toDateTime(localValue) {
  if (!localValue) return null
  return localValue.length === 16 ? localValue + ':00' : localValue
}

async function onSave() {
  if (!form.title || !form.productId) {
    notifyError('请填写活动标题和商品 ID')
    return
  }
  submitting.value = true
  const payload = {
    id: form.id,
    title: form.title,
    productId: form.productId,
    seckillPrice: form.seckillPrice,
    activityStock: form.activityStock,
    startAt: toDateTime(form.startAt),
    endAt: toDateTime(form.endAt)
  }
  try {
    if (form.id) {
      await updateActivity(payload)
      notifySuccess('活动已更新')
    } else {
      await createActivity(payload)
      notifySuccess('活动已创建')
    }
    resetForm()
    load()
  } catch (e) {
    // 拦截器已提示
  } finally {
    submitting.value = false
  }
}

function onEdit(a) {
  Object.assign(form, {
    id: a.id,
    title: a.title,
    productId: a.productId,
    seckillPrice: a.seckillPrice,
    activityStock: a.activityStock,
    startAt: String(a.startAt).replace(' ', 'T').slice(0, 16),
    endAt: String(a.endAt).replace(' ', 'T').slice(0, 16)
  })
}

function resetForm() {
  Object.assign(form, emptyForm())
}

onMounted(load)
</script>

<style scoped>
.page-title {
  margin-bottom: 20px;
}

.form-card {
  margin-bottom: 20px;
}

.form-card h3 {
  margin-bottom: 16px;
}

.form-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 16px;
}

.actions {
  margin-top: 16px;
  display: flex;
  gap: 12px;
}

.table-card table {
  width: 100%;
  border-collapse: collapse;
}

.table-card th,
.table-card td {
  border-bottom: 1px solid var(--border);
  padding: 10px;
  text-align: left;
  font-size: 14px;
}
</style>
