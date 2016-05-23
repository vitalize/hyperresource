package com.bodybuilding.hyper.resource.converters;

import static org.apache.commons.lang3.Validate.notEmpty;

import java.io.IOException;
import java.net.URL;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ResourceLoader;

import com.github.jknack.handlebars.io.StringTemplateSource;
import com.github.jknack.handlebars.io.TemplateSource;
import com.github.jknack.handlebars.springmvc.SpringTemplateLoader;

/**
 * 1) Trying to load template remotely for all templates with //;
 * 2) Trying to load locally;
 * 3) Returns default template;
 */
public class RemoteTemplateLoader extends SpringTemplateLoader {	
	
    private static final Logger LOG = LoggerFactory.getLogger(RemoteTemplateLoader.class);

	private static final StringTemplateSource DEFAULT_SOURCE = new StringTemplateSource("/default", "");
	
	public RemoteTemplateLoader(ResourceLoader loader) {
		super(loader);
	}


	@Override
	public TemplateSource sourceAt(final String uri) throws IOException {
		notEmpty(uri, "The uri is required.");
		if (uri.startsWith("//"))
		{
			RemoteTemplateSource source = new RemoteTemplateSource(uri, new URL("http:" + uri));
			if (source.isExist()) {
				return source;
			}
		}
		try{
			TemplateSource source =  super.sourceAt(uri);
			return source;
		} catch (IOException ex){
			LOG.error("Template {} not found using default source.", uri);
		}
		return DEFAULT_SOURCE;

	}

}
