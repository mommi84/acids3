package org.aksw.tsoru.acids3.evaluation;

import java.util.Iterator;
import java.util.TreeSet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.aksw.tsoru.acids3.io.CBDBuilder;
import org.aksw.tsoru.acids3.io.Processing;
import org.aksw.tsoru.acids3.learner.SMOSVMClassifier;
import org.aksw.tsoru.acids3.model.Example;
import org.aksw.tsoru.acids3.model.Instance;
import org.aksw.tsoru.acids3.sim.SimilarityController;
import org.aksw.tsoru.acids3.util.Oracle;
import org.apache.log4j.Logger;

/**
 * Divide the evaluation into N_THREADS threads. Perform an evaluation every
 * CHUNK_SIZE instances to free memory.
 * 
 * @author Tommaso Soru <tsoru@informatik.uni-leipzig.de>
 *
 */
public class MultiThreadEvaluation {

	private static final int N_THREADS = 3;
//	private static final int CHUNK_SIZE = 1000;

	private static final Logger LOGGER = Logger
			.getLogger(MultiThreadEvaluation.class);

	public static void fmeasure(SMOSVMClassifier svm, Oracle oracle,
			Processing srcPro, Processing tgtPro, TreeSet<String> featureNames)
			throws InterruptedException {
		LOGGER.info("Starting MULTITHREAD F-MEASURE evaluation");

		ExecutorService executor = Executors.newFixedThreadPool(N_THREADS);

		svm.initTest(oracle.getSourceSize());

		LOGGER.info("F-Measure evaluation test set size = "
				+ (srcPro.getIndex().size() * tgtPro.getIndex().size()));
		
		// initialize multi-thread test sets
		for(int i=0; i<N_THREADS; i++)
			svm.addMultiTest(tgtPro.getIndex().size());
		
		tgtPro.getSql().addMultiConnection(N_THREADS + 1);

		Iterator<String> srcIt = srcPro.getIndex().iterator();
		for (int i=0; i<srcPro.getIndex().size(); i++) {
			
			// index of the test set
			int index = i % N_THREADS;

			String s = srcIt.next();
			Instance inst1 = new Instance(s);
			inst1.setProcessing(srcPro);
			CBDBuilder.buildMulti(N_THREADS, inst1);

			Runnable worker = new EvalRunnable(inst1, tgtPro, featureNames, svm, index);
			executor.execute(worker);

		}

		// This will make the executor accept no new threads
		// and finish all existing threads in the queue
		executor.shutdown();
		// Wait until all threads are finished
		executor.awaitTermination(0, TimeUnit.MINUTES);
		LOGGER.info("Finished all threads");

	}

}

class EvalRunnable implements Runnable {

	private static final Logger LOGGER = Logger.getLogger(EvalRunnable.class);
	Instance inst1;
	Processing tgtPro;
	TreeSet<String> featureNames;
	SMOSVMClassifier svm;
	int index;

	public EvalRunnable(Instance inst1, Processing tgtPro,
			TreeSet<String> featureNames, SMOSVMClassifier svm, int index) {
		this.inst1 = inst1;
		this.tgtPro = tgtPro;
		this.featureNames = featureNames;
		this.svm = svm;
		this.index = index;
	}

	@Override
	public void run() {
		
		svm.clearMultiTest(index);

		for (String t : tgtPro.getIndex()) {

			Instance inst2 = new Instance(t);
			inst2.setProcessing(tgtPro);
			CBDBuilder.buildMulti(index, inst2);

			Example ex = new Example(inst1, inst2);
			ex.setLabel(true);
	
			ex.setSim(SimilarityController.compute(ex).getValue());
			ex.spoil(featureNames);
			LOGGER.debug("FEATS: " + ex.getFeatures());
	
			svm.addMultiTestInstance(index, ex);
		}
		
		svm.evaluateMultiTest(index);
	}

}
