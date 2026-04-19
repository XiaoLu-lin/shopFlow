package org.ysling.litemall.core.utils.chatgpt;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * ChatGPT3.5响应类
 */
public class ChatResult {

    private String id;
    private String object;
    private Integer created;
    private String model;
    private Usage usage;
    private List<Choice> choices;
    private List<Url> data;
    private Error error;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getObject() {
        return object;
    }

    public void setObject(String object) {
        this.object = object;
    }

    public Integer getCreated() {
        return created;
    }

    public void setCreated(Integer created) {
        this.created = created;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public Usage getUsage() {
        return usage;
    }

    public void setUsage(Usage usage) {
        this.usage = usage;
    }

    public List<Url> getData() {
        return data;
    }

    public void setData(List<Url> data) {
        this.data = data;
    }

    public List<Choice> getChoices() {
        return choices;
    }

    public void setChoices(List<Choice> choices) {
        this.choices = choices;
    }

    public Error getError() {
        return error;
    }

    public void setError(Error error) {
        this.error = error;
    }

    public static class Message {
        /**['system', 'assistant', 'user']*/
        private String role;

        private String content;

        public String getRole() {
            return role;
        }

        public void setRole(String role) {
            this.role = role;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }

        @Override
        public String toString() {
            return "ChatMessage{" +
                    "role='" + role + '\'' +
                    ", content='" + content + '\'' +
                    '}';
        }
    }

    public enum RoleEnum {
        user("user"),
        system("system"),
        assistant("assistant");
        RoleEnum(String role) {}
    }

    public static class Usage {
        @JsonProperty("prompt_tokens")
        private Integer promptTokens;
        @JsonProperty("completion_tokens")
        private Integer completionTokens;
        @JsonProperty("total_tokens")
        private Integer totalTokens;

        public Integer getPromptTokens() {
            return promptTokens;
        }

        public void setPromptTokens(Integer promptTokens) {
            this.promptTokens = promptTokens;
        }

        public Integer getCompletionTokens() {
            return completionTokens;
        }

        public void setCompletionTokens(Integer completionTokens) {
            this.completionTokens = completionTokens;
        }

        public Integer getTotalTokens() {
            return totalTokens;
        }

        public void setTotalTokens(Integer totalTokens) {
            this.totalTokens = totalTokens;
        }

        @Override
        public String toString() {
            return "Usage{" +
                    "promptTokens=" + promptTokens +
                    ", completionTokens=" + completionTokens +
                    ", totalTokens=" + totalTokens +
                    '}';
        }
    }

    public static class Choice {
        private Message message;
        @JsonProperty("total_tokens")
        private String finishReason;
        private Integer index;

        public Message getMessage() {
            return message;
        }

        public void setMessage(Message message) {
            this.message = message;
        }

        public String getFinishReason() {
            return finishReason;
        }

        public void setFinishReason(String finishReason) {
            this.finishReason = finishReason;
        }

        public Integer getIndex() {
            return index;
        }

        public void setIndex(Integer index) {
            this.index = index;
        }

        @Override
        public String toString() {
            return "Choice{" +
                    "message=" + message +
                    ", finishReason='" + finishReason + '\'' +
                    ", index=" + index +
                    '}';
        }
    }

    public static class Url{
        private String url;

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        @Override
        public String toString() {
            return "Url{" +
                    "url='" + url + '\'' +
                    '}';
        }
    }

    public static class Error {
        private String message;
        private String type;
        private String param;
        private String code;

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getParam() {
            return param;
        }

        public void setParam(String param) {
            this.param = param;
        }

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }

        @Override
        public String toString() {
            return "Error{" +
                    "message='" + message + '\'' +
                    ", type='" + type + '\'' +
                    ", param='" + param + '\'' +
                    ", code='" + code + '\'' +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "ChatResult{" +
                "id='" + id + '\'' +
                ", object='" + object + '\'' +
                ", created=" + created +
                ", model='" + model + '\'' +
                ", usage=" + usage +
                ", choices=" + choices +
                ", data=" + data +
                ", error=" + error +
                '}';
    }
}
