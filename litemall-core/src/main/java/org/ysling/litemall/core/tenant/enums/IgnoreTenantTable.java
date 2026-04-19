package org.ysling.litemall.core.tenant.enums;

/**
 * 忽略添加租户ID的表
 * @author Ysling
 */
public enum IgnoreTenantTable {

    /**忽略表*/
    table("*", "全部拦截");

    public final String tableName;
    public final String message;

    IgnoreTenantTable(String tableName, String message) {
        this.tableName = tableName;
        this.message = message;
    }

    /**
     * 对比表名
     * @param tableName 表名
     * @return 存在true 不存在false
     */
    public static Boolean parseValue(String tableName) {
        if (tableName != null) {
            for (IgnoreTenantTable item : values()) {
                if (item.tableName.equals(tableName)) {
                    return true;
                }
            }
        }
        return false;
    }
}
