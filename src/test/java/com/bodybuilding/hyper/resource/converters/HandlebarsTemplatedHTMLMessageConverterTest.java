package com.bodybuilding.hyper.resource.converters;

import static com.bodybuilding.commerce.cart.TestHelpers.mock0Pricing;
import static com.bodybuilding.commerce.cart.TestHelpers.mockFlag;
import static com.bodybuilding.commerce.cart.TestHelpers.mockGiftInfo;
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

import com.bodybuilding.commerce.cart.resources.CartResource;
import com.bodybuilding.commerce.cart.resources.Flag;
import com.bodybuilding.commerce.cart.resources.GiftInfo;
import com.bodybuilding.commerce.cart.resources.Pricing;
import com.bodybuilding.commerce.cart.resources.CartResource.Builder;
import com.bodybuilding.hyper.resource.HyperResource;
import com.bodybuilding.hyper.resource.TwoVariableHyperResource;
import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.io.GuavaCachedTemplateLoader;
import com.github.jknack.handlebars.io.StringTemplateSource;
import com.github.jknack.handlebars.io.TemplateLoader;
import com.github.jknack.handlebars.io.TemplateSource;
import com.github.jknack.handlebars.springmvc.SpringTemplateLoader;

import test.TestUtils;


public class HandlebarsTemplatedHTMLMessageConverterTest {
    
    public static final String QA3_STORE_WRAPPER_URL = "http://atg3-api2.dev:8080/wrapper-app-api-store/wrapper/store";
    public static final String QA3_CART_WRAPPER_URL = "http://atg3-api2.dev:8080/wrapper-app-api-store/wrapper/cart";
    public static final String STORE_WRAPPER_URL = "http://api.bodybuilding.com/wrapper/store";
    public static final String CART_WRAPPER_URL = "http://api.bodybuilding.com/wrapper/cart";
    
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
    
 
    public CartResource getCartResource() {
        GiftInfo giftInfo = mockGiftInfo();
        Flag[] flags = new Flag[1];
        flags[0] = mockFlag();
        Pricing noPricing = mock0Pricing();     
        String biToken = TestUtils.randomString();
        String fakeHeader = TestUtils.randomString();
        String fakeCSRF = TestUtils.randomString();

        CartResource cart = new Builder()
            .biToken(biToken)
            .headerText(fakeHeader)
            .giftInfo(giftInfo)
            .addFlag(flags[0])
            .pricing(noPricing)
            .csrfToken(fakeCSRF)
            .build();
        
        return cart;
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
    	TemplateSource templateSource = remoteLoader.sourceAt(CART_WRAPPER_URL);
    	assertFalse(templateSource instanceof RemoteTemplateSource);
    	
        TemplateSource templateCartSource = remoteLoader.sourceAt(QA3_STORE_WRAPPER_URL);
        assertTrue(templateCartSource instanceof RemoteTemplateSource);
    }
    
    @Test
    public void testRemoteTemplateLoaderWithDefaultSource() throws IOException {
        RemoteTemplateLoader remoteLoader = new RemoteTemplateLoader(new SpringTemplateLoader(new DefaultResourceLoader()));
    	remoteLoader.setPrefix("/templates/handlebars/");
    	TemplateSource templateSource = remoteLoader.sourceAt("/some_tempate");
    	assertTrue(templateSource instanceof StringTemplateSource);
    	assertTrue(templateSource.content().equals(""));
    }
      
    @Test
    public void testRemoteTemplateLoaderOnCartResources() throws IOException {
        RemoteTemplateLoader remoteLoader = new RemoteTemplateLoader(new SpringTemplateLoader(new DefaultResourceLoader()));
        remoteLoader.setPrefix("/templates/handlebars/");
        // now it loads from local
        TemplateSource templateSource = remoteLoader.sourceAt(QA3_STORE_WRAPPER_URL);
        assertTrue(templateSource instanceof RemoteTemplateSource);
        assertTrue(templateSource.content().startsWith("<!DOCTYPE html>"));
    }
    @Test
    public void testRemoteTemplateSource() throws IOException {
        RemoteTemplateSource remoteTemplateSource = new RemoteTemplateSource(QA3_STORE_WRAPPER_URL, new URL(QA3_STORE_WRAPPER_URL));
        assertNull(remoteTemplateSource.content());
        assertTrue(remoteTemplateSource.isExist());
        assertTrue(remoteTemplateSource.content().startsWith("<!DOCTYPE html>"));
    }
    @Test
    public void testRemoteTemplateSourceWithWrongUrl() throws IOException {
        RemoteTemplateSource remoteTemplateSource = new RemoteTemplateSource(STORE_WRAPPER_URL, new URL("http://"));
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
        
        templateLoader.setSuffix(".hbs");
        templateLoader.setPrefix("/templates/handlebars/");
         
        assertEquals("/templates/handlebars/head-meta.hbs.hbs", remoteLoader.resolve("head-meta.hbs"));
        
        Handlebars handlebars  = new Handlebars(templateLoader);
        HandlebarsTemplatedHTMLMessageConverter htmlMessageConverter = new HandlebarsTemplatedHTMLMessageConverter(handlebars);
        try{
            // if null writer?
            htmlMessageConverter.writeInternal(getCartResource(), null);
        } catch (Throwable e){
            e.printStackTrace();
            assertThat(e, instanceOf(NullPointerException.class));
        }
    }
    
}