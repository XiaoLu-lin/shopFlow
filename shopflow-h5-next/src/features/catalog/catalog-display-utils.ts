export interface CatalogHeroInput {
  name?: string | null
  desc?: string | null
}

export function resolveCatalogHeroCopy(category?: CatalogHeroInput | null) {
  const title = category?.name?.trim() || '精选分类'
  const description = category?.desc?.trim() || '按分类浏览 ShopFlow 精选好物，切换左侧目录继续往下看。'

  return {
    eyebrow: 'ShopFlow Catalog',
    title,
    description,
  }
}

export function resolveCatalogEmptyState(categoryName?: string | null) {
  const title = categoryName?.trim() || '当前分类'
  return {
    title: `${title} 暂时还没有子分类`,
    description: '可以先切换左侧分类继续浏览。',
  }
}
