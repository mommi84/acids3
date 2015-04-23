package org.aksw.tsoru.acids3.io;

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
	
	protected static Instance get(Processing p) {
		
		final Cache cache = p.getCache();
		final Arg arg = p.getArg();
		String base = Processing.getBase();
		Parameters param = p.getParam();

		cache.pick = (int) (cache.nTriples * new Random().nextDouble());
		LOGGER.debug("Random source index = "+cache.pick);

		// TODO use reservoir sampling to traverse the files only once
		
		StreamRDF dest = new StreamRDF() {
			
			@Override
			public void triple(Triple triple) {
				
				String p = triple.getPredicate().getURI();
				if(p.equals(URLs.RDF_TYPE))
					return;
				
				cache.i++;
				
				if(cache.pick != null && cache.i == cache.pick) {
					cache.instance = new Instance(triple.getSubject().getURI());
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
		RDFDataMgr.parse(dest, base + param.getSourcePath());
		
		LOGGER.info("Instance URI = "+cache.instance.getURI());
		return cache.instance;
		
	}

}
