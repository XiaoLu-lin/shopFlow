package org.ysling.litemall.core.utils.chatgpt;

import cn.hutool.json.JSONUtil;
import org.springframework.util.StringUtils;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * @author Ysling
 */
public class ChatGPTUtil {

    /**
     * ChatGpt3.5公益接口
     */
    private static final String API_URL = "http://owl9.vipi9.top:45111/gpt3_5";

    /**
     * 发送消息
     * @param content 消息内容
     * @return AI回复
     */
    public static String send(String content){
        HashMap<String, Object> body = new HashMap<>(3);
        body.put("q" , content);
        body.put("uuid" , UUID.randomUUID().toString());
        body.put("network" , true);
        List<String> results = sendPost(API_URL, body);
        if (results.size() > 0){
            String result = results.get(results.size() - 2);
            String first = result.replaceFirst("data:", "");
            Data data = JSONUtil.toBean(first, Data.class);
            return data.getMessage().getContent().getParts().get(0);
        }
        return null;
    }

    /**
     * 向指定 URL 发送POST方法的请求
     * @param url    发送请求的 URL
     * @param params 请求的参数集合
     * @return 远程资源的响应结果
     */
    private static List<String> sendPost(String url, Map<String, Object> params) {
        OutputStreamWriter out = null;
        BufferedReader in = null;
        ArrayList<String> results = new ArrayList<>();
        try {
            URL realUrl = new URL(url);
            HttpURLConnection conn = (HttpURLConnection) realUrl.openConnection();
            // 发送POST请求必须设置如下两行
            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.setUseCaches(false);
            // POST方法
            conn.setRequestMethod("POST");
            conn.setInstanceFollowRedirects(true);
            // 设置通用的请求属性
            conn.setRequestProperty("accept", "*/*");
            conn.setRequestProperty("connection", "Keep-Alive");
            conn.setRequestProperty("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
            conn.setRequestProperty("Content-Type", "application/json;charset=utf-8");
            conn.connect();
            // 获取URLConnection对象对应的输出流
            out = new OutputStreamWriter(conn.getOutputStream(), StandardCharsets.UTF_8);
            // 发送请求参数
            if (params != null) {
                out.write(JSONUtil.toJsonStr(params));
            }
            // flush输出流的缓冲
            out.flush();
            // 定义BufferedReader输入流来读取URL的响应
            in = new BufferedReader(new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8));
            String line;
            while ((line = in.readLine()) != null) {
                if (StringUtils.hasText(line)){
                    results.add(line);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        //使用finally块来关闭输出流、输入流
        finally {
            try {
                if (out != null) {
                    out.close();
                }
                if (in != null) {
                    in.close();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        return results;
    }


    private static class Data{
        private Message message;
        private String conversationId;
        private String error;
        public void setMessage(Message message) {
            this.message = message;
        }
        public Message getMessage() {
            return message;
        }

        public void setConversationId(String conversationId) {
            this.conversationId = conversationId;
        }
        public String getConversationId() {
            return conversationId;
        }

        public void setError(String error) {
            this.error = error;
        }
        public String getError() {
            return error;
        }
    }

    private static class FinishDetails {
        private String type;
        private String stop;
        public void setType(String type) {
            this.type = type;
        }
        public String getType() {
            return type;
        }

        public void setStop(String stop) {
            this.stop = stop;
        }
        public String getStop() {
            return stop;
        }
    }

    private static class Content {
        private String contentType;
        private List<String> parts;
        public void setContentType(String contentType) {
            this.contentType = contentType;
        }
        public String getContentType() {
            return contentType;
        }

        public void setParts(List<String> parts) {
            this.parts = parts;
        }
        public List<String> getParts() {
            return parts;
        }
    }


    private static class Metadata {
        private FinishDetails finishDetails;
        private String messageType;
        private String modelSlug;
        public void setFinishDetails(FinishDetails finishDetails) {
            this.finishDetails = finishDetails;
        }
        public FinishDetails getFinishDetails() {
            return finishDetails;
        }

        public void setMessageType(String messageType) {
            this.messageType = messageType;
        }
        public String getMessageType() {
            return messageType;
        }

        public void setModelSlug(String modelSlug) {
            this.modelSlug = modelSlug;
        }
        public String getModelSlug() {
            return modelSlug;
        }
    }

    private static class Message {
        private String id;
        private String role;
        private String user;
        private String createTime;
        private String updateTime;
        private Content content;
        private boolean endTurn;
        private int weight;
        private Metadata metadata;
        private String recipient;
        public void setId(String id) {
            this.id = id;
        }
        public String getId() {
            return id;
        }

        public void setRole(String role) {
            this.role = role;
        }
        public String getRole() {
            return role;
        }

        public void setUser(String user) {
            this.user = user;
        }
        public String getUser() {
            return user;
        }

        public void setCreateTime(String createTime) {
            this.createTime = createTime;
        }
        public String getCreateTime() {
            return createTime;
        }

        public void setUpdateTime(String updateTime) {
            this.updateTime = updateTime;
        }
        public String getUpdateTime() {
            return updateTime;
        }

        public void setContent(Content content) {
            this.content = content;
        }
        public Content getContent() {
            return content;
        }

        public void setEndTurn(boolean endTurn) {
            this.endTurn = endTurn;
        }
        public boolean getEndTurn() {
            return endTurn;
        }

        public void setWeight(int weight) {
            this.weight = weight;
        }
        public int getWeight() {
            return weight;
        }

        public void setMetadata(Metadata metadata) {
            this.metadata = metadata;
        }
        public Metadata getMetadata() {
            return metadata;
        }

        public void setRecipient(String recipient) {
            this.recipient = recipient;
        }
        public String getRecipient() {
            return recipient;
        }

    }

}
