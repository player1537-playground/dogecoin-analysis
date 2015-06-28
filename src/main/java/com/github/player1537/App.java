package com.github.player1537;

import java.io.InputStream;
import java.io.IOException;

import java.lang.RuntimeException;

import java.util.Properties;

public class App {
	private final static String CONFIG_FILENAME = "/application.properties";

	public static void main(String... args) {
		App app = new App();
		app.run();
	}

	private Properties getProperties(String filename) {
		Properties properties = new Properties();

		try {
			InputStream inputStream = this.getClass().getResourceAsStream(filename);

			properties.load(inputStream);
		} catch (IOException ex) {
			System.err.println("Caught exception: " + ex);
			throw new RuntimeException("Reading properties", ex);
		}

		return properties;
	}

	private void assertExists(Properties properties, String key) {
		assert properties.containsKey(key);
	}

	private void run() {
		Properties properties = getProperties(CONFIG_FILENAME);
		assertExists(properties, "reddit.username");
		assertExists(properties, "reddit.password");
		assertExists(properties, "project.version");
		assertExists(properties, "project.groupId");
		assertExists(properties, "project.artifactId");

		for (String key : properties.stringPropertyNames()) {
			System.out.println("'" + key + "' = '" + properties.get(key) + "'");
		}

		String userAgentFormat = "java:%s.%s:v%s (by /u/%s)";
		String userAgent = String.format(userAgentFormat,
		                                 properties.get("project.groupId"),
		                                 properties.get("project.artifactId"),
		                                 properties.get("project.version"),
		                                 properties.get("reddit.username"));
		System.out.println("User agent: " + userAgent);
	}
}
