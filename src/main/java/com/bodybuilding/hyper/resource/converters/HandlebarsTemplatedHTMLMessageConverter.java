package com.bodybuilding.hyper.resource.converters;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;

import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;

import com.bodybuilding.hyper.resource.HyperResource;
import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.Template;
import com.github.jknack.handlebars.io.TemplateSource;

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
        String templateName = resource.getClass().getSimpleName();
        TemplateSource source = handlebars.getLoader().sourceAt(templateName);
        Template template = handlebars.compile(source);

        Writer writer = null;
        try {
            writer = new OutputStreamWriter(httpOutputMessage.getBody());
            template.apply(resource, writer);
            writer.flush();
        } finally {
            if (writer != null) {
                writer.close();
            }
        }
    }
    
}