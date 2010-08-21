package se.cleancode.patterns.client;

import java.util.List;

import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * The async counterpart of <code>GreetingService</code>.
 */
public interface PatternServiceAsync {
	void get(String pattern, AsyncCallback<Pattern> callback);

	void storePattern(Pattern p, String name, AsyncCallback<String> callback);

	void getLatestPublic(AsyncCallback<List<String>> callback);
}
