package org.ysling.litemall.core.satoken.handler;

import cn.dev33.satoken.exception.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.ysling.litemall.core.utils.response.ResponseUtil;

/**
 * 全局异常处理
 * @author Ysling
 */
@RestControllerAdvice
public class SaExceptionHandler {


    /**
     * 拦截：未登录异常
     */
    @ResponseBody
    @ExceptionHandler(NotLoginException.class)
    public Object handlerException(NotLoginException e) {
        String message;
        switch (e.getType()){
            case NotLoginException.NOT_TOKEN:  message = "未提供token" ; break;
            case NotLoginException.INVALID_TOKEN : message = "token无效"; break;
            case NotLoginException.TOKEN_TIMEOUT : message = "token已过期"; break;
            case NotLoginException.BE_REPLACED : message = "token已被项下线"; break;
            case NotLoginException.KICK_OUT : message = "token已被踢下线"; break;
            case NotLoginException.NOT_TOKEN_MESSAGE : message = "未能读取到有效Token"; break;
            default : message = "当前会话未登录";
        }
        e.printStackTrace();
        return ResponseUtil.fail(message);
    }

    /**
     * 拦截：缺少权限异常
     */
    @ResponseBody
    @ExceptionHandler(NotPermissionException.class)
    public Object handlerException(NotPermissionException e) {
        e.printStackTrace();
        return ResponseUtil.fail("缺少权限：" + e.getPermission());
    }

    /**
     * 拦截：缺少角色异常
     */
    @ResponseBody
    @ExceptionHandler(NotRoleException.class)
    public Object handlerException(NotRoleException e) {
        e.printStackTrace();
        return ResponseUtil.fail("缺少角色：" + e.getRole());
    }

    /**
     * 拦截：二级认证校验失败异常
     */
    @ResponseBody
    @ExceptionHandler(NotSafeException.class)
    public Object handlerException(NotSafeException e) {
        e.printStackTrace();
        return ResponseUtil.fail("二级认证校验失败：" + e.getService());
    }

    /**
     * 拦截：服务封禁异常
     */
    @ResponseBody
    @ExceptionHandler(DisableServiceException.class)
    public Object handlerException(DisableServiceException e) {
        e.printStackTrace();
        return ResponseUtil.fail("当前账号 " + e.getService() + " 服务已被封禁 (level=" + e.getLevel() + ")：" + e.getDisableTime() + "秒后解封");
    }

    /**
     * 拦截：Http Basic 校验失败异常
     */
    @ResponseBody
    @ExceptionHandler(NotBasicAuthException.class)
    public Object handlerException(NotBasicAuthException e) {
        e.printStackTrace();
        return ResponseUtil.fail(e.getMessage());
    }

}
