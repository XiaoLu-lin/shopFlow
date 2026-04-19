package org.ysling.litemall.db.entity;

import lombok.Data;

import java.io.Serializable;

/**
 * 自定义表字段与Java字段映射
 */
@Data
public class ColumnOverride implements Serializable {

    /**
     * 字段名
     */
    private String column;

    /**
     * 字段名
     */
    private String javaType;

    /**
     * 类型
     */
    private String typeHandler;

    /**
     * 类型包路径
     */
    private String typeHandlerPackage;

    /**
     * @param column 字段名
     * @param type   javaType转换枚举
     */
    public ColumnOverride(String column, TYPE type){
        this.column = column;
        this.javaType = type.javaType;
        this.typeHandler = type.typeHandler;
        this.typeHandlerPackage = type.typeHandlerPackage;
    }

    /**
     * 自定义javaType转换枚举
     */
    public enum TYPE {

        /**
         * Integer数组
         */
        INTEGER("Integer[]","JsonIntegerArrayTypeHandler.class" , "org.ysling.litemall.db.handler.JsonIntegerArrayTypeHandler"),
        /**
         * String数组
         */
        STRING("String[]","JsonStringArrayTypeHandler.class" , "org.ysling.litemall.db.handler.JsonStringArrayTypeHandler"),
        /**
         * JsonNode链表
         */
        NODE("JsonNode","JsonNodeTypeHandler.class" , "org.ysling.litemall.db.handler.JsonNodeTypeHandler");

        /**
         * 字段名
         */
        private final String javaType;

        /**
         * 类型
         */
        private final String typeHandler;

        /**
         * 类型包路径
         */
        private final String typeHandlerPackage;


        public String getJavaType() {
            return javaType;
        }

        public String getTypeHandler() {
            return typeHandler;
        }

        public String getTypeHandlerPackage() {
            return typeHandlerPackage;
        }

        TYPE(String javaType , String typeHandler, String typeHandlerPackage) {
            this.javaType = javaType;
            this.typeHandler = typeHandler;
            this.typeHandlerPackage = typeHandlerPackage;
        }
    }

}
