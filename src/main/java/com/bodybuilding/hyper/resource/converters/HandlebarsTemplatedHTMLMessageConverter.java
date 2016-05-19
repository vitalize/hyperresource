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
import com.github.jknack.handlebars.io.TemplateSource;
import com.github.jknack.handlebars.io.URLTemplateSource;

/**
 * A MessageConverter used to serialize Hyper Resources as HTML using Handlerbars templates.
 */
public class HandlebarsTemplatedHTMLMessageConverter extends WriteOnlyHyperResourceMessageConverter {

    private Handlebars handlebars;

    public HandlebarsTemplatedHTMLMessageConverter(Handlebars handlebars) {
        super(new MediaType("text", "html"));
        this.handlebars = handlebars;

    }

    @Override
    protected void writeInternal(HyperResource resource, HttpOutputMessage httpOutputMessage) throws IOException {
        Writer writer = null;
        TemplateSource source =   handlebars.getLoader().sourceAt("/wrapper/cart");
        try {
            Template template = handlebars.compile(source);
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