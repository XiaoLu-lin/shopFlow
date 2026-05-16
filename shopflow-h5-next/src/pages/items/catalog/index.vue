<template>
  <view class="page">
    <view class="search-card" @click="goSearch">
      <view>
        <text class="brand-name">ShopFlow</text>
        <text class="search-copy">搜索商品、品牌和分类</text>
      </view>
      <text class="search-action">搜索</text>
    </view>

    <view class="catalog-layout">
      <scroll-view scroll-y class="sidebar">
        <view
          v-for="item in categories"
          :key="item.id"
          class="sidebar-item"
          :class="{ 'sidebar-item--active': currentCategory?.id === item.id }"
          @click="selectCategory(item.id)"
        >
          {{ item.name }}
        </view>
      </scroll-view>

      <scroll-view scroll-y class="content">
        <view class="hero">
          <image
            v-if="currentCategory?.picUrl"
            class="hero-image"
            :src="currentCategory.picUrl"
            mode="aspectFill"
          />
          <view v-else class="hero-placeholder">
            <text class="hero-placeholder-title">{{ currentCategory?.name || '精选分类' }}</text>
          </view>
          <view class="hero-body">
            <text class="hero-title">{{ currentCategory?.name || '分类加载中' }}</text>
            <text class="hero-desc">
              {{ currentCategory?.desc || '按分类浏览 ShopFlow 精选商品。' }}
            </text>
          </view>
        </view>

        <view v-if="currentSubCategories.length" class="sub-grid">
          <view
            v-for="item in currentSubCategories"
            :key="item.id"
            class="sub-card"
            @click="goCategoryGoods(item.id, item.name)"
          >
            <image
              v-if="item.picUrl"
              class="sub-image"
              :src="item.picUrl"
              mode="aspectFill"
            />
            <view v-else class="sub-image sub-image--empty" />
            <text class="sub-name">{{ item.name }}</text>
          </view>
        </view>

        <view v-else class="empty-card">
          <text class="empty-title">暂无子分类</text>
          <text class="empty-desc">可以切换左侧分类继续浏览。</text>
        </view>
      </scroll-view>
    </view>
  </view>
</template>

<script setup lang="ts">
import { computed, ref, watch } from 'vue'
import { fetchCatalogList, fetchCurrentCatalog } from '@/entities/catalog/api'
import type { CatalogCategory, CatalogCurrentPayload, CatalogListPayload } from '@/entities/catalog/api'

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

<style scoped lang="scss">
.page {
  min-height: 100vh;
  padding: 18rpx 20rpx 36rpx;
  background: rgb(var(--sf-color-page));
}

.search-card {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 20rpx;
  padding: 18rpx 20rpx;
  border: 2rpx solid rgb(var(--sf-color-line));
  border-radius: 16rpx;
  background: rgb(var(--sf-color-shell));
  box-shadow: var(--sf-shadow-card);
}

.brand-name,
.search-copy {
  display: block;
}

.brand-name {
  color: rgb(var(--sf-color-brand));
  font-size: 32rpx;
  font-weight: 700;
  line-height: 1.2;
}

.search-copy {
  margin-top: 4rpx;
  color: rgb(var(--sf-color-text-secondary));
  font-size: 22rpx;
  line-height: 1.35;
}

.search-action {
  flex-shrink: 0;
  padding: 8rpx 16rpx;
  border-radius: 999px;
  background: rgb(var(--sf-color-brand-soft));
  color: rgb(var(--sf-color-brand));
  font-size: 22rpx;
  font-weight: 700;
}

.catalog-layout {
  display: flex;
  gap: 16rpx;
  height: calc(100vh - 142rpx);
  margin-top: 18rpx;
}

.sidebar {
  width: 164rpx;
  padding: 8rpx;
  border: 2rpx solid rgb(var(--sf-color-line));
  border-radius: 16rpx;
  background: rgb(var(--sf-color-shell));
}

.sidebar-item {
  padding: 22rpx 8rpx;
  border-radius: 12rpx;
  color: rgb(var(--sf-color-text-secondary));
  font-size: 24rpx;
  line-height: 1.25;
  text-align: center;
}

.sidebar-item--active {
  background: rgb(var(--sf-color-brand-soft));
  color: rgb(var(--sf-color-brand));
  font-weight: 700;
}

.content {
  min-width: 0;
  flex: 1;
}

.hero,
.empty-card {
  overflow: hidden;
  border: 2rpx solid rgb(var(--sf-color-line));
  border-radius: 16rpx;
  background: rgb(var(--sf-color-shell));
  box-shadow: var(--sf-shadow-card);
}

.hero-image,
.hero-placeholder {
  width: 100%;
  height: 170rpx;
  background: linear-gradient(135deg, rgb(var(--sf-color-brand-soft)) 0%, rgb(var(--sf-color-price-soft)) 100%);
}

.hero-placeholder {
  display: flex;
  align-items: center;
  justify-content: center;
}

.hero-placeholder-title {
  color: rgb(var(--sf-color-brand-deep));
  font-size: 30rpx;
  font-weight: 700;
}

.hero-body {
  padding: 18rpx;
}

.hero-title {
  display: block;
  color: rgb(var(--sf-color-ink));
  font-size: 30rpx;
  font-weight: 700;
  line-height: 1.25;
}

.hero-desc {
  display: block;
  margin-top: 8rpx;
  color: rgb(var(--sf-color-text-secondary));
  font-size: 22rpx;
  line-height: 1.4;
}

.sub-grid {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 14rpx;
  margin-top: 16rpx;
}

.sub-card {
  padding: 12rpx 8rpx;
  border: 2rpx solid rgb(var(--sf-color-divider));
  border-radius: 16rpx;
  background: rgb(var(--sf-color-shell));
  text-align: center;
}

.sub-image {
  width: 100%;
  height: 92rpx;
  border-radius: 12rpx;
  background: rgb(var(--sf-color-mist));
}

.sub-image--empty {
  background: rgb(var(--sf-color-brand-soft));
}

.sub-name {
  display: block;
  margin-top: 10rpx;
  overflow: hidden;
  color: rgb(var(--sf-color-ink));
  font-size: 21rpx;
  line-height: 1.3;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.empty-card {
  margin-top: 16rpx;
  padding: 32rpx 24rpx;
  text-align: center;
}

.empty-title,
.empty-desc {
  display: block;
}

.empty-title {
  color: rgb(var(--sf-color-ink));
  font-size: 25rpx;
  font-weight: 700;
}

.empty-desc {
  margin-top: 8rpx;
  color: rgb(var(--sf-color-text-secondary));
  font-size: 22rpx;
}
</style>
