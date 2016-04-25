package com.bodybuilding.hyper.resource.converters;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import static org.mockito.Mockito.*;
import static org.junit.Assert.*;

public class HandlebarsWrapperTemplateLoaderTest {
    
    @Mock
    private HandlebarsTemplateDAO main;
    
    @Mock
    private HandlebarsTemplateDAO fallback;
    
    private HandlebarsWrapperTemplateLoader loader;
    
    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        loader = new HandlebarsWrapperTemplateLoader(main, fallback);
    }
    
    @Test
    public void testMainSourceIsUsed() {
        
        when(main.getParentTemplate()).thenReturn(Optional.of("main_wrapper"));
        when(fallback.getParentTemplate()).thenReturn(Optional.of("fallback_wrapper"));
        String wrapper = loader.getParentTemplate(); 
        assertEquals("main_wrapper", wrapper);
    }
    
    @Test
    public void testFallbackSourceIsUsed() {
        
        when(main.getParentTemplate()).thenReturn(Optional.empty());
        when(fallback.getParentTemplate()).thenReturn(Optional.of("fallback_wrapper"));
        String wrapper = loader.getParentTemplate(); 
        assertEquals("fallback_wrapper", wrapper);
    }
    
    @Test
    public void testCachedWrapperIsUsed() {
        
        when(main.getParentTemplate()).thenReturn(Optional.of("main_wrapper"));
        when(fallback.getParentTemplate()).thenReturn(Optional.of("fallback_wrapper"));               
        
        String wrapper = loader.getParentTemplate();         
        assertEquals("main_wrapper", wrapper);
        wrapper = loader.getParentTemplate();
        assertEquals("main_wrapper", wrapper);
        verify(main, times(1)).getParentTemplate();
    }
    
}
