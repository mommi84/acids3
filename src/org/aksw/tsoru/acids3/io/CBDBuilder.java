package org.aksw.tsoru.acids3.io;

import org.aksw.tsoru.acids3.algorithm.Parameters;
import org.aksw.tsoru.acids3.model.Instance;
import org.aksw.tsoru.acids3.util.Cache;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.riot.system.StreamRDF;
import org.apache.log4j.Logger;

import com.hp.hpl.jena.graph.Triple;
import com.hp.hpl.jena.sparql.core.Quad;

/**
 * @author Tommaso Soru <t.soru@informatik.uni-leipzig.de>
 *
 */
public class CBDBuilder {

	private static final Logger LOGGER = Logger.getLogger(CBDBuilder.class);
	
	protected static Instance build(Processing p, Instance inst) {
		
		final Instance instance = inst;
		
		final Cache cache = p.getCache();
		final Arg arg = p.getArg();
		String base = Processing.getBase();
		Parameters param = p.getParam();
		
		final String uri = cache.instance.getURI();

		StreamRDF dest = new StreamRDF() {
			
			@Override
			public void triple(Triple triple) {
				if(uri.equals(triple.getSubject().getURI()) || uri.equals(triple.getObject().toString()))
					instance.addTriple(triple);
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
		
		LOGGER.info("Instance CBD size = "+instance.getTriples().size());
		return instance;
		
	}

}
