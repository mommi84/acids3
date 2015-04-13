package org.aksw.tsoru.acids3.io;

import org.aksw.tsoru.acids3.algorithm.Parameters;
import org.aksw.tsoru.acids3.util.Cache;
import org.aksw.tsoru.acids3.util.URLs;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.riot.system.StreamRDF;
import org.apache.log4j.Logger;

import com.hp.hpl.jena.graph.Triple;
import com.hp.hpl.jena.sparql.core.Quad;

/**
 * @author Tommaso Soru <t.soru@informatik.uni-leipzig.de>
 *
 */
public class TripleCount {

	private static final Logger LOGGER = Logger.getLogger(TripleCount.class);
	
	protected static Integer count(Processing p) {
		
		final Cache cache = p.getCache();
		final Arg arg = p.getArg();
		String base = Processing.getBase();
		Parameters param = p.getParam();
		
		StreamRDF dest = new StreamRDF() {
			
			@Override
			public void triple(Triple triple) {
				
				String p = triple.getPredicate().getURI();
				if(p.equals(URLs.RDF_TYPE))
					return;
				
				cache.i++;
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
		LOGGER.info("# of triples ("+arg.getName()+") = " + cache.nTriples);
		return cache.nTriples;
		
	}

}
