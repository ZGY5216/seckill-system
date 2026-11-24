<template>
  <div>
    <div class="toolbar">
      <h2>秒杀大厅</h2>
      <div class="search">
        <input v-model="keyword" placeholder="搜索商品名称" @keyup.enter="onSearch" />
        <button class="btn btn-primary" @click="onSearch">搜索</button>
      </div>
    </div>

    <EmptyState v-if="!loading && products.length === 0" text="暂无秒杀商品" />

    <div v-else class="grid">
      <ProductCard v-for="p in products" :key="p.id" :product="p" />
    </div>
  </div>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import { listProducts } from '../../api/product'
import ProductCard from '../../components/ProductCard.vue'
import EmptyState from '../../components/EmptyState.vue'

const products = ref([])
const loading = ref(false)
const keyword = ref('')
const pageNum = ref(1)
const pageSize = ref(12)

async function load() {
  loading.value = true
  try {
    const data = await listProducts({ pageNum: pageNum.value, pageSize: pageSize.value, keyword: keyword.value })
    products.value = data.records || []
  } catch (e) {
    // 拦截器已提示
  } finally {
    loading.value = false
  }
}

function onSearch() {
  pageNum.value = 1
  load()
}

onMounted(load)
</script>

<style scoped>
.toolbar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 20px;
}

.search {
  display: flex;
  gap: 10px;
}

.search input {
  border: 1px solid var(--border);
  border-radius: 8px;
  padding: 9px 12px;
  width: 220px;
  outline: none;
}

.grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(220px, 1fr));
  gap: 18px;
}
</style>
