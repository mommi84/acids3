package org.aksw.tsoru.acids3.io;

import java.util.Random;

import org.aksw.tsoru.acids3.algorithm.Algorithm.Action;
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
public class Processing {
	
	private static final Logger LOGGER = Logger.getLogger(Processing.class);
	private static String base = "file://" + System.getProperty("user.dir") + "/";
	
	/**
	 * Use a seed for deterministic behavior.
	 */
	private static final long SEED = 123;
	
	private final Cache cache;
	
	public Processing() {
		super();
		this.cache = new Cache();
	}

	public Object query(final Arg arg, Parameters param, Action action) {
		
		LOGGER.debug("Called "+action+" for "+arg.getName());
		
		if(action == Action.COUNT) {
			if(cache.nTriples != null)
				// use cache when available
				return cache.nTriples;
		}
		
		if(action == Action.RANDOM_PICK) { 
			if(cache.nTriples == null)
				// random pick requires count
				this.query(arg, param, Action.COUNT);
			// will return the pick^th element
			cache.pick = (int) (cache.nTriples * new Random(SEED).nextDouble());
			LOGGER.debug("Random source index = "+cache.pick);
		}
		
		System.out.println(cache.nTriples);
		
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
		
		switch(action) {
		case COUNT:
			cache.saveCount();
			LOGGER.info("# of triples = " + cache.nTriples);
			return cache.nTriples;
		case RANDOM_PICK:
			LOGGER.info("Example URI = "+cache.example.getURI());
			return cache.example;
		}
		
		return null;		
		
	}
	
	public static void main(String[] args) {
		Parameters param = new Parameters();
		param.setSourcePath("data/ceur-ws.ttl");
		param.setTargetPath("data/colinda.nt");
		param.setOraclePath("data/oracle-person1.csv");
		new Processing().query(Arg.SOURCE, param, Action.RANDOM_PICK);
	}

}

class Cache {
	
	int i = 0;
	
	Integer nTriples = null;
	
	Integer pick = null;
	
	Example example = null;

	protected void saveCount() {
		nTriples = new Integer(i);
		i = 0;
	}
	
}
