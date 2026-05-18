<template>
</template>

<script setup lang="ts">
import { onHide, onLaunch, onShow } from '@dcloudio/uni-app'

onLaunch(() => {
  console.log('ShopFlow uni-app launch')
})

onShow(() => {
  console.log('ShopFlow uni-app show')
})

onHide(() => {
  console.log('ShopFlow uni-app hide')
})
</script>
