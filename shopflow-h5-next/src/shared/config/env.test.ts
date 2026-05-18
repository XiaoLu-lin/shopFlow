import { describe, expect, test } from 'vitest'
import { DEFAULT_H5_BASE_API, DEFAULT_MP_WEIXIN_BASE_API } from './env'

describe('env config', () => {
  test('uses a dedicated h5 api prefix to avoid colliding with the h5 site path', () => {
    expect(DEFAULT_H5_BASE_API).toBe('/wx-api')
  })

  test('uses a dedicated online mini program api origin path', () => {
    expect(DEFAULT_MP_WEIXIN_BASE_API).toBe('https://manager.enshipeixue.com/wx-api')
  })
})
