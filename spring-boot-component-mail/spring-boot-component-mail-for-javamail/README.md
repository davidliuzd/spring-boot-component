### Java Mail
#####  参考示例
###### 1. [biezhi/oh-my-email](https://github.com/biezhi/oh-my-email)
###### 2. [journaldev.com](https://www.journaldev.com/2532/javamail-example-send-mail-in-java-smtp)

#### 从这开始吧

##### 1.  编辑：mail.properties文件中相关值
```
mail.smtp.user=
mail.smtp.from=
mail.smtp.password=
```

##### 2.  运行测试用例[TestMailActuator](src\test\java\net\liuzd\java\mail\TestMailActuator.java)


##### 3. 其它参见下方 

###### 邮箱服务器配置
![](src\main\resources\static\images\sina.mail.config.png)

###### 邮箱属性配置明细
###### 1. [smtp](https://javaee.github.io/javamail/docs/api/com/sun/mail/smtp/package-summary.html)
###### 2. [imap](https://javaee.github.io/javamail/docs/api/com/sun/mail/imap/package-summary.html)
###### 3. [pop3](https://javaee.github.io/javamail/docs/api/com/sun/mail/pop3/package-summary.html)

##### 比较与区别
###### 1. [javax.mail vs com.sun.mail](https://stackoverflow.com/questions/22020533/javamail-api-from-maven)
```
So, you should either use com.sun.mail:javax.mail for compilation and packaging/deploy, or use javax.mail:javax.mail-api for compilation and then deploy the com.sun.mail:javax.mail jar where appropriate (e.g., your Tomcat lib).
```
##### 2. [codeday.me](https://codeday.me/bug/20180223/136298.html)
```
> groupId javax.mail不再用于实现。
> javax.mail有一个新的工件：javax.mail-api。它提供了javax.mail-api.jar文件。这仅包含JavaMail API定义，适用于编译。
> com.sun.mail：javax.mail包含javax.mail.jar文件，JavaMail引用实现jar文件，包括SMTP，IMAP和POP3协议提供者。

所以，您应该使用com.sun.mail：javax.mail进行编译和打包/部署，或者使用javax.mail：javax.mail-api进行编译，然后部署com.sun.mail：javax.mail jar适当的(例如，您的Tomcat lib)。
```

#####  [各版本使用情况](https://mvnrepository.com/search?q=javax.mail)
![](src\main\resources\static\images\mail-version-use.png)

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



