package com.bodybuilding.hyper.resource.serializer.haljson.jackson;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Collections;
import java.util.List;

import com.bodybuilding.hyper.resource.controls.Link;
import com.bodybuilding.hyper.resource.controls.TemplatedAction;
import com.bodybuilding.hyper.resource.serializer.HyperResourceSerializer;

import com.bodybuilding.hyper.resource.HyperResource;
import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationConfig;
import com.fasterxml.jackson.databind.introspect.Annotated;
import com.fasterxml.jackson.databind.introspect.JacksonAnnotationIntrospector;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.BeanPropertyWriter;
import com.fasterxml.jackson.databind.ser.BeanSerializer;
import com.fasterxml.jackson.databind.ser.BeanSerializerModifier;
import com.fasterxml.jackson.databind.ser.PropertyWriter;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;

/**
 * A HyperResourceSerializer that uses jackson to serialize Hyper Resources as HAL+JSON
 */
public class HALJSONJacksonSerializer implements HyperResourceSerializer {

    private static final String HYPER_RESOURCE_FILTER_ID = "hyper_resource";

    /**
     * An ObjectMapper to suit the specific needs of serializing an
     * hyper resource into HAL+JSON.
     * <p>
     * <p>Various steps are required to be able to serialize
     * {@code HyperResource} objects (that have hyper controls like {@code Link}s),
     * without changing the standard bean serialization provided by Jackson default
     * serializer.</p>
     * <ul>
     * <li>1.A {code JacksonAnnotationIntrospector} is configured to {@code ObjectMapper}
     * to identify the "hyper_resource" filter every time a {@code HyperResource} instance is found.</li>
     * <li>2.The hyper_resource filter is setup to exclude hyper controls from default bean serialization.</li>
     * <li>3.{@code BeanSerializerModifier} is configured in {@code ObjectMapper} to add custom serialization
     * to {@code HyperResouce} objects.</li>
     * </ul>
     *
     * @see HyperResource
     * @see HyperResourceHALSerializer
     * @see Link
     */
    private static final ObjectMapper MAPPER = new ObjectMapper()
        .setAnnotationIntrospector(new JacksonAnnotationIntrospector() {
            private static final long serialVersionUID = -5698257819286739964L;

            @Override
            public Object findFilterId(Annotated a) {
                //if it's an hyper resource, return the hyper_resource filter id.
                if (HyperResource.class.isAssignableFrom(a.getRawType())) {
                    return HYPER_RESOURCE_FILTER_ID;
                }
                return super.findFilterId(a);
            }
        })
        .setFilterProvider(
            new SimpleFilterProvider().addFilter(
                HYPER_RESOURCE_FILTER_ID,
                new SimpleBeanPropertyFilter() {

                    @Override
                    protected boolean include(PropertyWriter writer) {
                        //Don't include anything that is a Hyper Control.
                        if (writer instanceof BeanPropertyWriter) {
                            BeanPropertyWriter beanWriter = (BeanPropertyWriter) writer;
                            if (TemplatedAction.class.isAssignableFrom(beanWriter.getPropertyType())) {
                                return false;
                            }

                            if (Link.class.isAssignableFrom(beanWriter.getPropertyType())) {
                                return false;
                            }

                            if (Link[].class.isAssignableFrom(beanWriter.getPropertyType())) {
                                return false;
                            }
                            if (HyperResource.class.isAssignableFrom(beanWriter.getPropertyType())) {
                                return false;
                            }
                            if (HyperResource[].class.isAssignableFrom(beanWriter.getPropertyType())) {
                                return false;
                            }
                        }
                        return super.include(writer);
                    }
                }
        ))
        .registerModule(
            new SimpleModule("HALJsonModule")
                .setSerializerModifier(new BeanSerializerModifier() {
                    @Override
                    public JsonSerializer<?> modifySerializer(
                        SerializationConfig config,
                        BeanDescription beanDesc,
                        JsonSerializer<?> serializer
                    ) {
                        if (HyperResource.class.isAssignableFrom(beanDesc.getBeanClass())) {
                            return new HyperResourceHALSerializer((BeanSerializer) serializer);
                        }
                        return serializer;
                    }

                })
        )
        ;


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



    public void write(HyperResource resource, OutputStream output) throws IOException {
        MAPPER.writeValue(output, resource);
    }


    @Override
    public boolean canWrite(Class<? extends HyperResource> resourceClass) {
        return true;
    }
}
