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

import java.io.IOException;
import java.io.OutputStream;

import static org.hamcrest.Matchers.instanceOf;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;


public class HALJSONMessageConverterTest {

    MediaType mediaType = new MediaType("application", "hal+json");
    HALJSONMessageConverter writer = new HALJSONMessageConverter();

    @Mock
    HttpInputMessage mockInput;

    @Mock
    HttpOutputMessage mockOutput;

    @Mock
    OutputStream mockBodyStream;

    @Before
    public void setUp() throws IOException {
        MockitoAnnotations.initMocks(this);

        when(mockOutput.getBody()).thenReturn(mockBodyStream);
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
        assertTrue(writer.supports(new HyperResource() {
        }.getClass()));
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


    @Test
    public void testCanWrite(){

        assertFalse(writer.canWrite(HyperResource.class, new MediaType("text", "html")));

        assertTrue(writer.canWrite(HyperResource.class, mediaType));

    }


    @Test
    public void testWriteInternalSimpleResourceNoControls() throws IOException {
        HyperResource resource = new HyperResource(){

        };

        String expectedString = "This is hal+json representation\n" + resource.toString();

        writer.writeInternal(resource, mockOutput);

        verify(mockBodyStream, only()).write(expectedString.getBytes());



    }
}
