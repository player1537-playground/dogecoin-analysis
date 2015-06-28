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

import java.io.IOException;

import java.lang.RuntimeException;

import java.util.List;

public class App {
	public static void main(String... args) {
		App app = new App();
		app.run();
	}

	private String getUserAgent(Configuration config) {
		String userAgentFormat = "java:%s.%s:v%s (by /u/%s)";
		String userAgent = String.format(userAgentFormat,
		                                 config.get("project.groupId"),
		                                 config.get("project.artifactId"),
		                                 config.get("project.version"),
		                                 config.get("reddit.username"));
		System.out.println("User agent: " + userAgent);

		return userAgent;
	}

	private void run() {
		Configuration config = Configuration.create();

		String userAgent = getUserAgent(config);

		HttpRestClient restClient = new PoliteHttpRestClient();
		restClient.setUserAgent(userAgent);

		User user = new User(restClient,
		                     config.get("reddit.username"),
		                     config.get("reddit.password"));

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
