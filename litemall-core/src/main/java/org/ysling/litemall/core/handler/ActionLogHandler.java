package org.ysling.litemall.core.handler;
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

import cn.dev33.satoken.stp.StpUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.ysling.litemall.core.utils.ip.IpInfo;
import org.ysling.litemall.core.utils.ip.IpUtil;
import org.ysling.litemall.db.domain.LitemallLog;
import org.ysling.litemall.core.utils.http.GlobalWebUtil;
import org.ysling.litemall.db.service.ILogService;
import javax.servlet.http.HttpServletRequest;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * 这里的日志类型设计成四种（当然开发者需要可以自己扩展）
 * 一般日志：用户觉得需要查看的一般操作日志，建议是默认的日志级别
 * 安全日志：用户安全相关的操作日志，例如登录、删除管理员
 * 订单日志：用户交易相关的操作日志，例如订单发货、退款
 * 其他日志：如果以上三种不合适，可以选择其他日志，建议是优先级最低的日志级别
 * <p>
 * 当然可能很多操作是不需要记录到数据库的，例如编辑商品、编辑广告品之类。
 *
 * @author Ysling
 * 异步处理可防止事务回滚 @Async 
 */
@Slf4j
@Component
public class ActionLogHandler {

    /**一般日志*/
    public static final  Short LOG_TYPE_GENERAL = 0;
    /**安全日志*/
    public static final  Short LOG_TYPE_AUTH = 1;
    /**订单日志*/
    public static final  Short LOG_TYPE_ORDER = 2;
    /**其他日志*/
    public static final  Short LOG_TYPE_OTHER = 3;
    /**日志记录接口*/
    private static ILogService logService;
    /**线程池*/
    private static ThreadPoolExecutor executorService;
    
    @Autowired
    public void setLogService(ILogService logService) {
        ActionLogHandler.logService = logService;
    }

    @Autowired
    public void setExecutorService(ThreadPoolExecutor executorService) {
        ActionLogHandler.executorService = executorService;
    }

    /**
     * 操作成功
     * 一般日志：用户觉得需要查看的一般操作日志，建议是默认的日志级别
     * @param action 操作动作
     */
    public static void logGeneralSucceed(String action) {
        logAdmin(LOG_TYPE_GENERAL, action, true, "", "");
    }

    /**
     * 操作成功
     * 一般日志：用户觉得需要查看的一般操作日志，建议是默认的日志级别
     * @param action 操作动作
     * @param result 日志详情
     */
    public static void logGeneralSucceed(String action, String result) {
        logAdmin(LOG_TYPE_GENERAL, action, true, result, "");
    }

    /**
     * 操作成功
     * 一般日志：用户觉得需要查看的一般操作日志，建议是默认的日志级别
     * @param action 操作动作
     * @param result 日志详情
     * @param comment 操作备注
     */
    public static void logGeneralSucceed(String action, String result, String comment) {
        logAdmin(LOG_TYPE_GENERAL, action, true, result, comment);
    }

    /**
     * 操作失败
     * 一般日志：用户觉得需要查看的一般操作日志，建议是默认的日志级别
     * @param action 操作动作
     * @param error 日志详情
     */
    public static void logGeneralFail(String action, String error) {
        logAdmin(LOG_TYPE_GENERAL, action, false, error, "");
    }

    /**
     * 操作失败
     * 一般日志：用户觉得需要查看的一般操作日志，建议是默认的日志级别
     * @param action 操作动作
     * @param error 日志详情
     * @param comment 操作备注
     */
    public static void logGeneralFail(String action, String error, String comment) {
        logAdmin(LOG_TYPE_GENERAL, action, false, error, comment);
    }

    /**
     * 操作成功
     * 安全日志：用户安全相关的操作日志，例如登录、删除管理员
     * @param action 操作动作
     */
    public static void logAuthSucceed(String action) {
        logAdmin(LOG_TYPE_AUTH, action, true, "", "");
    }

    /**
     * 操作成功
     * 安全日志：用户安全相关的操作日志，例如登录、删除管理员
     * @param action 操作动作
     * @param result 日志详情
     */
    public static void logAuthSucceed(String action, String result) {
        logAdmin(LOG_TYPE_AUTH, action, true, result, "");
    }

    /**
     * 操作成功
     * 安全日志：用户安全相关的操作日志，例如登录、删除管理员
     * @param action 操作动作
     * @param error 日志详情
     * @param comment 操作备注
     */
    public static void logAuthSucceed(String action, String error, String comment) {
        logAdmin(LOG_TYPE_GENERAL, action, false, error, comment);
    }

    /**
     * 操作失败
     * 安全日志：用户安全相关的操作日志，例如登录、删除管理员
     * @param action 操作动作
     * @param error 日志详情
     */
    public static void logAuthFail(String action, String error) {
        logAdmin(LOG_TYPE_AUTH, action, false, error, "");
    }

    /**
     * 操作失败
     * 安全日志：用户安全相关的操作日志，例如登录、删除管理员
     * @param action 操作动作
     * @param error 日志详情
     * @param comment 操作备注
     */
    public static void logAuthFail(String action, String error, String comment) {
        logAdmin(LOG_TYPE_GENERAL, action, false, error, comment);
    }

    /**
     * 操作成功
     * 订单日志：用户交易相关的操作日志，例如订单发货、退款
     * @param action 操作动作
     */
    public static void logOrderSucceed(String action) {
        logAdmin(LOG_TYPE_ORDER, action, true, "", "");
    }

    /**
     * 操作成功
     * 订单日志：用户交易相关的操作日志，例如订单发货、退款
     * @param action 操作动作
     * @param result 日志详情
     */
    public static void logOrderSucceed(String action, String result) {
        logAdmin(LOG_TYPE_ORDER, action, true, result, "");
    }

    /**
     * 操作成功
     * 订单日志：用户交易相关的操作日志，例如订单发货、退款
     * @param action 操作动作
     * @param error 日志详情
     * @param comment 操作备注
     */
    public static void logOrderSucceed(String action, String error, String comment) {
        logAdmin(LOG_TYPE_GENERAL, action, false, error, comment);
    }

    /**
     * 操作失败
     * 订单日志：用户交易相关的操作日志，例如订单发货、退款
     * @param action 操作动作
     * @param error 日志详情
     */
    public static void logOrderFail(String action, String error) {
        logAdmin(LOG_TYPE_ORDER, action, false, error, "");
    }

    /**
     * 操作失败
     * 订单日志：用户交易相关的操作日志，例如订单发货、退款
     * @param action 操作动作
     * @param error 日志详情
     * @param comment 操作备注
     */
    public static void logOrderFail(String action, String error, String comment) {
        logAdmin(LOG_TYPE_GENERAL, action, false, error, comment);
    }

    /**
     * 操作成功
     * 其他日志：如果以上三种不合适，可以选择其他日志，建议是优先级最低的日志级别
     * @param action 操作动作
     */
    public static void logOtherSucceed(String action) {
        logAdmin(LOG_TYPE_OTHER, action, true, "", "");
    }

    /**
     * 操作成功
     * 其他日志：如果以上三种不合适，可以选择其他日志，建议是优先级最低的日志级别
     * @param action 操作动作
     * @param result 日志详情
     */
    public static void logOtherSucceed(String action, String result) {
        logAdmin(LOG_TYPE_OTHER, action, true, result, "");
    }

    /**
     * 操作成功
     * 其他日志：如果以上三种不合适，可以选择其他日志，建议是优先级最低的日志级别
     * @param action 操作动作
     * @param error 日志详情
     * @param comment 操作备注
     */
    public static void logOtherSucceed(String action, String error, String comment) {
        logAdmin(LOG_TYPE_GENERAL, action, false, error, comment);
    }

    /**
     * 操作失败
     * 其他日志：如果以上三种不合适，可以选择其他日志，建议是优先级最低的日志级别
     * @param action 操作动作
     * @param error 日志详情
     */
    public static void logOtherFail(String action, String error) {
        logAdmin(LOG_TYPE_OTHER, action, false, error, "");
    }

    /**
     * 操作失败
     * 其他日志：如果以上三种不合适，可以选择其他日志，建议是优先级最低的日志级别
     * @param action 操作动作
     * @param error 日志详情
     */
    public static void logOtherFail(String action, Throwable error) {
        String trace = getExceptionPrintStackTrace(error);
        logAdmin(LOG_TYPE_OTHER, action, false, trace, "");
    }

    /**
     * 操作失败
     * 其他日志：如果以上三种不合适，可以选择其他日志，建议是优先级最低的日志级别
     * @param action 操作动作
     * @param error 日志详情
     * @param comment 操作备注
     */
    public static void logOtherFail(String action, String error, String comment) {
        logAdmin(LOG_TYPE_GENERAL, action, false, error, comment);
    }

    /**
     * 操作失败
     * 其他日志：如果以上三种不合适，可以选择其他日志，建议是优先级最低的日志级别
     * @param action 操作动作
     * @param error 日志详情
     * @param comment 操作备注
     */
    public static void logOtherFail(String action, Throwable error, String comment) {
        String trace = getExceptionPrintStackTrace(error);
        logAdmin(LOG_TYPE_GENERAL, action, false, trace, comment);
    }

    /**
     * 获取异常堆栈信息
     * @param e 异常
     */
    public static String getExceptionPrintStackTrace(Throwable e) {
        StringWriter sw = new StringWriter();
        e.printStackTrace(new PrintWriter(sw));
        return sw.toString();
    }

    /**
     *
     * @param type  操作分类
     * @param action 操作动作
     * @param succeed 操作状态
     * @param result  日志详情
     * @param comment 补充信息
     */
    public static void logAdmin(Short type, String action, Boolean succeed, String result, String comment) {
        executorService.submit(()->{
            LitemallLog log = new LitemallLog();
            try {
                log.setAdmin(StpUtil.getLoginIdAsString());
            }catch (Exception e){
                log.setAdmin("匿名用户");
            }
            HttpServletRequest request = GlobalWebUtil.getRequest();
            if (request != null){
                if (!StringUtils.hasText(comment)){
                    log.setComment(request.getRequestURI());
                }
                String ip = IpUtil.getIpAddr(request);
                IpInfo ipInfo = IpUtil.getCityInfo(ip);
                if (ipInfo != null){
                    log.setIp(ipInfo.getAddress());
                }else {
                    log.setIp(ip);
                }
            }else {
                log.setIp("127.0.0.1");
            }
            log.setType(type);
            log.setAction(action);
            log.setStatus(succeed);
            log.setResult(result);
            if (StringUtils.hasText(comment)){
                log.setComment(comment);
            }
            logService.add(log);
        });
    }

}
