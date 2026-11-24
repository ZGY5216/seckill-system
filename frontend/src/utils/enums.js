// 订单状态字典
export const ORDER_STATUS = {
  PENDING_PAYMENT: { label: '待支付', color: '#e8a33d' },
  PAID: { label: '已支付', color: '#2e9e5b' },
  COMPLETED: { label: '已完成', color: '#666' },
  CANCELLED: { label: '已取消', color: '#999' }
}

// 活动状态字典
export const ACTIVITY_STATUS = {
  NOT_STARTED: { label: '未开始', color: '#666' },
  ONGOING: { label: '进行中', color: '#2e9e5b' },
  ENDED: { label: '已结束', color: '#999' }
}

export function orderStatusLabel(status) {
  return ORDER_STATUS[status]?.label || status
}

export function activityStatusLabel(status) {
  return ACTIVITY_STATUS[status]?.label || status
}
