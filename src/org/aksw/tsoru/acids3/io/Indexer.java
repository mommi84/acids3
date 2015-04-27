package org.aksw.tsoru.acids3.io;

import java.util.HashMap;
import java.util.TreeSet;

import org.aksw.tsoru.acids3.algorithm.Parameters;
import org.aksw.tsoru.acids3.db.Field;
import org.aksw.tsoru.acids3.db.SQLiteManager;
import org.aksw.tsoru.acids3.similarity.value.LogarithmicSimilarity;
import org.aksw.tsoru.acids3.util.Cache;
import org.aksw.tsoru.acids3.util.URLs;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.riot.system.StreamRDF;
import org.apache.log4j.Logger;

import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.graph.Triple;
import com.hp.hpl.jena.sparql.core.Quad;

/**
 * TODO change index() to void
 * 
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
		
		final TreeSet<String> ontology = new TreeSet<String>();
		final TreeSet<String> ontoBlankNodes = new TreeSet<String>();
		final TreeSet<String> properties = new TreeSet<String>();
		
		LOGGER.info("Indexing of "+arg.getName()+" started.");
		
		StreamRDF dest = new StreamRDF() {
			
			private void addClass(Node node) {
				if(node.isURI())
					ontology.add(node.getURI());
				else if(node.isBlank())
					ontoBlankNodes.add(node.getBlankNodeLabel());
				else
					LOGGER.warn("Node '"+node.toString()+"' not understood.");
			}
			
			@Override
			public void triple(Triple triple) {
				
				Node subj = triple.getSubject();
				Node pred = triple.getPredicate();
				Node obj = triple.getObject();
				
				String subjURI = subj.getURI();
				String predURI = pred.getURI();
				// collect predicates
				properties.add(predURI);
				
				if(predURI.equals(URLs.RDF_TYPE)) {
					// add classes to the ontology forbidden list
					addClass(obj);
					// if it's type class, add the subject
					if(obj.isURI())
						if(obj.getURI().equals(URLs.RDF_CLASS) || obj.getURI().equals(URLs.OWL_CLASS))
							addClass(subj);
					
					/*
					 * TODO Exploit type information by adding additional features.
					 * For instance, we have classes d1:A, d1:B, d2:A, d2:B.
					 * d1:a rdf:type d1:A
					 * d2:b rdf:type d2:B
					 * Additional boolean features could be the join of classes.
					 * x = [similarities, d1:A_d2:A, d1:A_d2:B, d1:B_d2:A, d1:B_d2:B] 
					 * x_ab = [..., 0, 1, 0, 0]
					 * Also, if d2:A rdfs:subClassOf d2:B
					 * x_ab = [..., 0, 1, 0, 1]
					 */
					return;
				}
				if(predURI.equals(URLs.RDFS_SUBCLASSOF)) {
					// add classes to the ontology forbidden list
					addClass(subj);
					addClass(obj);
					return;
				}
				
				sql.insert(triple);
				instances.add(subjURI);
				
//				sql.add(subjURI, Field.OUTCOMING);
				
				if(obj.isURI()) {
					String objURI = obj.getURI();
					instances.add(objURI);
//					sql.add(objURI, Field.INCOMING);
				} else {
					// compute minimum and maximum for double values
					Double d = null;
					try {
						d = Double.parseDouble("" + triple.getObject().getLiteral().getValue());
					} catch (NumberFormatException | UnsupportedOperationException e) {
						// not numerical or blank node => next triple
						return;
					}
					if(!cache.containsKey(predURI))
						cache.put(predURI, new Cache());
					if(d > cache.get(predURI).max)
						cache.get(predURI).max = d;
					if(d < cache.get(predURI).min)
						cache.get(predURI).min = d;
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
		
//		LOGGER.info("Ontology entities found = "+ontology.size());
//		for(String uri : ontology) {
//			LOGGER.trace("Class: "+uri);
//		}
//		LOGGER.info("Deleting unnecessary triples from store...");
//		sql.deleteAll(ontology);
//		instances.removeAll(ontology);
		
//		LOGGER.info("Ontology blank nodes found = "+ontoBlankNodes.size());
//		instances.removeAll(ontoBlankNodes);
		
		instances.removeAll(properties);
		
		return instances;
		
	}

}
