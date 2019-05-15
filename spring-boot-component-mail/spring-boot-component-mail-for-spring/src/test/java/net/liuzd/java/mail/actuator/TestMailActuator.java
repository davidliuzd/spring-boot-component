package net.liuzd.java.mail.actuator;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.mail.MessagingException;

import org.junit.Assert;
import org.junit.Test;

import freemarker.core.ParseException;
import freemarker.template.MalformedTemplateNameException;
import freemarker.template.TemplateException;
import freemarker.template.TemplateNotFoundException;
import net.liuzd.java.mail.Actuator;
import net.liuzd.java.mail.Assist;
import net.liuzd.java.mail.User;
import net.liuzd.java.mail.base.BaseJunit4Test;

public class TestMailActuator extends BaseJunit4Test {

    String to = "";

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
        Actuator.init().attach("我的.png", toFile()).subject("HTML邮件").body(
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
        Actuator.init().attach("test.png", toFile()).subject("HTML邮件").body(
                "<h1>您好！</h1>这是Html邮件，来个<font color='red'>红色</font>哟！").to(to).send();

        Assert.assertTrue(true);
    }

    @Test
    public void urlImgEmail() throws UnsupportedEncodingException, MalformedURLException, MessagingException {
        String htmlMsg = "<html><h1>您好！</h1>这是Html邮件，来个<font color='red'>红色</font><img src='http://www.apache.org/images/feather.gif'><br/> The apache logo - <img src=cid:myCid></html>";
        Actuator.init().subject("Html邮件+图片URL").body(htmlMsg).attach("Apache Logo",
                "https://www.apache.org/images/asf_logo_wide.gif").addInlines("myCid",
                        "https://www.apache.org/images/asf_logo_wide.gif").to(to).send();
        Assert.assertTrue(true);
    }

    @Test
    public void readTemplate() throws MessagingException, TemplateNotFoundException, MalformedTemplateNameException,
            ParseException, IOException, TemplateException {
        Map<String, Object> model = new HashMap<>();
        List<String> list = new ArrayList<String>();
        list.add("Jack1");
        list.add("Jack2");
        list.add("Jack3");
        model.put("list", list);
        // Freemarker不显示对象的属性值的原因
        // 属性没有getter方法。这个比较奇葩，估计是freemarker显示属性调用的是getter方法，虽然写的是属性名称。
        // 对象是内部类的对象。这个真的让我花了几个小时，没有任何错误提示，坑人呀。最后变成普通的类就可以了
        model.put("user", new User("may", 21));
        String htmlMsg = Assist.readFtl("user.ftl", model);
        System.out.println(htmlMsg);
        Assert.assertTrue(true);
    }

}
