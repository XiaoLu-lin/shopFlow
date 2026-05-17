import { describe, expect, test } from 'vitest'
import {
  resolveCategoryBrowseTabs,
  resolveGoodsBrief,
  resolveGoodsBrowseEmptyState,
  resolveGoodsBrowseHero,
  resolveGoodsBrowseInfoTags,
  shouldRenderBrowseHeroDescription,
  resolveTopicContent,
} from './browse-display-utils'

describe('browse-display-utils', () => {
  test('为搜索结果和品牌详情生成稳定 hero 文案', () => {
    expect(resolveGoodsBrowseHero('search-result', { title: '轻软跑鞋' })).toEqual({
      tone: 'brand',
      eyebrow: 'Result view',
      title: '“轻软跑鞋”',
      description: '和当前关键词相关的商品已经收在下面，可以继续往下看。',
    })

    expect(resolveGoodsBrowseHero('brand', { title: 'Blue Studio', description: '城市通勤系列' })).toEqual({
      tone: 'soft',
      eyebrow: 'Brand Story',
      title: 'Blue Studio',
      description: '城市通勤系列',
    })

    expect(resolveGoodsBrowseHero('brand-list')).toEqual({
      tone: 'soft',
      eyebrow: 'ShopFlow Brand',
      title: '品牌馆',
      description: '把熟悉的品牌整理成一组更轻的目录，方便你按风格继续浏览。',
    })

    expect(resolveGoodsBrowseHero('topic-list')).toEqual({
      tone: 'soft',
      eyebrow: 'Topic Feed',
      title: '专题内容',
      description: '把专题内容整理成一组轻量内容卡，浏览起来会更顺一点。',
    })

    expect(resolveGoodsBrowseHero('topic', { title: '春夏焕新', description: '当季轻搭配' })).toEqual({
      tone: 'soft',
      eyebrow: 'Topic Detail',
      title: '春夏焕新',
      description: '当季轻搭配',
    })

    expect(resolveGoodsBrowseHero('groupon')).toEqual({
      tone: 'soft',
      eyebrow: 'Group Buy',
      title: '拼团专区',
      description: '拼团商品会集中收在这里，成团人数和优惠信息也会一并展示。',
    })
  })

  test('为分类和拼团页生成正式空态文案', () => {
    expect(resolveGoodsBrowseEmptyState('category', { title: '女士夏装' })).toEqual({
      title: '女士夏装 暂时还没有商品',
      description: '可以先看看相邻分类，或者稍后再回来浏览。',
    })

    expect(resolveGoodsBrowseEmptyState('groupon')).toEqual({
      title: '暂时还没有拼团商品',
      description: '',
    })
  })

  test('为商品简介和专题正文提供保守降级内容', () => {
    expect(resolveGoodsBrief('  轻软透气  ')).toBe('轻软透气')
    expect(resolveGoodsBrief('')).toBe('把主要信息收得更紧一些，浏览会更顺手。')
    expect(resolveTopicContent('')).toBe('<p>专题内容整理中，稍后会继续补充。</p>')
    expect(resolveTopicContent('<p>专题正文</p>')).toBe('<p>专题正文</p>')
  })

  test('为商品流页面生成轻量标签，避免伪造运营数据', () => {
    expect(resolveGoodsBrowseInfoTags('new')).toEqual([
      { label: '本期上新', tone: 'brand' },
      { label: '轻量浏览', tone: 'light' },
    ])

    expect(resolveGoodsBrowseInfoTags('groupon')).toEqual([
      { label: '多人拼团', tone: 'brand' },
      { label: '优惠直降', tone: 'light' },
    ])
  })

  test('分类页沿用原来的切换条语义，保留当前分类并且只出现一次', () => {
    expect(resolveCategoryBrowseTabs({ id: 2, name: '拖鞋' }, [
      { id: 1, name: '鞋' },
      { id: 2, name: '拖鞋' },
      { id: 3, name: '靴' },
    ])).toEqual([
      { id: 1, name: '鞋' },
      { id: 2, name: '拖鞋' },
      { id: 3, name: '靴' },
    ])

    expect(resolveCategoryBrowseTabs({ id: 2, name: '拖鞋' }, [
      { id: 1, name: '鞋' },
      { id: 3, name: '靴' },
    ])).toEqual([
      { id: 2, name: '拖鞋' },
      { id: 1, name: '鞋' },
      { id: 3, name: '靴' },
    ])
  })

  test('可按页面类型关闭多余 hero 说明文案', () => {
    expect(shouldRenderBrowseHeroDescription('category')).toBe(false)
    expect(shouldRenderBrowseHeroDescription('brand-list')).toBe(false)
    expect(shouldRenderBrowseHeroDescription('brand')).toBe(false)
    expect(shouldRenderBrowseHeroDescription('new')).toBe(false)
    expect(shouldRenderBrowseHeroDescription('hot')).toBe(false)
    expect(shouldRenderBrowseHeroDescription('groupon')).toBe(false)
    expect(shouldRenderBrowseHeroDescription('topic-list')).toBe(false)
    expect(shouldRenderBrowseHeroDescription('topic')).toBe(false)
    expect(shouldRenderBrowseHeroDescription('search')).toBe(false)
    expect(shouldRenderBrowseHeroDescription('search-result')).toBe(true)
  })
})
