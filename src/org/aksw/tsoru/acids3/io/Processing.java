package org.aksw.tsoru.acids3.io;

import java.util.TreeSet;

import org.aksw.tsoru.acids3.algorithm.Parameters;
import org.aksw.tsoru.acids3.db.SQLiteManager;
import org.aksw.tsoru.acids3.model.Instance;
import org.aksw.tsoru.acids3.util.Cache;
import org.apache.log4j.Logger;

/**
 * @author Tommaso Soru <t.soru@informatik.uni-leipzig.de>
 *
 */
public class Processing {
	
	@SuppressWarnings("unused")
	private static final Logger LOGGER = Logger.getLogger(Processing.class);
	private static String base = "file://" + System.getProperty("user.dir") + "/";
	
	private SQLiteManager sql;
	
	private final Arg arg;
	private Parameters param;
	private TreeSet<String> index;
	
	private final Cache cache;
	
	public Processing(final Arg arg, Parameters param) {
		super();
		this.cache = new Cache();
		this.arg = arg;
		this.param = param;
		sql = new SQLiteManager(param.getPath(arg));
	}
	
	public SQLiteManager getSql() {
		return sql;
	}
	

	public static String getBase() {
		return base;
	}

	public Arg getArg() {
		return arg;
	}

	public Parameters getParam() {
		return param;
	}

	public Cache getCache() {
		return cache;
	}

	public Integer count() {
		// use cache when available
		if(cache.nTriples != null)
			return cache.nTriples;
		return TripleCount.count(this);
	}
	
	public Instance randomPick() {
		// random pick requires count
		if(cache.nTriples == null)
			this.count();
		Instance e = RandomInstance.get(this);
		CBDBuilder.build(this, e);
		return e;
	}

	public void index() {
		index = Indexer.index(this);
	}

	public TreeSet<String> getIndex() {
		return index;
	}

	public void topMatches(Instance src) {
		GetTopMatches.get(this, src);
	}
	
	public void close() {
		sql.close();
	}

}