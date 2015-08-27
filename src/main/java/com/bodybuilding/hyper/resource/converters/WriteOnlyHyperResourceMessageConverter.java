package com.bodybuilding.hyper.resource.converters;

import com.bodybuilding.hyper.resource.HyperResource;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.AbstractHttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;

import java.io.IOException;

/**
 * A MessageConverter base class for Message Converters that only Write
 */
public abstract class WriteOnlyHyperResourceMessageConverter extends AbstractHttpMessageConverter<HyperResource> {
    WriteOnlyHyperResourceMessageConverter(MediaType... supportedTypes) {
        super(supportedTypes);
    }


    //We don't bother with reading stuff today
    @Override
    public boolean canRead(Class<?> clazz, MediaType mediaType) {
        return false;
    }

    @Override
    protected boolean canRead(MediaType mediaType) {
        return false;
    }


    @Override
    protected HyperResource readInternal(Class<? extends HyperResource> aClass, HttpInputMessage httpInputMessage) throws IOException, HttpMessageNotReadableException {
        throw new HttpMessageNotReadableException("Reading not supported");
    }


    @Override
    protected boolean supports(Class<?> aClass) {
        return HyperResource.class.isAssignableFrom(aClass);
    }

}
