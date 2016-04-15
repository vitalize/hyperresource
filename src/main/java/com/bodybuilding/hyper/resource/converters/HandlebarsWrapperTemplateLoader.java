package com.bodybuilding.hyper.resource.converters;

import java.util.Calendar;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

public class HandlebarsWrapperTemplateLoader {
    
    private final long wrapperTTL;
    private long wrapperTimestamp;
    private String cachedWrapper;    
    private HandlebarsTemplateDAO mainTemplateLoader;
    private HandlebarsTemplateDAO fallbackTemplateLoader;
    
    private static final Logger LOG = LoggerFactory.getLogger(HandlebarsWrapperTemplateLoader.class);
    
    public HandlebarsWrapperTemplateLoader(HandlebarsTemplateDAO mainTemplateLoader, HandlebarsTemplateDAO fallbackTemplateLoader) {        
        this(ConfigFactory.load());
        this.mainTemplateLoader = mainTemplateLoader;
        this.fallbackTemplateLoader = fallbackTemplateLoader;
    }
    
    public HandlebarsWrapperTemplateLoader(Config config) {        
        wrapperTTL = config.getLong("wrapper-app.cached-wrapper-ttl");       
    }    
    
    public String getParentTemplate() {
        if(!StringUtils.isEmpty(cachedWrapper)) {
            long currentTime = Calendar.getInstance().getTimeInMillis();            
            if((currentTime - wrapperTimestamp) < wrapperTTL) {
                LOG.debug("Returning cached wrapper!");
                return cachedWrapper;
            }
        } 
        return loadParentTemplateFromMainSource().
                orElse(fallbackTemplateLoader.getParentTemplate().get());        
    }    
    
    private Optional<String> loadParentTemplateFromMainSource() {
        Optional<String> template = mainTemplateLoader.getParentTemplate();
        
        if(template.isPresent()) {
            wrapperTimestamp = Calendar.getInstance().getTimeInMillis();
            cachedWrapper = template.get();
        }
        return template;
    }
}
