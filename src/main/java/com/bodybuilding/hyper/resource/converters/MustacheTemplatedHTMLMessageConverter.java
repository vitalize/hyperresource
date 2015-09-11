package com.bodybuilding.hyper.resource.converters;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;

import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageNotWritableException;

import com.bodybuilding.hyper.resource.HyperResource;
import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.Mustache;
import com.github.mustachejava.MustacheFactory;
import com.github.mustachejava.MustacheResolver;
import com.github.mustachejava.resolver.DefaultResolver;

/**
 * A MessageConverter used to serialize Hyper Resources as HTML using Mustache templates.
 */
public class MustacheTemplatedHTMLMessageConverter extends WriteOnlyHyperResourceMessageConverter {
	
	private final MustacheFactory mustacheFactory;
	
    public MustacheTemplatedHTMLMessageConverter() {
        super(new MediaType("text", "html"));
        MustacheResolver mustacheResolver = new DefaultResolver("templates/mustache/");
        this.mustacheFactory = new DefaultMustacheFactory(mustacheResolver);
    }
    
    @Override
    protected void writeInternal(HyperResource resource, HttpOutputMessage httpOutputMessage) throws IOException, HttpMessageNotWritableException {
        Writer writer = null;
        try {
            String templateName = resource.getClass().getSimpleName();
            Mustache mustache = mustacheFactory.compile(templateName);
            writer = new OutputStreamWriter(httpOutputMessage.getBody());
            mustache.execute(writer, resource);
            writer.flush();
            writer.close();    
        } finally {
            if(writer !=null) {
                writer.close();
            }
        }
    }
    
}