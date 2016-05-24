package com.bodybuilding.hyper.resource.converters;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.net.URLConnection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import com.github.jknack.handlebars.io.URLTemplateSource;

public class RemoteTemplateSource extends URLTemplateSource{
	
    private static final Logger LOG = LoggerFactory.getLogger(RemoteTemplateSource.class);
	
	private String content = null;
	private URL resource;

	public RemoteTemplateSource(String filename, URL resource) {
		super(filename, resource);
		this.resource = resource;
	}
	
	/**
	 * Checks if remote resource exists and loads content.
	 * @return
	 */
	public boolean isExist() {

		try {
			content = load();
		} catch (Exception e) {
			LOG.error("Error loading resource: " + resource.getFile() + ".  Use local version.", e);
		}
		if (!StringUtils.isEmpty(content)) {
			return true;
		}

		return false;
	}

	/** 
	 * Returns cached content
	 */
	@Override
	public String content() throws IOException {
		return content;
	}

	/**
	 * 
	 * @return
	 * @throws IOException
	 */
	private String load() throws IOException {
		Reader reader = null;
		final int bufferSize = 1024;
		try {
			URLConnection connection = resource.openConnection();
			connection.setRequestProperty("Accept", "text/x-handlebars-template");
			InputStream in = connection.getInputStream();
			reader = new InputStreamReader(in, "UTF-8");
			char[] cbuf = new char[bufferSize];
			StringBuilder sb = new StringBuilder(bufferSize);
			int len;
			while ((len = reader.read(cbuf, 0, bufferSize)) != -1) {
				sb.append(cbuf, 0, len);
			}
			return sb.toString();
		} finally {
			if (reader != null) {
				reader.close();
			}
		}
	}

}
