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
      url: 'https://gitee.com/JoyAtMeeting/shopflow'
    }
  },
  methods: {
    goto() {
      window.open(this.url)
    }
  }
}
</script>