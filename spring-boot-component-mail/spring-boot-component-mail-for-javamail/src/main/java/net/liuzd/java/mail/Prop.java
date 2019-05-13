package net.liuzd.java.mail;

import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.util.Properties;

import com.sun.mail.util.MailSSLSocketFactory;

public class Prop {

    private static Properties   P;
    //
    private static final String MAIL_P              = "mail.properties";
    private static final String MAIL_SMTP_HOST      = "mail.smtp.host";
    private static final String MAIL_SMTP_PORT      = "mail.smtp.port";
    private static final String MAIL_USER_ACCOUT    = "mail.smtp.user";
    private static final String MAIL_USER_PWD       = "mail.smtp.password";
    private static final String MAIL_USER_FROM      = "mail.smtp.from";
    private static final String MAIL_SSL_SF         = "mail.smtp.ssl.socketFactory";
    private static final String MAIL_SESSION_DEBUG  = "mail.session.debug";
    private static final String MAIL_STORE_PROTOCOL = "mail.store.protocol";
    private static final String MAIL_POP3_HOST      = "mail.pop3.host";
    private static final String MAIL_POP3_PORT      = "mail.pop3.port";

    //
    public static synchronized Properties get() {
        if (null == P) {
            P = init(Prop.class.getClassLoader().getResourceAsStream(MAIL_P));
            revised(P);
        }
        return P;
    }

    private static void revised(Properties prop) {
        // 属性文件值转换为字符串类型，否则503错误（com.sun.mail.smtp.SMTPSendFailedException:
        // 530 Authentication required）
        prop.forEach((key, value) -> {
            if (!(value instanceof String)) {
                prop.put(key, String.valueOf(value));
            }
        });
        //
        try {
            MailSSLSocketFactory sf = new MailSSLSocketFactory();
            sf.setTrustAllHosts(true);
            prop.put(MAIL_SSL_SF, sf);
        } catch (GeneralSecurityException e) {
            throw new RuntimeException("GeneralSecurityException for MailSSLSocketFactory ...", e);
        }
        //
    }

    public static Properties init(InputStream is) {
        //
        Properties prop = System.getProperties();

        try {

            prop.load(is);

        } catch (IOException e) {

            throw new RuntimeException("no find file  error ...", e);

        }
        //
        return prop;
    }

    public static String getProtocol() {
        return get().getProperty(MAIL_STORE_PROTOCOL);
    }

    public static int getPop3Port() {
        return Integer.parseInt(get().getProperty(MAIL_POP3_PORT));
    }

    public static String getPop3Host() {
        return get().getProperty(MAIL_POP3_HOST);
    }

    public static String getSmtpHost() {
        return get().getProperty(MAIL_SMTP_HOST);
    }

    public static String getSmtpPort() {
        return get().getProperty(MAIL_SMTP_PORT);
    }

    public static void put(String smtpHost, int port) {
        get().replace(MAIL_SMTP_HOST, smtpHost);
        get().replace(MAIL_SMTP_PORT, String.valueOf(port));
    }

    public static String getPassword() {
        return get().getProperty(MAIL_USER_PWD);
    }

    public static String getFrom() {
        return get().getProperty(MAIL_USER_FROM);
    }

    public static String getNickName() {
        return get().getProperty(MAIL_USER_ACCOUT);
    }

    public static String getReplyTo() {
        return "\"" + get().getProperty(MAIL_USER_ACCOUT) + "\" <" + getFrom() + ">";
    }

    public static boolean getSessionDebug() {
        return Boolean.valueOf(get().getProperty(MAIL_SESSION_DEBUG));
    }

}
