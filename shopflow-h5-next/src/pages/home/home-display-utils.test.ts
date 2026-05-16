import { describe, expect, it } from 'vitest'
import {
  resolveHomeFeed,
  shouldShowCoupons,
  shouldShowFlashSale,
  type HomeFeedKey,
} from './home-display-utils'
import type { HomePayload } from '@/entities/home/api'

function makeHomePayload(overrides: Partial<HomePayload> = {}): HomePayload {
  return {
    banner: [],
    channel: [],
    couponList: [],
    grouponList: [],
    brandList: [],
    newGoodsList: [
      {
        id: 1,
        name: '新品',
        brief: '新品简介',
        picUrl: '/new.png',
        retailPrice: 99,
      },
    ],
    hotGoodsList: [
      {
        id: 2,
        name: '热销',
        brief: '热销简介',
        picUrl: '/hot.png',
        retailPrice: 88,
      },
    ],
    topicList: [],
    ...overrides,
  }
}

describe('home-display-utils', () => {
  it('根据 feed key 返回对应首页商品列表', () => {
    const payload = makeHomePayload()

    expect(resolveHomeFeed('new', payload)).toEqual(payload.newGoodsList)
    expect(resolveHomeFeed('hot', payload)).toEqual(payload.hotGoodsList)
  })

  it('推荐 feed 优先使用热销和新品组合兜底', () => {
    const payload = makeHomePayload()

    expect(resolveHomeFeed('recommend', payload).map((item) => item.id)).toEqual([2, 1])
  })

  it('未知 feed key 降级为推荐列表', () => {
    const payload = makeHomePayload()

    expect(resolveHomeFeed('unknown' as HomeFeedKey, payload).map((item) => item.id)).toEqual([2, 1])
  })

  it('根据数据判断优惠券和秒杀区块是否展示', () => {
    const payload = makeHomePayload({
      couponList: [
        {
          id: 1,
          name: '新人券',
          desc: '满减',
          tag: '新人',
          discount: 30,
          days: 7,
        },
      ],
      grouponList: [
        {
          id: 3,
          name: '团购',
          brief: '团购简介',
          picUrl: '/groupon.png',
          retailPrice: 49,
        },
      ],
    })

    expect(shouldShowCoupons(payload)).toBe(true)
    expect(shouldShowFlashSale(payload)).toBe(true)
    expect(shouldShowCoupons(makeHomePayload())).toBe(false)
    expect(shouldShowFlashSale(makeHomePayload())).toBe(false)
  })

})
