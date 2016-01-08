package com.bodybuilding.hyper.resource.converters;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;

import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.Template;
import com.github.jknack.handlebars.io.ClassPathTemplateLoader;
import com.github.jknack.handlebars.io.TemplateLoader;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageNotWritableException;

import com.bodybuilding.hyper.resource.HyperResource;

/**
 * A MessageConverter used to serialize Hyper Resources as HTML using Handlerbars templates.
 */
public class HandlebarsTemplatedHTMLMessageConverter extends WriteOnlyHyperResourceMessageConverter {

    private Handlebars handlebars;

    public HandlebarsTemplatedHTMLMessageConverter() {
        super(new MediaType("text", "html"));
        TemplateLoader loader = new ClassPathTemplateLoader();
        loader.setPrefix("/templates/handlebars");
        loader.setSuffix(".html");
        handlebars = new Handlebars(loader);
    }
    
    @Override
    protected void writeInternal(HyperResource resource, HttpOutputMessage httpOutputMessage) throws IOException, HttpMessageNotWritableException {
        Writer writer = null;
        try {
            String templateName = resource.getClass().getSimpleName();
            Template template =  handlebars.compile(templateName);
            writer = new OutputStreamWriter(httpOutputMessage.getBody());
            template.apply(resource, writer);
            writer.flush();
            writer.close();    
        } finally {
            if(writer !=null) {
                writer.close();
            }
        }
    }
    
}