package org.aksw.tsoru.acids3.io;

import java.util.ArrayList;
import java.util.HashMap;

import org.aksw.tsoru.acids3.algorithm.Parameters;
import org.aksw.tsoru.acids3.model.Instance;
import org.aksw.tsoru.acids3.util.URLs;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.riot.system.StreamRDF;
import org.apache.log4j.Logger;

import com.hp.hpl.jena.graph.Triple;
import com.hp.hpl.jena.sparql.core.Quad;

/**
 * TODO Base this on SQLite.
 * 
 * @author Tommaso Soru <tsoru@informatik.uni-leipzig.de>
 *
 */
public class CBDBuilder {

	private static final Logger LOGGER = Logger.getLogger(CBDBuilder.class);
	
	protected static void build(Processing p, final ArrayList<Instance> instances) {
		
		
		final HashMap<String, Instance> uriToInstance = new HashMap<String, Instance>();
		for(Instance in : instances)
			uriToInstance.put(in.getURI(), in);
		
		final Arg arg = p.getArg();
		String base = Processing.getBase();
		Parameters param = p.getParam();
		
		final ArrayList<String> uris = new ArrayList<String>();
		for(Instance in : instances)
			uris.add(in.getURI());
		
		StreamRDF dest = new StreamRDF() {
			
			@Override
			public void triple(Triple triple) {
				
				String p = triple.getPredicate().getURI();
				if(p.equals(URLs.RDF_TYPE))
					return;
				
				String subjURI = triple.getSubject().getURI();
				if(uris.contains(subjURI))
					uriToInstance.get(subjURI).add(triple);
				// TODO This way vs having inverse triples into DB?
				String objURI = triple.getObject().toString();
				if(uris.contains(objURI))
					uriToInstance.get(objURI).addInverse(triple);
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
		
		
	}

}
