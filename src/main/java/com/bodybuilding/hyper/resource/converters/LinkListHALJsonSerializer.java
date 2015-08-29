package com.bodybuilding.hyper.resource.converters;

import java.io.IOException;
import java.util.List;

import org.springframework.util.StringUtils;

import com.bodybuilding.hyper.resource.controls.Link;
import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

public class LinkListHALJsonSerializer extends StdSerializer<List<Link>> {
    
    public LinkListHALJsonSerializer() {
        super(List.class, false);
    }

    @Override
    public void serialize(List<Link> links, JsonGenerator jgen,
            SerializerProvider sp) throws IOException,
            JsonGenerationException {
        
        if(links != null && !links.isEmpty()) {
            String rel = links.get(0).getRel();
            
            boolean writingLinkArray = false;
            if(links.size() > 1 
                    || rel.equalsIgnoreCase("profile")) {
                jgen.writeStartArray();
                writingLinkArray = true;
            }
            for(Link link: links) {
                if(!StringUtils.isEmpty(link.getRel()) && !StringUtils.isEmpty(link.getHref())) {                   
                    jgen.writeStartObject();        
                    jgen.writeStringField("href", link.getHref());
                    if(!StringUtils.isEmpty(link.getName())) {
                        jgen.writeStringField("name", link.getName());
                    }
                    if(!StringUtils.isEmpty(link.getType())) {
                        jgen.writeStringField("type", link.getType());
                    }
                    jgen.writeEndObject();
                }   
            }
            if(writingLinkArray) {
                jgen.writeEndArray();
            }
        }
    }

}
