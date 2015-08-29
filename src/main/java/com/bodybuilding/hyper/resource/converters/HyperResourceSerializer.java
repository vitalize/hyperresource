package com.bodybuilding.hyper.resource.converters;

import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import com.bodybuilding.hyper.resource.HyperResource;
import com.bodybuilding.hyper.resource.controls.Link;
import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

public class HyperResourceSerializer extends StdSerializer<HyperResource> {	
	
	private static final Logger LOG = LoggerFactory.getLogger(HALJSONMessageConverter.class);

	public HyperResourceSerializer(Class<HyperResource> arg0) {
		super(arg0);
	}

	@Override
	public void serialize(HyperResource hr, JsonGenerator jgen,
			SerializerProvider sp) throws IOException,
			JsonGenerationException {

		Map<String, List<Link>> linksMap = new TreeMap<String, List<Link>>();		
		jgen.writeStartObject();
		try {
			for(PropertyDescriptor pd : 				
			    Introspector.getBeanInfo(hr.getClass(), Object.class).getPropertyDescriptors()) {
				
				Method m = pd.getReadMethod();
				
				if(m.getReturnType().equals(Link.class)) {
					Object o = m.invoke(hr);
					if(o != null) {
						Link link = (Link) o;
						addLinkToMap(linksMap, link);
					}											
				}if(m.getReturnType().equals(Link[].class)) {
					Object o = m.invoke(hr);					
					if(o != null) {
						Link [] links = (Link[]) o;						
						for(Link link: links) {
							addLinkToMap(linksMap, link);
						}
					}					
				}
				else if(m.getReturnType().equals(Integer.TYPE)) {						
					jgen.writeNumberField(pd.getName(), (int) m.invoke(hr));
				} else if(m.getReturnType().equals(String.class)) {						
					jgen.writeStringField(pd.getName(), (String) m.invoke(hr));
				} else if(m.getReturnType().equals(Float.TYPE)) {						
					jgen.writeNumberField(pd.getName(), (float) m.invoke(hr));
				} else if(m.getReturnType().equals(Double.TYPE)) {						
					jgen.writeNumberField(pd.getName(), (double) m.invoke(hr));
				}			 
				
			}
		} catch (Exception e) {
			LOG.error("Error while introspecting hyper resource class", e);
		}
		
		if(!linksMap.isEmpty()) {
			serializeLinks(linksMap, jgen);
		}
		jgen.writeEndObject();
	}

	private void addLinkToMap(Map<String, List<Link>> linksMap, Link link) {
		if(!linksMap.containsKey(link.getRel())) {
			linksMap.put(link.getRel(), new LinkedList<Link>());
		}
		linksMap.get(link.getRel()).add(link);
	}
	
	private void serializeLinks(Map<String, List<Link>> links, JsonGenerator jgen) throws JsonGenerationException, IOException {
		jgen.writeFieldName("_links");
		jgen.writeStartObject();
		for(String rel : links.keySet()) {
			jgen.writeFieldName(rel);
			boolean writingLinkArray = false;
			if(links.get(rel).size() > 1 
					|| rel.equalsIgnoreCase("profile")) {
				jgen.writeStartArray();
				writingLinkArray = true;
			}
			for(Link link: links.get(rel)) {
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
		jgen.writeEndObject();
	}
}
 