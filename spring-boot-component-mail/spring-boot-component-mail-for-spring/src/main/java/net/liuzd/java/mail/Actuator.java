package net.liuzd.java.mail;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;

import javax.activation.FileDataSource;
import javax.activation.URLDataSource;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;

public class Actuator extends MimeMessageHelper {

    private static String             from;

    private static String             personal;

    private static JavaMailSenderImpl mailSender;

    public static void set(JavaMailSenderImpl _mailSender, String _from, String _personal) {
        mailSender = _mailSender;
        from = _from;
        personal = _personal;
    }

    public Actuator(MimeMessage mimeMessage) throws MessagingException {
        super(mimeMessage, true, "utf-8");
    }

    public static Actuator init() throws MessagingException, UnsupportedEncodingException {
        return new Actuator(mailSender.createMimeMessage());
    }

    public Actuator subject(String subject) throws MessagingException {
        setSubject(subject);
        return this;
    }

    public Actuator body(String body) throws MessagingException {
        setText(body, true);
        return this;
    }

    public Actuator to(String... to) throws MessagingException {
        setTo(to);
        return this;
    }

    public Actuator replyTo(String replyTo) throws MessagingException {
        setReplyTo(replyTo);
        return this;
    }

    public Actuator cc(String... cc) throws MessagingException {
        setCc(cc);
        return this;
    }

    public Actuator bcc(String... bcc) throws MessagingException {
        setBcc(bcc);
        return this;
    }

    public Actuator attach(File file) throws MessagingException {
        return attach(null, file);
    }

    public Actuator attach(String name, File file) throws MessagingException {
        addAttachment(Assist.defStr(name, file.getName()), new FileDataSource(file));
        return this;
    }

    public Actuator attach(String name, String url) throws MessagingException, MalformedURLException {
        attach(name, new URL(url));
        return this;
    }

    public Actuator attach(String name, URL url) throws MessagingException, MalformedURLException {
        addAttachment(name, new URLDataSource(url));
        return this;
    }

    public Actuator addInlines(String contentId, File file) throws MessagingException {
        addInline(contentId, file);
        return this;
    }

    public Actuator addInlines(String contentId, String url) throws MessagingException, MalformedURLException {
        addInline(contentId, new URLDataSource(new URL(url)));
        return this;
    }

    public void send() throws MessagingException, UnsupportedEncodingException {
        setFrom(from, personal);
        setSentDate(new Date());
        send(getMimeMessage());
    }

    public void send(MimeMessage... mimeMessages) {
        mailSender.send(mimeMessages);
    }

}
