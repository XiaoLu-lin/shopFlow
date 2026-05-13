import { describe, expect, test } from 'vitest'
import { createMemoryStorage } from '@/shared/platform/storage'
import {
  buildH5RedirectUrl,
  buildJsapiInvokePayload,
  buildPaymentStatusRouteQuery,
  clearPrepayData,
  persistPrepayData,
  readPrepayData,
  resolvePaymentResultCopy,
  resolvePaymentStatus,
} from './payment-utils'

describe('payment utils', () => {
  test('builds jsapi invoke payload from prepay payload', () => {
    expect(buildJsapiInvokePayload({
      appId: 'wx-1',
      timeStamp: '123',
      nonceStr: 'nonce',
      packageValue: 'prepay_id=1',
      paySign: 'sign',
    })).toEqual({
      appId: 'wx-1',
      timeStamp: '123',
      nonceStr: 'nonce',
      package: 'prepay_id=1',
      signType: 'MD5',
      paySign: 'sign',
    })
  })

  test('persists and reads prepay data from local storage', () => {
    const storage = createMemoryStorage()
    persistPrepayData({
      appId: 'wx-1',
      timeStamp: '123',
      nonceStr: 'nonce',
      packageValue: 'prepay_id=1',
      paySign: 'sign',
    }, storage)

    expect(readPrepayData(storage)).toEqual({
      appId: 'wx-1',
      timeStamp: '123',
      nonceStr: 'nonce',
      package: 'prepay_id=1',
      signType: 'MD5',
      paySign: 'sign',
    })

    clearPrepayData(storage)
    expect(readPrepayData(storage)).toBeNull()
  })

  test('resolves payment status from wechat bridge callback', () => {
    expect(resolvePaymentStatus('get_brand_wcpay_request:ok')).toBe('success')
    expect(resolvePaymentStatus('get_brand_wcpay_request:cancel')).toBe('cancel')
    expect(resolvePaymentStatus('get_brand_wcpay_request:fail')).toBe('failed')
  })

  test('builds h5 redirect url for payment callback', () => {
    expect(buildH5RedirectUrl('http://localhost:6257', '/', 'success', ['66', '67'])).toBe(
      'http://localhost:6257/#/order/payment/success?orderId=66&orderIds=66%2C67',
    )
  })

  test('builds payment status route query with order context', () => {
    expect(buildPaymentStatusRouteQuery(['66', '67'])).toEqual({
      orderId: '66',
      orderIds: '66,67',
    })

    expect(buildPaymentStatusRouteQuery([])).toEqual({})
  })

  test('resolves payment result copy from route status and real order status', () => {
    expect(resolvePaymentResultCopy({
      routeStatus: 'success',
      orderStatusText: '待发货',
    })).toMatchObject({
      title: '支付成功',
      description: '订单已支付，当前状态：待发货。',
      accent: 'success',
      autoRedirect: true,
    })

    expect(resolvePaymentResultCopy({
      routeStatus: 'success',
      orderStatusText: '待付款',
    })).toMatchObject({
      title: '支付结果确认中',
      accent: 'pending',
      autoRedirect: false,
    })

    expect(resolvePaymentResultCopy({
      routeStatus: 'cancel',
    })).toMatchObject({
      title: '支付已取消',
      accent: 'warning',
      autoRedirect: false,
    })
  })
})
