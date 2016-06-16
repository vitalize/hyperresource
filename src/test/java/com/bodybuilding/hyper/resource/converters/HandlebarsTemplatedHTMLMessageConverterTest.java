package com.bodybuilding.hyper.resource.converters;

import static org.hamcrest.Matchers.instanceOf;
import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageNotReadableException;

import com.bodybuilding.hyper.resource.HyperResource;
import com.github.jknack.handlebars.Handlebars;

public class HandlebarsTemplatedHTMLMessageConverterTest {

    MediaType mediaType = new MediaType("text", "html");
    
    @Mock
    Handlebars handlebars;
    
    
    HandlebarsTemplatedHTMLMessageConverter htmlMessageConverter ; 
           
    @Mock
    HttpInputMessage mockInput;

    @Mock
    HttpOutputMessage httpOutputMessage;    
    
    
    OutputStream outputStream;

    @Before
    public void setUp() throws IOException {
        MockitoAnnotations.initMocks(this);
        outputStream = new ByteArrayOutputStream();
        when(httpOutputMessage.getBody()).thenReturn(outputStream);        
        htmlMessageConverter = new HandlebarsTemplatedHTMLMessageConverter(handlebars);
    }

    @Test
    public void testCanReadReturnsFalse(){
        assertFalse(htmlMessageConverter.canRead(null));
        assertFalse(htmlMessageConverter.canRead(mediaType));
    }

    @Test
    public void testSupports(){
        assertFalse(htmlMessageConverter.supports(Object.class));
        assertTrue(htmlMessageConverter.supports(HyperResource.class));
        assertTrue(htmlMessageConverter.supports(new HyperResource() {
        }.getClass()));
    }

    @Test
    public void testReadInternalThrows(){
        try{
            htmlMessageConverter.readInternal(HyperResource.class, mockInput);
            fail("expected exception not thrown");
        } catch (Throwable e){
            assertThat(e, instanceOf(HttpMessageNotReadableException.class));
        }
    }

    @Test
    public void testCanWrite(){

        assertFalse(htmlMessageConverter.canWrite(HyperResource.class, new MediaType("application", "hal+json")));

        assertTrue(htmlMessageConverter.canWrite(HyperResource.class, mediaType));

    }
}