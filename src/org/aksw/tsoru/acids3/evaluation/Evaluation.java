package org.aksw.tsoru.acids3.evaluation;

import java.util.TreeSet;

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
public class Evaluation {

	private static final Logger LOGGER = Logger.getLogger(Evaluation.class);

	public static void recall(SMOSVMClassifier svm, Oracle oracle, Processing srcPro, Processing tgtPro, TreeSet<String> featureNames) {
		
		LOGGER.info("Starting RECALL evaluation");
		
		svm.initTest(oracle.getSourceSize());
		
		LOGGER.info("Theoretical test set size = "+(srcPro.getIndex().size() * tgtPro.getIndex().size()));
		LOGGER.info("Recall evaluation test set size = "+oracle.getSourceSize());
		
		int i = 0;
		for(String s : oracle.sourceKeySet()) {
			
			Instance inst1 = new Instance(s);
			inst1.setProcessing(srcPro);
			CBDBuilder.build(inst1);
			
			Instance inst2 = new Instance(oracle.get(s));
			inst2.setProcessing(tgtPro);
			CBDBuilder.build(inst2);
			
			Example ex = new Example(inst1, inst2);
			ex.setLabel(true);
			LOGGER.debug("("+i+") Evaluating "+ex+"... (label = "+ex.getLabel()+")");
			
			// TODO might use filtering for faster evaluation
			ex.setSim(SimilarityController.compute(ex).getValue());
			ex.spoil(featureNames);
			LOGGER.debug("NAMES: "+ex.getFeatureNames());
			LOGGER.debug("FEATS: "+ex.getFeatures());
			
			svm.addTestInstance(ex);
			
			i++;
		}
			
		svm.evaluate();
		
	}

	public static void fmeasure(SMOSVMClassifier svm, Oracle oracle,
			Processing srcPro, Processing tgtPro, TreeSet<String> featureNames) {
		
		LOGGER.info("Starting F-MEASURE evaluation");
		
		svm.initTest(10 * tgtPro.getIndex().size());
		
		LOGGER.info("F-Measure evaluation test set size = "+srcPro.getIndex().size() * tgtPro.getIndex().size());
		
		int i = 0, j = 0;
		for(String s : srcPro.getIndex()) {
			
			Instance inst1 = new Instance(s);
			inst1.setProcessing(srcPro);
			CBDBuilder.build(inst1);
			
			for(String t : tgtPro.getIndex()) {
				
				Instance inst2 = new Instance(t);
				inst2.setProcessing(tgtPro);
				CBDBuilder.build(inst2);
				
				Example ex = new Example(inst1, inst2);
				ex.setLabel(oracle.ask(s, t));
//				LOGGER.debug("("+i+") Evaluating "+ex+"... (label = "+ex.getLabel()+")");
				
				// TODO might use filtering for faster evaluation
				ex.setSim(SimilarityController.compute(ex).getValue());
				ex.spoil(featureNames);
//				LOGGER.debug("NAMES: "+ex.getFeatureNames());
//				LOGGER.debug("FEATS: "+ex.getFeatures());
				
				svm.addTestInstance(ex);
//				LOGGER.debug("inst="+svm.getInstCount());
				
//				i++;
			}
			LOGGER.debug("inst="+svm.getInstCount());
			
			if(conditions(j)) {
				LOGGER.debug("evaluating...");
				svm.evaluate();
				svm.clearInstances();
				LOGGER.debug("cleared");
			}
			j++;
		}
			
	}

	private static boolean conditions(int j) {
		return j%10 == 9;
	}

}
