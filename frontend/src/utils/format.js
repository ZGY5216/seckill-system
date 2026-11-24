// 金额格式化：保留两位小数，前缀 ¥
export function formatPrice(value) {
  if (value === null || value === undefined || value === '') return '¥0.00'
  const num = Number(value)
  return '¥' + num.toFixed(2)
}

// 时间格式化
export function formatDateTime(value) {
  if (!value) return '-'
  return String(value).replace('T', ' ').slice(0, 19)
}
