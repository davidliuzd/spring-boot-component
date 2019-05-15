package net.liuzd.java.mail.config;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.stereotype.Component;

import net.liuzd.java.mail.Actuator;

@Component
public class Apps implements ApplicationContextAware {

    private static ApplicationContext applicationContext;

    @Value("${mail.smtp.from}")
    private String                    from;

    @Value("${mail.smtp.user}")
    private String                    personal;
    
    @Autowired
    private JavaMailSenderImpl mailSender;

    @Override
    public void setApplicationContext(ApplicationContext context) throws BeansException {
        Apps.applicationContext = context;
        //Actuator.set(getBean(JavaMailSenderImpl.class), from, personal);
        Actuator.set(mailSender, from, personal);
    }

    public static ApplicationContext getApplicationContext() {
        assertApplicationContext();
        return applicationContext;
    }

    @SuppressWarnings("unchecked")
    public static <T> T getBean(String beanName) {
        assertApplicationContext();
        return (T) applicationContext.getBean(beanName);
    }

    public static <T> T getBean(Class<T> requiredType) {
        assertApplicationContext();
        return applicationContext.getBean(requiredType);
    }

    private static void assertApplicationContext() {
        if (Apps.applicationContext == null) {
            throw new RuntimeException("applicaitonContext属性为null,请检查是否注入了Apps!");
        }
    }

}
