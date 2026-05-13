<script setup lang="ts">
defineProps<{
  title: string
  eyebrow?: string
  description?: string
}>()
</script>

<template>
  <section
    class="relative overflow-hidden rounded-shell border border-line/80 bg-white p-3 shadow-card"
  >
    <div class="absolute inset-x-4 top-0 h-px bg-gradient-to-r from-transparent via-brand/35 to-transparent" />
    <p v-if="eyebrow" class="text-[10px] uppercase tracking-[0.12em] text-brand-deep/75">
      {{ eyebrow }}
    </p>
    <div class="flex items-start justify-between gap-4">
      <div>
        <h2 class="font-display text-[16px] font-semibold text-ink">
          {{ title }}
        </h2>
        <p v-if="description" class="mt-1 text-[12px] leading-[1.4] text-ink/62">
          {{ description }}
        </p>
      </div>
      <slot name="action" />
    </div>
    <div class="mt-2">
      <slot />
    </div>
  </section>
</template>
