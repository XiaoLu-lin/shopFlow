package org.ysling.litemall.core.weixin.enums;

/**
 * ACCOUNT_FROZEN：账户冻结
 * REAL_NAME_CHECK_FAIL：用户未实名
 * NAME_NOT_CORRECT：用户姓名校验失败
 * OPENID_INVALID：Openid校验失败
 * TRANSFER_QUOTA_EXCEED：超过用户单笔收款额度
 * DAY_RECEIVED_QUOTA_EXCEED：超过用户单日收款额度
 * MONTH_RECEIVED_QUOTA_EXCEED：超过用户单月收款额度
 * DAY_RECEIVED_COUNT_EXCEED：超过用户单日收款次数
 * PRODUCT_AUTH_CHECK_FAIL：产品权限校验失败
 * OVERDUE_CLOSE：转账关闭
 * ID_CARD_NOT_CORRECT：用户身份证校验失败
 * ACCOUNT_NOT_EXIST：用户账户不存在
 * TRANSFER_RISK：转账存在风险
 * REALNAME_ACCOUNT_RECEIVED_QUOTA_EXCEED：用户账户收款受限，请引导用户在微信支付查看详情
 * RECEIVE_ACCOUNT_NOT_PERMMIT：未配置该用户为转账收款人
 * PAYER_ACCOUNT_ABNORMAL：商户账户付款受限，可前往商户平台-违约记录获取解除功能限制指引
 * PAYEE_ACCOUNT_ABNORMAL：用户账户收款异常，请引导用户完善其在微信支付的身份信息以继续收款
 * TRANSFER_REMARK_SET_FAIL：转账备注设置失败，请调整对应文案后重新再试
 */
public enum FailReasonStatus {

    /////////////////////////////////
    //       转账状态
    ////////////////////////////////

    /**账户冻结*/
    ACCOUNT_FROZEN("ACCOUNT_FROZEN","账户冻结"),
    /**用户未实名*/
    REAL_NAME_CHECK_FAIL("WAIT_PAY","用户未实名"),
    /**用户姓名校验失败*/
    NAME_NOT_CORRECT("NAME_NOT_CORRECT","用户姓名校验失败"),
    /**Openid校验失败*/
    OPENID_INVALID("OPENID_INVALID","Openid校验失败"),
    /**超过用户单日收款额度*/
    DAY_RECEIVED_QUOTA_EXCEED("DAY_RECEIVED_QUOTA_EXCEED","超过用户单日收款额度"),
    /**超过用户单月收款额度*/
    MONTH_RECEIVED_QUOTA_EXCEED("MONTH_RECEIVED_QUOTA_EXCEED","超过用户单月收款额度"),
    /**超过用户单日收款次数*/
    DAY_RECEIVED_COUNT_EXCEED("DAY_RECEIVED_COUNT_EXCEED","超过用户单日收款次数"),
    /**产品权限校验失败*/
    PRODUCT_AUTH_CHECK_FAIL("PRODUCT_AUTH_CHECK_FAIL","产品权限校验失败"),
    /**转账关闭*/
    OVERDUE_CLOSE("OVERDUE_CLOSE","转账关闭"),
    /**用户身份证校验失败*/
    ID_CARD_NOT_CORRECT("ID_CARD_NOT_CORRECT","用户身份证校验失败"),
    /**用户账户不存在*/
    ACCOUNT_NOT_EXIST("ACCOUNT_NOT_EXIST","用户账户不存在"),
    /**转账存在风险*/
    TRANSFER_RISK("TRANSFER_RISK","转账存在风险"),
    /**用户账户收款受限*/
    REALNAME_ACCOUNT_RECEIVED_QUOTA_EXCEED("REALNAME_ACCOUNT_RECEIVED_QUOTA_EXCEED","用户账户收款受限"),
    /**未配置该用户为转账收款人*/
    RECEIVE_ACCOUNT_NOT_PERMMIT("RECEIVE_ACCOUNT_NOT_PERMMIT","未配置该用户为转账收款人"),
    /**商户账户付款受限*/
    PAYER_ACCOUNT_ABNORMAL("PAYER_ACCOUNT_ABNORMAL","商户账户付款受限"),
    /**用户账户收款异常*/
    PAYEE_ACCOUNT_ABNORMAL("PAYEE_ACCOUNT_ABNORMAL","用户账户收款异常"),
    /**转账备注设置失败*/
    TRANSFER_REMARK_SET_FAIL("TRANSFER_REMARK_SET_FAIL","转账备注设置失败"),
    /**超过用户单笔收款额度*/
    TRANSFER_QUOTA_EXCEED("TRANSFER_QUOTA_EXCEED","超过用户单笔收款额度");


    /**状态*/
    private final String status;
    /**描述*/
    private final String depict;

    /**状态*/
    public String getStatus() {
        return status;
    }

    /**描述*/
    public String getDepict() {
        return depict;
    }


    FailReasonStatus(String status, String depict) {
        this.status = status;
        this.depict = depict;
    }

    /**
     * 状态判断
     */
    public Boolean equals(String status){
        return this.getStatus().equals(status);
    }

    /**
     * 根据状态获取描述
     * @param status 状态
     * @return 返回描述
     */
    public static String parseValue(String status) {
        if (status != null) {
            for (FailReasonStatus item : values()) {
                if (item.status.equals(status)) {
                    return item.depict;
                }
            }
        }
        return "";
    }

}
