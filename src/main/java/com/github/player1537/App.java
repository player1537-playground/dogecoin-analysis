package com.github.player1537;

import org.json.simple.parser.ParseException;

import com.github.jreddit.entity.Submission;
import com.github.jreddit.entity.User;
import com.github.jreddit.exception.RedditError;
import com.github.jreddit.exception.RetrievalFailedException;
import com.github.jreddit.retrieval.Submissions;
import com.github.jreddit.retrieval.params.SubmissionSort;
import com.github.jreddit.utils.restclient.HttpRestClient;
import com.github.jreddit.utils.restclient.PoliteHttpRestClient;

import java.io.InputStream;
import java.io.IOException;

import java.lang.RuntimeException;

import java.util.List;
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

	private String getUserAgent(Properties properties) {
		assertExists(properties, "reddit.username");
		assertExists(properties, "project.version");
		assertExists(properties, "project.groupId");
		assertExists(properties, "project.artifactId");

		String userAgentFormat = "java:%s.%s:v%s (by /u/%s)";
		String userAgent = String.format(userAgentFormat,
		                                 properties.get("project.groupId"),
		                                 properties.get("project.artifactId"),
		                                 properties.get("project.version"),
		                                 properties.get("reddit.username"));
		System.out.println("User agent: " + userAgent);

		return userAgent;
	}

	private void run() {
		Properties properties = getProperties(CONFIG_FILENAME);

		for (String key : properties.stringPropertyNames()) {
			System.out.println("'" + key + "' = '" + properties.get(key) + "'");
		}

		String userAgent = getUserAgent(properties);

		HttpRestClient restClient = new PoliteHttpRestClient();
		restClient.setUserAgent(userAgent);

		assertExists(properties, "reddit.username");
		assertExists(properties, "reddit.password");

		User user = new User(restClient,
		                     properties.getProperty("reddit.username"),
		                     properties.getProperty("reddit.password"));

		try {
			user.connect();
		} catch (IOException ex) {
			throw new RuntimeException("Connecting user", ex);
		} catch (ParseException ex) {
			throw new RuntimeException("Connecting user", ex);
		}

		try {
			Submissions subms = new Submissions(restClient, user);

			List<Submission> submissions = subms.ofSubreddit("dogecoin",
			                                                 SubmissionSort.TOP,
			                                                 -1 /* count */,
			                                                 100 /* limit */,
			                                                 null /* before */,
			                                                 null /* after */,
			                                                 true /* show all */);

			for (Submission submission : submissions) {
				System.out.println(submission);
			}
		} catch (RetrievalFailedException ex) {
			ex.printStackTrace();
		} catch (RedditError ex) {
			ex.printStackTrace();
		}
	}
}
