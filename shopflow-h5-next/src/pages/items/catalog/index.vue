<template>
  <view class="page">
    <view class="catalog-shell">
      <view class="catalog-search" @click="goSearch">
        <view class="catalog-search-copy">
          <text class="catalog-search-desc">搜索商品、品牌和分类</text>
        </view>
        <text class="catalog-search-action">搜索</text>
      </view>

      <view class="catalog-layout">
        <scroll-view scroll-y class="catalog-sidebar">
          <view class="catalog-sidebar-inner">
            <view
              v-for="item in categories"
              :key="item.id"
              class="catalog-sidebar-item"
              :class="{ 'catalog-sidebar-item--active': currentCategory?.id === item.id }"
              @click="selectCategory(item.id)"
            >
              {{ item.name }}
            </view>
          </view>
        </scroll-view>

        <scroll-view scroll-y class="catalog-content">
          <view class="catalog-content-inner">
            <view class="catalog-banner">
              <view class="catalog-banner-copy">
                <text class="catalog-banner-eyebrow">{{ heroCopy.eyebrow }}</text>
                <text class="catalog-banner-title">{{ heroCopy.title }}</text>
                <text class="catalog-banner-desc">{{ heroCopy.description }}</text>
              </view>

              <image
                v-if="currentCategory?.picUrl"
                class="catalog-banner-image"
                :src="currentCategory.picUrl"
                mode="aspectFill"
              />
              <view v-else class="catalog-banner-placeholder">
                <text class="catalog-banner-placeholder-title">{{ currentCategory?.name || '精选分类' }}</text>
              </view>
            </view>

            <view v-if="currentSubCategories.length" class="catalog-grid">
              <view
                v-for="item in currentSubCategories"
                :key="item.id"
                class="catalog-grid-card"
                @click="goCategoryGoods(item.id, item.name)"
              >
                <image
                  v-if="item.picUrl"
                  class="catalog-grid-image"
                  :src="item.picUrl"
                  mode="aspectFill"
                />
                <view v-else class="catalog-grid-image catalog-grid-image--empty" />
                <text class="catalog-grid-name">{{ item.name }}</text>
              </view>
            </view>

            <view v-else class="catalog-empty">
              <text class="catalog-empty-title">{{ emptyState.title }}</text>
              <text class="catalog-empty-desc">{{ emptyState.description }}</text>
            </view>
          </view>
        </scroll-view>
      </view>
    </view>
  </view>
</template>

<script setup lang="ts">
import { computed, ref, watch } from 'vue'
import { fetchCatalogList, fetchCurrentCatalog } from '@/entities/catalog/api'
import type { CatalogCategory, CatalogCurrentPayload, CatalogListPayload } from '@/entities/catalog/api'
import { resolveCatalogEmptyState, resolveCatalogHeroCopy } from '@/features/catalog/catalog-display-utils'

const catalog = ref<CatalogListPayload | null>(null)
const currentCatalog = ref<CatalogCurrentPayload | null>(null)
const currentId = ref<number | null>(null)

const categories = computed(() => catalog.value?.categoryList || [])
const currentCategory = computed<CatalogCategory | undefined>(
  () => currentCatalog.value?.currentCategory || catalog.value?.currentCategory,
)
const currentSubCategories = computed(
  () => currentCatalog.value?.currentSubCategory || catalog.value?.currentSubCategory || [],
)
const heroCopy = computed(() => resolveCatalogHeroCopy(currentCategory.value))
const emptyState = computed(() => resolveCatalogEmptyState(currentCategory.value?.name))

watch(categories, (value) => {
  if (!currentId.value && value.length) {
    currentId.value = value[0].id
  }
}, { immediate: true })

watch(currentId, async (value) => {
  if (!value) {
    return
  }

  try {
    currentCatalog.value = await fetchCurrentCatalog(value)
  } catch (error) {
    console.error(error)
  }
}, { immediate: true })

bootstrap()

async function bootstrap() {
  try {
    catalog.value = await fetchCatalogList()
    currentId.value = catalog.value.currentCategory?.id || catalog.value.categoryList[0]?.id || null
  } catch (error) {
    console.error(error)
  }
}

function selectCategory(id: number) {
  if (currentId.value === id) {
    return
  }

  currentId.value = id
}

function goSearch() {
  uni.navigateTo({
    url: '/pages/items/search/index',
  })
}

function goCategoryGoods(id: number, name: string) {
  uni.navigateTo({
    url: `/pages/items/category/index?categoryId=${id}&title=${encodeURIComponent(name)}`,
  })
}
</script>

<style scoped lang="scss" src="./catalog-page.scss"></style>
