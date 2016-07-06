package com.bodybuilding.hyper.resource.serializer.haljson.jackson;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Collections;
import java.util.List;

import com.bodybuilding.hyper.resource.serializer.HyperResourceSerializer;

import com.bodybuilding.hyper.resource.HyperResource;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * A HyperResourceSerializer that uses jackson to serialize Hyper Resources as HAL+JSON
 */
public class HALJSONJacksonSerializer implements HyperResourceSerializer {


    public HALJSONJacksonSerializer() {

    }

    private static final List<String> MEDIA_TYPES = Collections.unmodifiableList(
        Collections.singletonList(
            "application/hal+json"
        )
    );

    public List<String> getContentTypes(){
        return MEDIA_TYPES;
    }

    private static final ObjectMapper MAPPER = HALJsonObjectMapperFactory.getInstance();

    public void write(HyperResource resource, OutputStream output) throws IOException {
        MAPPER.writeValue(output, resource);
    }


    @Override
    public boolean canWrite(Class<? extends HyperResource> resourceClass) {
        return true;
    }
}
