package org.aksw.tsoru.acids3.io;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeSet;

import org.aksw.tsoru.acids3.algorithm.Parameters;
import org.aksw.tsoru.acids3.db.SQLiteManager;
import org.aksw.tsoru.acids3.filters.ReededFilter;
import org.aksw.tsoru.acids3.model.Example;
import org.aksw.tsoru.acids3.model.Instance;
import org.aksw.tsoru.acids3.similarity.LogarithmicSimilarity;
import org.aksw.tsoru.acids3.util.Cache;
import org.apache.log4j.Logger;

/**
 * @author Tommaso Soru <tsoru@informatik.uni-leipzig.de>
 *
 */
public class Processing {
	
	@SuppressWarnings("unused")
	private static final Logger LOGGER = Logger.getLogger(Processing.class);
	private static String base = "file://" + System.getProperty("user.dir") + "/";
	
	// TODO Replace this with a simple cache for "extrema".
	private HashMap<String, LogarithmicSimilarity> logsims = new HashMap<String, LogarithmicSimilarity>();
	
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
	
	public ArrayList<Instance> randomPick(final int N) {
		// random pick requires count
		if(cache.nTriples == null)
			this.count();
		ArrayList<Instance> instances = RandomInstance.get(this, N);
		CBDBuilder.build(this, instances);
		return instances;
	}
	
	public Instance randomPick() {
		return randomPick(1).get(0);
	}

	public void index() {
		index = Indexer.index(this);
	}

	public TreeSet<String> getIndex() {
		return index;
	}

	public ArrayList<Example> topMatches(Instance src, final ReededFilter filter) {
		return GetTopMatches.get(this, src, filter);
	}
	
	public void close() {
		sql.close();
	}
	
	public LogarithmicSimilarity getLogsim(String p) {
		return logsims.get(p);
	}
	
	public void setLogsim(String p, LogarithmicSimilarity logsim) {
		logsims.put(p, logsim);
	}

}