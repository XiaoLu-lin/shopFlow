package org.ysling.shopflow.core.weixin.enums;

import java.io.Serializable;

/**
 * 转账状态
 */
public enum TransferStatus implements Serializable {

    /////////////////////////////////
    //       转账状态
    ////////////////////////////////

    /**初始态，系统转账校验中*/
    INIT("INIT","转账校验中"),
    /**待确认*/
    WAIT_PAY("WAIT_PAY","待确认"),
    /**转账中*/
    PROCESSING("PROCESSING","转账中"),
    /**转账成功*/
    SUCCESS("SUCCESS","交易成功"),
    /**转账失败*/
    FAIL("FAIL","交易失败");

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


    TransferStatus(String status, String depict) {
        this.status = status;
        this.depict = depict;
    }

    public static Boolean isSuccess(String status){
        return TransferStatus.SUCCESS.status.equals(status);
    }

    public static Boolean isFail(String status){
        return TransferStatus.FAIL.status.equals(status);
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
            for (TransferStatus item : values()) {
                if (item.status.equals(status)) {
                    return item.depict;
                }
            }
        }
        return "";
    }

}
