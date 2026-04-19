package org.ysling.litemall.core.utils;
/**
 *  Copyright (c) [ysling] [927069313@qq.com]
 *  [litemall-plus] is licensed under Mulan PSL v2.
 *  You can use this software according to the terms and conditions of the Mulan PSL v2.
 *  You may obtain a copy of Mulan PSL v2 at:
 *              http://license.coscl.org.cn/MulanPSL2
 *  THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND,
 *  EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT,
 *  MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 *  See the Mulan PSL v2 for more details.
 */
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

/**
 * 桌面二维码生成
 */
public class QRCodeUtil {

	/**
	 * 生成二维码
	 * @param httpTcp  	协议
	 * @param tableNo	桌号
	 * @param imgSize	图片大小
	 * @return
	 */
	public static byte[] createPicBlob(String httpTcp ,String tableNo , Integer imgSize){
		String generateQRCodeImage = generateQRCodeImage(httpTcp+"?tableNo="+tableNo, imgSize, tableNo+".jpg");

		File file = new File(generateQRCodeImage);
		byte[] imageData = new byte[(int)file.length()-1];
		try {
			FileInputStream is= new FileInputStream(file);
			is.read(imageData);
			is.close();
			file.delete();
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
		return imageData;
	}

	/**
	 * 生成二维码
	 * @param text  	文本
	 * @param imgSize  	二维码大小
	 * @param fileName	文件名
	 */
	public static String generateQRCodeImage(String text, Integer imgSize, String fileName) {
		QRCodeWriter qrCodeWriter = new QRCodeWriter();
		String basePath = FileUtil.getBasePath("storage") + File.separator + fileName;

		try {
			BitMatrix bitMatrix = qrCodeWriter.encode(text, BarcodeFormat.QR_CODE, imgSize, imgSize);
			Path path = FileSystems.getDefault().getPath(basePath);
			MatrixToImageWriter.writeToPath(bitMatrix, "png", path);
		} catch (WriterException | IOException e) {
			e.printStackTrace();
		}
		return basePath;
	}


}
