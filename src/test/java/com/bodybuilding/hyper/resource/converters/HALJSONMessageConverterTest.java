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

import java.io.ByteArrayOutputStream;
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


    ByteArrayOutputStream outputStream;



    @Before
    public void setUp() throws IOException {
        MockitoAnnotations.initMocks(this);
        outputStream = new ByteArrayOutputStream();

        when(mockOutput.getBody()).thenReturn(outputStream);
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
            public int val = 1;
        };

        writer.writeInternal(resource, mockOutput);

        String expectedString = "{\"val\":1}";
        String actual = outputStream.toString();
        assertEquals(expectedString, actual);


    }
}
