package net.liuzd.java.mail;

import javax.mail.Multipart;
import javax.mail.internet.MimeMultipart;

import lombok.Data;

@Data
public class SMTPParam {
    
    /**
     * 发送名称
     */
    private String nickName;    

    /**
     * 邮件标题
     */
    private String    subject;

    /**
     * 发送邮件
     */
    private String[]  to;

    /**
     * 设置回复人(收件人回复此邮件时,默认收件人)
     */
    private String[]  replyTos;

    /**
     * 设置抄送人
     */
    private String[]  cc;

    /**
     * 设置密送人
     */
    private String[]  bcc;

    /**
     * 发送数据
     */
    private Multipart multipart = new MimeMultipart();

}
