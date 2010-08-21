package se.cleancode.patterns.server;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;

import se.cleancode.patterns.client.Pattern;
import se.cleancode.patterns.client.PatternService;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Text;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.SortDirection;
import com.google.appengine.api.memcache.MemcacheService;
import com.google.appengine.api.memcache.MemcacheServiceFactory;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

/**
 * The server side implementation of the RPC service.
 */
@SuppressWarnings("serial")
public class PatternServiceImpl extends RemoteServiceServlet implements
		PatternService {

	private static final Logger log = Logger.getLogger(PatternServiceImpl.class
			.getName());
	DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

	public String storePattern(Pattern input, String name)
			throws IllegalArgumentException {

		String patternText = escapeHtml(input.getPatternText());
		Query query = new Query("pattern").addFilter("name",
				FilterOperator.EQUAL, name);
		if (datastore.prepare(query).countEntities() > 0)
			throw new IllegalArgumentException("Name taken, try another one");
		Entity e = new Entity("pattern");
		e.setProperty("name", name);
		e.setProperty("p", new Text(patternText));
		e.setProperty("rows", input.getRows());
		e.setProperty("columns", input.getColumns());
		e.setProperty("public", input.isPublic());
		e.setProperty("time", System.currentTimeMillis());
		datastore.put(e);

		if (input.isPublic()) {
			updateCache(name);
		}
		return name;
	}

	private void updateCache(String name) {
		MemcacheService cache = initCache();
		Integer pos = (Integer) cache.get("Latest_key");
		pos= pos==null? 1 : pos.intValue();
//		for (int i = 5; i > 1; i--) {
//			cache.put("recent_" + i, cache.get("recent_" + (i - 1)));
//		}
		System.out.println("putting: "+name+" at: "+pos%5);
		cache.put("recent_"+pos%5, name);
		cache.put("Latest_key",++pos);
	}

	private MemcacheService initCache() {
		MemcacheService service = MemcacheServiceFactory.getMemcacheService();
		return service;
		
	}

	public Pattern get(String pattern) {
		DatastoreService datastore = DatastoreServiceFactory
				.getDatastoreService();
		Query query = new Query("pattern").addFilter("name",
				FilterOperator.EQUAL, pattern);
		Entity result = datastore.prepare(query).asSingleEntity();

		if (result == null)
			return new Pattern("", "", 20, 20, true);
		Pattern p = createPatternFromEntity(result);
		return p;
	}

	public List<String> getLatestPublic() {
		List<String> patterns = new ArrayList<String>();
		List<String> cached = getCachedLatest();
		if (cached.size() > 0)
			return cached;
		patterns = getFromStorage();
		Collections.reverse(patterns);
		updateCache(patterns);
		return patterns;

	}

	private void updateCache(List<String> patterns) {
		for(String p : patterns) {
			updateCache(p);
		}
	}

	private List<String> getFromStorage() {
		List<String> patterns = new ArrayList<String>();
		Query q = new Query("pattern").addFilter("public",
				FilterOperator.EQUAL, Boolean.TRUE).addSort("time",
				SortDirection.DESCENDING);

		Iterable<Entity> entityIterator = datastore.prepare(q).asIterable();
		for (Entity e : entityIterator) {
			if (patterns.size() < 5) {
				Pattern p = createPatternFromEntity(e);
				patterns.add(p.getName());
			}
		}
		return patterns;
	}

	private List<String> getCachedLatest() {
		List<String> result = new ArrayList<String>();
		MemcacheService cache = initCache();
	
		Integer pos = (Integer) cache.get("Latest_key");
		System.out.println("pos: "+pos);
		pos = pos==null? 0 : pos.intValue();
		for (int i = 0; i < 5; i++) {
			int getInt = ((pos+i) % 5) ;
			String name = (String) cache.get("recent_" + getInt);
			if (name != null)
				result.add(name);
		}
		return result;
	}

	private Pattern createPatternFromEntity(Entity result) {
		Text value = (Text) result.getProperty("p");
		String patternText = value == null ? "" : value.getValue();
		String name = result.getProperty("name") == null ? "" : (String) result
				.getProperty("name");
		int rows = getIntValue(result, "rows", 20);
		int columns = getIntValue(result, "columns", 20);
		Pattern p = new Pattern(name, patternText, rows, columns);

		return p;
	}

	private int getIntValue(Entity result, String property, int def) {
		Long columns = (Long) result.getProperty(property);
		int intval = columns == null ? def : columns.intValue();
		return intval;
	}

	private String escapeHtml(String html) {
		if (html == null) {
			return null;
		}
		return html.replaceAll("&", "&amp;").replaceAll("<", "&lt;")
				.replaceAll(">", "&gt;");
	}
}
