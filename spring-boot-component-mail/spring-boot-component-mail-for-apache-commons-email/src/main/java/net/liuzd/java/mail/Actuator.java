package net.liuzd.java.mail;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.activation.FileDataSource;
import javax.mail.internet.MimeMultipart;

import org.apache.commons.mail.DefaultAuthenticator;
import org.apache.commons.mail.EmailAttachment;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.ImageHtmlEmail;
import org.apache.commons.mail.resolver.DataSourceUrlResolver;

/***
 * https://commons.apache.org/proper/commons-email/userguide.html
 */
public class Actuator extends ImageHtmlEmail {

    private static final Actuator ACTUATOR = new Actuator();

    private Actuator() {
        String from = Prop.getFrom();
        try {
            setFrom(from);
        } catch (EmailException e) {
            throw new RuntimeException(e);
        }
        setHostName(Prop.getSmtpHost());
        setSmtpPort(Prop.getSmtpPort());
        setAuthenticator(new DefaultAuthenticator(from, Prop.getPassword()));
        setSSLOnConnect(true);
        setContent(new MimeMultipart());
        updateContentType("text/html; charset=utf-8");
    }

    public static Actuator get() {
        return ACTUATOR;
    }

    private URL toURL(String url) throws MalformedURLException {
        return new URL(url);
    }

    public Actuator subject(String subject) {
        setSubject(subject);
        return this;
    }

    public Actuator attach(File file, String name) throws EmailException {
        return attach(file, null, name);
    }

    public Actuator attach(File file, String description, String name) throws EmailException {
        FileDataSource fds = new FileDataSource(file);
        attach(fds, name, description);
        return this;
    }

    public Actuator attach(String path, String description, String name) throws EmailException {
        EmailAttachment attachment = new EmailAttachment();
        attachment.setPath(path);
        attach(attachment, EmailAttachment.ATTACHMENT, description, name);
        return this;
    }

    private void attach(EmailAttachment attachment, String disposition, String description, String name)
            throws EmailException {
        attachment.setDisposition(disposition);
        attachment.setDescription(description);
        attachment.setName(name);
        super.attach(attachment);
    }

    public Actuator attachURL(String url, String description, String name) throws EmailException,
            MalformedURLException {
        return attachURL(toURL(url), description, name);
    }

    public Actuator attachURL(URL url, String description, String name) throws EmailException, MalformedURLException {
        EmailAttachment attachment = new EmailAttachment();
        attachment.setURL(url);
        attach(attachment, EmailAttachment.ATTACHMENT, description, name);
        return this;
    }

    public Actuator attach(URL url, String name) throws EmailException, MalformedURLException {
        return attachURL(url, null, name);
    }

    /**
     * @Title: setHtmlMsg
     * @Description: 内嵌图片
     * @param htmlMsg 请使用%s顺序占位符
     * @param urlNameMap key=name,val=url
     * @return
     * @throws EmailException Actuator
     * @throws MalformedURLException
     */
    public Actuator setMsg(String htmlMsg, Map<String, String> urlNameMap) throws EmailException,
            MalformedURLException {
        if (null != urlNameMap && urlNameMap.size() > 0) {
            List<String> cids = new ArrayList<>();
            for (Map.Entry<String, String> entry : urlNameMap.entrySet()) {
                String name = entry.getValue();
                URL url = toURL(entry.getKey());
                String cid = embed(url, name);
                cids.add(cid);
            }
            htmlMsg = String.format(htmlMsg, cids.toArray());
        }
        setHtmlMsg(htmlMsg);
        return this;
    }

    public Actuator setMsg(String htmlMsg, String url, String name) throws EmailException, MalformedURLException {
        //
        return setMsg(htmlMsg, new HashMap<String, String>() {

            //
            private static final long serialVersionUID = 1L;

            {
                put(url, name);
            }
        });
    }

    public Actuator setMsg(String htmlMsg, String url) throws MalformedURLException, EmailException {
        if (null != url && url.length() > 0) {
            setDataSourceResolver(new DataSourceUrlResolver(toURL(url)));
        }
        setHtmlMsg(htmlMsg);
        return this;
    }

    public Actuator setMsg(String htmlMsg) throws EmailException {
        setHtmlMsg(htmlMsg);
        return this;
    }

}
