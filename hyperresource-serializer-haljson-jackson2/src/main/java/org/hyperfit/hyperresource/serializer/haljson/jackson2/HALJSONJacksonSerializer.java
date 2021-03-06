package org.hyperfit.hyperresource.serializer.haljson.jackson2;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import com.fasterxml.jackson.databind.*;
import org.hyperfit.hyperresource.controls.Link;
import org.hyperfit.hyperresource.controls.TemplatedAction;
import org.hyperfit.hyperresource.serializer.HyperResourceSerializer;

import org.hyperfit.hyperresource.HyperResource;
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
 * A HyperResourceSerializer that uses jackson2 to serialize Hyper Resources as HAL+JSON
 */
public class HALJSONJacksonSerializer implements HyperResourceSerializer {

    private static final String HYPER_RESOURCE_FILTER_ID = "org.hyperfit.hyperresource";

    //TOOD: this should be configurable based on the constructor of the HALJSONJacksonSerializer
    //so people could register their own controls maybe.
    private static final Class<?>[] HYPER_CONTROL_CLASSES = new Class[]{
        TemplatedAction.class,
        Link.class,
        Link[].class,
        HyperResource.class,
        HyperResource[].class
    };

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
     * to {@code HyperResource} objects.</li>
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
                            JavaType propertyType = writer.getType();

                            for(Class<?> controlClass : HYPER_CONTROL_CLASSES) {

                                if (propertyType.isTypeOrSubTypeOf(controlClass)) {
                                    return false;
                                }
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

    public static final String CONTENT_TYPE_HAL_JSON = "application/hal+json";

    private static final List<String> CONTENT_TYPES = Collections.unmodifiableList(
        Collections.singletonList(
            CONTENT_TYPE_HAL_JSON
        )
    );

    public List<String> getContentTypes(){
        return CONTENT_TYPES;
    }



    public void write(
        HyperResource resource,
        Locale locale,
        OutputStream output
    ) throws IOException {
        MAPPER.writeValue(output, resource);
    }

    public String writeToString(
        HyperResource resource,
        Locale locale
    ) throws IOException {
        return MAPPER.writeValueAsString(resource);
    }


    @Override
    public boolean canWrite(Class<? extends HyperResource> resourceClass) {
        return true;
    }
}
