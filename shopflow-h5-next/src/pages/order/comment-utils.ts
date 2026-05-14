export function getCommentStarText(star: number) {
  if (star <= 1) return '很差'
  if (star === 2) return '不太满意'
  if (star === 3) return '满意'
  if (star === 4) return '比较满意'
  return '十分满意'
}

export function validateOrderCommentForm(input: {
  star: number
  content: string
  picUrls: string[]
}) {
  if (!input.star || input.star < 1) {
    return '请选择评分'
  }

  if (!input.content.trim()) {
    return '请填写评论'
  }

  return ''
}

export function buildOrderCommentPayload(input: {
  goodsId: number
  star: number
  content: string
  picUrls: string[]
}) {
  return {
    goodsId: input.goodsId,
    star: input.star,
    content: input.content.trim(),
    hasPicture: input.picUrls.length > 0,
    picUrls: input.picUrls,
  }
}
