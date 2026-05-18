const H5_TAB_BAR_PAGE_ERROR = 'setTabBarStyle:fail not TabBar page'

type UniRuntimeLike = {
  setTabBarStyle?: (...args: any[]) => unknown
}

type GuardedSetTabBarStyle = ((...args: any[]) => unknown) & {
  __shopflowGuarded?: boolean
}

export function installH5TabBarStyleCompat(runtime?: UniRuntimeLike) {
  const target = runtime ?? resolveUniRuntime()
  const original = target?.setTabBarStyle as GuardedSetTabBarStyle | undefined

  if (!target || !original || original.__shopflowGuarded) {
    return
  }

  const guarded: GuardedSetTabBarStyle = (...args: any[]) => {
    const result = original(...args)

    if (isPromiseLike(result)) {
      return result.catch((error) => {
        if (isIgnoredH5TabBarError(error)) {
          return error
        }

        throw error
      })
    }

    return result
  }

  guarded.__shopflowGuarded = true
  target.setTabBarStyle = guarded
}

export function isIgnoredH5TabBarError(error: unknown) {
  const message = resolveErrorMessage(error)
  return message === H5_TAB_BAR_PAGE_ERROR
}

function isPromiseLike(value: unknown): value is Promise<unknown> {
  return typeof value === 'object'
    && value !== null
    && typeof (value as Promise<unknown>).then === 'function'
    && typeof (value as Promise<unknown>).catch === 'function'
}

function resolveErrorMessage(error: unknown) {
  if (typeof error === 'object' && error !== null && 'errMsg' in error) {
    return String((error as { errMsg?: unknown }).errMsg || '').trim()
  }

  if (error instanceof Error) {
    return error.message.trim()
  }

  return String(error || '').trim()
}

function resolveUniRuntime(): UniRuntimeLike | undefined {
  return typeof uni !== 'undefined' ? (uni as unknown as UniRuntimeLike) : undefined
}
