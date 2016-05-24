package com.bodybuilding.hyper.resource.converters;

import static org.hamcrest.Matchers.instanceOf;
import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

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
import com.github.jknack.handlebars.io.GuavaCachedTemplateLoader;
import com.github.jknack.handlebars.io.StringTemplateSource;
import com.github.jknack.handlebars.io.TemplateLoader;
import com.github.jknack.handlebars.io.TemplateSource;
import com.github.jknack.handlebars.springmvc.SpringTemplateLoader;


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
    	RemoteTemplateLoader remoteLoader = new RemoteTemplateLoader(new SpringTemplateLoader(new DefaultResourceLoader()));
    	remoteLoader.setPrefix("/templates/handlebars/");
    	TemplateSource templateSource = remoteLoader.sourceAt("/api.bodybuilding.com/wrapper/cart");
    	assertFalse(templateSource instanceof RemoteTemplateSource);
    	assertTrue(templateSource.content().startsWith("<!DOCTYPE html>"));
    }

    @Test
    public void testRemoteTemplateLoaderOnRemoteResources() throws IOException {
        RemoteTemplateLoader remoteLoader = new RemoteTemplateLoader(new SpringTemplateLoader(new DefaultResourceLoader()));
    	remoteLoader.setPrefix("/templates/handlebars/");
        // now it loads from local
    	TemplateSource templateSource = remoteLoader.sourceAt("http://api.bodybuilding.com/wrapper/cart");
    	assertFalse(templateSource instanceof RemoteTemplateSource);
    	assertTrue(templateSource.content().startsWith("<!DOCTYPE html>"));
    }
    
    @Test
    public void testRemoteTemplateLoaderWithDefaultSource() throws IOException {
        RemoteTemplateLoader remoteLoader = new RemoteTemplateLoader(new SpringTemplateLoader(new DefaultResourceLoader()));
    	remoteLoader.setPrefix("/templates/handlebars/");
    	TemplateSource templateSource = remoteLoader.sourceAt("/some_tempate");
    	assertTrue(templateSource instanceof StringTemplateSource);
    	assertTrue(templateSource.content().equals(""));
    }
    
//    http://api.bodybuilding.com/wrapper/store does not accept text/x-handlebars-template yet     
//    @Test
//    public void testRemoteTemplateLoaderOnCartResources() throws IOException {
//        RemoteTemplateLoader remoteLoader = new RemoteTemplateLoader(new SpringTemplateLoader(new DefaultResourceLoader()));
//        remoteLoader.setPrefix("/templates/handlebars/");
//        // now it loads from local
//        TemplateSource templateSource = remoteLoader.sourceAt("http://api.bodybuilding.com/wrapper/store");
//        assertTrue(templateSource instanceof RemoteTemplateSource);
//        assertTrue(templateSource.content().startsWith("<!DOCTYPE html>"));
//    }
//    @Test
//    public void testRemoteTemplateSource() throws IOException {
//        RemoteTemplateSource remoteTemplateSource = new RemoteTemplateSource("http://api.bodybuilding.com/wrapper/store", new URL("http://api.bodybuilding.com/wrapper/store"));
//        assertNull(remoteTemplateSource.content());
//        assertTrue(remoteTemplateSource.isExist());
//        assertTrue(remoteTemplateSource.content().startsWith("<!DOCTYPE html>"));
//    }
    @Test
    public void testRemoteTemplateSourceWithWrongUrl() throws IOException {
        RemoteTemplateSource remoteTemplateSource = new RemoteTemplateSource("http://api.bodybuilding.com/wrapper/store", new URL("http://"));
        assertNull(remoteTemplateSource.content());
        assertFalse(remoteTemplateSource.isExist());
        assertNull(remoteTemplateSource.content());
    }
    
    @Test
    public void testRemoteTemplateLoaderDelegate() throws IOException {
        
        SpringTemplateLoader templateLoader = new SpringTemplateLoader(new DefaultResourceLoader());
        templateLoader.setSuffix(".hbs1");
        templateLoader.setPrefix("/templates/handlebars1/");
         
        TemplateLoader remoteLoader = new RemoteTemplateLoader(templateLoader);
        
        assertEquals(".hbs1", remoteLoader.getSuffix());
        assertEquals("/templates/handlebars1/", remoteLoader.getPrefix());
        
        remoteLoader.setSuffix(".hbs2");
        remoteLoader.setPrefix("/templates/handlebars2/");
        
        assertEquals(".hbs2", remoteLoader.getSuffix());
        assertEquals("/templates/handlebars2/", templateLoader.getPrefix());
        
    }
    
}