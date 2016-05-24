package com.bodybuilding.hyper.resource.converters;

import static org.apache.commons.lang3.Validate.notEmpty;

import java.io.IOException;
import java.net.URL;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ResourceLoader;

import com.github.jknack.handlebars.io.StringTemplateSource;
import com.github.jknack.handlebars.io.TemplateLoader;
import com.github.jknack.handlebars.io.TemplateSource;
import com.github.jknack.handlebars.springmvc.SpringTemplateLoader;

/**
 * 1) Trying to load template remotely for all templates with http://;
 * 2) Trying to load locally;
 * 3) Returns default template;
 */
public class RemoteTemplateLoader implements  TemplateLoader {	
	
    private static final Logger LOG = LoggerFactory.getLogger(RemoteTemplateLoader.class);

	private static final StringTemplateSource DEFAULT_SOURCE = new StringTemplateSource("/default", "");
	
	private final TemplateLoader delegate;
	
	public RemoteTemplateLoader(final TemplateLoader delegate) {
		this.delegate = delegate;
	}


	@Override
	public TemplateSource sourceAt(String uri) throws IOException {
		notEmpty(uri, "The uri is required.");
		if (uri.startsWith("http://")){
			URL url = new URL(uri);
			RemoteTemplateSource source = new RemoteTemplateSource(uri, url);
			if (source.isExist()) {
				return source;
			}
			uri = url.getHost() + url.getPath();
		}
		try {
			TemplateSource source =  delegate.sourceAt(uri);
			return source;
		} catch (IOException ex){
			LOG.error("Template " + uri + " not found using default source.", ex);
		}
		return DEFAULT_SOURCE;

	}

	  /**
	   * {@inheritDoc}
	   */
	  public String resolve(final String location) {
	    return delegate.resolve(location);
	  }

	  /**
	   * {@inheritDoc}
	   */
	  public String getPrefix() {
	    return delegate.getPrefix();
	  }

	  /**
	   * {@inheritDoc}
	   */
	  public String getSuffix() {
	    return delegate.getSuffix();
	  }

	  /**
	   * {@inheritDoc}
	   */
	  public void setPrefix(final String prefix) {
	    delegate.setPrefix(prefix);
	  }

	  /**
	   * {@inheritDoc}
	   */
	  public void setSuffix(final String suffix) {
	    delegate.setSuffix(suffix);
	  }

}
