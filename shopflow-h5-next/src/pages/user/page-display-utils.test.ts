import { describe, expect, test } from 'vitest'
import {
  resolveAddressEditorMeta,
  resolveCouponEmptyState,
  resolveCouponTone,
  resolveProfileEditorMeta,
  resolveUserPageHero,
} from './page-display-utils'

describe('page display utils', () => {
  test('resolves compact hero copy for address, coupon and collect pages', () => {
    expect(resolveUserPageHero('address')).toEqual({
      eyebrow: 'Daily address',
      title: '收货地址',
      description: '保留一点生活感，但头部已经缩轻，不再压页面。',
    })

    expect(resolveUserPageHero('coupon')).toEqual({
      eyebrow: 'Coupon diary',
      title: '我的优惠券',
      description: '常用优惠都会收在这里，方便你随时查看和使用。',
    })

    expect(resolveUserPageHero('collect')).toEqual({
      eyebrow: 'SHOPFLOW PICKS',
      title: '我的收藏',
      description: '把喜欢的商品先收起来，之后随时回看。',
    })

    expect(resolveUserPageHero('profile')).toEqual({
      eyebrow: 'Profile home',
      title: '资料设置',
      description: '把昵称、手机号和密码入口收成一组更轻的资料卡片。',
    })

    expect(resolveUserPageHero('help')).toEqual({
      eyebrow: 'Help center',
      title: '帮助中心',
      description: '常见问题会收在这里，方便你快速找到答案。',
    })

    expect(resolveUserPageHero('service')).toEqual({
      eyebrow: 'Service hub',
      title: '服务中心',
      description: '把帮助、售后和反馈入口整理成一组更清楚的服务清单。',
    })

    expect(resolveUserPageHero('feedback')).toEqual({
      eyebrow: 'Feedback note',
      title: '意见反馈',
      description: '商品、页面或服务建议都可以留在这里，我们会继续跟进。',
    })

    expect(resolveUserPageHero('refund')).toEqual({
      eyebrow: 'After-sale',
      title: '退款 / 售后',
      description: '售后记录会收在这里，方便你随时查看进度。',
    })

    expect(resolveUserPageHero('refund-detail')).toEqual({
      eyebrow: 'Detail view',
      title: '售后详情',
      description: '关键信息分组收好后，查看进度会更轻松一些。',
    })

    expect(resolveUserPageHero('refund-apply')).toEqual({
      eyebrow: 'Request form',
      title: '申请售后',
      description: '按顺序补充原因、金额和凭证，就能提交新的售后申请。',
    })
  })

  test('resolves address editor hero and action labels by mode', () => {
    expect(resolveAddressEditorMeta(true)).toEqual({
      eyebrow: 'Soft form',
      title: '新增地址',
      description: '把常用收货信息先填好，后面下单时会更顺手。',
      submitLabel: '保存地址',
      deleteLabel: '删除地址',
    })

    expect(resolveAddressEditorMeta(false)).toEqual({
      eyebrow: 'Soft form',
      title: '编辑地址',
      description: '调整现有收货信息，常用地址会同步保持最新。',
      submitLabel: '保存地址',
      deleteLabel: '删除地址',
    })
  })

  test('resolves profile editor hero and action labels by field type', () => {
    expect(resolveProfileEditorMeta('nickname')).toEqual({
      eyebrow: 'Soft form',
      title: '修改昵称',
      description: '更新一个更顺手的展示名，保存后资料页会同步显示。',
      submitLabel: '保存修改',
      placeholder: '请输入昵称',
    })

    expect(resolveProfileEditorMeta('mobile')).toEqual({
      eyebrow: 'Soft form',
      title: '修改手机号',
      description: '保持登录信息和常用联系方式一致，后续查看订单也更方便。',
      submitLabel: '保存修改',
      placeholder: '请输入手机号',
    })

    expect(resolveProfileEditorMeta('password')).toEqual({
      eyebrow: 'Reset flow',
      title: '重置密码',
      description: '继续使用短信验证方式，按顺序完成手机号、验证码和新密码设置。',
      submitLabel: '确认重置',
      placeholder: '请输入新密码',
      captchaLabel: '获取验证码',
    })
  })

  test('resolves coupon tone and empty copy by status', () => {
    expect(resolveCouponTone(0)).toBe('brand')
    expect(resolveCouponTone(1)).toBe('deep')
    expect(resolveCouponTone(2)).toBe('muted')
    expect(resolveCouponTone(7)).toBe('brand')

    expect(resolveCouponEmptyState(0)).toEqual({
      title: '当前没有可用优惠券',
      description: '等你领券或下单后，这里会出现真实记录。',
    })

    expect(resolveCouponEmptyState(1)).toEqual({
      title: '还没有已使用优惠券',
      description: '下单抵扣后，已使用的优惠会收纳在这里。',
    })

    expect(resolveCouponEmptyState(2)).toEqual({
      title: '暂时没有已过期优惠券',
      description: '过期的优惠券记录会继续保留在这里。',
    })
  })
})
