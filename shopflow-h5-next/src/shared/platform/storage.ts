export interface StorageLike {
  getItem(key: string): string | null
  setItem(key: string, value: string): void
  removeItem(key: string): void
  clear?(): void
}

function hasWindowStorage() {
  return typeof window !== 'undefined' && typeof window.localStorage !== 'undefined'
}

export function createMemoryStorage(seed: Record<string, string> = {}): StorageLike {
  const store = new Map(Object.entries(seed))

  return {
    getItem(key: string) {
      return store.has(key) ? store.get(key)! : null
    },
    setItem(key: string, value: string) {
      store.set(key, value)
    },
    removeItem(key: string) {
      store.delete(key)
    },
    clear() {
      store.clear()
    },
  }
}

export function createUniStorage(): StorageLike {
  return {
    getItem(key: string) {
      const value = uni.getStorageSync(key)
      if (value === '' || value === undefined || value === null) {
        return null
      }
      return String(value)
    },
    setItem(key: string, value: string) {
      uni.setStorageSync(key, value)
    },
    removeItem(key: string) {
      uni.removeStorageSync(key)
    },
    clear() {
      uni.clearStorageSync()
    },
  }
}

export function resolveStorage(): StorageLike {
  if (hasWindowStorage()) {
    return window.localStorage
  }

  return createUniStorage()
}
