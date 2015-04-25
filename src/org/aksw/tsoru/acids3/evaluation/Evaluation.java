package org.aksw.tsoru.acids3.evaluation;

import java.util.ArrayList;
import java.util.TreeSet;

import org.aksw.tsoru.acids3.db.SQLiteManager;
import org.aksw.tsoru.acids3.db.Tuple;
import org.aksw.tsoru.acids3.io.Processing;
import org.aksw.tsoru.acids3.learner.SMOSVMClassifier;
import org.aksw.tsoru.acids3.model.Example;
import org.aksw.tsoru.acids3.model.Instance;
import org.aksw.tsoru.acids3.similarity.OverallSimilarity;
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
		
		ArrayList<String> src = new ArrayList<String>(srcPro.getIndex());
		ArrayList<String> tgt = new ArrayList<String>(tgtPro.getIndex());
		SQLiteManager srcMan = srcPro.getSql();
		SQLiteManager tgtMan = tgtPro.getSql();
		
		OverallSimilarity osim = new OverallSimilarity();
		svm.initTest(oracle.getSize());
		
		LOGGER.info("Theoretical test set size = "+(src.size() * tgt.size()));
		LOGGER.info("Recall evaluation test set size = "+oracle.getSize());
		
		int i = 0;
		for(String s : oracle.keySet()) {
			
			ArrayList<Tuple> cbd1 = srcMan.getTuples(s);
			Instance inst1 = new Instance(s);
			inst1.setProcessing(srcPro);
			for(Tuple tu : cbd1) {
				if(tu.getS().equals(s))
					inst1.add(tu);
				else
					inst1.addInverse(tu);
			}
			String t = oracle.get(s);
			ArrayList<Tuple> cbd2 = tgtMan.getTuples(t);
			Instance inst2 = new Instance(t);
			inst2.setProcessing(tgtPro);
			for(Tuple tu : cbd2) {
				if(tu.getS().equals(t))
					inst2.add(tu);
				else
					inst2.addInverse(tu);
			}
			
			Example ex = new Example(inst1, inst2);
			ex.setLabel(true);
			LOGGER.debug("("+i+") Evaluating "+ex+"... (label = "+ex.getLabel()+")");
			
			// TODO might use filtering for faster evaluation
			ex.setSim(osim.compute(ex, null));
			ex.spoil(featureNames);
			LOGGER.debug("NAMES: "+ex.getFeatureNames());
			LOGGER.debug("FEATS: "+ex.getFeatures());
			
			svm.addTestInstance(ex);
			
			i++;
		}
			
		svm.evaluate();
		
	}

}
