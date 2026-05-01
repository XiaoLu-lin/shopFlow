package org.ysling.shopflow.core.service;
/**
 *  Copyright (c) [ysling] [927069313@qq.com]
 *  [ShopFlow] is licensed under Mulan PSL v2.
 *  You can use this software according to the terms and conditions of the Mulan PSL v2.
 *  You may obtain a copy of Mulan PSL v2 at:
 *              http://license.coscl.org.cn/MulanPSL2
 *  THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND,
 *  EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT,
 *  MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 *  See the Mulan PSL v2 for more details.
 */
import cn.binarywang.wx.miniapp.api.WxMaService;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.error.WxErrorException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.ResourceLoader;
import org.ysling.shopflow.core.storage.service.StorageService;
import org.ysling.shopflow.core.system.SystemUtils;
import org.ysling.shopflow.core.system.enums.SystemConfig;
import org.ysling.shopflow.core.system.enums.SystemMallConfig;
import org.ysling.shopflow.db.domain.*;
import org.springframework.stereotype.Service;


import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URL;

/**
 * @author Ysling
 */
@Slf4j
@Service
public class QrcodeCoreService {

    @Autowired
    private WxMaService wxMaService;
    @Autowired
    private StorageService storageService;

    /**默认图片类型*/
    private static final String IMAGE_TYPE = "image/jpeg";
    /**微信小程序首页地址*/
    private static final String WX_HOME_PATH = "pages/index/index";


    /**
     * 生成小程序二维码
     * 默认"release" 要打开的小程序版本。正式版为 "release"，体验版为 "trial"，开发版为 "develop"
     * @param page      小程序跳转页面
     * @param scene     参数
     * @return 二维码地址
     */
    public String createWxQrcode(String page, String scene) {
        try {
            //创建二维码
//            File file = wxMaService.getQrcodeService().createWxaCodeUnlimit(scene, page, false, "trial", 430, true, null, false);
            //线上小程序
            File file = wxMaService.getQrcodeService().createWxaCodeUnlimit(scene, page);
            //存储分享图
            return storageService.store(file).getUrl();
        } catch (WxErrorException e) {
            log.error(e.getMessage(), e);
        }
        return "";
    }

    /**
     * 创建小程序二维码
     * @param user  二维码参数 ： "goods,xxx"
     * @return 二维码url
     */
    public String createUserShareQrcode(ShopflowUser user) {
        try {
            String value = "inviter," + user.getId();
            //创建二维码
            File file = wxMaService.getQrcodeService().createWxaCodeUnlimit(value, WX_HOME_PATH);
            //存储分享图
            return storageService.store(file).getUrl();
        } catch (WxErrorException e) {
            log.error(e.getMessage(), e);
        }
        return "";
    }

    /**
     * 创建团购分享图
     * @param grouponRules 团购规则
     * @param grouponId 参团id
     * @return 二维码url
     */
    public String createGrouponShareImage(ShopflowGrouponRules grouponRules, String grouponId) {
        String value = "grouponId," + grouponId;
        String imgName = "GROUPON_QRCODE_" + grouponId + ".jpg";
        String goodsName = grouponRules.getGoodsName();
        String goodPicUrl = grouponRules.getPicUrl();
        return createQrcode(value, goodsName, goodPicUrl, imgName);
    }

    /**
     * 创建商品分享图
     * @param goods 商品
     * @return 二维码url
     */
    public String createGoodShareImage(ShopflowGoods goods) {
        if (!SystemConfig.isAutoCreateShareImage()){
            return "";
        }
        String value = "goodsId," + goods.getId();
        String goodName = goods.getName();
        String goodPicUrl = goods.getPicUrl();
        String imgName = "GOODS_QRCODE_" + goods.getId() + ".jpg";
        return createQrcode(value, goodName, goodPicUrl, imgName);
    }


    /**
     * 创建赏金分享图
     * @param rewardTask 赏金规则
     * @param rewardId 赏金id
     * @return 二维码url
     */
    public String createRewardShareImage(ShopflowRewardTask rewardTask, Integer rewardId) {
        String value = "rewardId," + rewardId;
        String imgName = "REWARD_QRCODE_" + rewardId + ".jpg";
        String goodsName = rewardTask.getGoodsName();
        String goodPicUrl = rewardTask.getPicUrl();
        return createQrcode(value, goodsName, goodPicUrl, imgName);
    }

    /**
     * 创建小程序二维码
     * @param value  二维码参数 ： "goods,xxx";
     * @param goodName 商品名称
     * @param goodPicUrl 商品图片
     * @param imgName 二维码名称
     * @return 二维码url
     */
    private String createQrcode(String value, String goodName, String goodPicUrl, String imgName) {
        try {
            //创建二维码
            File file = wxMaService.getQrcodeService().createWxaCodeUnlimit(value, WX_HOME_PATH);
            //将图片，名字,商城名字画到模版图中
            byte[] imageData = drawPicture(new FileInputStream(file), goodPicUrl, goodName);
            //存储分享图
            return storageService.store(imageData, imgName).getUrl();
        } catch (WxErrorException | IOException e) {
            log.error(e.getMessage(), e);
        }
        return "";
    }


    /**
     * 将商品图片，商品名字画到模版图中
     *
     * @param qrCodeImg  二维码图片
     * @param goodPicUrl 商品图片地址
     * @param goodName   商品名称
     * @return  图片数据 byte[]
     * @throws IOException 地址错误
     */
    private byte[] drawPicture(InputStream qrCodeImg, String goodPicUrl, String goodName) throws IOException {
        ResourceLoader resourceLoader = new DefaultResourceLoader();
        InputStream inputStream = resourceLoader.getResource("static/back.png").getInputStream();
        BufferedImage red = ImageIO.read(inputStream);

        //商品图片
        URL goodPic = new URL(goodPicUrl);
        BufferedImage goodImage = ImageIO.read(goodPic);
        //小程序二维码
        BufferedImage qrCodeImage = ImageIO.read(qrCodeImg);
        // --- 画图 ---
        //底层空白 bufferedImage
        BufferedImage baseImage = new BufferedImage(red.getWidth(), red.getHeight(), BufferedImage.TYPE_4BYTE_ABGR_PRE);
        //画上图片
        drawImgInImg(baseImage, red, 0, 0, red.getWidth(), red.getHeight());
        //画上商品图片
        drawImgInImg(baseImage, goodImage, 71, 69, 660, 660);
        //画上小程序二维码
        drawImgInImg(baseImage, qrCodeImage, 448, 767, 300, 300);
        //写上商品名称
        drawTextInImg(baseImage, goodName, 65, 867);
        //写上商城名称
        drawTextInImgCenter(baseImage, SystemMallConfig.getMallName(), 98);
        //转jpg
        BufferedImage result = new BufferedImage(baseImage.getWidth(), baseImage.getHeight(), BufferedImage.TYPE_3BYTE_BGR);
        result.getGraphics().drawImage(baseImage, 0, 0, null);
        ByteArrayOutputStream bs = new ByteArrayOutputStream();
        ImageIO.write(result, "jpg", bs);

        //最终byte数组
        return bs.toByteArray();
    }

    private void drawTextInImgCenter(BufferedImage baseImage, String textToWrite, int y) {
        Graphics2D g2D = (Graphics2D) baseImage.getGraphics();
        g2D.setColor(new Color(167, 136, 69));

        String fontName = "Microsoft YaHei";

        Font f = new Font(fontName, Font.PLAIN, 28);
        g2D.setFont(f);
        g2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // 计算文字长度，计算居中的x点坐标
        FontMetrics fm = g2D.getFontMetrics(f);
        int textWidth = fm.stringWidth(textToWrite);
        int widthX = (baseImage.getWidth() - textWidth) / 2;
        // 表示这段文字在图片上的位置(x,y) .第一个是你设置的内容。

        g2D.drawString(textToWrite, widthX, y);
        // 释放对象
        g2D.dispose();
    }

    private void drawTextInImg(BufferedImage baseImage, String textToWrite, int x, int y) {
        Graphics2D g2D = (Graphics2D) baseImage.getGraphics();
        g2D.setColor(new Color(167, 136, 69));

        //TODO 注意，这里的字体必须安装在服务器上
        g2D.setFont(new Font("Microsoft YaHei", Font.PLAIN, 28));
        g2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g2D.drawString(textToWrite, x, y);
        g2D.dispose();
    }

    private void drawImgInImg(BufferedImage baseImage, BufferedImage imageToWrite, int x, int y, int width, int heigth) {
        Graphics2D g2D = (Graphics2D) baseImage.getGraphics();
        g2D.drawImage(imageToWrite, x, y, width, heigth, null);
        g2D.dispose();
    }
}
