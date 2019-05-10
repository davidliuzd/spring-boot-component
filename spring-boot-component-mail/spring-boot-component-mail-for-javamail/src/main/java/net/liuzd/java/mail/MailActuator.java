package net.liuzd.java.mail;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.util.Date;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class MailActuator {

    private MailParam mailParam;

    private MailActuator() {
        mailParam = new MailParam();
    }

    public static MailActuator init() {
        return new MailActuator();
    }

    public MailActuator attach(File file) {
        return attach(null, file);
    }

    public MailActuator attach(String name, File file) {
        MailAssist.attach(mailParam, name, file);
        return this;
    }

    public MailActuator attach(URL url) {
        return attach(null, url);
    }

    public MailActuator attach(String name, URL url) {
        MailAssist.attach(mailParam, name, url);
        return this;
    }

    public MailActuator nickName(String nickName) {
        mailParam.setNickName(nickName);
        return this;
    }

    public MailActuator subject(String subject) {
        mailParam.setSubject(subject);
        return this;
    }

    public MailActuator body(String body) {
        MailAssist.addBody(mailParam, body);
        return this;
    }

    public MailActuator to(String... to) {
        mailParam.setTo(to);
        return this;
    }

    public MailActuator replyTos(String... replyTos) {
        mailParam.setReplyTos(replyTos);
        return this;
    }

    public MailActuator cc(String... cc) {
        mailParam.setCc(cc);
        return this;
    }

    public MailActuator bcc(String... bcc) {
        mailParam.setBcc(bcc);
        return this;
    }

    public void send() throws MessagingException, UnsupportedEncodingException {
        send(mailParam);
    }

    public void send(MailParam param) throws MessagingException, UnsupportedEncodingException {
        //
        MimeMessage message = MailSession.getMessage();
        message.setSubject(param.getSubject(), MailAssist.UTF_8);
        message.setFrom(MailAssist.getFrom(param));
        message.setRecipients(Message.RecipientType.TO, MailAssist.joining(param.getTo()));
        //
        String[] emails = param.getReplyTos();
        if (!MailAssist.isNotEmpty(emails)) {
            emails = new String[] { MailProp.getReplyTo() };
        }
        message.setReplyTo(new InternetAddress[] { MailAssist.transform(emails) });
        //
        emails = param.getCc();
        if (MailAssist.isNotEmpty(emails)) {
            message.setRecipients(Message.RecipientType.CC, MailAssist.joining(emails));
        }
        emails = param.getBcc();
        if (MailAssist.isNotEmpty(emails)) {
            message.setRecipients(Message.RecipientType.BCC, MailAssist.joining(emails));
        }
        message.setContent(param.getMultipart());
        //
        send(message);
        //
    }

    public void send(MimeMessage message) throws MessagingException {
        message.setSentDate(new Date());
        Transport.send(message);
    }

}
