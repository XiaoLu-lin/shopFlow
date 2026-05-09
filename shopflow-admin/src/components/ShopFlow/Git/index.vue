<template>
  <div>
    <svg-icon icon-class="github" @click="goto" />
  </div>
</template>

<script>
export default {
  name: 'ShopFlowGit',
  data() {
    return {
      url: 'https://github.com/XiaoLu-lin/shopFlow'
    }
  },
  methods: {
    goto() {
      window.open(this.url)
    }
  }
}
</script>
