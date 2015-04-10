package org.aksw.tsoru.acids3.tdb;

import java.util.ArrayList;

import org.aksw.tsoru.acids3.algorithm.Parameters;
import org.aksw.tsoru.acids3.io.Arg;
import org.aksw.tsoru.acids3.io.Processing;
import org.aksw.tsoru.acids3.model.Instance;
import org.aksw.tsoru.acids3.util.Cache;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.riot.system.StreamRDF;
import org.apache.log4j.Logger;

import com.hp.hpl.jena.graph.Triple;
import com.hp.hpl.jena.query.Dataset;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.ReadWrite;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.sparql.core.Quad;
import com.hp.hpl.jena.tdb.TDBFactory;

/**
 * 
 * WARNING: Experimental.
 * 
 * @author Tommaso Soru <t.soru@informatik.uni-leipzig.de>
 *
 */
public class TDBManager {

	private static final Logger LOGGER = Logger.getLogger(TDBManager.class);

	public static final int BUFFER_SIZE = 1000;

	private Parameters param;
	private Arg arg;

	private Dataset dataset;

	public TDBManager(Parameters param, Arg arg) {
		super();
		this.param = param;
		this.arg = arg;
		LOGGER.warn("Experimental feature.");
	}

	public void build() {
		// Make a TDB-backed dataset
		String directory = param.getDir(arg);
		dataset = TDBFactory.createDataset(directory);
		
		System.out.println(directory);

		// Get model inside the transaction
		Model model = dataset.getDefaultModel();
		
		final Model modelF = model;
		final Dataset datasetF = dataset;
		final Cache cache = new Cache();
		final Model buffermodel = ModelFactory.createDefaultModel();
				
		StreamRDF dest = new StreamRDF() {

			@Override
			public void triple(Triple triple) {
				
//				if (cache.i % BUFFER_SIZE == 0)
//					dataset.begin(ReadWrite.WRITE);
				
				buffermodel.add(buffermodel.asStatement(triple));
				
				
				if (cache.i % BUFFER_SIZE == BUFFER_SIZE - 1) {
					dataset.begin(ReadWrite.WRITE);
					
					modelF.add(buffermodel);
					
					datasetF.commit();
					datasetF.end();
					System.out.println("Committed "+buffermodel.size()+ " (tot="+modelF.size()+ ") triples");
					buffermodel.removeAll();
				}

				cache.i++;
			}

			@Override
			public void start() {
			}

			@Override
			public void finish() {
			}

			@Override
			public void quad(Quad quad) {
			}

			@Override
			public void prefix(String prefix, String iri) {
			}

			@Override
			public void base(String base) {
			}

		};
		RDFDataMgr.parse(dest, Processing.getBase() + param.getPath(arg));

		if(!buffermodel.isEmpty()) {
			datasetF.commit();
			datasetF.end();
		}
		
		cache.iReset();
	}

	public ArrayList<Triple> getTriples(Instance inst) {

		dataset.begin(ReadWrite.READ);

		ArrayList<Triple> results = new ArrayList<Triple>();

		String qs1 = "DESCRIBE <" + inst.getURI() + ">";

		try (QueryExecution qExec = QueryExecutionFactory.create(qs1, dataset)) {
			Model m = qExec.execDescribe();
			StmtIterator it = m.listStatements();
			while (it.hasNext()) {
				Triple t = it.next().asTriple();
				results.add(t);
				System.out.println(t);
			}
		} finally {
			dataset.end();
		}

		return results;

	}

	public static void main(String[] args) {
		Parameters param = new Parameters();
		param.setSourcePath("data/ceur-ws/ceur-ws.ttl");
//		param.setSourcePath("data/person11/person11.nt");
		param.setTargetPath("data/colinda/colinda.nt");
		TDBManager tdb = new TDBManager(param, Arg.SOURCE);
		tdb.build();
		for (Triple t : tdb.getTriples(new Instance(
				"http://www.okkam.org/oaie/person1-Person330")))
			LOGGER.info(t);
	}

}
