package com.bodybuilding.hyper.resource.converters;

import static org.hamcrest.Matchers.instanceOf;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.when;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.UUID;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageNotReadableException;

import com.bodybuilding.hyper.resource.HyperResource;
import com.bodybuilding.hyper.resource.TwoVariableHyperResource;
import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.io.StringTemplateSource;
import com.github.jknack.handlebars.io.TemplateSource;


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


    @Test
    public void testRemoteTemplateLoaderOnLocalResources() throws IOException {
    	RemoteTemplateLoader remoteLoader = new RemoteTemplateLoader(new DefaultResourceLoader());
    	remoteLoader.setSuffix(".html");
    	remoteLoader.setPrefix("/templates/handlebars/");
    	TemplateSource templateSource = remoteLoader.sourceAt("/api.bodybuilding.com/wrapper/cart");
    	assertFalse(templateSource instanceof RemoteTemplateSource);
    	assertTrue(templateSource.content().startsWith("<!DOCTYPE html>"));
    }

    @Test
    public void testRemoteTemplateLoaderOnRemoteResources() throws IOException {
    	RemoteTemplateLoader remoteLoader = new RemoteTemplateLoader(new DefaultResourceLoader());
    	remoteLoader.setSuffix(".html");
    	remoteLoader.setPrefix("/templates/handlebars/");
        // not it loads from local
    	TemplateSource templateSource = remoteLoader.sourceAt("//api.bodybuilding.com/wrapper/cart");
    	assertFalse(templateSource instanceof RemoteTemplateSource);
    	assertTrue(templateSource.content().startsWith("<!DOCTYPE html>"));
    }
    
    @Test
    public void testRemoteTemplateLoaderWithDefaultSource() throws IOException {
    	RemoteTemplateLoader remoteLoader = new RemoteTemplateLoader(new DefaultResourceLoader());
    	remoteLoader.setSuffix(".html");
    	remoteLoader.setPrefix("/templates/handlebars/");
    	TemplateSource templateSource = remoteLoader.sourceAt("/some_tempate");
    	assertTrue(templateSource instanceof StringTemplateSource);
    	assertTrue(templateSource.content().equals(""));
    }
}