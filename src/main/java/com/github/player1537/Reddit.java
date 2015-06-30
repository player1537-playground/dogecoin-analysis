package com.github.player1537;

import org.json.simple.parser.ParseException;

import com.github.jreddit.entity.Submission;
import com.github.jreddit.entity.Subreddit;
import com.github.jreddit.entity.User;
import com.github.jreddit.exception.RedditError;
import com.github.jreddit.exception.RetrievalFailedException;
import com.github.jreddit.retrieval.Submissions;
import com.github.jreddit.retrieval.Subreddits;
import com.github.jreddit.retrieval.params.SubmissionSort;
import com.github.jreddit.utils.restclient.HttpRestClient;
import com.github.jreddit.utils.restclient.PoliteHttpRestClient;

import java.io.IOException;

import java.lang.RuntimeException;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

import java.util.Collections;
import java.util.List;

public class Reddit {
	Configuration config;
	String userAgent;
	HttpRestClient restClient = null;
	User user = null;
	Submissions subms = null;
	Subreddits subrs = null;

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

	private Subreddits getSubreddits() {
		if (this.subrs != null) {
			return this.subrs;
		}

		Subreddits subrs = new Subreddits(getRestClient(), getUser());

		this.subrs = subrs;
		return subrs;
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
	
	public Submission mostRecentSubmission() {
		Submissions subms = getSubmissions();
		try {
			List<Submission> submissions = subms.ofSubreddit("all",
			                                                 SubmissionSort.NEW,
			                                                 -1 /* count */,
			                                                 1 /* limit */,
			                                                 null /* before */,
			                                                 null /* after */,
			                                                 true /* show all */);
			
			assert submissions.size() == 1;
			return submissions.get(0);
		} catch (RetrievalFailedException ex) {
			ex.printStackTrace();
		} catch (RedditError ex) {
			ex.printStackTrace();
		}
		
		throw new RuntimeException("Unable to get submission");
	}

	public LocalDateTime getStartOfSubreddit(String name) {
		Subreddits subrs = getSubreddits();

		List<Subreddit> subreddits = subrs.search(name /* query */,
		                                          0 /* count */,
		                                          1 /* limit */,
		                                          null /* before */,
		                                          null /* after */);
		assert subreddits.size() == 1;
		Subreddit subreddit = subreddits.get(0);

		System.out.println("Got subreddit "
		                   + subreddit.getTitle() + " : "
		                   + subreddit.getDisplayName() + " : "
		                   + subreddit.getIdentifier() + " : "
		                   + subreddit.getCreatedUTC());
		
		Instant instant = Instant.ofEpochSecond((long)subreddit.getCreatedUTC());
		LocalDateTime localDateTime = LocalDateTime.ofInstant(instant,
		                                                      ZoneId.of("UTC"));
		
		return localDateTime;
	}
}
