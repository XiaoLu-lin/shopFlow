export const SESSION_KEYS = {
  authorization: 'Authorization',
  tenantToken: 'ShopFlowTenantToken',
  avatar: 'avatar',
  nickName: 'nickName',
  mobile: 'mobile',
  prepayData: 'prepay_data',
  addressId: 'AddressId',
  cartId: 'CartId',
  couponId: 'CouponId',
  userCouponId: 'UserCouponId',
} as const

export const SHOPFLOW_HEADERS = {
  userToken: 'X-ShopFlow-User-Token',
  tenantToken: 'X-ShopFlow-TenantId',
} as const
