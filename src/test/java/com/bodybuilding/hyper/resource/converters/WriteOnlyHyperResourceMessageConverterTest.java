package com.bodybuilding.hyper.resource.converters;

import com.bodybuilding.hyper.resource.HyperResource;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;

import java.io.IOException;

import static org.junit.Assert.*;
import static org.hamcrest.Matchers.*;


public class WriteOnlyHyperResourceMessageConverterTest {

    MediaType mediaType = new MediaType("text", "html");
    WriteOnlyHyperResourceMessageConverter writer = new WriteOnlyHyperResourceMessageConverter(mediaType){

        @Override
        protected void writeInternal(HyperResource hyperResource, HttpOutputMessage outputMessage) throws IOException, HttpMessageNotWritableException {

        }
    };

    @Mock
    HttpInputMessage mockInput;

    @Before
    public void setUp(){
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testCanReadReturnsFalse(){
        assertFalse(writer.canRead(null));
        assertFalse(writer.canRead(mediaType));
    }


    @Test
    public void testSupports(){
        assertFalse(writer.supports(Object.class));
        assertTrue(writer.supports(HyperResource.class));
        assertTrue(writer.supports(new HyperResource(){}.getClass()));
    }


    @Test
    public void testReadInternalThrows(){
        try{
            writer.readInternal(HyperResource.class, mockInput);
            fail("expected exception not thrown");
        } catch (Throwable e){
            assertThat(e, instanceOf(HttpMessageNotReadableException.class));
        }
    }
}
