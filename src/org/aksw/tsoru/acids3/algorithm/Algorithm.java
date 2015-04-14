package org.aksw.tsoru.acids3.algorithm;

import java.io.IOException;
import java.util.ArrayList;
import java.util.TreeSet;

import org.aksw.tsoru.acids3.db.Tuple;
import org.aksw.tsoru.acids3.evaluation.Evaluation;
import org.aksw.tsoru.acids3.filters.ReededFilter;
import org.aksw.tsoru.acids3.io.Arg;
import org.aksw.tsoru.acids3.io.Processing;
import org.aksw.tsoru.acids3.learner.SMOSVMClassifier;
import org.aksw.tsoru.acids3.math.PointPlaneDistance;
import org.aksw.tsoru.acids3.model.Example;
import org.aksw.tsoru.acids3.model.Instance;
import org.aksw.tsoru.acids3.util.Oracle;
import org.apache.log4j.Logger;

/**
 * @author Tommaso Soru <t.soru@informatik.uni-leipzig.de>
 *
 */
public class Algorithm implements Runnable {
	
	private static final Logger LOGGER = Logger.getLogger(Algorithm.class);
	
	private Parameters param;
	
	public Algorithm(Parameters param) {
		super();
		this.param = param;
	}

	public Parameters getParam() {
		return param;
	}
	

	public void run() {
		LOGGER.info("Algorithm bound.");
		
		Oracle oracle = new Oracle(param.getOraclePath());
		try {
			oracle.build();
		} catch (IOException e) {
			LOGGER.fatal("No oracle's answers found.");
			e.printStackTrace();
			return;
		} finally {
			LOGGER.info("Oracle's answers loaded.");
		}
		
		Processing srcPro = new Processing(Arg.SOURCE, param);
		Processing tgtPro = new Processing(Arg.TARGET, param);
		
		srcPro.index();
		tgtPro.index();
		
		// classifier
		SMOSVMClassifier svm = new SMOSVMClassifier();
		
		// features present in training set
		TreeSet<String> trainFeatures = new TreeSet<String>();
		
		for(int round = 1; round <= Parameters.ROUNDS_ACTIVE; round ++) {
			LOGGER.info("Round #"+round+" of questions has started.");
			
			ArrayList<Example> training = new ArrayList<Example>();
			
			for(int j=0; j<Parameters.QUERIES_PER_ROUND; j++) {
				
				// get (pseudo-)random source example
				Instance src = (Instance) srcPro.randomPick();
				src.setProcessing(srcPro);
				
				/*
				 *  TODO implement heuristic for 'new ReededFilter()'
				 *  (see OverallSimilarity:83)
				 */
				ArrayList<Example> topM = tgtPro.topMatches(src, null);
				for(Example ex : topM) {
					String s = ex.getSource().getURI();
					String t = ex.getTarget().getURI();
					LOGGER.info("Question: Are <"+s+"> and <"+t+"> the same?");
					for(Tuple tu : ex.getSource().getTuples())
						LOGGER.debug(tu);
					for(Tuple tu : ex.getTarget().getTuples())
						LOGGER.debug(tu);
					LOGGER.info("Answer: "+oracle.ask(s, t));
					ex.setLabel(oracle.ask(s, t));
					
					trainFeatures.addAll(ex.getFeatureNames());
				}
				
				training.addAll(topM);
			}
			
			for(Example ex : training)
				ex.spoil(trainFeatures);
			
			if(training.isEmpty()) {
				LOGGER.fatal("No example made the cut. Try decreasing the filter thresholds.");
				break;
			}
			
			svm.init(training.get(0), training.size());
			
			for(Example ex : training)
				svm.addInstance(ex);
			
			svm.train(true);
			
			for(Example ex : training)
				LOGGER.info(ex
						+ " | d = " + PointPlaneDistance.compute(ex, svm.getWeights(), svm.getBias())
						+ " | c(x) = " + svm.classify(ex));
			
//			PseudoEvaluation.run(svm, oracle, srcPro, tgtPro, featureNames);
			Evaluation.recall(svm, oracle, srcPro, tgtPro, trainFeatures);
		}
		
		srcPro.close();
		tgtPro.close();
	}


}
