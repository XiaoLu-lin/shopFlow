package org.ysling.shopflow.core.utils.response;
/**
 *  Copyright (c) [ysling] [927069313@qq.com]
 *  [naonao-jub] is licensed under Mulan PSL v2.
 *  You can use this software according to the terms and conditions of the Mulan PSL v2.
 *  You may obtain a copy of Mulan PSL v2 at:
 *              http://license.coscl.org.cn/MulanPSL2
 *  THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND,
 *  EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT,
 *  MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 *  See the Mulan PSL v2 for more details.
 */

import com.fasterxml.jackson.annotation.JsonInclude;
import com.github.pagehelper.PageInfo;
import lombok.Data;
import org.ysling.shopflow.db.entity.PageResult;
import java.io.Serializable;
import java.util.List;

/**
 * 响应操作结果
 * <pre>
 *  {
 *      errno： 错误码，
 *      errmsg：错误消息，
 *      data：  响应数据
 *  }
 * </pre>
 * @author Ysling
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ResponseUtil<T> implements Serializable {

    /** 状态码 */
    private final String errno;
    /** 返回信息 */
    private final String errmsg;
    /** 返回实体结果 */
    private final T data;


    /**
     * 响应实体类
     * @param status 响应枚举
     * @param data   响应结果
     */
    public ResponseUtil(ResponseStatus status, T data) {
        this.errno = status.getErrno();
        this.errmsg = status.getErrMsg();
        this.data = data;
    }

    /**
     * 响应实体类
     * @param errno     状态码
     * @param errmsg    返回信息
     * @param data      结果
     */
    public ResponseUtil(String errno, String errmsg, T data) {
        this.errno = errno;
        this.errmsg = errmsg;
        this.data = data;
    }

    /**
     * 响应成功
     */
    public static <T> ResponseUtil<T> ok() {
        return new ResponseUtil<>(ResponseStatus.SUCCESS, null);
    }

    /**
     * 响应成功
     * @param data 响应结果
     */
    public static <T> ResponseUtil<T> ok(T data) {
        return new ResponseUtil<>(ResponseStatus.SUCCESS, data);
    }

    /**
     * 响应成功列表
     * @param list 结果列表
     */
    public static <V> ResponseUtil<PageResult<V>> okList(List<V> list) {
        return ok(new PageResult<>(PageInfo.of(list), list));
    }

    /**
     * 响应成功列表
     * @param list      封装后的list
     * @param pageList  分页list (用于获取分页信息)
     */
    public static <V> ResponseUtil<PageResult<V>> okList(List<V> list, List<?> pageList) {
        return ok(new PageResult<>(PageInfo.of(pageList), list));
    }

    /**
     * 响应失败
     */
    public static <T> ResponseUtil<T> fail() {
        return fail(ResponseStatus.FAILED);
    }

    /**
     * 响应失败
     * @param errmsg 失败信息
     */
    public static <T> ResponseUtil<T> fail(String errmsg) {
        return new ResponseUtil<>(ResponseStatus.FAILED.getErrno(), errmsg, null);
    }

    /**
     * 响应失败
     * @param status 响应枚举
     */
    public static <T> ResponseUtil<T> fail(ResponseStatus status) {
        return new ResponseUtil<>(status, null);
    }

    /**
     * 响应失败
     * @param errno     响应状态
     * @param errmsg    失败信息
     */
    public static <T> ResponseUtil<T> fail(Integer errno, String errmsg) {
        return new ResponseUtil<>(String.valueOf(errno), errmsg, null);
    }

    /**
     * 响应失败
     * @param errno     响应状态
     * @param errmsg    失败信息
     */
    public static <T> ResponseUtil<T> fail(String errno, String errmsg) {
        return new ResponseUtil<>(errno, errmsg, null);
    }

    /**用户请求参数错误*/
    public static <T> ResponseUtil<T> badArgument() {
        return fail(ResponseStatus.USER_ERROR_A0400);
    }

    /**请求必填参数为空*/
    public static <T> ResponseUtil<T> badArgumentValue() {
        return fail(ResponseStatus.USER_ERROR_A0410);
    }

    /**用户未获得第三方登陆授权*/
    public static <T> ResponseUtil<T> unlogin() {
        return fail(ResponseStatus.USER_ERROR_A0223);
    }

    /**用户账户不存在*/
    public static <T> ResponseUtil<T> notRegister() {
        return fail(ResponseStatus.USER_ERROR_A0201);
    }

    /**系统执行出错*/
    public static <T> ResponseUtil<T> serious() {
        return fail(ResponseStatus.SYSTEM_ERROR_B0001);
    }

    /**更新失败，请再尝试一次*/
    public static <T> ResponseUtil<T> updatedDateExpired() {
        return fail(ResponseStatus.SYSTEM_ERROR_B0000);
    }

    /**添加失败，请再尝试一次*/
    public static <T> ResponseUtil<T> addDataFailed() {
        return fail(ResponseStatus.SYSTEM_ERROR_B0002);
    }

    /**更新失败，请再尝试一次*/
    public static <T> ResponseUtil<T> updatedDataFailed() {
        return fail(ResponseStatus.SYSTEM_ERROR_B0000);
    }

    /**删除失败，请再尝试一次*/
    public static <T> ResponseUtil<T> deletedDataFailed() {
        return fail(ResponseStatus.SYSTEM_ERROR_B0003);
    }



}
