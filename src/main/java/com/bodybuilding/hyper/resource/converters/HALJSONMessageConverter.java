package com.bodybuilding.hyper.resource.converters;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageNotWritableException;

import com.bodybuilding.hyper.resource.HyperResource;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;

/**
 * A MessageConverter used to serialize Hyper Resources as HAL+JSON
 */
public class HALJSONMessageConverter extends WriteOnlyHyperResourceMessageConverter {
	
	 private static final Logger LOG = LoggerFactory.getLogger(HALJSONMessageConverter.class);
	
    public HALJSONMessageConverter() {
        super(new MediaType("application", "hal+json"));
    }

    private static ObjectMapper mapper = new ObjectMapper();
    
    static {    	
    	SimpleModule simpleModule = new SimpleModule("SimpleModule");
    	simpleModule.addSerializer(new LinkListHALJsonSerializer());
    	mapper.registerModule(simpleModule);
    }


    @Override
    protected void writeInternal(HyperResource resource, HttpOutputMessage httpOutputMessage) throws IOException, HttpMessageNotWritableException {
    	mapper.writeValue(httpOutputMessage.getBody(), HyperResourceToMap.convert(resource));
        //System.out.println(mapper.writeValueAsString(HyperResourceToMap.convert(resource)));
    }
}
