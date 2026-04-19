package org.ysling.litemall.core.utils;

import cn.hutool.http.HttpRequest;
import cn.hutool.json.JSONUtil;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;

/**
 * 常用接口请求
 */
public class ApiUtil {

    /**
     * 随机头像输出
     *
     * 文档地址：https://www.free-api.com/doc/304
     * @param sort      选填	选择输出分类[男，女，动漫男，动漫女]，为空随机输出
     * @param format    选填	选择输出格式[json，images]
     * @return JSON返回示例：
     * {
     * 	"code": 1,
     * 	"imgurl": "https:\/\/ws2.sinaimg.cn\/large\/005zWjpngy1fuvgjtiihyj31400p0ajp.jpg"
     * }
     */
    public static String avatarRequest(String sort, String format){
        HashMap<String, Object> body = new HashMap<>();
        body.put("sort" , sort);
        body.put("format" , format);
        return HttpRequest.post("https://api.uomg.com/api/rand.avatar")
                .header("Content-Type", "application/json")
                .body(JSONUtil.toJsonStr(body))
                .execute().body();
    }


    /**
     * 毒鸡汤输出
     *
     * 文档地址：https://du.shadiao.pro/
     * @return JSON返回示例：
     * {
     *     "data": {
     *         "type": "毒鸡汤",
     *         "text": "瘦的人能把衣服穿出故事，胖的人只能穿成事故。"
     *     }
     * }
     */
    public static String duRequest(){
        return HttpRequest.get("https://api.shadiao.pro/du")
                .header("Content-Type", "application/json")
                .charset(StandardCharsets.UTF_8)
                .execute().body();
    }


}
