package com.bodybuilding.hyper.resource.converters;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageNotWritableException;

import com.bodybuilding.hyper.resource.HyperResource;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * A MessageConverter used to serialize Hyper Resources as HAL+JSON
 */
public class HALJSONMessageConverter extends WriteOnlyHyperResourceMessageConverter {

    private static final Logger LOG = LoggerFactory.getLogger(HALJSONMessageConverter.class);

    public HALJSONMessageConverter() {
        super(new MediaType("application", "hal+json"));
    }

    private static ObjectMapper mapper = HALJsonObjectMapperFactory.getInstance();

    @Override
    protected void writeInternal(HyperResource resource, HttpOutputMessage httpOutputMessage) throws IOException {
        mapper.writeValue(httpOutputMessage.getBody(), resource);
        // System.out.println(mapper.writeValueAsString(resource));
    }
}
