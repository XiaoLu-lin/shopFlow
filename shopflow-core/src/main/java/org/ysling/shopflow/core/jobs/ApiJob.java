package org.ysling.shopflow.core.jobs;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import org.ysling.shopflow.core.handler.ActionLogHandler;
import org.ysling.shopflow.core.utils.ApiUtil;
import org.ysling.shopflow.db.domain.ShopflowDynamic;
import org.ysling.shopflow.db.service.IDynamicService;

/**
 * @author Ysling
 */
@Slf4j
@Component
public class ApiJob {


    @Autowired
    private IDynamicService dynamicService;

    public static final String USER_ID = "0000000000";

    /**
     * 生成每日段子
     */
    @Scheduled(cron = "0 0 1 * * ?")
    public void duRequest() {
        String request = ApiUtil.duRequest();
        JSONObject requestJson = JSONObject.parseObject(request);
        String data = requestJson.getString("data");
        JSONObject dataJson = JSONObject.parseObject(data);
        String text = dataJson.getString("text");
        if (text != null){
            ShopflowDynamic dynamic = new ShopflowDynamic();
            dynamic.setContent(text);
            dynamic.setUserId(USER_ID);
            dynamic.setPicUrls(new String[0]);
            dynamicService.add(dynamic);
        }
        ActionLogHandler.logGeneralSucceed("系统处理延时任务", "生成每日段子");
    }
}
