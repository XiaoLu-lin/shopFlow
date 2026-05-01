package org.ysling.shopflow.wx.web;

import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.error.WxErrorException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.ysling.shopflow.core.weixin.service.LogisticsPluginService;
import org.ysling.shopflow.wx.annotation.LoginUser;
import org.ysling.shopflow.wx.web.impl.WxWebExpressService;

/**
 * 物流查询接口
 * @author Ysling
 */
@Slf4j
@RestController
@RequestMapping("/wx/express")
@Validated
public class WxExpressController {

    @Autowired
    private WxWebExpressService expressService;
    @Autowired
    private LogisticsPluginService logisticsPluginService;

    /**
     * 查询物流公司
     * @return 物流公司
     */
    @GetMapping("channel")
    public Object channel() {
        return expressService.channel();
    }

    /**
     * 通过快递鸟查询物流
     * @param userId 用户id
     * @param orderSn 订单编号
     * @return waybillToken
     */
    @GetMapping("api-track")
    public Object apiTrack(@LoginUser String userId, String orderSn){
        return expressService.apiTrack(userId , orderSn);
    }

    /**
     * 获取微信物流查询插件token
     * @param userId 用户id
     * @param orderSn 订单编号
     * @return waybillToken
     */
    @GetMapping("logistics")
    public Object logistics(@LoginUser String userId, String orderSn) throws WxErrorException {
        return logisticsPluginService.getWaybillToken(userId ,orderSn);
    }


}
