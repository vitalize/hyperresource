package com.bodybuilding.hyper.resource.converters;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.net.URL;

import org.junit.Before;
import org.junit.Test;
import org.springframework.core.io.DefaultResourceLoader;

import com.github.jknack.handlebars.io.TemplateSource;
import com.github.jknack.handlebars.springmvc.SpringTemplateLoader;


public class HandlebarsTemplatedHTMLMessageConverterIT {

    @Before
    public void setUp() throws IOException {
}

    //TODO: prod  http://api.bodybuilding.com/wrapper/store does not accept text/x-handlebars-template yet - works on QA3    
    @Test
    public void testRemoteTemplateLoaderOnCartResources() throws IOException {
        RemoteTemplateLoader remoteLoader = new RemoteTemplateLoader(new SpringTemplateLoader(new DefaultResourceLoader()));
        remoteLoader.setPrefix("/templates/handlebars/");
        // now it loads from local
        TemplateSource templateSource = remoteLoader.sourceAt("http://api.bodybuilding.com/wrapper/store");
        assertTrue(templateSource instanceof RemoteTemplateSource);
        assertTrue(templateSource.content().startsWith("<!DOCTYPE html>"));
    }
    @Test
    public void testRemoteTemplateSource() throws IOException {
        RemoteTemplateSource remoteTemplateSource = new RemoteTemplateSource("http://api.bodybuilding.com/wrapper/store", new URL("http://api.bodybuilding.com/wrapper/store"));
        assertNull(remoteTemplateSource.content());
        assertTrue(remoteTemplateSource.isExist());
        assertTrue(remoteTemplateSource.content().startsWith("<!DOCTYPE html>"));
    }
}