package org.aksw.tsoru.acids3.io;

import java.util.ArrayList;
import java.util.Collections;

import org.aksw.tsoru.acids3.algorithm.Parameters;
import org.aksw.tsoru.acids3.db.SQLiteManager;
import org.aksw.tsoru.acids3.db.Tuple;
import org.aksw.tsoru.acids3.model.Example;
import org.aksw.tsoru.acids3.model.Instance;
import org.aksw.tsoru.acids3.similarity.Similarity;
import org.apache.log4j.Logger;

/**
 * @author Tommaso Soru <t.soru@informatik.uni-leipzig.de>
 *
 */
public class GetTopMatches {

	private static final Logger LOGGER = Logger.getLogger(GetTopMatches.class);
	
	protected static ArrayList<Example> get(Processing p, Instance src) {
	
		ArrayList<Example> results = new ArrayList<Example>();
		
		SQLiteManager sql = p.getSql();
		Parameters param = p.getParam();
		
		for(String uri : p.getIndex()) {
			
			LOGGER.debug("Trying with <"+uri+">...");
			ArrayList<Tuple> cbd = sql.getTuples(uri);
			Instance inst = new Instance(uri);
			
			for(Tuple t : cbd) {
				if(t.getS().equals(uri))
					inst.addTuple(t);
				else
					inst.addInverseTuple(t);
			}
			
			LOGGER.debug("CBD size = "+inst.getTuples().size());
			
			Example ex = new Example(src, inst);
			ex.setSim(Similarity.sim(ex));
			results.add(ex);
			
			Collections.sort(results, new OrderBySimDesc());
			for(int i=param.MAX_EX_ROUND; i<results.size(); i++)
				results.remove(i);
			
		}
		
		LOGGER.info("Examples to label: " + results);
		
	
		return null;
	}

}
