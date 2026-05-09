<template>
	<md-field-group class="foget_view">
		<md-field
			v-model="password"
			icon="lock"
			:is-error="isErrow"
			placeholder="请输入新密码"/>

		<md-field
			v-model="passwordRepeat"
			type="password"
			icon="lock"
			:is-error="isErrow"
			placeholder="请再次输入密码" />
		<div class="red" v-show="isErrow">两次密码输入不一致</div>

		<div class="foget_submit">
			<van-button size="large" type="danger" @click="submitCode">重置</van-button>
		</div>
	</md-field-group>
</template>

<script>
import field from '@/components/field/';
import fieldGroup from '@/components/field-group/';
import { authReset } from '@/api/api';
import { Toast } from 'vant';

export default {
  data() {
    return {
      isErrow: false,
      mobile: '',
      code: '',
      password: '',
      passwordRepeat: ''
    };
  },

  created() {
    this.mobile = this.$route.query.mobile || '';
    this.code = this.$route.query.code || '';
  },

  methods: {
    submitCode() {
      this.isErrow = this.password !== this.passwordRepeat;
      if (this.isErrow) {
        return;
      }
      if (this.password === '' || this.code === '' || this.mobile === '') {
        Toast.fail('请完整填写重置信息');
        return;
      }
      authReset({
        mobile: this.mobile,
        code: this.code,
        password: this.password
      }).then(() => {
        this.$router.push({
          name: 'forgetStatus',
          params: { status: 'success' }
        });
      }).catch(error => {
        Toast.fail(error.data.errmsg);
      });
    }
  },

  components: {
    [field.name]: field,
    [fieldGroup.name]: fieldGroup,
    Toast
  }
};
</script>

<style lang="scss" scoped>
div.foget_view {
  background-color: #fff;
  padding-top: 30px;
}

div.foget_submit {
  padding-top: 30px;
  padding-bottom: 20px;
}
</style>
