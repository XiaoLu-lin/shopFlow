export type GoodsBrowseHeroType =
  | 'search'
  | 'search-result'
  | 'category'
  | 'brand-list'
  | 'brand'
  | 'new'
  | 'hot'
  | 'topic-list'
  | 'topic'
  | 'groupon'

export type GoodsBrowseTone = 'soft' | 'brand'

export interface GoodsBrowseHeroOptions {
  title?: string
  description?: string
}

export interface GoodsBrowseInfoTag {
  label: string
  tone: 'brand' | 'light'
}

export interface CategoryBrowsePill {
  id: number
  name: string
}

export function resolveGoodsBrowseHero(type: GoodsBrowseHeroType, options: GoodsBrowseHeroOptions = {}) {
  const title = options.title?.trim()
  const description = options.description?.trim()

  if (type === 'search') {
    return {
      tone: 'soft' as GoodsBrowseTone,
      eyebrow: 'Search picks',
      title: '搜索发现',
      description: '热门搜索和最近浏览会收在这里，方便你快速继续逛。',
    }
  }

  if (type === 'search-result') {
    return {
      tone: 'brand' as GoodsBrowseTone,
      eyebrow: 'Result view',
      title: title ? `“${title}”` : '搜索结果',
      description: description || '和当前关键词相关的商品已经收在下面，可以继续往下看。',
    }
  }

  if (type === 'category') {
    return {
      tone: 'soft' as GoodsBrowseTone,
      eyebrow: 'ShopFlow Category',
      title: title || '分类商品',
      description: description || '当前分类下的商品会统一收在这里，方便继续挑选。',
    }
  }

  if (type === 'brand-list') {
    return {
      tone: 'soft' as GoodsBrowseTone,
      eyebrow: 'ShopFlow Brand',
      title: '品牌馆',
      description: '把熟悉的品牌整理成一组更轻的目录，方便你按风格继续浏览。',
    }
  }

  if (type === 'brand') {
    return {
      tone: 'soft' as GoodsBrowseTone,
      eyebrow: 'Brand Story',
      title: title || '品牌详情',
      description: description || '先看一眼品牌介绍，再顺着这组商品继续挑选。',
    }
  }

  if (type === 'new') {
    return {
      tone: 'soft' as GoodsBrowseTone,
      eyebrow: 'Goods Feed',
      title: '新品首发',
      description: description || '这段时间刚刚上新的商品，都会优先收在这里。',
    }
  }

  if (type === 'hot') {
    return {
      tone: 'soft' as GoodsBrowseTone,
      eyebrow: 'Goods Feed',
      title: '热卖推荐',
      description: description || '当前更受欢迎的商品会排在这里，方便你集中浏览。',
    }
  }

  if (type === 'topic-list') {
    return {
      tone: 'soft' as GoodsBrowseTone,
      eyebrow: 'Topic Feed',
      title: '专题内容',
      description: '把专题内容整理成一组轻量内容卡，浏览起来会更顺一点。',
    }
  }

  if (type === 'topic') {
    return {
      tone: 'soft' as GoodsBrowseTone,
      eyebrow: 'Topic Detail',
      title: title || '专题详情',
      description: description || '先看专题内容，再顺着下面的商品继续往下浏览。',
    }
  }

  return {
    tone: 'soft' as GoodsBrowseTone,
    eyebrow: 'Group Buy',
    title: '拼团专区',
    description: description || '拼团商品会集中收在这里，成团人数和优惠信息也会一并展示。',
  }
}

export function resolveGoodsBrowseEmptyState(type: GoodsBrowseHeroType, options: GoodsBrowseHeroOptions = {}) {
  const title = options.title?.trim()

  if (type === 'search-result') {
    return {
      title: '没有找到相关商品',
      description: '可以换个关键词再试试，或者先逛逛别的分类。',
    }
  }

  if (type === 'category') {
    return {
      title: title ? `${title} 暂时还没有商品` : '当前分类还没有商品',
      description: '可以先看看相邻分类，或者稍后再回来浏览。',
    }
  }

  if (type === 'brand-list') {
    return {
      title: '暂时还没有品牌内容',
      description: '品牌馆整理完成后，会继续在这里展示。',
    }
  }

  if (type === 'brand') {
    return {
      title: '这个品牌暂时还没有商品',
      description: '等品牌商品上架后，会继续收在这里。',
    }
  }

  if (type === 'topic-list') {
    return {
      title: '暂时还没有专题内容',
      description: '等专题内容准备好后，会继续在这里更新。',
    }
  }

  if (type === 'topic') {
    return {
      title: '当前专题还没有关联商品',
      description: '专题内容会先保留在这里，相关商品稍后会继续补充。',
    }
  }

  if (type === 'groupon') {
    return {
      title: '暂时还没有拼团商品',
      description: '',
    }
  }

  return {
    title: '暂时还没有商品',
    description: '等商品数据准备好后，这里会继续更新。',
  }
}

export function resolveGoodsBrief(brief?: string | null, fallback = '把主要信息收得更紧一些，浏览会更顺手。') {
  const normalized = brief?.trim()
  return normalized || fallback
}

export function resolveGoodsBrowseInfoTags(type: GoodsBrowseHeroType): GoodsBrowseInfoTag[] {
  if (type === 'new') {
    return [
      { label: '本期上新', tone: 'brand' },
      { label: '轻量浏览', tone: 'light' },
    ]
  }

  if (type === 'hot') {
    return [
      { label: '热卖精选', tone: 'brand' },
      { label: '人气集中', tone: 'light' },
    ]
  }

  if (type === 'groupon') {
    return [
      { label: '多人拼团', tone: 'brand' },
      { label: '优惠直降', tone: 'light' },
    ]
  }

  return []
}

export function shouldRenderBrowseHeroDescription(type: GoodsBrowseHeroType) {
  return !['search', 'category', 'brand-list', 'brand', 'new', 'hot', 'topic-list', 'topic', 'groupon'].includes(type)
}

export function resolveCategoryBrowseTabs(currentCategory: CategoryBrowsePill | null, categoryList: CategoryBrowsePill[]) {
  const currentId = currentCategory?.id
  const normalized = currentId ? categoryList.filter((item, index, list) => {
    if (item.id !== currentId) {
      return true
    }

    return list.findIndex((entry) => entry.id === item.id) === index
  }) : categoryList

  if (!currentCategory) {
    return normalized
  }

  if (normalized.some((item) => item.id === currentId)) {
    return normalized
  }

  return [currentCategory, ...normalized]
}

export function resolveTopicContent(content?: string | null) {
  const normalized = content?.trim()
  return normalized || '<p>专题内容整理中，稍后会继续补充。</p>'
}
