package net.liuzd.java.mail;

import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.internet.MimeMessage;

public class MailSession {

    private static Session session;

    public static Session init(String smtpHost, int port, String from, String password, boolean isDebug) {
        if (MailAssist.isNotEmpty(smtpHost) && port > 0) {
            MailProp.put(smtpHost, port);
        }
        return init(MailProp.get(), getAuthenticator(from, password), isDebug);
    }

    public static Session init(String smtpHost, int port, String from, String password) {
        MailProp.put(smtpHost, port);
        return init(MailProp.get(), getAuthenticator(from, password), false);
    }

    public static Session init(Properties prop, Authenticator auth, boolean isDebug) {
        Session session = Session.getDefaultInstance(prop, auth);
        session.setDebug(isDebug);
        return session;
    }

    public static Authenticator getAuthenticator(String from, String password) {
        //
        return new Authenticator() {

            //
            public PasswordAuthentication getPasswordAuthentication() {
                // 发件人邮件用户名、密码
                return new PasswordAuthentication(from, password);
            }
        };
    }

    public static synchronized Session get() {
        if (null == session) {
            session = init(null, -1, MailProp.getFrom(), MailProp.getPassword(), MailProp.getSessionDebug());
        }
        return session;
    }

    public static MimeMessage getMessage() throws MessagingException {
        MimeMessage msg = new MimeMessage(get());
        // set message headers
        msg.addHeader("Content-type", "text/HTML; charset=UTF-8");
        msg.addHeader("format", "flowed");
        msg.addHeader("Content-Transfer-Encoding", "8bit");
        return msg;
    }
}
