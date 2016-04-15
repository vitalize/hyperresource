package com.bodybuilding.hyper.resource.converters;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StreamUtils;
import org.springframework.util.StringUtils;

import com.typesafe.config.Config;

public class HandlebarsTemplateDAOStaticFileImpl implements HandlebarsTemplateDAO {
    
    private static final Logger LOG = LoggerFactory.getLogger(HandlebarsTemplateDAOStaticFileImpl.class);
    private String fallbackWrapper;
    
    public HandlebarsTemplateDAOStaticFileImpl() {

        InputStream in = this.getClass().getResourceAsStream("/templates/handlebars/wrapper.html");        
        if(in != null) {
            try {                
                fallbackWrapper = StreamUtils.copyToString(in, StandardCharsets.UTF_8);                
            } catch (IOException e) {
                LOG.error("Unable to load fallback wrapper!", e);             
                
               //throw new RuntimeException(e); // Shouldn't let app start if no fallback available?
            }
        }        
    }
    
    public Optional<String> getParentTemplate() {
        return (!StringUtils.isEmpty(fallbackWrapper) ? Optional.of(fallbackWrapper) : Optional.empty());        
    }        
}
