package org.ysling.litemall.core;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ysling.litemall.core.express.service.ExpressService;
import org.ysling.litemall.core.express.model.ExpressInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;

@WebAppConfiguration
@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
public class ExpressTest {

    private final Log logger = LogFactory.getLog(ExpressTest.class);
    @Autowired
    private ExpressService expressService;

    @Test
    public void test() {
        ExpressInfo ei = null;
        try {
            ei = expressService.getExpressInfo("YTO", "YT6808452487835");
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        logger.info(ei);
    }
}
