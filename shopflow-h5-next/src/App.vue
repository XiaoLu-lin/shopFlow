<template>
</template>

<script setup lang="ts">
import { onHide, onLaunch, onShow } from '@dcloudio/uni-app'
import { onBeforeUnmount, onMounted } from 'vue'
import { resolveLegacyHashRedirectTarget } from '@/shared/utils/legacy-route-map'

onLaunch(() => {
  syncLegacyHashRoute()
  console.log('ShopFlow uni-app launch')
})

onShow(() => {
  syncLegacyHashRoute()
  console.log('ShopFlow uni-app show')
})

onHide(() => {
  console.log('ShopFlow uni-app hide')
})

onMounted(() => {
  if (typeof window === 'undefined') {
    return
  }

  window.addEventListener('hashchange', handleHashChange)
})

onBeforeUnmount(() => {
  if (typeof window === 'undefined') {
    return
  }

  window.removeEventListener('hashchange', handleHashChange)
})

function handleHashChange() {
  syncLegacyHashRoute()
}

function syncLegacyHashRoute() {
  if (typeof window === 'undefined') {
    return
  }

  const nextHash = resolveLegacyHashRedirectTarget(window.location.href)

  if (!nextHash || nextHash === window.location.hash) {
    return
  }

  window.location.replace(`${window.location.pathname}${window.location.search}${nextHash}`)
}
</script>
