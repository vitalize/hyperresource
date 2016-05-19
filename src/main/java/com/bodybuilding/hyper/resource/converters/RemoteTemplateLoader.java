package com.bodybuilding.hyper.resource.converters;

import static org.apache.commons.lang3.Validate.notEmpty;

import java.io.IOException;
import java.net.URL;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ResourceLoader;

import com.github.jknack.handlebars.io.TemplateSource;
import com.github.jknack.handlebars.springmvc.SpringTemplateLoader;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

public class RemoteTemplateLoader extends SpringTemplateLoader {
	
	private static final Logger LOG = LoggerFactory.getLogger(RemoteTemplateLoader.class);
	
	private String wrapperAppEndpoint;
	
	public RemoteTemplateLoader(ResourceLoader loader) {
		super(loader);
        Config config = ConfigFactory.load();
        wrapperAppEndpoint = config.getString("wrapper-app.endpoint");
	}


	@Override
	public TemplateSource sourceAt(final String uri) throws IOException {
		notEmpty(uri, "The uri is required.");
		String wrapperResource = wrapperAppEndpoint + uri;
		RemoteTemplateSource source = new RemoteTemplateSource(wrapperResource, new URL(wrapperResource));
		if (source.isExist()) {
			return source;
		}
		LOG.warn("Resource can not be loaded from {}. Loading local template....", wrapperResource);
		return super.sourceAt(uri);
	}

}
