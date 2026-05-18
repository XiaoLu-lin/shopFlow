interface BrowsePagedPayload<T> {
  list?: T[] | null
  page?: number | null
  pages?: number | null
}

export function resolveBrowsePageState<T>(existingList: T[], payload: BrowsePagedPayload<T>) {
  const currentPage = Math.max(payload.page || 1, 1)
  const totalPages = Math.max(payload.pages || currentPage, currentPage)
  const nextList = payload.list || []

  return {
    list: currentPage > 1 ? [...existingList, ...nextList] : nextList,
    nextPage: currentPage + 1,
    hasMore: currentPage < totalPages,
  }
}
