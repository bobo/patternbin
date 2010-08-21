package se.cleancode.patterns.client;

import java.util.List;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * The client side stub for the RPC service.
 */
@RemoteServiceRelativePath("pattern")
public interface PatternService extends RemoteService {
	public String storePattern(Pattern p, String name) throws IllegalArgumentException;
	public Pattern get(String pattern);
	public List<String> getLatestPublic();
	
}
