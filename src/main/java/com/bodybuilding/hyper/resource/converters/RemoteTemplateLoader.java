package com.bodybuilding.hyper.resource.converters;

import static org.apache.commons.lang3.Validate.notEmpty;

import java.io.IOException;
import java.net.URL;

import org.springframework.core.io.ResourceLoader;

import com.github.jknack.handlebars.io.TemplateSource;
import com.github.jknack.handlebars.springmvc.SpringTemplateLoader;

public class RemoteTemplateLoader extends SpringTemplateLoader {	
	
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
		return super.sourceAt(uri);
	}

}
