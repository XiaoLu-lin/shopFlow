package org.ysling.litemall.core.weixin.service;

import cn.binarywang.wx.miniapp.api.WxMaSecCheckService;
import cn.binarywang.wx.miniapp.api.WxMaService;
import cn.binarywang.wx.miniapp.bean.security.WxMaMsgSecCheckCheckRequest;
import cn.binarywang.wx.miniapp.bean.security.WxMaMsgSecCheckCheckResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.ysling.litemall.core.utils.FileUtil;
import org.ysling.litemall.core.weixin.enums.MsgSecCheckType;

import java.io.File;


/**
 * 违规信息校验
 */
@Slf4j
@Service
public class WxSecCheckService {

    @Autowired
    private WxMaService wxService;

    /**
     * 违规图片校验
     * @param file 文件
     */
    public void checkImage(File file){
        if ((file.length() / 1024 / 1024) < 1){
            try {
                wxService.getSecCheckService().checkImage(file);
            }catch (Exception e){
                throw new RuntimeException(e.getMessage());
            }
        }
    }

    /**
     * 违规图片校验
     * @param file 文件
     */
    public void checkImage(MultipartFile file){
        if ((file.getSize() / 1024 / 1024) < 1){
            try {
                File toFile = FileUtil.MultipartFileToFile(file);
                wxService.getSecCheckService().checkImage(toFile);
            }catch (Exception e){
                throw new RuntimeException(e.getMessage());
            }
        }
    }

    /**
     * 违规文本校验
     * @param openid 微信openid
     * @param content 文本
     */
    public void checkMessage(String openid , String content){
        try {
            WxMaSecCheckService service = wxService.getSecCheckService();
            WxMaMsgSecCheckCheckResponse.ResultBean result = service.checkMessage(WxMaMsgSecCheckCheckRequest.builder()
                    .content(content)
                    .openid(openid)
                    .scene(1)
                    .version("2")
                    .build()).getResult();
            if (!"pass".equals(result.getSuggest())){
                String value = MsgSecCheckType.parseValue(result.getLabel());
                throw new RuntimeException(String.format("当前内容包含[%s]信息" ,value));
            }
        }catch (Exception e){
            throw new RuntimeException(e.getMessage());
        }
    }


}
