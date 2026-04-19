package org.ysling.litemall.core.baidu.config;
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

/**
 * @author Ysling
 */
@Data
@ConfigurationProperties(prefix = "litemall.baidu")
public class BaiduProperties {

    /**
     * 是否开启内容审核
     */
    private boolean enable;
    /**
     * 百度应用id
     */
    private String appid;
    /**
     * 接口key
     */
    private String apiKey;
    /**
     * 接口密钥
     */
    private String secretKey;

}
