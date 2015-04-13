package org.aksw.tsoru.acids3.io;

import java.util.ArrayList;
import java.util.Collections;

import org.aksw.tsoru.acids3.algorithm.Parameters;
import org.aksw.tsoru.acids3.db.SQLiteManager;
import org.aksw.tsoru.acids3.db.Tuple;
import org.aksw.tsoru.acids3.filters.ReededFilter;
import org.aksw.tsoru.acids3.model.Example;
import org.aksw.tsoru.acids3.model.Instance;
import org.aksw.tsoru.acids3.similarity.OverallSimilarity;
import org.apache.log4j.Logger;

/**
 * @author Tommaso Soru <t.soru@informatik.uni-leipzig.de>
 *
 */
public class GetTopMatches {

	private static final Logger LOGGER = Logger.getLogger(GetTopMatches.class);
	
	protected static ArrayList<Example> get(Processing p, Instance src, final ReededFilter filter) {
	
		LOGGER.info("Computing similarity matches for property alignment...");
		
		ArrayList<Example> results = new ArrayList<Example>();
		
		SQLiteManager sql = p.getSql();
		
		OverallSimilarity osim = new OverallSimilarity();
		
		for(String uri : p.getIndex()) {
			
			LOGGER.debug("Trying with <"+uri+">...");
			ArrayList<Tuple> cbd = sql.getTuples(uri);
			Instance inst = new Instance(uri);
			inst.setProcessing(p);
			
			for(Tuple t : cbd) {
				if(t.getS().equals(uri))
					inst.addTuple(t);
				else
					inst.addInverseTuple(t);
			}
			
			LOGGER.debug("CBD size = "+inst.getTuples().size());
			
			// similarity call
			Example ex = new Example(src, inst);
			Double sim = osim.compute(ex, filter);
			if(sim != null) { // example made the cut
				ex.setSim(sim);
				LOGGER.debug("sim = "+sim);
			} else // example didn't make the cut
				continue;
			results.add(ex);
			
			// TODO sort every N loops?
			Collections.sort(results, new OrderBySimDesc());
			for(int i=Parameters.EX_PER_QUERY; i<results.size(); i++)
				results.remove(i);
			
		}
		
		
		LOGGER.info("Examples to label: " + results);
	
		return results;
	}

}
