package org.aksw.tsoru.acids3.evaluation;

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
 * @author Tommaso Soru <tsoru@informatik.uni-leipzig.de>
 *
 */
public class MultiThreadEvaluation {
	
	private static final int NTHREADS = 3;

	private static final Logger LOGGER = Logger.getLogger(MultiThreadEvaluation.class);

	public static void fmeasure(SMOSVMClassifier svm, Oracle oracle,
			Processing srcPro, Processing tgtPro, TreeSet<String> featureNames) throws InterruptedException {
		LOGGER.info("Starting MULTITHREAD F-MEASURE evaluation");
		
		ExecutorService executor = Executors.newFixedThreadPool(NTHREADS);

		svm.initTest(oracle.getSourceSize());
		
		LOGGER.info("F-Measure evaluation test set size = "+(srcPro.getIndex().size() * tgtPro.getIndex().size()));
		
		int i = 0;
		for(String s : srcPro.getIndex()) {
			
			Instance inst1 = new Instance(s);
			inst1.setProcessing(srcPro);
			CBDBuilder.build(inst1);
			
			for(String t : tgtPro.getIndex()) {
				
				Instance inst2 = new Instance(t);
				inst2.setProcessing(tgtPro);
				CBDBuilder.build(inst2);

				LOGGER.info("("+i+")");
				
				Runnable worker = new EvalRunnable(inst1, inst2, featureNames, svm);
				executor.execute(worker);

				
				i++;
			}
		}		
		
		// This will make the executor accept no new threads
		// and finish all existing threads in the queue
		executor.shutdown();
		// Wait until all threads are finish
		executor.awaitTermination(0, TimeUnit.MINUTES);
		LOGGER.info("Finished all threads");
			
		svm.evaluate();
	}

}
class EvalRunnable implements Runnable {
	
	private static final Logger LOGGER = Logger.getLogger(EvalRunnable.class);
	Instance inst1, inst2;
	TreeSet<String> featureNames;
	SMOSVMClassifier svm;
	
	public EvalRunnable(Instance inst1, Instance inst2, TreeSet<String> featureNames, SMOSVMClassifier svm) {
		this.inst1 = inst1;
		this.inst2 = inst2;
		this.featureNames = featureNames;
		this.svm = svm;
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		Example ex = new Example(inst1, inst2);
		ex.setLabel(true);
//		LOGGER.debug("("+i+") Evaluating "+ex+"... (label = "+ex.getLabel()+")");
		
		// TODO might use filtering for faster evaluation
		ex.setSim(SimilarityController.compute(ex).getValue());
		ex.spoil(featureNames);
//		LOGGER.debug("NAMES: "+ex.getFeatureNames());
		LOGGER.debug("FEATS: "+ex.getFeatures());
		
		svm.addTestInstance(ex);
	}
	
}
