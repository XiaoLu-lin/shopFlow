import axios from 'axios'
import { Dialog, Toast } from 'vant';
import {
  applyShopFlowHeaders,
  extractTenantToken,
  getShopFlowHomeAuthPath,
  getShopFlowHomeIndexPath,
  getShopFlowUserToken,
  isShopFlowSuccess,
  normalizeWxRequestUrl,
  readTenantToken,
  resolveShopFlowAppId,
  shouldBootstrapTenant,
  withAppIdParam,
  writeTenantToken
} from '@/utils/shopflow-compat';

// create an axios instance
const service = axios.create({
  baseURL: process.env.VUE_APP_BASE_API, // api 的 base_url
  timeout: 5000 // request timeout
})

const bootstrapClient = axios.create({
  baseURL: process.env.VUE_APP_BASE_API,
  timeout: 5000
})

let tenantBootstrapPromise = null;

function bootstrapTenantToken(userToken) {
  if (tenantBootstrapPromise) {
    return tenantBootstrapPromise;
  }
  const appid = resolveShopFlowAppId();
  tenantBootstrapPromise = bootstrapClient({
    url: getShopFlowHomeAuthPath(),
    method: 'post',
    data: { appid },
    headers: applyShopFlowHeaders({}, userToken, '')
  }).then(response => {
    const res = response.data || {};
    if (!isShopFlowSuccess(res)) {
      return Promise.reject(response);
    }
    const tenantToken = writeTenantToken(
      window.localStorage,
      extractTenantToken(res.data)
    );
    if (!tenantToken) {
      return Promise.reject(new Error('未获取到租户授权'));
    }
    return tenantToken;
  }).finally(() => {
    tenantBootstrapPromise = null;
  });
  return tenantBootstrapPromise;
}

// request interceptor
service.interceptors.request.use(
  function(config) {
    const requestConfig = Object.assign({}, config);
    const requestPath = normalizeWxRequestUrl(requestConfig.url);
    if (requestPath === getShopFlowHomeIndexPath()) {
      requestConfig.params = withAppIdParam(
        requestConfig.params,
        resolveShopFlowAppId()
      );
    }
    const headers = Object.assign({}, requestConfig.headers || {});
    const userToken = getShopFlowUserToken(window.localStorage);
    const tenantToken = readTenantToken(window.localStorage);
    if (shouldBootstrapTenant(requestPath, tenantToken)) {
      return bootstrapTenantToken(userToken).then(function(nextTenantToken) {
        requestConfig.headers = applyShopFlowHeaders(headers, userToken, nextTenantToken);
        return markTenantPersistence(requestConfig, requestPath);
      });
    }
    requestConfig.headers = applyShopFlowHeaders(headers, userToken, tenantToken);
    return markTenantPersistence(requestConfig, requestPath);
  },
  err => Promise.reject(err)
)

function markTenantPersistence(requestConfig, requestPath) {
  if (requestPath === getShopFlowHomeIndexPath()) {
    requestConfig.meta = Object.assign({}, requestConfig.meta || {}, {
      persistTenantToken: true
    });
  }
  return requestConfig;
}

// response interceptor
service.interceptors.response.use(
  response => {
    const shouldPersistTenantToken =
      response &&
      response.config &&
      response.config.meta &&
      response.config.meta.persistTenantToken;
    if (shouldPersistTenantToken) {
      writeTenantToken(window.localStorage, extractTenantToken(response.data));
    }
    const res = response.data

    if (isShopFlowSuccess(res)) {
      return response
    } else if (res.errno === 'A0223' || res.errno === 501) {
        Toast.fail('请登录');
        setTimeout(() => {
          window.location = '#/login/'
        }, 1500)
      return Promise.reject('error')
    } else if (res.errno === 502) {
        Toast.fail('网站内部错误，请联系网站维护人员')
      return Promise.reject('error')
    } if (res.errno === 401 || res.errno === 'A0400') {
      Toast.fail('参数不对');
      return Promise.reject('error')
    } if (res.errno === 402 || res.errno === 'A0410') {
      Toast.fail('参数值不对');
      return Promise.reject('error')
    } else if (!isShopFlowSuccess(res)) {
      // 非5xx的错误属于业务错误，留给具体页面处理
      return Promise.reject(response)
    }
    return response
  }, error => {
    console.log('err' + error)// for debug
    Dialog.alert({
        title: '警告',
        message: '登录连接超时'
      });
    return Promise.reject(error)
  })

export default service
