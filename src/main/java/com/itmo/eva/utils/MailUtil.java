package com.itmo.eva.utils;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

public class MailUtil {
    /**
     * 创建邮件消息
     * @return 创建的邮件消息
     */
    private static MimeMessage createMail(){
        try {
            Properties properties = new Properties();
            properties.put("mail.smtp.auth",true);
            properties.put("mail.smtp.host", EntityCode.HOST);
            properties.put("mail.user",EntityCode.USER);
            properties.put("mail.password",EntityCode.PWD);
            // 构建授权信息，用于进行SMTP进行身份验证
            Authenticator authenticator = new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(EntityCode.USER, EntityCode.PWD);
                }
            };
            // 使用环境属性和授权信息，创建邮件会话
            Session mailSession = Session.getInstance(properties, authenticator);
            // 创建邮件消息
            MimeMessage message = new MimeMessage(mailSession);
            return message;
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 发送一个简单的文本邮件
     * @param to 收件人邮箱
     * @param title 邮件标题
     * @param text  邮件内容
     * @return
     */
    public static boolean sendMail(String to,String title,String text){
        MimeMessage message = createMail();
        if (message==null){
            return false;
        }
        try {
            // 设置发件人
            InternetAddress form = new InternetAddress(EntityCode.USER);
            message.setFrom(form);

            // 设置收件人
            InternetAddress toAddress = new InternetAddress(to);
            message.setRecipient(Message.RecipientType.TO, toAddress);

            // 设置邮件标题
            message.setSubject(title);

            // 设置邮件的内容体
            message.setContent(text, "text/html;charset=UTF-8");
            // 发送邮件
            Transport.send(message);
        }catch (Exception e){
            e.printStackTrace();
        }
        return true;
    }
}
