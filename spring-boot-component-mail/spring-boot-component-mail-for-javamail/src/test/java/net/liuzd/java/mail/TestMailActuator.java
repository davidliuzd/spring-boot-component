package net.liuzd.java.mail;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringWriter;
import java.io.Writer;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.apache.commons.io.IOUtils;
import org.junit.Assert;
import org.junit.Test;

import com.mitchellbosecke.pebble.PebbleEngine;
import com.mitchellbosecke.pebble.error.PebbleException;
import com.mitchellbosecke.pebble.template.PebbleTemplate;

public class TestMailActuator {

    static String to = "you for mail";

    @Test
    public void sendText() throws Exception {
        MailActuator.init().subject("文本邮件").body("您好！这是纯文本邮件哟！").to(to).send();
        Assert.assertTrue(true);
    }

    @Test
    public void sendHtml() throws Exception {
        MailActuator.init().subject("HTML邮件").body("<h1>您好！</h1>这是Html邮件，来个<font color='red'>红色</font>哟！").to(to)
                .send();
        Assert.assertTrue(true);
    }

    @Test
    public void sendFile() throws Exception {
        MailActuator.init().attach("我的", toFile()).subject("HTML邮件").body(
                "<h1>您好！</h1>这是Html邮件，来个<font color='red'>红色</font>哟！").to(to).send();
        Assert.assertTrue(true);
    }

    private File toFile() throws IOException {
        InputStream inputStream = TestMailActuator.class.getClassLoader().getResourceAsStream("\\static\\images\\demo.png");
        File file = new File("src\\test\\resources\\static\\images\\demo_tmp.png");
        file.createNewFile();
        try (OutputStream outputStream = new FileOutputStream(file)) {
            IOUtils.copy(inputStream, outputStream);
        } catch (FileNotFoundException e) {
            // handle exception here
        } catch (IOException e) {
            // handle exception here
        }
        return file;
    }

    @Test
    public void sendUrl() throws Exception {
        MailActuator.init().attach("我的图片", toURL()).subject("HTML邮件").body(
                "<h1>您好！</h1>这是Html邮件，来个<font color='red'>红色</font>哟！").to(to).send();
        Assert.assertTrue(true);
    }

    private URL toURL() throws MalformedURLException {
        return new URL("https://avatars1.githubusercontent.com/u/2784452?s=40&v=4");
    }

    @Test
    public void send() throws Exception {
        MailActuator.init().nickName("测试").attach("test", toFile()).subject("HTML邮件").body(
                "<h1>您好！</h1>这是Html邮件，来个<font color='red'>红色</font>哟！").to(to).send();;

        Assert.assertTrue(true);
    }

    @Test
    public void testPebble() throws IOException, PebbleException {
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
        System.out.println(output);
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

}
