package com.bodybuilding.hyper.resource.serializer.html.handlebars;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.Collections;
import java.util.List;

import com.bodybuilding.hyper.resource.serializer.HyperResourceSerializer;

import com.bodybuilding.hyper.resource.HyperResource;
import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.Template;

/**
 * A HyperResourceSerializer that uses JKnack Handlebars to serialize Hyper Resources as HTML.
 */
public class HTMLHandlebarsSerializer implements HyperResourceSerializer {

    private final Handlebars handlebars;

    public HTMLHandlebarsSerializer(Handlebars handlebars) {
        if(handlebars == null){
            throw new IllegalArgumentException("handlebars can not be null");
        }
        this.handlebars = handlebars;
    }


    private static final List<String> MEDIA_TYPES = Collections.unmodifiableList(
        Collections.singletonList(
            "text/html"
        )
    );


    @Override
    public List<String> getContentTypes() {
        return MEDIA_TYPES;
    }

    @Override
    public void write(HyperResource resource, OutputStream output) throws IOException {
        String templateName = resource.getClass().getSimpleName();
        Template template = handlebars.compile(templateName);

        OutputStreamWriter writer = null;
        try {
            writer = new OutputStreamWriter(output);
            template.apply(resource, writer);
        } finally {
            if (writer != null) {
                writer.close();
            }
        }
    }
    
}