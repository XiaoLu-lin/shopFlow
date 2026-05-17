export function resolveCartSubmitMeta(input: {
  editing: boolean
  checkedCount: number
}) {
  return {
    label: input.editing ? '删除所选商品' : '去结算',
    disabled: input.checkedCount <= 0,
  }
}

export function resolveCartSelectionCopy(checkedCount: number) {
  if (checkedCount <= 0) {
    return '未选择商品'
  }

  return `已选 ${checkedCount} 件`
}
