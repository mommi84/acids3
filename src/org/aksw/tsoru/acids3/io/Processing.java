package org.aksw.tsoru.acids3.io;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeSet;

import org.aksw.tsoru.acids3.algorithm.Parameters;
import org.aksw.tsoru.acids3.db.SQLiteManager;
import org.aksw.tsoru.acids3.db.Tuple;
import org.aksw.tsoru.acids3.filters.AllowedFilter;
import org.aksw.tsoru.acids3.model.Example;
import org.aksw.tsoru.acids3.model.Instance;
import org.aksw.tsoru.acids3.sim.SimType;
import org.aksw.tsoru.acids3.sim.value.LogarithmicSimilarity;
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
	
	/**
 	 * TODO Arrange this better...
	 * Map between feature names and their similarity type counts.
	 */
	private static HashMap<String, HashMap<SimType, Integer>> map = new HashMap<String, HashMap<SimType, Integer>>();
	
	private SQLiteManager sql;

	private TreeSet<String> hubs, auths;
	
	private final Arg arg;
	private Parameters param;
	private TreeSet<String> index;
	
	private final Cache cache;
	
	public Processing(final Arg arg, Parameters param) {
		super();
		this.cache = new Cache();
		this.arg = arg;
		this.param = param;
		sql = new SQLiteManager(this, param.getPath(arg));
		hubs = new TreeSet<String>();
		auths = new TreeSet<String>();
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
		CBDBuilder.build(instances);
		return instances;
	}
	
	public Instance randomPick() {
		return randomPick(1).get(0);
	}

	public void index() {
		index = Indexer.index(this);
	}

	public TreeSet<String> getIndex() {
		// TODO Make this scalable by querying the database and ask for .nextURI()
		return index;
	}

	public ArrayList<Example> topMatches(Instance src, final ArrayList<AllowedFilter> allowedFilters) {
		return GetTopMatches.get(this, src, allowedFilters);
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
	
	public TreeSet<String> getHubs() {
		return hubs;
	}

	public TreeSet<String> getAuths() {
		return auths;
	}

	public void setHubs(TreeSet<String> hubs) {
		this.hubs = hubs;
	}

	public void setAuths(TreeSet<String> auths) {
		this.auths = auths;
	}
	
	public void countFeatureRecord(String fname, SimType type) {
		HashMap<SimType, Integer> subMap;
		if(map.containsKey(fname)) {
			subMap = map.get(fname);
		} else {
			subMap = new HashMap<SimType, Integer>();
			map.put(fname, subMap);
		}
		if(subMap.containsKey(type))
			subMap.put(type, subMap.get(type) + 1);
		else
			subMap.put(type, 1);
	}
	
	public SimType simTypeOf(String fname) {
		HashMap<SimType, Integer> subMap = map.get(fname);
		SimType top = null;
		int max = 0;
		for(SimType type : subMap.keySet()) {
			Integer count = subMap.get(type);
			if(count > max) {
				max = count;
				top = type;
			}
		}
		return top;
	}
 
}