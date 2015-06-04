package org.aksw.tsoru.acids3.io;

import org.aksw.tsoru.acids3.util.Cache;
import org.apache.log4j.Logger;

/**
 * @author Tommaso Soru <tsoru@informatik.uni-leipzig.de>
 *
 */
public class TripleCount {

	private static final Logger LOGGER = Logger.getLogger(TripleCount.class);
	
	protected static Integer count(Processing p) {
		
		Cache cache = p.getCache();
		Arg arg = p.getArg();
		
		int n = p.getSql().count();
		
		cache.setNTriples(n);
		LOGGER.info("# of triples ("+arg.getName()+") = " + cache.nTriples);
		return n;
		
	}

}
