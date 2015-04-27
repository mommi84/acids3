package org.aksw.tsoru.acids3.io;

import java.util.ArrayList;
import java.util.HashMap;

import org.aksw.tsoru.acids3.db.SQLiteManager;
import org.aksw.tsoru.acids3.db.Tuple;
import org.aksw.tsoru.acids3.model.Instance;
import org.apache.log4j.Logger;

/**
 * 
 * @author Tommaso Soru <tsoru@informatik.uni-leipzig.de>
 *
 */
public class CBDBuilder {

	private static final Logger LOGGER = Logger.getLogger(CBDBuilder.class);
	
	protected static void build(Processing p, final ArrayList<Instance> instances) {
			
		final HashMap<String, Instance> uriToInstance = new HashMap<String, Instance>();
		for(Instance in : instances)
			uriToInstance.put(in.getID(), in);
		
		SQLiteManager sql = p.getSql();
		
		for(Instance in : instances) {
			
			String uri = in.getID();
			for(Tuple t : sql.getTuples(uri)) {
				if(t.getS().equals(uri))
					in.add(t);
				else
					in.addInverse(t);
			}
			
			LOGGER.trace(uri+" has now "+in.getTuples().size()+" triples.");
			
		}
				
	}

}
