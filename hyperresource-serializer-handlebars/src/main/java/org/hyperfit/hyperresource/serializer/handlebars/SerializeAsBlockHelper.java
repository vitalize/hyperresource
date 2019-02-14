package org.hyperfit.hyperresource.serializer.handlebars;

import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.Helper;
import com.github.jknack.handlebars.Options;
import org.hyperfit.hyperresource.HyperResource;
import org.hyperfit.hyperresource.serializer.HyperResourceSerializer;

import java.io.IOException;
import java.util.*;

/**
 * <p>
 * Basic usage:
 * </p>
 *
 * <pre>
 *  Handlebars hbs = new Handlebars();
 *
 *  hbs.registerHelper(SerializeAsBlockHelper.HELPER_MARKUP_TAG_NAME, new SerializeAsBlockHelper(new HALJSONJacksonSerializer()));
 *
 *  ...
 *  {{serializeAs "application/hal+json" hyperResource}}
 * </pre>
 *
 */
public class SerializeAsBlockHelper implements Helper<String> {

    public static final String HELPER_MARKUP_TAG_NAME = "serializeAs";

    private final Map<String, HyperResourceSerializer> serializersMap;


    public SerializeAsBlockHelper(HyperResourceSerializer... serializers) {

        serializersMap = new HashMap<>();
        for (HyperResourceSerializer s: serializers){
            registerSerializer(s);
        }
    }

    private void registerSerializer(HyperResourceSerializer serializer){

        for (String contentType: serializer.getContentTypes()){
            serializersMap.put(contentType, serializer);
        }
    }

    public boolean supportContentType(String contentType){
        return serializersMap.get(contentType) != null;
    }

    @Override
    public Object apply(
            String context,
            Options options
    ) throws IOException {

        if(!supportContentType(context)){
            throw new IOException(context + " is not supported by " + this.getClass().getSimpleName());
        }

        Object obj = options.param(0);

        HyperResource resource = null;

        if (obj == null){
            return null;
        } else if (obj instanceof HyperResource){
            resource = (HyperResource) obj;
        } else if (obj.getClass().isArray()){
            HyperResource[] resources = (HyperResource[]) obj;
            resource = new WrappedHyperResource(Arrays.asList(resources));
        } else {
            throw new IOException(this.getClass().getSimpleName() + " does not support class: " + obj.getClass().getSimpleName());
        }

        HyperResourceSerializer serializer = serializersMap.get(context);

        return new Handlebars.SafeString(serializer.writeToString(resource, null));

    }

    private static class WrappedHyperResource extends LinkedList<HyperResource> implements HyperResource {
        public WrappedHyperResource(Collection<? extends HyperResource> c) {
            super(c);
        }
    }

}
