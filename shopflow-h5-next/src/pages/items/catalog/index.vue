<template>
  <view class="page">
    <view class="search-card" @click="goSearch">
      <text class="search-label">搜索商品、品牌和分类</text>
      <text class="search-copy">进入搜索页继续查找你想看的内容</text>
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
          <view v-else class="hero-placeholder" />
          <view class="hero-body">
            <text class="hero-title">{{ currentCategory?.name || '分类加载中' }}</text>
            <text class="hero-desc">
              {{ currentCategory?.desc || '正在整理当前分类和子分类内容。' }}
            </text>
          </view>
        </view>

        <view class="sub-grid">
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

        <view v-if="!currentSubCategories.length" class="empty-card">
          <text class="empty-title">当前分类还没有子分类</text>
          <text class="empty-desc">后续会继续补商品列表和更多筛选能力。</text>
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
  padding: 20rpx;
  background: linear-gradient(180deg, #ffffff 0%, #f6f8fb 100%);
}

.search-card {
  padding: 20rpx 22rpx;
  border-radius: 12rpx;
  background: #ffffff;
  box-shadow: 0 10rpx 24rpx rgba(23, 32, 51, 0.06);
}

.search-label {
  display: block;
  font-size: 26rpx;
  line-height: 1.3;
  color: #172033;
}

.search-copy {
  display: block;
  margin-top: 6rpx;
  font-size: 22rpx;
  line-height: 1.35;
  color: #748194;
}

.catalog-layout {
  display: flex;
  gap: 16rpx;
  height: calc(100vh - 180rpx);
  margin-top: 16rpx;
}

.sidebar {
  width: 160rpx;
  padding: 8rpx;
  border-radius: 12rpx;
  background: #f7faff;
}

.sidebar-item {
  padding: 22rpx 10rpx;
  border-radius: 10rpx;
  font-size: 23rpx;
  line-height: 1.3;
  color: #5e6a7d;
  text-align: center;
}

.sidebar-item--active {
  background: #ffffff;
  color: #1677ff;
  box-shadow: 0 8rpx 18rpx rgba(22, 119, 255, 0.08);
}

.content {
  min-width: 0;
  flex: 1;
}

.hero {
  overflow: hidden;
  border-radius: 12rpx;
  background: #ffffff;
  box-shadow: 0 10rpx 24rpx rgba(23, 32, 51, 0.06);
}

.hero-image,
.hero-placeholder {
  width: 100%;
  height: 220rpx;
  background: #edf4ff;
}

.hero-body {
  padding: 18rpx;
}

.hero-title {
  display: block;
  font-size: 28rpx;
  line-height: 1.3;
  color: #172033;
}

.hero-desc {
  display: block;
  margin-top: 6rpx;
  font-size: 22rpx;
  line-height: 1.4;
  color: #748194;
}

.sub-grid {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 14rpx;
  margin-top: 16rpx;
}

.sub-card {
  padding: 14rpx 10rpx;
  border-radius: 12rpx;
  background: #ffffff;
  box-shadow: 0 10rpx 22rpx rgba(23, 32, 51, 0.05);
}

.sub-image {
  width: 100%;
  height: 120rpx;
  border-radius: 10rpx;
  background: #f3f6fb;
}

.sub-image--empty {
  background: #edf4ff;
}

.sub-name {
  display: block;
  margin-top: 10rpx;
  font-size: 22rpx;
  line-height: 1.35;
  color: #172033;
  text-align: center;
}

.empty-card {
  margin-top: 16rpx;
  padding: 24rpx;
  border-radius: 12rpx;
  background: #ffffff;
}

.empty-title {
  display: block;
  font-size: 25rpx;
  line-height: 1.3;
  color: #172033;
}

.empty-desc {
  display: block;
  margin-top: 8rpx;
  font-size: 22rpx;
  line-height: 1.4;
  color: #748194;
}
</style>
