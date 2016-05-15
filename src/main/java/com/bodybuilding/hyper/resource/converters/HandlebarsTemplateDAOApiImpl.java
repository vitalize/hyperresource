package com.bodybuilding.hyper.resource.converters;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StreamUtils;
import org.springframework.util.StringUtils;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

public class HandlebarsTemplateDAOApiImpl implements HandlebarsTemplateDAO {

    private String wrapperAppEndpoint;
    private static final Logger LOG = LoggerFactory.getLogger(HandlebarsTemplateDAOApiImpl.class);
    
    public HandlebarsTemplateDAOApiImpl() {
        Config config = ConfigFactory.load();
        wrapperAppEndpoint = config.getString("wrapper-app.endpoint");
    }
    
    public Optional<String> getParentTemplate() {
        String template = null;
        try {
            URL url = new URL(wrapperAppEndpoint);
            URLConnection connection = url.openConnection();

            //HACK: This dao is probably going away anyways
            //but many of the TestControllerIntegrationTests end up calling this
            //which means they wait around for default timeout of 30 if you aren't on vpn
            //so let's just drop that down
            //also those tests should have something that relies on external services like this
            //be mocked out
            connection.setConnectTimeout(1000 * 3);
            connection.setReadTimeout(100 * 3);

            connection.setRequestProperty("Accept", "text/x-handlebars-template");
            template = StreamUtils.copyToString(connection.getInputStream(), StandardCharsets.UTF_8);                     
        } catch (IOException e) {
            LOG.error("Unable to load wrapper from API!", e);
        }
        if (!StringUtils.isEmpty(template)) {
            LOG.debug("Loading wrapper from API!");            
            return Optional.of(template); 
        }
        return Optional.empty();
    }    
    
}
