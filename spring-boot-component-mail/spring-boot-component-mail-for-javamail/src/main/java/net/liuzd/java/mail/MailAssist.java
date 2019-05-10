package net.liuzd.java.mail;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.util.Arrays;
import java.util.stream.Collectors;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.mail.BodyPart;
import javax.mail.MessagingException;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeUtility;

public class MailAssist {

    public static final String UTF_8 = "UTF-8";

    public static String defStr(String val, String defVal) {
        return isEmpty(val) ? defVal : val;
    }

    public static boolean isNotEmpty(String[] vals) {
        return null != vals && vals.length > 0;
    }

    public static boolean isEmpty(String val) {
        return null == val || val.length() == 0 || val.trim().isEmpty();
    }

    public static boolean isNotEmpty(String val) {
        return !isEmpty(val);
    }

    public static InternetAddress transform(String... recipients) throws AddressException {
        return new InternetAddress(joining(recipients));
    }

    public static String joining(String... recipients) {
        return Arrays.asList(recipients).stream().map(n -> n).collect(Collectors.joining(","));
    }

    public static InternetAddress[] transform(boolean strict, String... recipients) throws AddressException {
        return InternetAddress.parse(joining(recipients), strict);
    }

    public static InternetAddress getFrom(MailParam param) throws UnsupportedEncodingException, AddressException {
        String name = encodeText(defStr(param.getNickName(), MailProp.getNickName()));
        return InternetAddress.parse("\"" + name + "\" <" + MailProp.getFrom() + ">")[0];
    }

    public static BodyPart transform(File file) {
        return transform(null, file);
    }

    public static BodyPart transform(String name, File file) {
        //
        FileDataSource fds = new FileDataSource(file);
        return transform(name, new DataHandler(fds));
    }

    public static String encodeText(String name) throws UnsupportedEncodingException {
        return MimeUtility.encodeText(name, UTF_8, null);
    }

    public static BodyPart transform(URL url) {
        return transform(null, url);
    }

    public static BodyPart transform(String name, URL url) {
        return transform(name, new DataHandler(url));
    }

    public static BodyPart transform(String name, DataHandler dataHandler) {
        //
        BodyPart bodyPart = new MimeBodyPart();
        try {
            bodyPart.setDataHandler(dataHandler);
            bodyPart.setFileName(encodeText(defStr(name, dataHandler.getName())));
        } catch (Exception e) {
            throw new RuntimeException("DataHandler transform BodyPart error ..." + name, e);
        }
        return bodyPart;
    }

    public static void attach(MailParam mailParam, String name, File file) {
        try {
            mailParam.getMultipart().addBodyPart(transform(name, file));
        } catch (MessagingException e) {
            throw new RuntimeException("attach file error ..." + name, e);
        }
    }

    public static void attach(MailParam mailParam, String name, URL url) {
        try {
            mailParam.getMultipart().addBodyPart(transform(name, url));
        } catch (MessagingException e) {
            throw new RuntimeException("attach url error ..." + name, e);
        }
    }

    public static void addBody(MailParam mailParam, String body) {
        try {
            BodyPart messageBodyPart = new MimeBodyPart();
            messageBodyPart.setContent(body, "text/html; charset=utf-8");
            mailParam.getMultipart().addBodyPart(messageBodyPart);
        } catch (MessagingException e) {
            throw new RuntimeException("addBody error ...", e);
        }
    }

    public static BodyPart inlineImage(String contentId, DataHandler dataHandler) throws MessagingException {
        BodyPart imgPart = new MimeBodyPart();
        imgPart.setDataHandler(dataHandler);
        imgPart.setHeader("Content-ID", contentId);
        imgPart.setDisposition(MimeBodyPart.INLINE);
        return imgPart;
    }

    public static void inlineImage(MailParam mailParam, String contentId, File imageFile) {
        try {
            FileDataSource fds = new FileDataSource(imageFile);
            mailParam.getMultipart().addBodyPart(inlineImage(contentId, new DataHandler(fds)));
        } catch (MessagingException e) {
            throw new RuntimeException("inlineImage error ..." + contentId, e);
        }
    }

    public static void inlineImage(MailParam mailParam, String contentId, URL url) {
        try {
            mailParam.getMultipart().addBodyPart(inlineImage(contentId, new DataHandler(url)));
        } catch (MessagingException e) {
            throw new RuntimeException("inlineImage error ..." + contentId, e);
        }
    }

}
