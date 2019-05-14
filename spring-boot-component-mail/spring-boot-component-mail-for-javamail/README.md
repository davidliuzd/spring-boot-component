### Java Mail
#####  参考示例
###### [biezhi/oh-my-email](https://github.com/biezhi/oh-my-email)
###### [journaldev.com](https://www.journaldev.com/2532/javamail-example-send-mail-in-java-smtp)

#### 从这开始吧

##### 1.  编辑：mail.properties文件中相关值
```
mail.smtp.user=
mail.smtp.from=
mail.smtp.password=
```

##### 2. pom.xml
```
<dependency>
			<groupId>com.sun.mail</groupId>
			<artifactId>javax.mail</artifactId>
			<!-- <version>1.6.2</version> -->
		</dependency>
```


##### 3. 邮箱服务器配置
![](https://github.com/davidliuzd/spring-boot-component/tree/master/spring-boot-component-mail/spring-boot-component-mail-for-javamail/src/main/resources/static/images\sina.mail.config.png)

###### [smtp](https://javaee.github.io/javamail/docs/api/com/sun/mail/smtp/package-summary.html)
###### [imap](https://javaee.github.io/javamail/docs/api/com/sun/mail/imap/package-summary.html)
###### [pop3](https://javaee.github.io/javamail/docs/api/com/sun/mail/pop3/package-summary.html)

##### 4. 版本比较与区别
###### [javax.mail vs com.sun.mail](https://stackoverflow.com/questions/22020533/javamail-api-from-maven)
```
So, you should either use com.sun.mail:javax.mail for compilation and packaging/deploy, or use javax.mail:javax.mail-api for compilation and then deploy the com.sun.mail:javax.mail jar where appropriate (e.g., your Tomcat lib).
```
##### [codeday.me](https://codeday.me/bug/20180223/136298.html)
```
> groupId javax.mail不再用于实现。
> javax.mail有一个新的工件：javax.mail-api。它提供了javax.mail-api.jar文件。这仅包含JavaMail API定义，适用于编译。
> com.sun.mail：javax.mail包含javax.mail.jar文件，JavaMail引用实现jar文件，包括SMTP，IMAP和POP3协议提供者。

所以，您应该使用com.sun.mail：javax.mail进行编译和打包/部署，或者使用javax.mail：javax.mail-api进行编译，然后部署com.sun.mail：javax.mail jar适当的(例如，您的Tomcat lib)。
```

#####  [各版本使用情况](https://mvnrepository.com/search?q=javax.mail)
![](https://github.com/davidliuzd/spring-boot-component/tree/master/spring-boot-component-mail/spring-boot-component-mail-for-javamail/src/main/resources/static/images\mail-version-use.png)

#####  [Common SMTP port numbers](https://docs.mailpoet.com/article/59-default-ports-numbers-smtp-pop-imap)
```
Find the most common port numbers below. Hosts have a tendency to block some of them.
Contact your host or read their documentation to make sure which ports they use.

Common SMTP ports:
SMTP - port 25 or 2525 or 587
Secure SMTP (SSL / TLS) - port 465 or 25 or 587, 2526 (Elastic Email)
Automate bounce handling (Premium users):
POP3 - port 110
IMAP - port 143
IMAP SSL (IMAPS) - port 993
Elastic Email
Since we use their API, you only need to fill out the following fields:

SMTP Hostname
Login
Password
Amazon SES
Amazon SES requires you to use the SMTP port 25, 465 (with a secure SSL) or 587.MailPoet does not support their API.

SendGrid
You can connect via unencrypted or TLS on ports 25, 2525, and 587. You can connect via SSL on port 465.
Alternatively, you can send with their API ( recommended).
The option to enable the API appears when the SMTP hostname is set to "smtp.sendgrid.net".

Mandrill
You can connect via unencrypted or TLS on ports 25, 2525, and 587. You can connect via SSL on port 465.
MailPoet does not support their API.
```

##### [Pebble模板引擎](https://github.com/PebbleTemplates/pebble) [语法介绍](https://www.bbsmax.com/A/obzbM0xBdE/)


##### 5. 测试用例
```
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

```


