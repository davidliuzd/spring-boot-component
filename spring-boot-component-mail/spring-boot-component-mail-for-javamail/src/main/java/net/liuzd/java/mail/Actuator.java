package net.liuzd.java.mail;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.NoSuchProviderException;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.search.BodyTerm;
import javax.mail.search.SearchTerm;
import javax.mail.search.SentDateTerm;
import javax.mail.search.SubjectTerm;

public class Actuator {

    private SMTPParam smtpParam;
    private POP3Param pop3Param;

    private Actuator() {
        smtpParam = new SMTPParam();
        pop3Param = new POP3Param();
    }

    public static Actuator init() {
        return new Actuator();
    }

    public Actuator attach(File file) {
        return attach(null, file);
    }

    public Actuator attach(String name, File file) {
        Assist.attach(smtpParam, name, file);
        return this;
    }

    public Actuator inlineImage(String contentId, File imageFile) {
        Assist.inlineImage(smtpParam, contentId, imageFile);
        return this;
    }

    public Actuator inlineImage(String contentId, URL url) {
        Assist.inlineImage(smtpParam, contentId, url);
        return this;
    }

    public Actuator attach(URL url) {
        return attach(null, url);
    }

    public Actuator attach(String name, URL url) {
        Assist.attach(smtpParam, name, url);
        return this;
    }

    public Actuator nickName(String nickName) {
        smtpParam.setNickName(nickName);
        return this;
    }

    public Actuator subject(String subject) {
        smtpParam.setSubject(subject);
        return this;
    }

    public Actuator body(String body) {
        Assist.addBody(smtpParam, body);
        return this;
    }

    public Actuator to(String... to) {
        smtpParam.setTo(to);
        return this;
    }

    public Actuator replyTos(String... replyTos) {
        smtpParam.setReplyTos(replyTos);
        return this;
    }

    public Actuator cc(String... cc) {
        smtpParam.setCc(cc);
        return this;
    }

    public Actuator bcc(String... bcc) {
        smtpParam.setBcc(bcc);
        return this;
    }

    public void send() throws MessagingException, UnsupportedEncodingException {
        send(smtpParam);
    }

    public void send(SMTPParam param) throws MessagingException, UnsupportedEncodingException {
        //
        MimeMessage message = Sessions.getMessage();
        message.setSubject(param.getSubject(), Assist.UTF_8);
        message.setFrom(Assist.getFrom(param));
        message.setRecipients(Message.RecipientType.TO, Assist.joining(param.getTo()));
        //
        String[] emails = param.getReplyTos();
        if (!Assist.isNotEmpty(emails)) {
            emails = new String[] { Prop.getReplyTo() };
        }
        message.setReplyTo(new InternetAddress[] { Assist.transform(emails) });
        //
        emails = param.getCc();
        if (Assist.isNotEmpty(emails)) {
            message.setRecipients(Message.RecipientType.CC, Assist.joining(emails));
        }
        emails = param.getBcc();
        if (Assist.isNotEmpty(emails)) {
            message.setRecipients(Message.RecipientType.BCC, Assist.joining(emails));
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

    public Actuator setMailReadWrite() {
        pop3Param.setReadStats(Folder.READ_WRITE);
        return this;
    }

    /**
     * @Title: readMailFrom
     * @Description: 从第多少个开始读取邮件
     * @param msgNum
     * @return MailActuator
     */
    public Actuator readMailFrom(int msgNum) {
        pop3Param.setStartMsgNum(msgNum);
        return this;
    }

    /**
     * @Title: setFilterReadMail
     * @Description: 过滤只读邮件
     * @return MailActuator
     */
    public Actuator setFilterReadMail() {
        pop3Param.setFilterReadMail(true);
        return this;
    }

    /**
     * @Title: setSearchTime
     * @Description: 
     * @param comparisonTerm of ComparisonTerm
     * @param searchTime
     * @return Actuator
     */
    public Actuator setSearchTime(int comparisonTerm, Date searchTime) {
        //ComparisonTerm.EQ
        pop3Param.addSearchTerm(new SentDateTerm(comparisonTerm, searchTime));
        return this;
    }

    /**
     * @Title: isContainsSubject
     * @Description: 搜索指定的标题
     * @param isContainsSubject
     * @return MailActuator
     */
    public Actuator isContainsSubject(String isContainsSubject) {
        pop3Param.addSearchTerm(new SubjectTerm(isContainsSubject));
        return this;
    }

    /**
     * @Title: isContainsSubject
     * @Description: 搜索指定的正文
     * @param isContainsSubject
     * @return MailActuator
     */
    public Actuator isContainsBody(String isContainsBody) {
        pop3Param.addSearchTerm(new BodyTerm(isContainsBody));
        return this;
    }

    public Message[] search(SearchTerm term) throws NoSuchProviderException, MessagingException {
        return getFolder().search(term);
    }

    public Message[] search() throws NoSuchProviderException, MessagingException {
        Integer start = pop3Param.getStartMsgNum();
        if (null == start) {
            return search(pop3Param.getAndTerm());
        }
        int end = getMessageCount();
        if (start > end) {
            return null;
        }
        List<Message> messages = new ArrayList<>();
        Folder folder = getFolder();
        SearchTerm term = pop3Param.getAndTerm();
        for (int i = start; i <= end; i++) {
            //
            MimeMessage message =(MimeMessage)folder.getMessage(i);
            if (null == message || pop3Param.isFilterReadMail() && Assist.isSeen(message)) {
                continue;
            }
            if (!message.match(term)) {
                continue;
            }
            messages.add(message);
            //
        }
        return messages.toArray(new Message[messages.size()]);
    }

    public Folder getFolder() throws NoSuchProviderException, MessagingException {
        return Sessions.getFolder(pop3Param);
    }

    /**
     * @Title: getUnreadMessageCount
     * @Description: 由于POP3协议无法获知邮件的状态,所以getUnreadMessageCount得到的是收件箱的邮件总数
     * @return int
     * @throws MessagingException
     * @throws NoSuchProviderException
     */
    public int getUnreadMessageCount() throws NoSuchProviderException, MessagingException {
        return getFolder().getUnreadMessageCount();
    }

    /**
     * @Title: getDeletedMessageCount
     * @Description: 由于POP3协议无法获知邮件的状态,所以下面得到的结果始终都是为0
     * @return
     * @throws NoSuchProviderException
     * @throws MessagingException int
     */
    public int getDeletedMessageCount() throws NoSuchProviderException, MessagingException {
        return getFolder().getDeletedMessageCount();
    }

    /**
     * @Title: getNewMessageCount
     * @Description: 由于POP3协议无法获知邮件的状态,所以下面得到的结果始终都是为0
     * @return
     * @throws NoSuchProviderException
     * @throws MessagingException int
     */
    public int getNewMessageCount() throws NoSuchProviderException, MessagingException {
        return getFolder().getNewMessageCount();
    }

    /**
     * @Title: getMessageCount
     * @Description: 邮件总数
     * @return
     * @throws NoSuchProviderException
     * @throws MessagingException int
     */
    public int getMessageCount() throws NoSuchProviderException, MessagingException {
        return getFolder().getMessageCount();
    }

}
