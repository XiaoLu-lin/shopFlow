import { describe, expect, test } from 'vitest'
import {
  getAuthActionSuccessMessage,
  resolveAuthActionHero,
  validateAuthActionForm,
} from './auth-form-display-utils'

describe('auth-form-display-utils', () => {
  test('为登录、注册、找回密码生成稳定 hero 文案', () => {
    expect(resolveAuthActionHero('login')).toEqual({
      eyebrow: 'ShopFlow',
      title: '账号登录',
      description: '',
    })

    expect(resolveAuthActionHero('register')).toEqual({
      eyebrow: 'ShopFlow',
      title: '注册账号',
      description: '用手机号完成注册，流程会压成一页，体验更连贯。',
    })

    expect(resolveAuthActionHero('reset')).toEqual({
      eyebrow: 'ShopFlow',
      title: '找回密码',
      description: '在同一页完成验证码校验和新密码设置，不再拆成迁移期多页面。',
    })
  })

  test('为注册与找回密码校验必填项、手机号和密码一致性', () => {
    expect(validateAuthActionForm('register', {
      mobile: '',
      code: '',
      username: '',
      password: '',
      confirmPassword: '',
    })).toBe('请完整填写注册信息')

    expect(validateAuthActionForm('register', {
      mobile: '123',
      code: '123456',
      username: 'tester',
      password: 'pass123456',
      confirmPassword: 'pass123456',
    })).toBe('请输入正确的手机号')

    expect(validateAuthActionForm('register', {
      mobile: '13800000000',
      code: '123456',
      username: 'tester',
      password: 'pass123456',
      confirmPassword: 'pass123',
    })).toBe('两次密码输入不一致')

    expect(validateAuthActionForm('reset', {
      mobile: '13800000000',
      code: '654321',
      password: 'new-pass-001',
      confirmPassword: 'new-pass-001',
    })).toBe('')
  })

  test('为注册与找回密码生成成功提示文案', () => {
    expect(getAuthActionSuccessMessage('register')).toBe('注册成功，请登录')
    expect(getAuthActionSuccessMessage('reset')).toBe('密码重置成功，请登录')
  })
})
