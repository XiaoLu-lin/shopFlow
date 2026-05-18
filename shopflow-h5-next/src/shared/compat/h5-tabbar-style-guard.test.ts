import { describe, expect, test, vi } from 'vitest'
import { installH5TabBarStyleCompat, isIgnoredH5TabBarError } from './h5-tabbar-style-guard'

describe('h5 tabbar style guard', () => {
  test('swallows known h5 tabbar page mismatch rejection', async () => {
    const runtime = {
      setTabBarStyle: vi.fn().mockRejectedValue({
        errMsg: 'setTabBarStyle:fail not TabBar page',
      }),
    }

    installH5TabBarStyleCompat(runtime)

    await expect(runtime.setTabBarStyle?.({})).resolves.toEqual({
      errMsg: 'setTabBarStyle:fail not TabBar page',
    })
  })

  test('keeps unexpected tabbar errors visible', async () => {
    const runtime = {
      setTabBarStyle: vi.fn().mockRejectedValue(new Error('unexpected tabbar failure')),
    }

    installH5TabBarStyleCompat(runtime)

    await expect(runtime.setTabBarStyle?.({})).rejects.toThrow('unexpected tabbar failure')
  })

  test('matches the known h5 tabbar page mismatch error shape', () => {
    expect(isIgnoredH5TabBarError({ errMsg: 'setTabBarStyle:fail not TabBar page' })).toBe(true)
    expect(isIgnoredH5TabBarError(new Error('setTabBarStyle:fail not TabBar page'))).toBe(true)
    expect(isIgnoredH5TabBarError({ errMsg: 'other error' })).toBe(false)
  })
})
