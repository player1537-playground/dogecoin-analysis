package com.github.player1537;

import java.io.InputStream;
import java.io.IOException;

import java.lang.RuntimeException;

import java.util.Properties;

public class Configuration {
	private final static String DEFAULT_CONFIG_FILENAME
		= "/application.properties";
	private Properties properties = null;
	private String filename = DEFAULT_CONFIG_FILENAME;

	public static Configuration create() {
		return create(DEFAULT_CONFIG_FILENAME);
	}

	public static Configuration create(String fname) {
		Properties props = new Properties();

		try {
			InputStream inputStream = Configuration.class.getResourceAsStream(fname);

			props.load(inputStream);
		} catch (IOException ex) {
			System.err.println("Caught exception: " + ex);
			throw new RuntimeException("Reading properties", ex);
		}

		return new Configuration(props);
	}

	private Configuration(Properties props) {
		properties = props;
	}

	private void assertExists(String key) {
		assert properties.containsKey(key);
	}

	public void setFilename(String fname) {
		filename = fname;
	}

	public String get(String key) {
		assertExists(key);

		return properties.getProperty(key);
	}
}
