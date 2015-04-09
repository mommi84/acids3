package org.aksw.tsoru.acids3.io;

import java.util.Random;

import org.aksw.tsoru.acids3.algorithm.Parameters;
import org.aksw.tsoru.acids3.model.Example;
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
	
	/**
	 * Use a seed for deterministic behavior.
	 */
	private static final long SEED = 123;
	
	protected static Example get(Processing p, Example src) {
	/*	
		final Cache cache = p.getCache();
		final Arg arg = p.getArg();
		String base = Processing.getBase();
		Parameters param = p.getParam();

		cache.pick = (int) (cache.nTriples * new Random(SEED).nextDouble());
		LOGGER.debug("Random source index = "+cache.pick);

		StreamRDF dest = new StreamRDF() {
			
			@Override
			public void triple(Triple triple) {
				cache.i++;
				
				if(cache.pick != null && cache.i == cache.pick) {
					cache.example = new Example(triple.getSubject().getURI());
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
		
		RDFDataMgr.parse(dest, base + param.getSourcePath());
		
		cache.saveCount();
		LOGGER.info("Example URI = "+cache.example.getURI());
		
		return cache.example;
		*/
		
		return null;
	}

}
