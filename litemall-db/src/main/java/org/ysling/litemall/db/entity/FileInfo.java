package org.ysling.litemall.db.entity;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;


@Data
public class FileInfo implements Serializable {

    /**
     * 文件地址
     */
    private String filePath;

    /**
     * 文件名称
     */
    private String fileName;

    /**
     * 文件大小
     */
    private Long fileSize;

    /**
     * 文件大小文本
     */
    private String sizeText;

    /**
     * 文件类型
     */
    private String fileType;

    /**
     * 将文件大小转文本
     * @param size 文件大小
     */
    public void setSizeText(Long size) {
        BigDecimal decimal = BigDecimal.valueOf(size);
        BigDecimal value = BigDecimal.valueOf(1024 * 1024);
        BigDecimal sizeText = decimal.divide(value,2, RoundingMode.HALF_UP);
        this.sizeText = sizeText + "MB";
    }


}
