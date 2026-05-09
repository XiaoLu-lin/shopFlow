const assert = require('assert');

const {
  DEFAULT_SHOPFLOW_APPID,
  SHOPFLOW_TENANT_TOKEN_KEY,
  applyShopFlowHeaders,
  extractTenantToken,
  extractUserInfo,
  extractUserToken,
  isShopFlowSuccess,
  isTenantBootstrapWhitelisted,
  normalizeWxRequestUrl,
  persistLegacyLoginSession,
  readTenantToken,
  resolveShopFlowAppId,
  shouldBootstrapTenant,
  withAppIdParam,
  writeTenantToken
} = require('../src/utils/shopflow-compat');

function createStorage(initialValue) {
  const state = {};
  if (typeof initialValue !== 'undefined') {
    state[SHOPFLOW_TENANT_TOKEN_KEY] = initialValue;
  }
  return {
    getItem(key) {
      return Object.prototype.hasOwnProperty.call(state, key) ? state[key] : null;
    },
    setItem(key, value) {
      state[key] = value;
    },
    dump() {
      return { ...state };
    }
  };
}

function run() {
  assert.strictEqual(
    resolveShopFlowAppId({ VUE_APP_SHOPFLOW_APPID: ' demo-app ' }),
    'demo-app',
    'should prefer explicit ShopFlow appid config'
  );

  assert.strictEqual(
    resolveShopFlowAppId({ VUE_APP_SHOPFLOW_APPID: '   ' }),
    DEFAULT_SHOPFLOW_APPID,
    'should fall back to the verified default appid when config is blank'
  );

  assert.strictEqual(
    normalizeWxRequestUrl('http://localhost:8080/wx/home/index'),
    '/home/index',
    'should normalize full wx urls to relative request paths'
  );

  assert.strictEqual(
    isTenantBootstrapWhitelisted('/wx/home/auth'),
    true,
    'should treat the tenant auth endpoint as a bootstrap whitelist entry'
  );

  assert.strictEqual(
    shouldBootstrapTenant('/auth/login_legacy', ''),
    true,
    'should bootstrap a tenant token before legacy login requests'
  );

  assert.strictEqual(
    shouldBootstrapTenant('/goods/list', ''),
    true,
    'should bootstrap a tenant token before protected wx requests'
  );

  assert.strictEqual(
    shouldBootstrapTenant('/home/index', ''),
    false,
    'should skip bootstrap for homepage warmup requests'
  );

  assert.deepStrictEqual(
    withAppIdParam(undefined, '1649067'),
    { appid: '1649067' },
    'should inject appid when homepage params are absent'
  );

  assert.deepStrictEqual(
    withAppIdParam({ page: 1, appid: 'custom-appid' }, '1649067'),
    { page: 1, appid: 'custom-appid' },
    'should preserve a caller provided appid'
  );

  assert.strictEqual(
    extractTenantToken({ data: { tenantId: 'tenant-from-home-index' } }),
    'tenant-from-home-index',
    'should read tenant token from homepage payloads'
  );

  assert.strictEqual(
    extractTenantToken({ data: 'tenant-from-home-auth' }),
    'tenant-from-home-auth',
    'should read tenant token from tenant auth payloads'
  );

  assert.strictEqual(
    isShopFlowSuccess({ errno: 'success' }),
    true,
    'should treat ShopFlow success responses as successful'
  );

  assert.strictEqual(
    isShopFlowSuccess({ errno: 0 }),
    true,
    'should keep supporting legacy numeric success responses'
  );

  assert.strictEqual(
    extractUserToken({ data: { userToken: 'shopflow-user-token' } }),
    'shopflow-user-token',
    'should read the current ShopFlow user token field'
  );

  assert.deepStrictEqual(
    extractUserInfo({
      data: {
        userInfo: {
          nickName: '测试昵称',
          avatarUrl: 'https://example.com/avatar.png'
        }
      }
    }),
    {
      nickName: '测试昵称',
      avatarUrl: 'https://example.com/avatar.png'
    },
    'should read the user info payload from ShopFlow login responses'
  );

  const headers = applyShopFlowHeaders({}, 'user-token', 'tenant-token');
  assert.deepStrictEqual(
    headers,
    {
      'X-ShopFlow-User-Token': 'user-token',
      'X-ShopFlow-TenantId': 'tenant-token'
    },
    'should map the legacy local tokens onto the current ShopFlow headers'
  );

  const storage = createStorage();
  writeTenantToken(storage, 'tenant-token');
  assert.strictEqual(
    readTenantToken(storage),
    'tenant-token',
    'should persist the tenant token in local storage'
  );

  const sessionStorage = createStorage();
  persistLegacyLoginSession(sessionStorage, {
    data: {
      userToken: 'shopflow-user-token',
      userInfo: {
        nickName: '测试昵称',
        avatarUrl: 'https://example.com/avatar.png'
      }
    }
  });
  assert.deepStrictEqual(
    sessionStorage.dump(),
    {
      Authorization: 'shopflow-user-token',
      avatar: 'https://example.com/avatar.png',
      nickName: '测试昵称'
    },
    'should normalize ShopFlow login responses into the legacy H5 local storage fields'
  );
}

run();
console.log('shopflow-compat tests passed');
