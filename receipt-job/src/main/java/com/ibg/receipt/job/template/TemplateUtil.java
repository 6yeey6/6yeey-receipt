package com.ibg.receipt.job.template;

import java.io.IOException;

import org.apache.commons.io.IOUtils;
import org.springframework.core.io.ClassPathResource;

import com.samskivert.mustache.Mustache;
import com.samskivert.mustache.Template;

public class TemplateUtil {

    public static String replaceTags(final String template, final MessageContext context) throws IOException {
        Template tmpl = Mustache.compiler()
                .compile(IOUtils.toString(new ClassPathResource("templates/" + template).getInputStream(), "UTF-8"));
        return tmpl.execute(context);
    }
}
