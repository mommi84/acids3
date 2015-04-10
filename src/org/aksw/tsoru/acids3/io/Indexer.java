package org.aksw.tsoru.acids3.io;

import java.util.TreeSet;

import org.aksw.tsoru.acids3.algorithm.Parameters;
import org.aksw.tsoru.acids3.db.SQLiteManager;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.riot.system.StreamRDF;
import org.apache.log4j.Logger;

import com.hp.hpl.jena.graph.Triple;
import com.hp.hpl.jena.sparql.core.Quad;

/**
 * @author Tommaso Soru <t.soru@informatik.uni-leipzig.de>
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
		
		LOGGER.info("Indexing of "+arg.getName()+" started.");
		
		StreamRDF dest = new StreamRDF() {
			
			@Override
			public void triple(Triple triple) {
				sql.insert(triple);
				instances.add(triple.getSubject().getURI());
				if(triple.getObject().isURI())
					instances.add(triple.getObject().getURI());
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
		
		return instances;
		
	}

}
