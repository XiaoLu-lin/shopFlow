package org.ysling.litemall.wx.service;
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

import org.springframework.cache.annotation.CacheConfig;
import org.springframework.stereotype.Service;
import org.ysling.litemall.db.service.impl.NoticeAdminServiceImpl;


/**
 * 通知管理员服务
 * @author Ysling
 */
@Service
@CacheConfig(cacheNames = "litemall_notice_admin")
public class WxNoticeAdminService extends NoticeAdminServiceImpl {


}
