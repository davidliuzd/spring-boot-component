package net.liuzd.java.mail;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.io.Writer;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.junit.Assert;
import org.junit.Test;

import com.mitchellbosecke.pebble.PebbleEngine;
import com.mitchellbosecke.pebble.error.PebbleException;
import com.mitchellbosecke.pebble.template.PebbleTemplate;

public class TestMailActuator {

    static String to = "";

    @Test
    public void sendText() throws Exception {
        Actuator.init().subject("文本邮件").body("您好！这是纯文本邮件哟！").to(to).send();
        Assert.assertTrue(true);
    }

    @Test
    public void sendHtml() throws Exception {
        Actuator.init().subject("HTML邮件").body("<h1>您好！</h1>这是Html邮件，来个<font color='red'>红色</font>哟！").to(to).send();
        Assert.assertTrue(true);
    }

    @Test
    public void sendFile() throws Exception {
        Actuator.init().attach("我的", toFile()).subject("HTML邮件").body(
                "<h1>您好！</h1>这是Html邮件，来个<font color='red'>红色</font>哟！").to(to).send();
        Assert.assertTrue(true);
    }

    private File toFile() throws IOException {
        InputStream inputStream = TestMailActuator.class.getClassLoader().getResourceAsStream(
                "\\static\\images\\demo.png");
        File file = new File("src\\test\\resources\\static\\images\\demo_tmp.png");
        //
        return Assist.copy(inputStream, file);
    }

    @Test
    public void sendUrl() throws Exception {
        Actuator.init().attach("我的图片", toURL()).subject("HTML邮件").body(
                "<h1>您好！</h1>这是Html邮件，来个<font color='red'>红色</font>哟！").to(to).send();
        Assert.assertTrue(true);
    }

    private URL toURL() throws MalformedURLException {
        return new URL("https://avatars1.githubusercontent.com/u/2784452?s=40&v=4");
    }

    @Test
    public void send() throws Exception {
        Actuator.init().nickName("测试").attach("test.png", toFile()).subject("HTML邮件").body(
                "<h1>您好！</h1>这是Html邮件，来个<font color='red'>红色</font>哟！").to(to).send();

        Assert.assertTrue(true);
    }

    @Test
    public void testPebble() throws IOException, PebbleException, MessagingException {
        PebbleEngine engine = new PebbleEngine.Builder().build();
        // html,pebble 都可以
        PebbleTemplate compiledTemplate = engine.getTemplate("template\\users.pebble");
        Map<String, Object> context = new HashMap<String, Object>();
        context.put("username", "david");
        context.put("set", new Random().nextInt(2));
        List<User> users = new ArrayList<>();
        User user = new User();
        user.setName("张三");
        users.add(user);
        //
        user = new User();
        user.setName("李四");
        users.add(user);
        context.put("users", users);
        Writer writer = new StringWriter();
        compiledTemplate.evaluate(writer, context);
        String output = writer.toString();
        //
        Actuator.init().nickName("测试").inlineImage("myImgFile", toFile()).inlineImage("myImgUrl", toURL()).subject(
                "HTML邮件").body(output).to(to).send();
        //
        Assert.assertTrue(true);
    }

    /**
     * 属性值必写get,set，否则读不出来
     */
    class User {

        private String name;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

    @Test
    public void mesageCounts() throws Exception {
        int unreadMessageCount = Actuator.init().getUnreadMessageCount();
        int deletedMessageCount = Actuator.init().getDeletedMessageCount();
        int newMessageCount = Actuator.init().getNewMessageCount();
        int messageCount = Actuator.init().getMessageCount();
        System.out.println(String.format("unreadMessageCount : %d ,deletedMessageCount : %d,newMessageCount : %d , "
                + "messageCount : %d", unreadMessageCount, deletedMessageCount, newMessageCount, messageCount));
        Assert.assertTrue(true);
    }

    @Test
    public void mesages() throws Exception {
        Message[] messages = Actuator.init().readMailFrom(90).search();
        for (Message message : messages) {
            MimeMessage msg = (MimeMessage) message;
            System.out.println("解析第" + msg.getMessageNumber() + "封邮件");
            System.out.println("主题: " + Assist.getSubject(msg));
            System.out.println("发件人: " + Assist.getFrom(msg));
            System.out.println("收件人：" + Assist.getReceiveAddress(msg, null));
            System.out.println("发送时间：" + Assist.getSentDate(msg, null));
            System.out.println("是否已读：" + Assist.isSeen(msg));
            System.out.println("邮件优先级：" + Assist.getPriority(msg));
            System.out.println("是否需要回执：" + Assist.isReplySign(msg));
            System.out.println("大小：" + msg.getSize() * 1024 + "kb");
            System.out.println("类型：" + msg.getContentType());
        }
        Assert.assertTrue(true);
    }

}
