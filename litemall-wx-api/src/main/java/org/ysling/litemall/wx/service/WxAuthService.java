package org.ysling.litemall.wx.service;

import cn.binarywang.wx.miniapp.api.WxMaService;
import cn.binarywang.wx.miniapp.api.WxMaUserService;
import cn.binarywang.wx.miniapp.bean.WxMaJscode2SessionResult;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.error.WxErrorException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.ysling.litemall.core.utils.JacksonUtil;
import org.ysling.litemall.wx.model.auth.result.LoginQrResult;
import org.ysling.litemall.wx.model.auth.result.WxAuthResult;
import org.ysling.litemall.wx.socket.WxWebSocketClient;
import java.net.URISyntaxException;


/**
 * 微信授权服务
 * @author Ysling
 */
@Slf4j
@Service
public class WxAuthService {

    @Autowired
    private Environment environment;
    @Autowired
    private WxMaService wxService;

    /**
     * 获取微信授权
     */
    public WxAuthResult wxAuth(String wxCode){
        if (wxCode == null) {
            throw new RuntimeException("微信授权失败");
        }
        String sessionKey = null;
        String openId = null;
        try {
            WxMaJscode2SessionResult result = wxService.getUserService().getSessionInfo(wxCode);
            sessionKey = result.getSessionKey();
            openId = result.getOpenid();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (sessionKey == null || openId == null) {
            throw new RuntimeException("微信授权失败");
        }
        WxAuthResult result = new WxAuthResult();
        result.setOpenId(openId);
        result.setSessionKey(sessionKey);
        return result;
    }

    /**
     * 获取微信手机号
     * @param phoneCode        手机授权code
     * @return                 手机号
     */
    public String getPhoneNumber(String phoneCode){
        if (phoneCode == null){
            throw new RuntimeException("手机号授权获取失败");
        }
        WxMaUserService userService = wxService.getUserService();
        try {
            return userService.getNewPhoneNoInfo(phoneCode).getPhoneNumber();
        }catch (Exception e){
            throw new RuntimeException("手机号授权获取失败");
        }
    }

    /**
     * 获取微信手机号
     * @param sessionKey        微信sessionKey
     * @param encryptedData     微信密钥
     * @param iv                微信密钥
     * @return                  手机号
     */
    public String getPhoneNumber(String sessionKey, String encryptedData, String iv){
        if (sessionKey == null || encryptedData == null || iv == null){
            throw new RuntimeException("手机号授权获取失败");
        }
        WxMaUserService userService = wxService.getUserService();
        return userService.getPhoneNoInfo(sessionKey, encryptedData, iv).getPhoneNumber();
    }

    /**
     * 发送socket请求
     * @param authCode 授权码
     * @param result 消息体
     */
    public void sendSocket(String authCode, LoginQrResult result){
        WxWebSocketClient client = null;
        try {
            String port = environment.getProperty("server.port");
            client = new WxWebSocketClient("ws://localhost:" + port + "/websocket/admin/" + authCode + "=wx");
            client.send(JacksonUtil.toJson(result));
            //等等返回消息最多等待5秒
            for (int i = 0; i < 5; i++) {
                if (!client.isMessage.get()){
                    Thread.sleep(1000);
                } else {
                    break;
                }
            }
            if (!client.isMessage.get()){
                throw new RuntimeException("二维码已过期");
            }
        } catch (URISyntaxException | InterruptedException e){
            log.error(e.getMessage());
            throw new RuntimeException("授权失败4");
        } finally {
            if (client != null){
                if (client.isMessage.get()){
                    client.close();
                }
            }
        }
    }



}
