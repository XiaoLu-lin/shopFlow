<script setup lang="ts">
import BrandShell from './BrandShell.vue'

defineProps<{
  title: string
  description: string
  bullets?: string[]
}>()
</script>

<template>
  <BrandShell :title="title" :description="description">
    <ul v-if="bullets?.length" class="space-y-1.5">
      <li
        v-for="bullet in bullets"
        :key="bullet"
        class="rounded-[8px] border border-line/80 bg-shell px-2.5 py-1.5 text-[12px] leading-[1.4] text-ink/74"
      >
        {{ bullet }}
      </li>
    </ul>
  </BrandShell>
</template>
