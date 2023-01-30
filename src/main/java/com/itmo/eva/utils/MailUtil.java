package com.itmo.eva.utils;

import com.itmo.eva.model.entity.Teacher;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Date;
import java.util.List;
import java.util.Properties;

public class MailUtil {

    // 发件人的 邮箱 和 密码
    public static String myEmailAccount = "2473159069@qq.com";
    public static String myEmailPassword = "pglxksrspsnudjhh";

    // 发件人邮箱的 SMTP 服务器地址
    public static String myEmailSMTPHost = "smtp.qq.com";

    /**
     * 发送邮件方法
     *
     * @param teacherList 发送教师信息
     * @param sendTime 发送时间
     */
    public static void sendEmail(List<Teacher> teacherList, Date sendTime) throws Exception {
        // 1. 创建参数配置, 用于连接邮件服务器的参数配置
        Properties props = new Properties();                    // 参数配置
        props.setProperty("mail.transport.protocol", "smtp");   // 使用的协议（JavaMail规范要求）
        props.setProperty("mail.smtp.host", myEmailSMTPHost);   // 发件人的邮箱的 SMTP 服务器地址
        props.setProperty("mail.smtp.auth", "true");            // 需要请求认证

        // 根据配置创建会话对象, 用于和邮件服务器交互
        Session session = Session.getInstance(props);
        session.setDebug(true);                                 // 设置为debug模式, 可以查看详细的发送 log

        // 创建一封邮件
        MimeMessage message = createMimeMessage(session, myEmailAccount, teacherList, sendTime);

        // 根据 Session 获取邮件传输对象
        Transport transport = session.getTransport();

        transport.connect(myEmailAccount, myEmailPassword);

        // 发送邮件, 发到所有的收件地址, message.getAllRecipients() 获取到的是在创建邮件对象时添加的所有收件人, 抄送人, 密送人
        transport.sendMessage(message, message.getAllRecipients());

        // 关闭连接
        transport.close();

    }

    /**
     * 创建一封只包含文本的简单邮件
     *
     * @param session      和服务器交互的会话
     * @param sendMail     发件人邮箱
     * @param teacherList  收件人邮箱
     * @param sendTime     发送时间
     * @return 邮件信息
     */
    public static MimeMessage createMimeMessage(Session session, String sendMail, List<Teacher> teacherList, Date sendTime) throws Exception {
        // 1. 创建一封邮件
        MimeMessage message = new MimeMessage(session);

        // 2. From: 发件人
        message.setFrom(new InternetAddress(sendMail, "chenjiahan", "UTF-8"));

        // 3. To: 收件人
        for (Teacher teacher : teacherList) {
            // 添加收件人
            String name = "尊敬的" + teacher.getName() + "老师";
            message.addRecipient(MimeMessage.RecipientType.TO, new InternetAddress(teacher.getEmail(), name, "UTF-8"));
        }

        // 4. Subject: 邮件主题（标题有广告嫌疑，避免被邮件服务器误认为是滥发广告以至返回失败，请修改标题）
        message.setSubject("教学评价系统", "UTF-8");

        // 5. Content: 邮件正文
        message.setContent("测试邮件", "text/html;charset=UTF-8");

        // 6. 设置发件时间
        message.setSentDate(sendTime);

        // 7. 保存设置
        message.saveChanges();

        return message;
    }

}
