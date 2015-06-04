package org.aksw.tsoru.acids3.io;

import java.util.ArrayList;
import java.util.Random;

import org.aksw.tsoru.acids3.algorithm.Parameters;
import org.aksw.tsoru.acids3.model.Instance;
import org.aksw.tsoru.acids3.util.Cache;
import org.aksw.tsoru.acids3.util.URLs;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.riot.system.StreamRDF;
import org.apache.log4j.Logger;

import com.hp.hpl.jena.graph.Triple;
import com.hp.hpl.jena.sparql.core.Quad;

/**
 * @author Tommaso Soru <tsoru@informatik.uni-leipzig.de>
 *
 */
public class RandomInstance {

	private static final Logger LOGGER = Logger.getLogger(RandomInstance.class);
	
	/**
	 * Use a seed for deterministic behavior.
	 */
//	private static final long SEED = 123;
	
	protected RandomInstance() {
		super();
	}
	
	protected static ArrayList<Instance> get(Processing p, final int N) {
		
		final Cache cache = p.getCache();
		final Arg arg = p.getArg();
		String base = Processing.getBase();
		Parameters param = p.getParam();

		// extract `N` different random numbers
		for(int i=0; i<N; i++) {
			int n;
			do {
				n = (int) (cache.nTriples * new Random().nextDouble());
			} while(cache.pick.contains(n));
			cache.pick.add(n);
		}
		
		LOGGER.debug("Random source indices = "+cache.pick);

		// TODO use reservoir sampling to traverse the files only once
		
		StreamRDF dest = new StreamRDF() {
			
			@Override
			public void triple(Triple triple) {
				
				String p = triple.getPredicate().getURI();
				if(p.equals(URLs.RDF_TYPE))
					return;
				
				cache.i++;
				
				if(cache.pick != null && cache.pick.contains(cache.i)) {
					cache.instances.add(new Instance(triple.getSubject().getURI()));
					// TODO may halt to save runtime
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
		RDFDataMgr.parse(dest, base + param.getPath(p.getArg()));
		
		for(Instance inst : cache.instances) {
			inst.setProcessing(p);
			LOGGER.trace("Instance URI = "+inst.getID());
		}
		
		return cache.instances;
		
	}

}
