### Apache Commons Email
#####  参考示例
###### [userguide](https://commons.apache.org/proper/commons-email/userguide.html)

##### 1. pom.xml
```
<dependency>
			<groupId>org.apache.commons</groupId>
			<artifactId>commons-email</artifactId>
			<version>1.5</version>
		</dependency>
```

##### 2. 编辑：mail.properties文件中相关值
```
mail.smtp.user=
mail.smtp.from=
mail.smtp.password=
```


##### 3. 邮箱属性配置明细
###### [smtp](https://javaee.github.io/javamail/docs/api/com/sun/mail/smtp/package-summary.html)
###### [imap](https://javaee.github.io/javamail/docs/api/com/sun/mail/imap/package-summary.html)
###### [pop3](https://javaee.github.io/javamail/docs/api/com/sun/mail/pop3/package-summary.html)

##### 4. 邮箱乱码设置
```
 updateContentType("text/html; charset=utf-8");
```

##### 4. 测试用例[TestMailActuator](src\test\java\net\liuzd\java\mail\TestMailActuator.java)
```
package net.liuzd.java.mail;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.commons.io.IOUtils;
import org.apache.commons.mail.EmailException;
import org.junit.Assert;
import org.junit.Test;

public class TestMailActuator {

    static String to = "davidliuzd@sina.com";

    private File toFile() throws IOException {
        InputStream is = TestMailActuator.class.getClassLoader().getResourceAsStream("\\static\\images\\demo.png");
        File file = new File("src\\test\\resources\\static\\images\\demo_tmp.png");
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

    private URL toURL() throws MalformedURLException {
        return new URL("https://avatars1.githubusercontent.com/u/2784452?s=40&v=4");
    }

    @Test
    public void simpleEmail() throws EmailException {
        Actuator.get().subject("文本邮件").setMsg("您好！这是纯文本邮件哟！").addTo(to).send();
        Assert.assertTrue(true);
    }

    @Test
    public void attacheFileEmail() throws EmailException, IOException {
        Actuator.get().subject("Html邮件+附件").attach(toFile(),"我的文件").setMsg("</h1>这是Html邮件，来个<font color='red'>红色</font>").addTo(to).send();
        Assert.assertTrue(true);
    }
    
    @Test
    public void attacheUrlEmail() throws EmailException, IOException {
        Actuator.get().subject("Html邮件+URl附件").attach(toURL(),"我的文件").setMsg("</h1>这是Html邮件，来个<font color='red'>红色</font>").addTo(to).send();
        Assert.assertTrue(true);
    }
    
    @Test
    public void attacheImgEmail() throws EmailException, IOException {
        String htmlMsg = "<html></h1>这是Html邮件，来个<font color='red'>红色</font> The apache logo - <img src=cid:%s></html>";        
        String url = "https://www.apache.org/images/asf_logo_wide.gif";
        Actuator.get().subject("Html邮件+图片附件").setMsg(htmlMsg, url, "apache logo").addTo(to).send();
        Assert.assertTrue(true);
    }
    
    @Test
    public void urlImgEmail() throws EmailException, IOException {
        String htmlMsg = "<html><h1>您好！</h1>这是Html邮件，来个<font color='red'>红色</font><img src='http://www.apache.org/images/feather.gif'></html>";        
        String url ="http://www.apache.org";
        Actuator.get().subject("Html邮件+图片URL").setMsg(htmlMsg, url).addTo(to).send();
        Assert.assertTrue(true);
    }


}

```
