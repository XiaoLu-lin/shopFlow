import { beforeEach, describe, expect, test, vi } from 'vitest'

const client = {
  get: vi.fn(),
  post: vi.fn(),
}

vi.mock('@/shared/request', () => ({
  getApiClient: () => client,
}))

describe('auth api', () => {
  beforeEach(() => {
    client.get.mockReset()
    client.post.mockReset()
  })

  test('requests register sms captcha with legacy payload', async () => {
    client.post.mockResolvedValueOnce({
      data: {
        errno: 0,
        data: null,
      },
    })

    const { requestRegisterCaptcha } = await import('./api')
    await requestRegisterCaptcha({ mobile: '13800000000' })

    expect(client.post).toHaveBeenLastCalledWith('/auth/captcha/mobile', {
      mobile: '13800000000',
    })
  })

  test('submits register payload with code, username, mobile and password', async () => {
    client.post.mockResolvedValueOnce({
      data: {
        errno: 0,
        data: null,
      },
    })

    const { submitRegister } = await import('./api')
    await submitRegister({
      code: '123456',
      username: 'shopflow-user',
      mobile: '13800000000',
      password: 'pass123456',
    })

    expect(client.post).toHaveBeenLastCalledWith('/auth/register', {
      code: '123456',
      username: 'shopflow-user',
      mobile: '13800000000',
      password: 'pass123456',
    })
  })

  test('requests forget password captcha with mobile only', async () => {
    client.post.mockResolvedValueOnce({
      data: {
        errno: 0,
        data: null,
      },
    })

    const { requestResetCaptcha } = await import('./api')
    await requestResetCaptcha({ mobile: '13800000000' })

    expect(client.post).toHaveBeenLastCalledWith('/auth/captcha/mobile', {
      mobile: '13800000000',
    })
  })

  test('submits password reset payload with mobile code and password', async () => {
    client.post.mockResolvedValueOnce({
      data: {
        errno: 0,
        data: null,
      },
    })

    const { submitPasswordReset } = await import('./api')
    await submitPasswordReset({
      mobile: '13800000000',
      code: '654321',
      password: 'new-pass-001',
    })

    expect(client.post).toHaveBeenLastCalledWith('/auth/reset', {
      mobile: '13800000000',
      code: '654321',
      password: 'new-pass-001',
    })
  })

  test('requests scene captcha and submits auth profile updates', async () => {
    client.post
      .mockResolvedValueOnce({
        data: {
          errno: 0,
          data: null,
        },
      })
      .mockResolvedValueOnce({
        data: {
          errno: 0,
          data: null,
        },
      })

    const { requestSceneCaptcha, updateAuthProfile } = await import('./api')
    await requestSceneCaptcha({
      mobile: '13800000000',
      type: 'bind-mobile',
    })
    await updateAuthProfile({
      nickname: '新昵称',
    })

    expect(client.post).toHaveBeenNthCalledWith(1, '/auth/captcha/mobile', {
      mobile: '13800000000',
      type: 'bind-mobile',
    })
    expect(client.post).toHaveBeenNthCalledWith(2, '/auth/profile', {
      nickname: '新昵称',
    })
  })
})
