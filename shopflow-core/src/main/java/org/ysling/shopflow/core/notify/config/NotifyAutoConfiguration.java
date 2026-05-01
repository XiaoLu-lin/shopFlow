package org.ysling.shopflow.core.notify.config;
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
import com.github.qcloudsms.SmsSingleSender;
import org.springframework.beans.factory.annotation.Autowired;
import org.ysling.shopflow.core.notify.model.SmsType;
import org.ysling.shopflow.core.notify.service.NotifyMailService;
import org.ysling.shopflow.core.notify.service.NotifyMobileService;
import org.ysling.shopflow.core.notify.sender.AliyunSmsSender;
import org.ysling.shopflow.core.notify.sender.TencentSmsSender;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import java.util.Properties;

/**
 * @author Ysling
 */
@Configuration
@EnableConfigurationProperties(NotifyProperties.class)
public class NotifyAutoConfiguration {

    @Autowired
    private NotifyProperties properties;

    @Bean
    public NotifyMailService mailService() {
        NotifyMailService mailService = new NotifyMailService();
        NotifyProperties.Mail mailConfig = properties.getMail();
        if (mailConfig.isEnable()) {
            mailService.setMailSender(mailSender());
            mailService.setSendFrom(mailConfig.getSendFrom());
            mailService.setSendTo(mailConfig.getSendTo());
        }
        return mailService;
    }

    @Bean
    public NotifyMobileService mobileService() {
        NotifyMobileService mobileService = new NotifyMobileService();
        NotifyProperties.Sms smsConfig = properties.getSms();
        if (smsConfig.isEnable()) {
            if(SmsType.TENCENT.getType().equals(smsConfig.getActive())) {
                mobileService.setSmsSender(tencentSmsSender());
            }
            else if(SmsType.ALIYUN.getType().equals(smsConfig.getActive())) {
                mobileService.setSmsSender(aliyunSmsSender());
            }
            mobileService.setSmsTemplate(smsConfig.getTemplate());
        }
        return mobileService;
    }

    public JavaMailSenderImpl mailSender() {
        NotifyProperties.Mail mailConfig = properties.getMail();
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost(mailConfig.getHost());
        mailSender.setUsername(mailConfig.getUsername());
        mailSender.setPassword(mailConfig.getPassword());
        mailSender.setPort(mailConfig.getPort());
        Properties properties = new Properties();
        properties.put("mail.smtp.auth",true);
        properties.put("mail.smtp.timeout",5000);
        properties.put("mail.smtp.starttls.enable",true);
        properties.put("mail.smtp.socketFactory.fallback", "false");
        //阿里云 必须加入配置 outlook配置又不需要 视情况而定.发送不成功多数是这里的配置问题
        properties.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        properties.put("mail.smtp.socketFactory.port", mailConfig.getPort());
        properties.put("debug", true);
        mailSender.setJavaMailProperties(properties);
        return mailSender;
    }

    public TencentSmsSender tencentSmsSender() {
        NotifyProperties.Sms smsConfig = properties.getSms();
        TencentSmsSender smsSender = new TencentSmsSender();
        NotifyProperties.Sms.Tencent tencent = smsConfig.getTencent();
        smsSender.setSender(new SmsSingleSender(tencent.getAppid(), tencent.getAppKey()));
        smsSender.setSign(smsConfig.getSign());
        return smsSender;
    }

    public AliyunSmsSender aliyunSmsSender() {
        NotifyProperties.Sms smsConfig = properties.getSms();
        AliyunSmsSender smsSender = new AliyunSmsSender();
        NotifyProperties.Sms.Aliyun aliyun = smsConfig.getAliyun();
        smsSender.setSign(smsConfig.getSign());
        smsSender.setRegionId(aliyun.getRegionId());
        smsSender.setAccessKeyId(aliyun.getAccessKeyId());
        smsSender.setAccessKeySecret(aliyun.getAccessKeySecret());
        return smsSender;
    }


}
