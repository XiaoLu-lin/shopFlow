package org.ysling.litemall.core.notify.config;
/**
 *  Copyright (c) [ysling] [927069313@qq.com]
 *  [litemall-plus] is licensed under Mulan PSL v2.
 *  You can use this software according to the terms and conditions of the Mulan PSL v2.
 *  You may obtain a copy of Mulan PSL v2 at:
 *              http://license.coscl.org.cn/MulanPSL2
 *  THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND,
 *  EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT,
 *  MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 *  See the Mulan PSL v2 for more details.
 */
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author Ysling
 */
@Data
@ConfigurationProperties(prefix = "litemall.notify")
public class NotifyProperties {
    /**
     * 邮箱
     */
    private Mail mail;
    /**
     * 短信
     */
    private Sms sms;

    @Data
    public static class Mail {
        /**
         * 是否开启邮箱服务
         */
        private boolean enable;
        /**
         * 发送邮件服务器
         */
        private String host;
        /**
         * 发送邮件的邮箱地址
         */
        private String username;
        /**
         * 客户端授权码，不是邮箱密码
         */
        private String password;
        /**
         * 发送邮件的地址，和上面username一致
         */
        private String sendFrom;
        /**
         * 默认收件邮箱
         */
        private String sendTo;
        /**
         * 服务器端口
         */
        private Integer port;
    }

    @Data
    public static class Sms {
        /**
         * 是否开启短信服务
         */
        private boolean enable;
        /**
         * 短信类型 tencent或 aliyun
         */
        private String active;
        /**
         * 签名
         */
        private String sign;
        /**
         * 腾讯短信
         */
        private Tencent tencent;
        /**
         * 阿里短信
         */
        private Aliyun aliyun;
        /**
         * 短信模板
         */
        private List<Map<String, String>> template = new ArrayList<>();

        @Data
        public static class Tencent {
            /**
             * 腾讯短信appid
             */
            private int appid;
            /**
             * 腾讯短信appKey
             */
            private String appKey;
        }

        @Data
        public static class Aliyun {
            /**
             * 阿里短信regionId
             */
            private String regionId;
            /**
             * 阿里短信accessKeyId
             */
            private String accessKeyId;
            /**
             * 阿里短信accessKeySecret
             */
            private String accessKeySecret;
        }

    }

}
