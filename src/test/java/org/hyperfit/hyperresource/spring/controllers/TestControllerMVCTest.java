package org.hyperfit.hyperresource.spring.controllers;


import javax.servlet.http.HttpServletResponse;

import org.hyperfit.hyperresource.serializer.haljson.jackson.HALJSONJacksonSerializer;
import org.hyperfit.hyperresource.serializer.handlebars.HandlebarsSerializer;
import org.hyperfit.hyperresource.spring.converters.WriteOnlyHyperResourceMessageConverter;
import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.springmvc.SpringTemplateLoader;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.http.MediaType;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.test.web.servlet.MockMvc;
import test.TestUtils;


public class TestControllerMVCTest {


    private static HttpMessageConverter<?> htmlHandlebarsConverter(){
        SpringTemplateLoader templateLoader = new SpringTemplateLoader(new DefaultResourceLoader());
        templateLoader.setSuffix(".hbs");
        templateLoader.setPrefix("/templates/handlebars/");

        return new WriteOnlyHyperResourceMessageConverter(
            new HandlebarsSerializer(
                new Handlebars(
                    templateLoader
                ),
                MediaType.TEXT_HTML_VALUE
            )
        );
    }


    private static HttpMessageConverter<?> halJsonJacksonConverter(){
        return new WriteOnlyHyperResourceMessageConverter(
            new HALJSONJacksonSerializer()
        );
    }

    private MockMvc mockMvc = standaloneSetup(new TestController())
        .setMessageConverters(
            htmlHandlebarsConverter(),
            halJsonJacksonConverter()
        )
        .build();


    MediaType hal = MediaType.valueOf("application/hal+json");
    MediaType qPoint5 = MediaType.valueOf("*/*;q=.5");
    MediaType qPoint8 = MediaType.valueOf("*/*;q=.8");


    @Test
    public void testRespondsWithHandlebars() throws Exception {
        
        String oneIn = TestUtils.randomString();
        String twoIn = TestUtils.randomString();
        
        this.mockMvc.perform(
            get("/testHandlebarsTemplateWorksWithController/{one}/{two}", oneIn, twoIn)
            .accept(MediaType.TEXT_HTML)
        )
        .andExpect(status().isOk())
        .andExpect(content().contentType("text/html"))
        .andExpect(content().string(containsString(oneIn)))
        .andExpect(content().string(containsString(twoIn)))
        ;

    }


    @Test
    public void testRespondsWithHALJson() throws Exception {

        String oneIn = TestUtils.randomString();
        String twoIn = TestUtils.randomString();

        this.mockMvc.perform(
            get("/testHandlebarsTemplateWorksWithController/{one}/{two}", oneIn, twoIn)
            .accept(hal)
        )
        .andExpect(status().isOk())
        .andExpect(content().contentType("application/hal+json"))
        .andExpect(jsonPath("$.one").value(oneIn))
        .andExpect(jsonPath("$.two").value(twoIn))
        ;

    }


    @Test
    public void testRespondsWithHALJsonOverTextHTML() throws Exception {

        String oneIn = TestUtils.randomString();
        String twoIn = TestUtils.randomString();

        this.mockMvc.perform(
            get("/testHandlebarsTemplateWorksWithController/{one}/{two}", oneIn, twoIn)
            .accept(MediaType.TEXT_HTML.copyQualityValue(qPoint5), hal.copyQualityValue(qPoint8))
        )
        .andExpect(status().isOk())
        .andExpect(content().contentType("application/hal+json"))
        .andExpect(jsonPath("$.one").value(oneIn))
        .andExpect(jsonPath("$.two").value(twoIn))
        ;

    }
    
    @Test
    public void testControllerDoesNotProcessHandlebarsTemplateIfIncorrectAcceptHeader() throws Exception {
        
        String oneIn = TestUtils.randomString();
        String twoIn = TestUtils.randomString();
        
        this.mockMvc.perform(
            get("/testHandlebarsTemplateWorksWithController/{one}/{two}", oneIn, twoIn)
            .accept(MediaType.APPLICATION_ATOM_XML)
        )
        .andExpect(status().isNotAcceptable())
        .andExpect(content().string(""))
        ;

        
    }

    @Ignore("until we figure out what exception handler we need to register http://jira/browse/COMAPI-5257")
    @Test
    public void testHandlebarsTemplateDoesNotExistError() throws Exception {
        
        this.mockMvc.perform(
            get("/testHandlebarsTemplateDoesNotExist")
            .accept(MediaType.TEXT_HTML)
        )
        .andExpect(status().is5xxServerError())
        .andReturn()
        ;

    }

    @Ignore("until we figure out what exception handler we need to register http://jira/browse/COMAPI-5257")
    @Test
    public void testRuntimeExceptionInController() throws Exception {
        
        this.mockMvc.perform(
            get("/testRuntimeExceptionInController")
            .accept(MediaType.TEXT_HTML)
        )
        .andExpect(status().is5xxServerError())
        .andExpect(content().string(""))

        ;
        
    }
    
    @Test
    public void testExceptionInControllerWrappedByDefaultHandlerExceptionResolver() throws Exception {
        
        this.mockMvc.perform(
            get("/testExceptionInControllerWrappedByDefaultHandlerExceptionResolver")
            .accept(MediaType.TEXT_HTML)
        )
        .andExpect(status().is(HttpServletResponse.SC_UNSUPPORTED_MEDIA_TYPE))
        .andExpect(content().string(""))
        ;
        
    }
    
}