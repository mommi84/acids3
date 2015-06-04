package org.aksw.tsoru.acids3.io;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Vector;

import org.aksw.tsoru.acids3.algorithm.Parameters;
import org.aksw.tsoru.acids3.db.SQLiteManager;
import org.aksw.tsoru.acids3.db.Tuple;
import org.aksw.tsoru.acids3.filters.AllowedFilter;
import org.aksw.tsoru.acids3.filters.ReededFilter;
import org.aksw.tsoru.acids3.model.Example;
import org.aksw.tsoru.acids3.model.Instance;
import org.aksw.tsoru.acids3.model.util.OrderBySimDesc;
import org.aksw.tsoru.acids3.sim.SimilarityController;
import org.aksw.tsoru.acids3.util.Conventions;
import org.apache.log4j.Logger;

/**
 * @author Tommaso Soru <tsoru@informatik.uni-leipzig.de>
 *
 */
public class GetTopMatches {

	private static final Logger LOGGER = Logger.getLogger(GetTopMatches.class);
	
	private static ReededFilter filter = new ReededFilter();
	
	protected static ArrayList<Example> get(Processing p, Instance src, final ArrayList<AllowedFilter> allowedFilters) {
		
//		AllowedFilter measure = allowedFilters.get(0);
//		LOGGER.info("Selected measure to filter: "+measure);
		
		LOGGER.info("Computing similarity matches for property alignment...");
		
		ArrayList<Example> results = new ArrayList<Example>();
		
//		String p1 = Conventions.toFirstProperty(measure.getMeasure());
//		ArrayList<String> srcObj = new ArrayList<String>();
//		for(Tuple t : src.getTuples())
//			if(t.getP().equals(p1))
//				srcObj.add(t.getO());
//		if(srcObj.isEmpty())
//			return results;
//		
//		LOGGER.debug("Preparing to compare SRC objects: "+srcObj);
//		HashMap<String, Vector<Character>> sps = new HashMap<String, Vector<Character>>();
//		for(String sp : srcObj)
//			sps.put(sp, filter.indexSource(sp));
//		filter.computeTau(0.2);
		
		LOGGER.info("Searching for targets...");
		
		for(String uri : p.getIndex()) {
			
			LOGGER.trace("Trying with <"+uri+">...");
			
			// actual filtering
//			ArrayList<Tuple> cbd = sql.getTuples(uri);
//			String p2 = Conventions.toSecondProperty(measure.getMeasure());
//			ArrayList<String> tgtObj = new ArrayList<String>();
//			for(Tuple t : cbd) {
//				if(t.getP().equals(p2))
//					tgtObj.add(t.getO());
//			}
//			boolean passed = false;
//			outer: for(String sp : srcObj)
//				for(String tp : tgtObj)
//					if(filter.filterTargets(sp, sps.get(sp), tp)) {
//						passed = true;
//						break outer;
//					}
//			if(!passed)
//				continue;
			
			Instance tgt = new Instance(uri);
			tgt.setProcessing(p);
			CBDBuilder.build(tgt);
			
			LOGGER.trace("Target CBD size = "+tgt.getTuples().size());
			
			// similarity call
			Example ex = new Example(src, tgt);
			Double sim = SimilarityController.compute(ex).getValue();
			if(sim != null) { // example made the cut
				ex.setSim(sim);
				LOGGER.trace("sim = "+sim);
			} else // example didn't make the cut
				continue;
			results.add(ex);
			
			
		}
		
		// TODO sort every N loops?
		Collections.sort(results, new OrderBySimDesc());
		while(results.size() > Parameters.EX_PER_QUERY)
			results.remove(results.size() - 1);
		
		LOGGER.info("Examples to label: " + results);
	
		return results;
	}

}
