package org.ysling.litemall.core.handler;

/**
 * 自定义未登录异常
 * @author Ysling
 */
public class NotLoginException extends RuntimeException{

    public NotLoginException(String message) {
        super(message);
    }

}
