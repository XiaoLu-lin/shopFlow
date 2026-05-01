package org.ysling.shopflow.core.notify.service;
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

import lombok.Data;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.ysling.shopflow.core.system.SystemUtils;
import org.ysling.shopflow.core.system.enums.SystemMallConfig;
import org.ysling.shopflow.core.utils.FileUtil;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.File;
import java.io.Serializable;

/**
 * 商城通知服务类
 * @author Ysling
 */
@Data
public class NotifyMailService implements Serializable {

    private JavaMailSenderImpl mailSender;
    private String sendFrom;
    private String sendTo;

    public boolean isMailEnable() {
        return mailSender != null;
    }

    /**
     * 邮件消息通知,
     * 接收者在spring.mail.sendto中指定
     *
     * @param subject 邮件标题
     * @param content 邮件内容
     */
    @Async
    public void notifyMail(String subject, String content ,String...mails) {
        if (mailSender == null) {
            return;
        }
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(SystemMallConfig.getMallName()+'<'+sendFrom+'>');
        if (mails.length > 0) {
        	for (String mail : mails) {
        		message.setTo(mail);
    		}
		}else {
			message.setTo(sendTo);
		}
        message.setSubject(subject);
        message.setText(content);
        mailSender.send(message);
    }

    /**
     * 带多个附件的Email
     * @param subject 标题
     * @param content 内容
     * @param accessory 附件url
     * @param mails 收件邮箱
     */
    @Async
    public void notifyAttachmentMail(String subject, String content ,String[] accessory, String[] mails) {
        if (mailSender == null) {
            return;
        }
        MimeMessage message = mailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = getMimeMessageHelper(subject, content, mails, message);
            if (accessory != null && accessory.length > 0){
                for (String url :accessory) {
                    File file = FileUtil.uploadFile(url);
                    if (file == null) {
                        throw new RuntimeException("附件获取失败");
                    }
                    helper.addAttachment(file.getName(), file);
                }
            }
        } catch (MessagingException e){
            e.printStackTrace();
        }
        mailSender.send(message);
    }

    /**
     * 带多个附件的Email
     * @param subject 标题
     * @param content 内容
     * @param accessory 附件文件
     * @param mails 收件邮箱
     */
    @Async
    public void notifyAttachmentMail(String subject, String content ,File[] accessory, String[] mails) {
        if (mailSender == null) {
            return;
        }
        MimeMessage message = mailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = getMimeMessageHelper(subject, content, mails, message);
            if (accessory != null && accessory.length > 0){
                for (File file :accessory) {
                    helper.addAttachment(file.getName(), file);
                }
            }
        } catch (MessagingException e){
            e.printStackTrace();
        }
        mailSender.send(message);
    }

    private MimeMessageHelper getMimeMessageHelper(String subject, String content, String[] mails, MimeMessage message) throws MessagingException {
        MimeMessageHelper helper = new MimeMessageHelper(message, true);
        helper.setFrom(SystemMallConfig.getMallName()+'<'+sendFrom+'>');
        helper.setSubject(subject);
        helper.setText(content);
        if (mails != null && mails.length > 0) {
            helper.setTo(mails);
        }else {
            helper.setTo(sendTo);
        }
        return helper;
    }

}
