package com.bodybuilding.hyper.resource.spring.converters;

import com.bodybuilding.hyper.resource.HyperResource;
import com.bodybuilding.hyper.resource.serializer.HyperResourceSerializer;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.AbstractHttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;

import java.io.IOException;
import java.util.Optional;

/**
 * A Spring MessageConverter that adapts HyperResourceSerializers into Spring Message Converters
 */
public class WriteOnlyHyperResourceMessageConverter extends AbstractHttpMessageConverter<HyperResource> {
    private final HyperResourceSerializer serializer;

    public WriteOnlyHyperResourceMessageConverter(HyperResourceSerializer serializer) {
        super(
            Optional.ofNullable(serializer)
            .map(
                s -> s.getContentTypes().stream()
                    .map(MediaType::valueOf)
                    .toArray(MediaType[]::new)
            )
            .orElseThrow(() -> new IllegalArgumentException("serializer can not be null"))
        );

        this.serializer = serializer;
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
    protected HyperResource readInternal(Class<? extends HyperResource> aClass, HttpInputMessage httpInputMessage) throws IOException {
        throw new HttpMessageNotReadableException("Reading not supported");
    }

    @Override
    protected void writeInternal(HyperResource hyperResource, HttpOutputMessage outputMessage) throws IOException, HttpMessageNotWritableException {
        //normally i'd check for outputMessage being null...but it'll throw in the
        //base class' write() way before it gets here so no need for the double check
        serializer.write(hyperResource, outputMessage.getBody());
    }


    @Override
    protected boolean supports(Class<?> aClass) {
        if(!HyperResource.class.isAssignableFrom(aClass)){
            //if it's not a hyper resource, we can't support it
            return false;
        }

        return serializer.canWrite((Class<? extends HyperResource>) aClass);
    }

    public HyperResourceSerializer getSerializer() {
        return serializer;
    }
}
