export function resolveOrderEmptyState(activeTab: number) {
  if (activeTab === 1) {
    return {
      title: '还没有待付款订单',
      description: '待付款订单会在你提交订单后出现在这里。',
    }
  }

  if (activeTab === 2) {
    return {
      title: '还没有待发货订单',
      description: '商家确认发货后，订单会展示在这里。',
    }
  }

  if (activeTab === 3) {
    return {
      title: '还没有待收货订单',
      description: '发货后的订单会在这里提醒你确认收货。',
    }
  }

  if (activeTab === 4) {
    return {
      title: '还没有待评价订单',
      description: '确认收货后的订单会在这里等待评价。',
    }
  }

  return {
    title: '暂时还没有订单',
    description: '挑几件喜欢的商品，下单后就会展示在这里。',
  }
}

export function resolvePaymentMethodOptions(inWechat: boolean) {
  return [
    {
      key: 'wx',
      title: '微信支付',
      description: inWechat ? '当前在微信内，将优先发起 JSAPI 支付。' : '当前环境将优先尝试 H5 支付跳转。',
      stateLabel: '推荐',
      enabled: true,
    },
    {
      key: 'ali',
      title: '支付宝',
      description: '入口保留，当前版本暂未开放。',
      stateLabel: '暂未开放',
      enabled: false,
    },
  ] as const
}

export function resolveOrderOverviewStats(items: Array<{ label: string; count: number }>) {
  return items.map((item) => ({
    ...item,
    value: item.count > 0 ? `${item.count} 笔` : '暂无',
  }))
}
