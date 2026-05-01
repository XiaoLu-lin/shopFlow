<template>
  <div>
    <svg-icon icon-class="question" @click="goto" />
  </div>
</template>

<script>
export default {
  name: 'ShopFlowDoc',
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