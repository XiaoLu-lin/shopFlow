package org.ysling.litemall.admin;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.ysling.litemall.core.service.QrcodeCoreService;
import org.ysling.litemall.db.domain.LitemallGoods;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.ysling.litemall.db.service.IGoodsService;

@WebAppConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
public class CreateShareImageTest {
    @Autowired
    QrcodeCoreService qCodeService;
    @Autowired
    IGoodsService goodsService;

    @Test
    public void test() {
        LitemallGoods goods = goodsService.findById("1181010");
        qCodeService.createGoodShareImage(goods);
    }
}
