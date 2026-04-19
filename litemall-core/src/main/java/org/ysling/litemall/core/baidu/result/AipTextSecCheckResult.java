package org.ysling.litemall.core.baidu.result;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

/**
 * 百度文本检测响应
 */
@Data
public class AipTextSecCheckResult {

    /**
     * 审核结果，可取值：合规、不合规、疑似、审核失败
     */
    private String conclusion;
    /**
     * 请求唯一id
     */
    @JsonProperty("log_id")
    private long logId;
    /**
     * 不合规/疑似/命中白名单项详细信息。响应成功并且conclusion为疑似或不合规或命中白名单时才返回，响应失败或conclusion为合规且未命中白名单时不返回。
     */
    private List<Data> data;
    /**
     * 是否加密
     */
    @JsonProperty("isHitMd5")
    private boolean isHitMd5;
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
     * 审核结果类型，可取值1.合规，2.不合规，3.疑似，4.审核失败
     */
    private int conclusionType;


    /**
     * 不合规/疑似/命中白名单项详细信息。响应成功并且conclusion为疑似或不合规或命中白名单时才返回，响应失败或conclusion为合规且未命中白名单时不返回。
     */
    @lombok.Data
    public static class Data {
        /**
         * 不合规项描述信息
         */
        private String msg;
        /**
         * 审核结果，可取值：合规、不合规、疑似、审核失败
         */
        private String conclusion;
        /**
         * 送检文本违规原因的详细信息
         */
        private List<Hits> hits;
        /**
         * 审核子类型
         */
        private int subType;
        /**
         * 审核结果类型，可取值1.合规，2.不合规，3.疑似，4.审核失败
         */
        private int conclusionType;
        /**
         * 审核主类型，11：百度官方违禁词库、12：文本反作弊、13:自定义文本黑名单、14:自定义文本白名单
         */
        private int type;
    }


    /**
     * 送检文本违规原因的详细信息
     */
    @lombok.Data
    public static class Hits {
        /**
         * 送检文本命中词库的详细信息，为Object的列表，包含words、positions、label三个字段，详细描述如下表所示。返回示例见下方“成功响应示例——不合规”部分
         */
        private List<Position> wordHitPositions;
        /**
         * 不合规项置信度
         */
        private Integer probability;
        /**
         * 违规项目所属数据集名称
         */
        private String datasetName;
        /**
         * 送检文本命中词库的关键词（备注：建议参考新字段“wordHitPositions”，包含信息更丰富：关键词以及对应的位置及标签信息）
         */
        private List<String> words;
        /**
         * 送检文本命中模型的详细信息，包含位置信息及置信度信息。位置信息：命中模型的违规内容在送检原文中的位置（从0开始计算）：“开始位置”、“结束位置”；置信度信息：命中模型对应的置信度分数（范围：0至1，数字越大，可能性越高）。返回示例见下方“成功响应示例——不合规”部分
         */
        private List<List<Integer>> modelHitPositions;
    }

    @lombok.Data
    public static class Position{
        /**
         * 标签
         */
        private String label;
        /**
         * 关键字
         */
        private String keyword;
        /**
         * 关键字
         */
        private List<List<Integer>> positions;

    }

}