package org.ysling.shopflow.wx;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.ysling.shopflow.db.domain.ShopflowOrder;
import org.ysling.shopflow.db.service.IOrderService;


@WebAppConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
public class WxConfigTest {
    @Autowired
    private Environment environment;
    @Autowired
    private IOrderService orderService;

    @Test
    public void test() {
        ShopflowOrder order = orderService.findById("438");
        order.setOrderSn("1213123123123");
        System.out.println(order);
        System.out.println("影响行数："+orderService.updateVersionSelective(order));

        ShopflowOrder order1 = orderService.findById("438");
        order.setOrderSn("1213123123123");
        System.out.println(order1);
        System.out.println("影响行数："+orderService.updateVersionSelective(order1));
//        System.out.println("影响行数："+orderService.updateWithOptimisticLocker(order));
//        System.out.println("影响行数："+orderService.updateWithOptimisticLocker(order));



//        // 测试获取application-core.yml配置信息
//        System.out.println(environment.getProperty("shopflow.express.appId"));
//        // 测试获取application-db.yml配置信息
//        System.out.println(environment.getProperty("spring.datasource.druid.url"));
//        // 测试获取application-wx.yml配置信息
//        System.out.println(environment.getProperty("shopflow.wx.app-id"));
//        // 测试获取application-wx.yml配置信息
//        System.out.println(environment.getProperty("shopflow.wx.notify-url"));
//        // 测试获取application.yml配置信息
//        System.out.println(environment.getProperty("logging.level.org.ysling.shopflow.wx"));
    }

}
