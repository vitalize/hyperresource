package com.bodybuilding.hyper.resource.converter;

import com.bodybuilding.hyper.resource.HyperResource;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageNotWritableException;

import java.io.IOException;

/**
 * A MessageConverter used to serialize Hyper Resources as HAL+JSON
 */
public class HALJSONMessageConverter extends WriteOnlyHyperResourceMessageConverter {
    public HALJSONMessageConverter() {
        super(new MediaType("application", "hal+json"));
    }


    @Override
    protected void writeInternal(HyperResource resource, HttpOutputMessage httpOutputMessage) throws IOException, HttpMessageNotWritableException {

        String result = "\"This is hal+json representation\"\n" + resource.toString();
        httpOutputMessage.getBody().write(result.getBytes());
    }
}
