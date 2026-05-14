export function normalizeCommentGoodsId(value: string | number | undefined) {
  if (value === undefined || value === null || value === '') {
    return ''
  }

  return String(value)
}

export function createGoodsCommentQuery(input: {
  goodsId: string
  page: number
  limit: number
  hasPicture: boolean
}) {
  return {
    goodsId: input.goodsId,
    page: input.page,
    limit: input.limit,
    hasPicture: input.hasPicture,
  }
}

export function shouldFinishCommentPaging(list: unknown[], page: number, pages: number) {
  return list.length === 0 || page >= pages
}
