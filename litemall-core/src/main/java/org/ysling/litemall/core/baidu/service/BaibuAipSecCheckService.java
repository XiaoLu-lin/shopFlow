package org.ysling.litemall.core.baidu.service;


import com.baidu.aip.contentcensor.AipContentCensor;
import com.baidu.aip.contentcensor.EImgType;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.ysling.litemall.core.baidu.config.BaiduProperties;
import org.ysling.litemall.core.baidu.result.AipPicSecCheckResult;
import org.ysling.litemall.core.baidu.result.AipTextSecCheckResult;
import org.ysling.litemall.core.utils.JacksonUtil;
import org.ysling.litemall.core.utils.RegexUtil;
import java.util.HashMap;
import java.util.Objects;

/**
 * 百度内容审核
 */
@Component
@EnableConfigurationProperties(BaiduProperties.class)
public class BaibuAipSecCheckService {


    private static BaiduProperties properties;

    @Autowired
    public void setProperties(BaiduProperties properties) {
        BaibuAipSecCheckService.properties = properties;
    }

    /**
     * 内容安全审核
     */
    private static AipContentCensor aipContentCensor;

    /**
     * 内容安全审核
     */
    public static AipContentCensor aipContentCensor(){
        String APP_ID = properties.getAppid();
        String API_KEY = properties.getApiKey();
        String SECRET_KEY = properties.getSecretKey();
        if (aipContentCensor == null){
            aipContentCensor = new AipContentCensor(APP_ID, API_KEY, SECRET_KEY);
        }
        return aipContentCensor;
    }

    /**
     * 百度文本审核
     * @param text 文本信息
     */
    public static void textSecCheck(String text){
        if (!StringUtils.hasText(text) || text.length() > 2000 || !properties.isEnable()){
            return;
        }
        JSONObject jsonObject = aipContentCensor().textCensorUserDefined(text);
        String body = jsonObject.toString();
        AipTextSecCheckResult result = JacksonUtil.parseObject(body, AipTextSecCheckResult.class);
        if (result == null){
            throw new RuntimeException("接口调用失败");
        }
        if (Objects.equals(2 , result.getConclusionType())){
            for (AipTextSecCheckResult.Data data :result.getData()) {
                throw new RuntimeException(data.getMsg());
            }
        }
        if (result.getErrorMsg() != null){
            throw new RuntimeException(result.getErrorMsg());
        }
    }


    /**
     * 百度图片审核
     * @param imageUrl 图片地址
     */
    public static void picSecCheck(String imageUrl) {
        if (imageUrl == null || !RegexUtil.isImage(imageUrl) || !properties.isEnable()){
            return;
        }
        JSONObject jsonObject = aipContentCensor().imageCensorUserDefined(imageUrl, EImgType.URL, new HashMap<>());
        String body = jsonObject.toString();
        AipPicSecCheckResult result = JacksonUtil.parseObject(body, AipPicSecCheckResult.class);
        if (result == null){
            throw new RuntimeException("接口调用失败");
        }
        if (Objects.equals(2 , result.getConclusionType())){
            for (AipPicSecCheckResult.Data data :result.getData()) {
                throw new RuntimeException(data.getMsg());
            }
        }
        if (result.getErrorMsg() != null){
            throw new RuntimeException(result.getErrorMsg());
        }
    }

}
