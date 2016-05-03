package com.bodybuilding.hyper.resource.converters;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;

import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;

import com.bodybuilding.hyper.resource.HyperResource;
import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.Helper;
import com.github.jknack.handlebars.Options;
import com.github.jknack.handlebars.Template;
import com.github.jknack.handlebars.io.ClassPathTemplateLoader;
import com.github.jknack.handlebars.io.TemplateLoader;

/**
 * A MessageConverter used to serialize Hyper Resources as HTML using Handlerbars templates.
 */
public class HandlebarsTemplatedHTMLMessageConverter extends WriteOnlyHyperResourceMessageConverter {

    private Handlebars handlebars;
    private final HandlebarsWrapperTemplateLoader wrapperLoader;

    public HandlebarsTemplatedHTMLMessageConverter(HandlebarsWrapperTemplateLoader wrapperLoader) {
        super(new MediaType("text", "html"));
        TemplateLoader loader = new ClassPathTemplateLoader();
        loader.setPrefix("/templates/handlebars");
        loader.setSuffix(".html");
        handlebars = new Handlebars(loader);
        handlebars.registerHelper("contextName", new Helper<Object>() {            
            public CharSequence apply(Object context, Options options)
                    throws IOException {
                return context.getClass().getSimpleName();
            }            
        });                
        this.wrapperLoader = wrapperLoader;    
    }

    @Override
    protected void writeInternal(HyperResource resource, HttpOutputMessage httpOutputMessage) throws IOException {
        Writer writer = null;
        String parentTemplate = wrapperLoader.getParentTemplate();
        try {
            Template template = handlebars.compileInline(parentTemplate);
            writer = new OutputStreamWriter(httpOutputMessage.getBody());
            template.apply(resource, writer);
            writer.flush();
            writer.close();
        } finally {
            if (writer != null) {
                writer.close();
            }
        }
    }

}