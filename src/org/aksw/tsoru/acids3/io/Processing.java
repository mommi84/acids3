package org.aksw.tsoru.acids3.io;

import org.aksw.tsoru.acids3.algorithm.Parameters;
import org.aksw.tsoru.acids3.model.Example;
import org.apache.log4j.Logger;

/**
 * @author Tommaso Soru <t.soru@informatik.uni-leipzig.de>
 *
 */
public class Processing {
	
	@SuppressWarnings("unused")
	private static final Logger LOGGER = Logger.getLogger(Processing.class);
	private static String base = "file://" + System.getProperty("user.dir") + "/";
	
	private final Arg arg;
	private Parameters param;
	
	private final Cache cache;
	
	public Processing(final Arg arg, Parameters param) {
		super();
		this.cache = new Cache();
		this.arg = arg;
		this.param = param;
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
	
	public static void main(String[] args) {
		Parameters param = new Parameters();
		param.setSourcePath("data/ceur-ws.ttl");
		param.setTargetPath("data/colinda.nt");
		param.setOraclePath("data/oracle-person1.csv");
		new Processing(Arg.SOURCE, param).randomPick();
	}

	public Example randomPick() {
		// random pick requires count
		if(cache.nTriples == null)
			this.count();
		return RandomExample.get(this);
	}

}

class Cache {
	
	int i = 0;
	Integer nTriples = null;
	Integer pick = null;
	Example example = null;

	protected void saveCount() {
		nTriples = new Integer(i);
		i = 0;
	}
	
}
