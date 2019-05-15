package net.liuzd.java.mail;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;

import freemarker.core.ParseException;
import freemarker.template.Configuration;
import freemarker.template.DefaultObjectWrapper;
import freemarker.template.MalformedTemplateNameException;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.TemplateExceptionHandler;
import freemarker.template.TemplateNotFoundException;

public class Assist {

    public static final String UTF_8 = "UTF-8";

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

    public static String readFtl(String ftlName, Map<String, Object> model) throws TemplateNotFoundException,
            MalformedTemplateNameException, ParseException, IOException, TemplateException {

        Configuration cfg = new Configuration(Configuration.VERSION_2_3_28);

        // 设定去哪里读取相应的ftl模板

        cfg.setClassForTemplateLoading(Assist.class.getClass(), "/ftl");
        cfg.setDefaultEncoding("UTF-8");
        cfg.setObjectWrapper( new DefaultObjectWrapper(Configuration.VERSION_2_3_28) );
        cfg.setTemplateExceptionHandler(TemplateExceptionHandler.HTML_DEBUG_HANDLER);

        // 在模板文件目录中寻找名称为name的模板文件
        Template template = cfg.getTemplate(ftlName);

        return FreeMarkerTemplateUtils.processTemplateIntoString(template, model);
    }

}
