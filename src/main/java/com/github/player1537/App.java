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

import java.util.List;

public class App {
	public static void main(String... args) {
		App app = new App();
		app.run();
	}

	private String toBase36(int id) {
		return Integer.toString(id, 36);
	}
	
	private int fromBase36(String id) {
		return Integer.parseInt(id, 36);
	}
	
	/* Sun Dec 8 12:36 */

	private void run() {
		Configuration config = Configuration.create();
		Reddit reddit = new Reddit(config);

		List<Submission> submissions = reddit.byNames("t3_15bfi0");

		for (Submission s : submissions) {
			System.out.println(s);
		}
		
		Submission submission = reddit.mostRecentSubmission();
		System.out.println("Most recent: " 
		                   + submission.getIdentifier() + " : " 
		                   + fromBase36(submission.getIdentifier()));
		
		System.out.println(reddit.getStartOfSubreddit("dogecoin"));
	}
}
