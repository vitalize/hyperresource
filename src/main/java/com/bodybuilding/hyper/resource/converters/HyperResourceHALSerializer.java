package com.bodybuilding.hyper.resource.converters;

import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import org.springframework.util.StringUtils;

import com.bodybuilding.hyper.resource.HyperResource;
import com.bodybuilding.hyper.resource.annotation.Rel;
import com.bodybuilding.hyper.resource.controls.Link;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.BeanPropertyWriter;
import com.fasterxml.jackson.databind.ser.impl.BeanAsArraySerializer;
import com.fasterxml.jackson.databind.ser.impl.ObjectIdWriter;
import com.fasterxml.jackson.databind.ser.std.BeanSerializerBase;

/**
 * Serializes {@code HyperResouce} objects into HAL+JSON.
 * 
 * @author Bitwise@bodybuilding.com
 * @since 2015-09-07
 * @see Link
 *
 */
public class HyperResourceHALSerializer extends BeanSerializerBase {

    private static final String _EMBEDDED = "_embedded";

    public HyperResourceHALSerializer(BeanSerializerBase source) {
        super(source);
    }    

    @Override
    public void serialize(Object bean, JsonGenerator gen, SerializerProvider provider) throws IOException {
        //This is stolen from https://github.com/FasterXML/jackson-databind/blob/master/src/main/java/com/fasterxml/jackson/databind/ser/BeanSerializer.java
        if (_objectIdWriter != null) {
            gen.setCurrentValue(bean); // [databind#631]
            _serializeWithObjectId(bean, gen, provider, true);
            return;
        }
        gen.writeStartObject();
        // [databind#631]: Assign current value, to be accessible by custom serializers
        gen.setCurrentValue(bean);
        if (_propertyFilterId != null) {
            serializeFieldsFiltered(bean, gen, provider);
        } else {
            serializeFields(bean, gen, provider);
        }

        this.serializeControls(bean, gen, provider);

        gen.writeEndObject();
    }


    public void serializeControls(Object bean, JsonGenerator jgen, SerializerProvider provider) throws IOException { 

        Stream<BeanPropertyWriter> linksBeanPropertyWriterStream = Arrays.stream(_props)                
                .filter(p -> Link.class.isAssignableFrom(p.getPropertyType())
                        || Link[].class.isAssignableFrom(p.getPropertyType()));

        writeLinks(bean, jgen, linksBeanPropertyWriterStream);

        Stream<BeanPropertyWriter> hyperResourceBeanPropertyWriterStream = Arrays.stream(_props)                
                .filter(p -> HyperResource.class.isAssignableFrom(p.getPropertyType())
                        || HyperResource[].class.isAssignableFrom(p.getPropertyType()));
        writeEmbeddedHyperResources(bean, jgen, hyperResourceBeanPropertyWriterStream, provider);      
        
    }

    private void writeLinks(Object bean, JsonGenerator jgen,
            Stream<BeanPropertyWriter> beanPropertyWriterStream) {        

        // Step 1: Group all links by rel.
        // Maybe this can be simplified with stream collectors???
        Map<String, List<Link>> linksMap = new LinkedHashMap<String, List<Link>>();
        beanPropertyWriterStream.forEach(p -> {
            try {           
                Object o = p.get(bean);
                if(o != null) {
                    if(o instanceof Link) {
                        Link link = (Link) o;                   
                        linksMap.putIfAbsent(link.getRel(), new LinkedList<Link>() );                        
                        linksMap.get(link.getRel()).add(link);
                    } else if(o instanceof Link[]) {
                        Link [] linkArray = (Link[]) o;
                        for(Link l: linkArray) {
                            linksMap.putIfAbsent(l.getRel(), new LinkedList<Link>() );                        
                            linksMap.get(l.getRel()).add(l);
                        }
                    }
                }            
            } catch (Exception e) {
                throw new RuntimeException(e);
            }           
        });            
        
        // Step 2: Serialize all links grouped by rel.
        if(linksMap.size() > 0) {
            try {
                jgen.writeFieldName("_links");
                jgen.writeStartObject();   
            
                linksMap.forEach((k,v) -> {
                    try {                                                 
                        jgen.writeFieldName(k); // Writes rel.
                        boolean writingLinkArray = false;
                        if(v.size() > 1 
                                || k.equalsIgnoreCase("profile")) {
                            jgen.writeStartArray();
                            // To know later array has to be closed.
                            writingLinkArray = true;
                        }
                        v.forEach(l->{                            
                            try {
                                jgen.writeStartObject();                               
                                jgen.writeStringField("href", l.getHref());
                                
                                if(!StringUtils.isEmpty(l.getName())) {
                                    jgen.writeStringField("name", l.getName());
                                }
                                if(!StringUtils.isEmpty(l.getType())) {
                                    jgen.writeStringField("type", l.getType());
                                }
                                jgen.writeEndObject();
                            } catch (Exception e) {
                                throw new RuntimeException(e);
                            }     
                        });
                        if(writingLinkArray) {
                            jgen.writeEndArray();
                        }
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }               
                });                
                jgen.writeEndObject();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }


    private void writeEmbeddedHyperResources(Object bean, JsonGenerator jgen,
            Stream<BeanPropertyWriter> beanPropertyWriterStream,SerializerProvider provider) {
        
        Map<String, List<HyperResource>> resources = new LinkedHashMap<String, List<HyperResource>>();
        
        // 1. Group hyper resources by rel.
        beanPropertyWriterStream.forEach(p -> {
            Object o = null;
            try {
                o = p.get(bean);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }            
            if(o != null) {
                Rel rel = p.getAnnotation(Rel.class);
                // Use rel annotation if present, otherwise use property name.
                String relName = (rel != null ? rel.value() : p.getName());
                resources.putIfAbsent(relName, new LinkedList<HyperResource>());
                
                if(o instanceof HyperResource) {
                    resources.get(relName).add((HyperResource) o);
                } else if(o instanceof HyperResource[]){
                    for(HyperResource resource : (HyperResource[]) o) {
                        resources.get(relName).add(resource);
                    }
                }               
            }
        });
        
        // 2. Write hyper resource recursively.
        if(resources.size() > 0) {
            try {
                jgen.writeFieldName(_EMBEDDED);
                jgen.writeStartObject();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }            
            resources.forEach((k,v) -> {
                try {
                    jgen.writeFieldName(k);                
                
                    if(v.size() > 1) {
                        jgen.writeStartArray();
                    }
                    v.forEach(p->{                  
                        try {
                            /**
                             * "yo, dawg, I herd you like hyper resources, so I put hyper resources in your hyper resource
                             * so you can go full embedded."
                             * 
                             *  This is not final solution.  I know it's nasty. I just left this to be able to test everything else
                             *  and start the discussion.
                             *  I had tried to make a simple recursive call to serialize method, but exception happens
                             *  due to the inconsistency of "_props" array with the new current bean value.
                             *  
                             *  _props is a final array that is setup at construction level of the serializer class (this class). 
                             *  Maybe a solution is to create another method based on "serializeFields" method that allows 
                             *  to pass a _props array. We would have to generate a new BeanPropertyWriter array for every 
                             *  subresource found and pass it to the new method.
                             *  
                             *  Any other suggestions on how to solve this?
                             *  
                             */
                            HALJsonObjectMapperFactory.getInstance().writeValue(jgen, p);                                               
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                    });                
                    if(v.size() > 1) {
                        jgen.writeEndArray();
                    }
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            });
            try {
                jgen.writeEndObject();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }         
        }       
    }
    
    public HyperResourceHALSerializer(HyperResourceHALSerializer source,
                              ObjectIdWriter objectIdWriter) {
        super(source, objectIdWriter);
    }

    public HyperResourceHALSerializer(HyperResourceHALSerializer source,
                              String[] toIgnore) {
        super(source, toIgnore);
    }

    public HyperResourceHALSerializer(HyperResourceHALSerializer source,
                              ObjectIdWriter objectIdWriter,
                              Object filterId) {
        super(source, objectIdWriter, filterId);
    }

    @Override
    public BeanSerializerBase withObjectIdWriter(
            ObjectIdWriter objectIdWriter) {
        return new HyperResourceHALSerializer(this, objectIdWriter);
    }

    @Override
    protected BeanSerializerBase withIgnorals(String[] toIgnore) {
        return new HyperResourceHALSerializer(this, toIgnore);
    }

    @Override
    protected BeanSerializerBase asArraySerializer() {
        /* Can not:
         *
         * - have Object Id (may be allowed in future)
         * - have "any getter"
         * - have per-property filters
         */
        if ((_objectIdWriter == null)
                && (_anyGetterWriter == null)
                && (_propertyFilterId == null)
                ) {
            return new BeanAsArraySerializer(this);
        }
        // already is one, so:
        return this;
    }

    @Override
    public BeanSerializerBase withFilterId(Object filterId) {
        return new HyperResourceHALSerializer(this, _objectIdWriter, filterId);
    }
}
