import { describe, expect, it } from 'vitest'
import { resolveCatalogEmptyState, resolveCatalogHeroCopy } from './catalog-display-utils'

describe('catalog-display-utils', () => {
  it('为分类 banner 提供兜底文案', () => {
    expect(resolveCatalogHeroCopy({ name: '包袋鞋履', desc: '' })).toEqual({
      eyebrow: 'ShopFlow Catalog',
      title: '包袋鞋履',
      description: '按分类浏览 ShopFlow 精选好物，切换左侧目录继续往下看。',
    })
  })

  it('优先展示真实分类描述', () => {
    expect(resolveCatalogHeroCopy({ name: '服饰上新', desc: '轻松切换子分类，快速找到想看的上新。' })).toEqual({
      eyebrow: 'ShopFlow Catalog',
      title: '服饰上新',
      description: '轻松切换子分类，快速找到想看的上新。',
    })
  })

  it('生成空分类提示', () => {
    expect(resolveCatalogEmptyState('轻食饮品')).toEqual({
      title: '轻食饮品 暂时还没有子分类',
      description: '可以先切换左侧分类继续浏览。',
    })
  })
})
