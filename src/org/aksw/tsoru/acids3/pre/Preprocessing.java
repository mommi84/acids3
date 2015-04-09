package org.aksw.tsoru.acids3.pre;

import org.aksw.tsoru.acids3.algorithm.Parameters;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.riot.system.StreamRDF;
import org.apache.log4j.Logger;

import com.hp.hpl.jena.graph.Triple;
import com.hp.hpl.jena.sparql.core.Quad;

/**
 * @author Tommaso Soru <t.soru@informatik.uni-leipzig.de>
 *
 */
public class Preprocessing {
	
	private static final Logger LOGGER = Logger.getLogger(Preprocessing.class);

	private static String base = "file://" + System.getProperty("user.dir") + "/";
	
	public static void scroll(Arg arg, Parameters param, Action action) {
		
		StreamRDF dest = new StreamRDF() {
			
			@Override
			public void triple(Triple triple) {
//				LOGGER.info(triple.getSubject().getURI());
				// TODO action!
			}
			
			@Override
			public void start() {
				LOGGER.info("");
			}
			
			@Override
			public void finish() {
				LOGGER.info("Done.");
			}
			
			@Override
			public void quad(Quad quad) {}
			
			@Override
			public void prefix(String prefix, String iri) {}
			
			@Override
			public void base(String base) {}
		};
		
		RDFDataMgr.parse(dest, base + param.getSourcePath());
		
	}
	
	public static void main(String[] args) {
		Parameters param = new Parameters();
		param.setSourcePath("data/ceur-ws.ttl");
		param.setTargetPath("data/colinda.nt");
		param.setOraclePath("data/oracle-person1.csv");
		scroll(Arg.SOURCE, param, Action.COUNT);
	}

}
