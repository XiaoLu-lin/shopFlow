package org.ysling.litemall.core.express.config;
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
import org.springframework.context.annotation.Configuration;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author Ysling
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "litemall.express")
public class ExpressProperties implements Serializable {
    /**
     * 是否开启物流查询
     */
    private boolean enable;
    /**
     * 物流appId
     */
    private String appId;
    /**
     * 物流appKey
     */
    private String appKey;
    /**
     * 物流公司列表
     */
    private List<Map<String, String>> vendors = new ArrayList<>();

}
