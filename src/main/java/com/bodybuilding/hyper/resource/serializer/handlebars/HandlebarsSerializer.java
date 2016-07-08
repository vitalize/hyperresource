package com.bodybuilding.hyper.resource.serializer.handlebars;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.*;

import com.bodybuilding.hyper.resource.serializer.HyperResourceSerializer;

import com.bodybuilding.hyper.resource.HyperResource;
import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.Template;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A HyperResourceSerializer that uses JKnack Handlebars to serialize Hyper Resources, usually as HTML.
 */
public class HandlebarsSerializer implements HyperResourceSerializer {
    private static final Logger LOG = LoggerFactory.getLogger(HandlebarsSerializer.class);

    private final Handlebars handlebars;
    private final List<String> contentTypes;

    public HandlebarsSerializer(Handlebars handlebars, String... handledContentTypes) {
        if(handlebars == null){
            throw new IllegalArgumentException("handlebars can not be null");
        }
        this.handlebars = handlebars;

        if(handledContentTypes == null || handledContentTypes.length == 0){
            throw new IllegalArgumentException("handledContentTypes can not be null or empty");
        }
        this.contentTypes = Collections.unmodifiableList(Arrays.asList(handledContentTypes));

    }


    @Override
    public List<String> getContentTypes() {
        return contentTypes;
    }

    //TODO: should these entries have an expiration? if it's in the war it doesn't matter
    //but what if you configure your loader to look in some external path which can change
    private final Set<String> missingTemplateLoaders = new HashSet<>();

    @Override
    public boolean canWrite(Class<? extends HyperResource> resourceClass) {
        String templateName = resourceClass.getSimpleName();
        if(missingTemplateLoaders.contains(templateName)){
            return false;
        }

        try{
            //I don't think this is strictly required as i believe loaders throw vs returning null
            //but i don't want to get bit by a bad behaving loader
            if(handlebars.getLoader().sourceAt(templateName) != null){
                return true;
            }
            LOG.warn("remembering canWrite:false for resource class {} because handlers loader returned null", resourceClass);
        } catch (IOException e){
            LOG.warn("remembering canWrite:false for resource class {} because of exception", resourceClass, e);
        }

        missingTemplateLoaders.add(templateName);
        return false;
    }

    @Override
    public void write(HyperResource resource, OutputStream output) throws IOException {
        String templateName = resource.getClass().getSimpleName();
        Template template = handlebars.compile(templateName);
        OutputStreamWriter writer = new OutputStreamWriter(output);
        template.apply(resource, writer);

        //TODO: is this necessary or even a good idea? OutputStreamWriter
        //uses a string encoder that seems like it would want a flush
        writer.flush();

        //Not closing as it seems we should not close the passed in outputstream
        //for example spring wants to flush the output stream see https://github.com/spring-projects/spring-framework/blob/56db1af11dbe51c88c753421e022bc5389361c04/spring-web/src/main/java/org/springframework/http/converter/AbstractGenericHttpMessageConverter.java#L101
        //after a call to write..so assume that above layers will actually close it
        //though some converters do close on success see https://github.com/spring-projects/spring-framework/blob/56db1af11dbe51c88c753421e022bc5389361c04/spring-web/src/main/java/org/springframework/http/converter/json/GsonHttpMessageConverter.java#L203
        //the StringConverter flushes but doesn't close https://github.com/spring-projects/spring-framework/blob/56db1af11dbe51c88c753421e022bc5389361c04/spring-web/src/main/java/org/springframework/http/converter/StringHttpMessageConverter.java#L107 which calls https://github.com/spring-projects/spring-framework/blob/56db1af11dbe51c88c753421e022bc5389361c04/spring-core/src/main/java/org/springframework/util/StreamUtils.java#L110

        //asked at http://stackoverflow.com/questions/37937583/do-i-need-to-flush-or-close-the-outputstream-in-my-custom-spring-web-messageconv

    }
    
}