package org.aksw.tsoru.acids3.io;

import java.util.ArrayList;
import java.util.HashMap;

import org.aksw.tsoru.acids3.algorithm.Parameters;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.riot.system.StreamRDF;
import org.apache.log4j.Logger;

import com.hp.hpl.jena.graph.Triple;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.sparql.core.Quad;

/**
 * @author Tommaso Soru <t.soru@informatik.uni-leipzig.de>
 *
 */
public class Indexer {

	private static final Logger LOGGER = Logger.getLogger(Indexer.class);
	
	private static HashMap<String, ArrayList<Integer>> index = new HashMap<String, ArrayList<Integer>>();
	
	public static HashMap<String, ArrayList<Integer>> getIndex() {
		return index;
	}

	protected static void index(Processing p) {
		
		final Cache cache = p.getCache();
		final Arg arg = p.getArg();
		String base = Processing.getBase();
		Parameters param = p.getParam();
		
		LOGGER.info("Indexing of "+arg.getName()+" started.");
		
		StreamRDF dest = new StreamRDF() {
			
			@Override
			public void triple(Triple triple) {
				
				cache.i++;
				
				String sURI = triple.getSubject().getURI();
				
				if(!index.containsKey(sURI)) {
					ArrayList<Integer> arr = new ArrayList<Integer>();
					arr.add(cache.i);
					index.put(sURI, arr);
				} else {
					index.get(sURI).add(cache.i);
				}

				if(triple.getObject() instanceof Resource) {
					String oURI = triple.getObject().getURI();
					if(!index.containsKey(oURI)) {
						ArrayList<Integer> arr = new ArrayList<Integer>();
						arr.add(cache.i);
						index.put(oURI, arr);
					} else {
						index.get(oURI).add(cache.i);
					}
				}
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
		RDFDataMgr.parse(dest, base + param.getPath(arg));
		
		LOGGER.debug("Index = "+index);
		
	}

}
