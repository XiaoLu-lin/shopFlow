package org.ysling.litemall.core;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;
import org.springframework.core.task.SyncTaskExecutor;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.ysling.litemall.core.notify.service.NotifyMailService;

import javax.mail.MessagingException;
import java.util.concurrent.Executor;

/**
 * 测试邮件发送服务
 * <p>
 * 注意LitemallNotifyService采用异步线程操作
 * 因此测试的时候需要睡眠一会儿，保证任务执行
 * <p>
 * 开发者需要确保：
 * 1. 在相应的邮件服务器设置正确notify.properties已经设置正确
 * 2. 在相应的邮件服务器设置正确
 */
@WebAppConfiguration
@RunWith(SpringRunner.class)
@SpringBootTest
public class MailTest {

    @Autowired
    private NotifyMailService notifyService;

	@Test
	public void SendMail() throws MessagingException {

	}


    @Test
    public void testMail() {
    	StringBuilder inform = new StringBuilder("<html><head></head><body>");
		inform.append("<table border='1' style='font-size:15px;width:100%;'>");
		inform.append("<thead>");
		inform.append("<th style='padding:15px;'>" + "订单详情" + "</th>");
		inform.append("</thead>");
		inform.append("</table>");

    	inform.append("<table border='1' style='font-size:15px;width:100%;'>");
    	inform.append("<tr>");
    	inform.append("<td style='padding:15px;width: 65px;'>" + "订单编号:" + "</td>");
		inform.append("<td style='padding:15px;'>" + "123123123" + "</td>");
    	inform.append("</tr>");
		inform.append("<tr>");
		inform.append("<td style='padding:15px;'>" + "收获人:" + "</td>");
		inform.append("<td style='padding:15px;'>" + "xxx" + "</td>");
		inform.append("</tr>");
		inform.append("<tr>");
		inform.append("<td style='padding:15px;'>" + "联系电话:" + "</td>");
		inform.append("<td style='padding:15px;'>" + "1352334234" + "</td>");
		inform.append("</tr>");
		inform.append("<tr>");
		inform.append("<td style='padding:15px;'>" + "订单模式:" + "</td>");
		inform.append("<td style='padding:15px;'>" + "（预约模式）2022年03月15日12时02分" + "</td>");
		inform.append("</tr>");
		inform.append("<tr>");
		inform.append("<td style='padding:15px;color: rgb(0, 10, 255);'>" + "商品名称:" + "</td>");
		inform.append("<td style='padding:15px;color: rgb(0, 10, 255);'>" + "趣味粉彩系列笔记本" + "</td>");
		inform.append("</tr>");
		inform.append("<tr>");
		inform.append("<td style='padding:15px;color: rgb(0, 164, 255);'>" + "商品规格:" + "</td>");
		inform.append("<td style='padding:15px;color: rgb(0, 164, 255);'>" + "[标准]" + "</td>");
		inform.append("</tr>");
		inform.append("<tr>");
		inform.append("<td style='padding:15px;color: rgb(0, 164, 255);'>" + "商品数量:" + "</td>");
		inform.append("<td style='padding:15px;color: rgb(0, 164, 255);'>" + "x1" + "</td>");
		inform.append("</tr>");
		inform.append("<tr>");
		inform.append("<td style='padding:15px;color: rgb(255, 0, 0);'>" + "总付款:" + "</td>");
		inform.append("<td style='padding:15px;color: rgb(255, 0, 0);'>" + "20"+ "</td>");
		inform.append("</tr>");
		inform.append("<tr>");
		inform.append("<td style='padding:15px;'>" + "下单时间:" + "</td>");
		inform.append("<td style='padding:15px;'>" + "2022-03-15T12:03:04.595" + "</td>");
		inform.append("</tr>");
    	inform.append("</table>");

		inform.append("<p>");
		inform.append("<a href=\"http://www.makbk.com\" style='font-size: 16px; line-height: 45px; display: block; background-color: rgb(0, 164, 255); color: rgb(255, 255, 255); text-align: center; text-decoration: none; margin-top: 20px; border-radius: 3px;'>去发货</a>");
		inform.append("</p>");

    	inform.append("</body></html>");
    	    	
        notifyService.notifyMail("跳蚤校园新订单通知", inform.toString());
    }

    @Configuration
    @Import(Application.class)
    static class ContextConfiguration {
        @Bean
        @Primary
        public Executor executor() {
            return new SyncTaskExecutor();
        }
    }




}
