const WX_PREFIX = '/wx';
const SHOPFLOW_USER_TOKEN_HEADER = 'X-ShopFlow-User-Token';
const SHOPFLOW_TENANT_TOKEN_HEADER = 'X-ShopFlow-TenantId';
const SHOPFLOW_HOME_INDEX_PATH = '/home/index';
const SHOPFLOW_HOME_AUTH_PATH = '/home/auth';
const DEFAULT_SHOPFLOW_APPID = '1649067';
const SHOPFLOW_TENANT_TOKEN_KEY = 'ShopFlowTenantToken';
const SHOPFLOW_SUCCESS_VALUE = 'success';
const LEGACY_AUTH_TOKEN_KEY = 'Authorization';
const LEGACY_AVATAR_KEY = 'avatar';
const LEGACY_NICK_NAME_KEY = 'nickName';

function isNonEmptyString(value) {
  return typeof value === 'string' && value.trim().length > 0;
}

function resolveShopFlowAppId(env) {
  const currentEnv = env || process.env;
  const appId = currentEnv && currentEnv.VUE_APP_SHOPFLOW_APPID;
  if (isNonEmptyString(appId)) {
    return appId.trim();
  }
  return DEFAULT_SHOPFLOW_APPID;
}

function normalizeWxRequestUrl(url) {
  const normalizedUrl = typeof url === 'string' ? url : '';
  const wxIndex = normalizedUrl.indexOf(WX_PREFIX);
  const path = wxIndex >= 0 ? normalizedUrl.slice(wxIndex + WX_PREFIX.length) : normalizedUrl;
  const queryIndex = path.indexOf('?');
  return queryIndex >= 0 ? path.slice(0, queryIndex) : path;
}

function isTenantBootstrapWhitelisted(url) {
  const path = normalizeWxRequestUrl(url);
  return path === SHOPFLOW_HOME_INDEX_PATH || path === SHOPFLOW_HOME_AUTH_PATH;
}

function shouldBootstrapTenant(url, tenantToken) {
  const requestUrl = url || '';
  const currentTenantToken = tenantToken || '';
  if (isNonEmptyString(currentTenantToken)) {
    return false;
  }
  return !isTenantBootstrapWhitelisted(requestUrl);
}

function withAppIdParam(params, appid) {
  const nextParams = Object.assign({}, params || {});
  if (!isNonEmptyString(nextParams.appid)) {
    nextParams.appid = appid;
  }
  return nextParams;
}

function readTenantToken(storage) {
  if (!storage || typeof storage.getItem !== 'function') {
    return '';
  }
  return storage.getItem(SHOPFLOW_TENANT_TOKEN_KEY) || '';
}

function writeTenantToken(storage, tenantToken) {
  const currentTenantToken = tenantToken || '';
  if (!storage || typeof storage.setItem !== 'function') {
    return '';
  }
  if (!isNonEmptyString(currentTenantToken)) {
    return '';
  }
  storage.setItem(SHOPFLOW_TENANT_TOKEN_KEY, currentTenantToken.trim());
  return currentTenantToken.trim();
}

function extractTenantToken(payload) {
  if (isNonEmptyString(payload)) {
    return payload.trim();
  }
  if (payload && isNonEmptyString(payload.tenantId)) {
    return payload.tenantId.trim();
  }
  if (payload && payload.data) {
    return extractTenantToken(payload.data);
  }
  return '';
}

function applyShopFlowHeaders(headers, userToken, tenantToken) {
  const nextHeaders = Object.assign({}, headers || {});
  const currentUserToken = userToken || '';
  const currentTenantToken = tenantToken || '';
  delete nextHeaders['X-Litemall-Token'];
  if (isNonEmptyString(currentUserToken)) {
    nextHeaders[SHOPFLOW_USER_TOKEN_HEADER] = currentUserToken.trim();
  }
  if (isNonEmptyString(currentTenantToken)) {
    nextHeaders[SHOPFLOW_TENANT_TOKEN_HEADER] = currentTenantToken.trim();
  }
  return nextHeaders;
}

function getShopFlowUserToken(storage) {
  if (!storage || typeof storage.getItem !== 'function') {
    return '';
  }
  return storage.getItem(LEGACY_AUTH_TOKEN_KEY) || '';
}

function isShopFlowSuccess(payload) {
  if (!payload) {
    return false;
  }
  return payload.errno === SHOPFLOW_SUCCESS_VALUE || payload.errno === 0 || payload.errno === '0';
}

function extractUserToken(payload) {
  if (isNonEmptyString(payload)) {
    return payload.trim();
  }
  if (payload && isNonEmptyString(payload.userToken)) {
    return payload.userToken.trim();
  }
  if (payload && isNonEmptyString(payload.token)) {
    return payload.token.trim();
  }
  if (payload && payload.data) {
    return extractUserToken(payload.data);
  }
  return '';
}

function extractUserInfo(payload) {
  if (!payload) {
    return {};
  }
  if (payload.userInfo && typeof payload.userInfo === 'object') {
    return Object.assign({}, payload.userInfo);
  }
  if (payload.data) {
    return extractUserInfo(payload.data);
  }
  return {};
}

function persistLegacyLoginSession(storage, payload) {
  const userToken = extractUserToken(payload);
  const userInfo = extractUserInfo(payload);
  if (!storage || typeof storage.setItem !== 'function') {
    return {
      token: userToken,
      userInfo
    };
  }
  if (isNonEmptyString(userToken)) {
    storage.setItem(LEGACY_AUTH_TOKEN_KEY, userToken);
  }
  if (isNonEmptyString(userInfo.avatarUrl)) {
    storage.setItem(LEGACY_AVATAR_KEY, userInfo.avatarUrl.trim());
  }
  if (isNonEmptyString(userInfo.nickName)) {
    storage.setItem(LEGACY_NICK_NAME_KEY, userInfo.nickName.trim());
  }
  return {
    token: userToken,
    userInfo
  };
}

function getShopFlowHomeIndexPath() {
  return SHOPFLOW_HOME_INDEX_PATH;
}

function getShopFlowHomeAuthPath() {
  return SHOPFLOW_HOME_AUTH_PATH;
}

module.exports = {
  DEFAULT_SHOPFLOW_APPID,
  SHOPFLOW_TENANT_TOKEN_KEY,
  applyShopFlowHeaders,
  extractTenantToken,
  extractUserInfo,
  extractUserToken,
  getShopFlowHomeAuthPath,
  getShopFlowHomeIndexPath,
  getShopFlowUserToken,
  isShopFlowSuccess,
  isTenantBootstrapWhitelisted,
  normalizeWxRequestUrl,
  persistLegacyLoginSession,
  readTenantToken,
  resolveShopFlowAppId,
  shouldBootstrapTenant,
  withAppIdParam,
  writeTenantToken
};
