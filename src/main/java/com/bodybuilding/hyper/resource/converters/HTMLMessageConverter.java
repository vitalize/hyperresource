package com.bodybuilding.hyper.resource.converters;

import com.bodybuilding.hyper.resource.HyperResource;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageNotWritableException;

import java.io.IOException;

/**
 * A MessageConverter used to serialize Hyper Resources as HAL+JSON
 */
public class HTMLMessageConverter extends WriteOnlyHyperResourceMessageConverter {
    public HTMLMessageConverter() {
        super(new MediaType("text", "html"));
    }


    @Override
    protected void writeInternal(HyperResource resource, HttpOutputMessage httpOutputMessage) throws IOException, HttpMessageNotWritableException {

        String result = "\"This is html representation\"\n" + resource.toString();
        httpOutputMessage.getBody().write(result.getBytes());
    }
}
