package com.github.player1537;

import org.json.simple.parser.ParseException;

import com.github.jreddit.entity.Submission;
import com.github.jreddit.entity.User;
import com.github.jreddit.exception.RedditError;
import com.github.jreddit.exception.RetrievalFailedException;
import com.github.jreddit.retrieval.Submissions;
import com.github.jreddit.utils.restclient.HttpRestClient;
import com.github.jreddit.utils.restclient.PoliteHttpRestClient;

import java.io.IOException;

import java.lang.RuntimeException;

import java.util.Collections;
import java.util.List;

public class Reddit {
	Configuration config;
	String userAgent;
	HttpRestClient restClient = null;
	User user = null;
	Submissions subms = null;

	public Reddit(Configuration config) {
		this.config = config;
	}

	private String getUserAgent() {
		if (this.userAgent != null) {
			return this.userAgent;
		}

		String userAgentFormat = "java:%s.%s:v%s (by /u/%s)";
		String userAgent = String.format(userAgentFormat,
		                                 config.get("project.groupId"),
		                                 config.get("project.artifactId"),
		                                 config.get("project.version"),
		                                 config.get("reddit.username"));

		this.userAgent = userAgent;

		return userAgent;
	}

	private HttpRestClient getRestClient() {
		if (this.restClient != null) {
			return this.restClient;
		}

		HttpRestClient restClient = new PoliteHttpRestClient();
		restClient.setUserAgent(getUserAgent());

		this.restClient = restClient;
		return restClient;
	}

	private User getUser() {
		if (this.user != null) {
			return this.user;
		}

		User user = new User(getRestClient(),
		                     config.get("reddit.username"),
		                     config.get("reddit.password"));
		try {
			user.connect();
		} catch (IOException ex) {
			throw new RuntimeException("Connecting user", ex);
		} catch (ParseException ex) {
			throw new RuntimeException("Connecting user", ex);
		}

		this.user = user;
		return user;
	}

	private Submissions getSubmissions() {
		if (this.subms != null) {
			return this.subms;
		}

		Submissions subms = new Submissions(getRestClient(), getUser());

		this.subms = subms;
		return subms;
	}

	public List<Submission> byNames(String... names) {
		Submissions subms = getSubmissions();
		try {
			List<Submission> submissions = subms.byNames(names);

			return submissions;
		} catch (RetrievalFailedException ex) {
			ex.printStackTrace();
		} catch (RedditError ex) {
			ex.printStackTrace();
		}

		return Collections.EMPTY_LIST;
	}
}
