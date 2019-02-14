package org.hyperfit.hyperresource.serializer.handlebars;

import org.apache.commons.lang3.text.translate.LookupTranslator;
import org.hyperfit.hyperresource.HyperResource;
import org.hyperfit.hyperresource.serializer.HyperResourceSerializer;

import java.io.IOException;
import java.io.OutputStream;

import java.util.*;


public class WrappedSerializer implements HyperResourceSerializer {

    public final HyperResourceSerializer delegate;

    private final LookupTranslator translator;


    public WrappedSerializer(HyperResourceSerializer serializer, String[][] escapeMap){
        if(serializer == null){
            throw new IllegalArgumentException("A serializer is required.");
        }
        this.delegate = serializer;
        translator = new LookupTranslator(escapeMap);
    }

    @Override
    public List<String> getContentTypes() {
        return delegate.getContentTypes();
    }

    @Override
    public boolean canWrite(Class<? extends HyperResource> resourceClass) {
        return delegate.canWrite(resourceClass);
    }

    @Override
    public void write(HyperResource resource, Locale locale, OutputStream output) throws IOException {
        delegate.write(resource, locale, output);
    }

    @Override
    public String writeToString(HyperResource resource, Locale locale) throws IOException {
        String value = delegate.writeToString(resource, locale);
        return translator.translate(value);
    }
}