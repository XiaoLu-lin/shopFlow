package org.ysling.litemall.core.baidu.result;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class AipPicSecCheckResult implements Serializable {

    /**
     * 请求唯一id，用于问题排查
     */
    @JsonProperty("log_id")
    private long logId;
    /**
     * 审核结果，可取值描述：合规、不合规、疑似、审核失败
     */
    private String conclusion;
    /**
     * 错误提示码，失败才返回，成功不返回
     */
    @JsonProperty("error_code")
    private Long errorCode;
    /**
     * 错误提示信息，失败才返回，成功不返回
     */
    @JsonProperty("error_msg")
    private String errorMsg;
    /**
     * 是否加密
     */
    @JsonProperty("isHitMd5")
    private boolean isHitMd5;
    /**
     * 审核结果类型，可取值1、2、3、4，分别代表1：合规，2：不合规，3：疑似，4：审核失败
     */
    private int conclusionType;
    /**
     * 不合规/疑似/命中白名单项详细信息。响应成功并且conclusion为疑似或不合规或命中白名单时才返回，响应失败或conclusion为合规且未命中白名单时不返回
     */
    private List<Data> data;


    @lombok.Data
    public static class Data {
        /**
         * 结果具体命中的模型
         */
        private int type;
        /**
         * 审核子类型
         */
        private int subType;
        /**
         * 审核结果，可取值描述：合规、不合规、疑似、审核失败
         */
        private String conclusion;
        /**
         * 审核结果类型
         */
        private int conclusionType;
        /**
         * 不合规项描述信息
         */
        private String msg;
        /**
         * 违规模型
         */
        private List<Stars> stars;
    }

    @lombok.Data
    public static class Stars {
        /**
         * 名称
         */
        private String name;
        /**
         * 人脸相似度
         */
        private String probability;
        /**
         * 人脸所属数据集名称
         */
        private String datasetName;
    }

    @lombok.Data
    public static class Hits {
        /**
         * 违规文本关键字
         */
        private List<String> words;
        /**
         * 人脸相似度
         */
        private String probability;
        /**
         * 人脸所属数据集名称
         */
        private String datasetName;
    }

}
