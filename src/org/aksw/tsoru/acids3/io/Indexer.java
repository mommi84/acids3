package org.aksw.tsoru.acids3.io;

import java.util.HashMap;
import java.util.TreeSet;

import org.aksw.tsoru.acids3.algorithm.Parameters;
import org.aksw.tsoru.acids3.db.SQLiteManager;
import org.aksw.tsoru.acids3.similarity.LogarithmicSimilarity;
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
public class Indexer {

	private static final Logger LOGGER = Logger.getLogger(Indexer.class);
	
	protected static TreeSet<String> index(Processing p) {
		
		final TreeSet<String> instances = new TreeSet<String>();
		
		final SQLiteManager sql = p.getSql();

		final Arg arg = p.getArg();
		String base = Processing.getBase();
		Parameters param = p.getParam();
		
		final HashMap<String, Cache> cache = new HashMap<String, Cache>();
		
		LOGGER.info("Indexing of "+arg.getName()+" started.");
		
		StreamRDF dest = new StreamRDF() {
			
			@Override
			public void triple(Triple triple) {
				
				String p = triple.getPredicate().getURI();
				if(p.equals(URLs.RDF_TYPE))
					return;
					
				sql.insert(triple);
				instances.add(triple.getSubject().getURI());
				
				if(triple.getObject().isURI())
					instances.add(triple.getObject().getURI());
				else {
					// compute minimum and maximum for double values
					Double d = null;
					try {
						d = Double.parseDouble("" + triple.getObject().getLiteral().getValue());
					} catch (NumberFormatException | UnsupportedOperationException e) {
						// not numerical or blank node => next triple
						return;
					}
					if(!cache.containsKey(p))
						cache.put(p, new Cache());
					if(d > cache.get(p).max)
						cache.get(p).max = d;
					if(d < cache.get(p).min)
						cache.get(p).min = d;
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
		
		RDFDataMgr.parse(dest, base + param.getPath(arg));
		
		sql.commit();
		
		LOGGER.info("Index done.");
		for(String pr : cache.keySet()) {
			LOGGER.debug(pr+": Max = "+cache.get(pr).max+", Min = "+cache.get(pr).min);
			
			LogarithmicSimilarity logsim = new LogarithmicSimilarity();
			logsim.setMinMin(cache.get(pr).min);
			logsim.setDenomArg(cache.get(pr).max);
			p.setLogsim(pr, logsim);
			
		}
		
		return instances;
		
	}

}
