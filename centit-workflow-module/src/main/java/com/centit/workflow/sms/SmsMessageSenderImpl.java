package com.centit.workflow.sms;

import com.centit.framework.common.ResponseData;
import com.centit.framework.model.adapter.MessageSender;
import com.centit.framework.model.basedata.NoticeMessage;
import lombok.SneakyThrows;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.MultiPartEmail;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 测试短信
 */
public class SmsMessageSenderImpl implements MessageSender {

    private static final Logger logger = LoggerFactory.getLogger(SmsMessageSenderImpl.class);

    private SendSmsExecutor smsSender;
    private String serverSms;

    public SmsMessageSenderImpl() {
        smsSender = new SendSmsExecutor();
    }

    @SneakyThrows
    @Override
    public ResponseData sendMessage(String sender, String receiver, NoticeMessage message) {
        logger.info("发送短信");
        // 测试，由于短信无法测试，使用邮件替代
        MultiPartEmail multMail = new MultiPartEmail();
        // SMTP
        multMail.setHostName("mail.centit.com");
        multMail.setSmtpPort(25);
//        // 需要提供公用的邮件用户名和密码
        multMail.setAuthentication("alertmail@centit.com", "131511.cn");

        multMail.setFrom("no-reply@centit.com");
//        multMail.addTo("wang_xf@centit.com");
        multMail.addTo("wang_xf@centit.com");
//        multMail.addTo("liu_cc@centit.com");
        multMail.setCharset("utf-8");
        multMail.setSubject(message.getMsgSubject());
        String msgContent = message.getMsgContent().trim();
        multMail.setContent(msgContent, "text/plain;charset=gb2312");
        try {
            multMail.send();
            return ResponseData.successResponse;
        } catch (EmailException e) {
            logger.error(e.getMessage(), e);
            return ResponseData.makeErrorMessage(e.getMessage());
        }
    }
}
