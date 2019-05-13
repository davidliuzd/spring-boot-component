package net.liuzd.java.mail;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.stream.Collectors;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.mail.Address;
import javax.mail.BodyPart;
import javax.mail.Flags;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Part;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.internet.MimeUtility;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

public class Assist {

    public static final String  UTF_8   = "UTF-8";
    private static final String PATTERN = "yyyy-MM-dd HH:mm:ss";

    public static String defStr(String val) {
        return defStr(val, "");
    }

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

    public static InternetAddress getFrom(SMTPParam param) throws UnsupportedEncodingException, AddressException {
        String name = encodeText(defStr(param.getNickName(), Prop.getNickName()));
        return InternetAddress.parse("\"" + name + "\" <" + Prop.getFrom() + ">")[0];
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

    public static void attach(SMTPParam mailParam, String name, File file) {
        try {
            mailParam.getMultipart().addBodyPart(transform(name, file));
        } catch (MessagingException e) {
            throw new RuntimeException("attach file error ..." + name, e);
        }
    }

    public static void attach(SMTPParam mailParam, String name, URL url) {
        try {
            mailParam.getMultipart().addBodyPart(transform(name, url));
        } catch (MessagingException e) {
            throw new RuntimeException("attach url error ..." + name, e);
        }
    }

    public static void addBody(SMTPParam mailParam, String body) {
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

    public static void inlineImage(SMTPParam mailParam, String contentId, File imageFile) {
        try {
            FileDataSource fds = new FileDataSource(imageFile);
            mailParam.getMultipart().addBodyPart(inlineImage(contentId, new DataHandler(fds)));
        } catch (MessagingException e) {
            throw new RuntimeException("inlineImage error ..." + contentId, e);
        }
    }

    public static void inlineImage(SMTPParam mailParam, String contentId, URL url) {
        try {
            mailParam.getMultipart().addBodyPart(inlineImage(contentId, new DataHandler(url)));
        } catch (MessagingException e) {
            throw new RuntimeException("inlineImage error ..." + contentId, e);
        }
    }

    public static String decodeText(String encodeText) {
        if (isEmpty(encodeText)) {
            return null;
        }
        try {
            return MimeUtility.decodeText(encodeText);
        } catch (UnsupportedEncodingException e) {

        }
        return null;
    }

    /**
     * 获得邮件标题
     */
    public static String getSubject(MimeMessage msg) throws UnsupportedEncodingException, MessagingException {
        return decodeText(msg.getSubject());
    }

    /**
     * 获得邮件文本内容
     */
    public static void getMailTextBody(Part part, StringBuffer content) throws MessagingException, IOException {
        // 如果是文本类型的附件，通过getContent方法可以取到文本内容，但这不是我们需要的结果，所以在这里要做判断
        boolean isContainTextAttach = part.getContentType().indexOf("name") > 0;
        if (part.isMimeType("text/*") && !isContainTextAttach) {
            content.append(part.getContent().toString());
        } else if (part.isMimeType("message/rfc822")) {
            getMailTextBody((Part) part.getContent(), content);
        } else if (part.isMimeType("multipart/*")) {
            Multipart multipart = (Multipart) part.getContent();
            int partCount = multipart.getCount();
            for (int i = 0; i < partCount; i++) {
                BodyPart bodyPart = multipart.getBodyPart(i);
                getMailTextBody(bodyPart, content);
            }
        }
    }

    /**
     * 获得邮件正文内容
     */
    public static String getBody(Part part) throws MessagingException, IOException {
        StringBuffer content = new StringBuffer();
        getMailTextBody(part, content);
        return content.toString();
    }

    /**
     * 获得邮件发件人
     */
    public static String getFrom(MimeMessage msg) throws MessagingException, UnsupportedEncodingException {
        return joiningAddress(msg.getFrom());
    }

    private static String joiningAddress(Address[] address) {
        if (null == address || address.length == 0) {
            return null;
        }
        return Arrays.asList(address).stream().map(from -> {
            //
            return ((InternetAddress) from).toUnicodeString();
            //
        }).collect(Collectors.joining(", "));
    }

    /**
     * 根据收件人类型，获取邮件收件人、抄送和密送地址。如果收件人类型为空，则获得所有的收件人
     */
    public static String getReceiveAddress(MimeMessage msg, Message.RecipientType type) throws MessagingException {
        //
        return type == null ? joiningAddress(msg.getAllRecipients()) : joiningAddress(msg.getRecipients(type));
    }

    /**
     * 获得邮件发送时间
     */
    public static String getSentDate(MimeMessage msg, String pattern) throws MessagingException {
        Date sentDate = msg.getSentDate();
        if (null == sentDate) {
            return null;
        }
        return new SimpleDateFormat(PATTERN).format(sentDate);
    }

    /**
     * 判断邮件中是否包含附件
     */
    public static boolean isContainAttachment(Part part) throws MessagingException, IOException {
        boolean flag = false;
        if (part.isMimeType("multipart/*")) {
            MimeMultipart multipart = (MimeMultipart) part.getContent();
            int partCount = multipart.getCount();
            for (int i = 0; i < partCount; i++) {
                BodyPart bodyPart = multipart.getBodyPart(i);
                String disp = bodyPart.getDisposition();
                if (disp != null && (disp.equalsIgnoreCase(Part.ATTACHMENT) || disp.equalsIgnoreCase(Part.INLINE))) {
                    flag = true;
                } else if (bodyPart.isMimeType("multipart/*")) {
                    flag = isContainAttachment(bodyPart);
                } else {
                    String contentType = bodyPart.getContentType();
                    if (contentType.indexOf("application") != -1) {
                        flag = true;
                    }
                    if (contentType.indexOf("name") != -1) {
                        flag = true;
                    }
                }

                if (flag) break;
            }
        } else if (part.isMimeType("message/rfc822")) {
            flag = isContainAttachment((Part) part.getContent());
        }
        return flag;
    }

    /**
     * 判断邮件是否已读
     */
    public static boolean isSeen(MimeMessage msg) throws MessagingException {
        return msg.getFlags().contains(Flags.Flag.SEEN);
    }

    /**
     * 判断邮件是否需要阅读回执
     */
    public static boolean isReplySign(MimeMessage msg) throws MessagingException {
        boolean replySign = false;
        String[] headers = msg.getHeader("Disposition-Notification-To");
        if (headers != null) replySign = true;
        return replySign;
    }

    /**
     * 获得邮件的优先级
     * @param msg 邮件内容
     * @return 1(High):紧急 3:普通(Normal) 5:低(Low)
     * @throws MessagingException
     */
    public static String getPriority(MimeMessage msg) throws MessagingException {
        String priority = "普通";
        String[] headers = msg.getHeader("X-Priority");
        if (headers != null) {
            String headerPriority = headers[0];
            if (headerPriority.indexOf("1") != -1 || headerPriority.indexOf("High") != -1)
                priority = "紧急";
            else if (headerPriority.indexOf("5") != -1 || headerPriority.indexOf("Low") != -1)
                priority = "低";
            else
                priority = "普通";
        }
        return priority;
    }

    /**
     * 保存附件
     * @param part 邮件中多个组合体中的其中一个组合体
     * @param destDir 附件保存目录
     * @throws UnsupportedEncodingException
     * @throws MessagingException
     * @throws FileNotFoundException
     * @throws IOException
     */
    public static void saveAttachment(Part part, String destDir) throws UnsupportedEncodingException,
            MessagingException, FileNotFoundException, IOException {
        if (part.isMimeType("multipart/*")) {
            Multipart multipart = (Multipart) part.getContent(); // 复杂体邮件
            // 复杂体邮件包含多个邮件体
            int partCount = multipart.getCount();
            for (int i = 0; i < partCount; i++) {
                // 获得复杂体邮件中其中一个邮件体
                BodyPart bodyPart = multipart.getBodyPart(i);
                // 某一个邮件体也有可能是由多个邮件体组成的复杂体
                String disp = bodyPart.getDisposition();
                if (disp != null && (disp.equalsIgnoreCase(Part.ATTACHMENT) || disp.equalsIgnoreCase(Part.INLINE))) {
                    InputStream is = bodyPart.getInputStream();
                    toFile(is, destDir, decodeText(bodyPart.getFileName()));
                } else if (bodyPart.isMimeType("multipart/*")) {
                    saveAttachment(bodyPart, destDir);
                } else {
                    String contentType = bodyPart.getContentType();
                    if (contentType.indexOf("name") != -1 || contentType.indexOf("application") != -1) {
                        toFile(bodyPart.getInputStream(), destDir, decodeText(bodyPart.getFileName()));
                    }
                }
            }
        } else if (part.isMimeType("message/rfc822")) {
            saveAttachment((Part) part.getContent(), destDir);
        }
    }

    /**
     * 读取输入流中的数据保存至指定目录
     */
    public static void toFile(InputStream is, String destDir, String fileName) throws FileNotFoundException,
            IOException {
        FileUtils.copyInputStreamToFile(is, new File(destDir + fileName));
        // == copy
    }

    public static File copy(InputStream is, File file) throws FileNotFoundException, IOException {
        if (!file.exists()) {
            file.createNewFile();
        }
        try (OutputStream outputStream = new FileOutputStream(file)) {
            IOUtils.copy(is, outputStream);
        } catch (FileNotFoundException e) {
            // handle exception here
        } catch (IOException e) {
            // handle exception here
        }
        return file;
    }

}
