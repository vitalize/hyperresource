package com.bodybuilding.hyper.resource.converters;

import com.bodybuilding.hyper.resource.HyperResource;
import com.fasterxml.jackson.databind.ObjectMapper;
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

    private static ObjectMapper mapper = new ObjectMapper();


    @Override
    protected void writeInternal(HyperResource resource, HttpOutputMessage httpOutputMessage) throws IOException, HttpMessageNotWritableException {
        mapper.writeValue(httpOutputMessage.getBody(), resource);
    }
}
