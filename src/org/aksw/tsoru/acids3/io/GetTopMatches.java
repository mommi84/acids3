package org.aksw.tsoru.acids3.io;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import org.aksw.tsoru.acids3.algorithm.Parameters;
import org.aksw.tsoru.acids3.model.Example;
import org.aksw.tsoru.acids3.model.Instance;
import org.aksw.tsoru.acids3.similarity.Similarity;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.riot.system.StreamRDF;
import org.apache.log4j.Logger;

import com.hp.hpl.jena.graph.Triple;
import com.hp.hpl.jena.sparql.core.Quad;

/**
 * @author Tommaso Soru <t.soru@informatik.uni-leipzig.de>
 *
 */
public class GetTopMatches {

	private static final Logger LOGGER = Logger.getLogger(GetTopMatches.class);
	
	protected static ArrayList<Example> get(Processing p, Instance src) {
	
		ArrayList<Example> results = new ArrayList<Example>();
		
		final Cache cache = p.getCache();
		final Arg arg = p.getArg();
		String base = Processing.getBase();
		Parameters param = p.getParam();
		
		HashMap<String, ArrayList<Integer>> index = Indexer.getIndex();
		
		for(String uri : index.keySet()) {
			
			LOGGER.debug("Trying with <"+uri+">...");
			
			final ArrayList<Integer> indices = index.get(uri);
			final Instance inst = new Instance(uri);
			
			StreamRDF dest = new StreamRDF() {
				
				@Override
				public void triple(Triple triple) {
					cache.i++;
					if(indices.contains(cache.i))
						inst.addTriple(triple);
				}
				
				@Override
				public void start() {
					LOGGER.debug("Scrolling of "+arg.getName()+" started.");
				}
				
				@Override
				public void finish() {
					LOGGER.debug("Scrolling of "+arg.getName()+" finished.");
				}
				
				@Override
				public void quad(Quad quad) {}
				
				@Override
				public void prefix(String prefix, String iri) {}
				
				@Override
				public void base(String base) {}
				
			};
			
			cache.iReset();
			RDFDataMgr.parse(dest, base + param.getTargetPath());
			
			LOGGER.debug("CBD size = "+inst.getTriples().size());
			
			Example ex = new Example(src, inst);
			
			ex.setSim(Similarity.sim(ex));
			
			results.add(ex);
			
			Collections.sort(results, new OrderBySimDesc());
			
			LOGGER.debug(results);
		}
		
	
		return null;
	}

}
