package org.aksw.tsoru.acids3.evaluation;

import java.util.ArrayList;
import java.util.TreeSet;

import org.aksw.tsoru.acids3.db.SQLiteManager;
import org.aksw.tsoru.acids3.db.Tuple;
import org.aksw.tsoru.acids3.io.Processing;
import org.aksw.tsoru.acids3.learner.SeqMinOptSVM;
import org.aksw.tsoru.acids3.model.Example;
import org.aksw.tsoru.acids3.model.Instance;
import org.aksw.tsoru.acids3.similarity.OverallSimilarity;
import org.aksw.tsoru.acids3.util.Oracle;
import org.apache.log4j.Logger;

/**
 * @author Tommaso Soru <t.soru@informatik.uni-leipzig.de>
 *
 */
public class PseudoEvaluation {
	
	private static final Logger LOGGER = Logger.getLogger(PseudoEvaluation.class);
	
	private static final int TEST_SET_SIZE = 500;

	public static void run(SeqMinOptSVM svm, Oracle oracle, Processing srcPro, Processing tgtPro, TreeSet<String> featureNames) {
		
		ArrayList<String> src = new ArrayList<String>(srcPro.getIndex());
		ArrayList<String> tgt = new ArrayList<String>(tgtPro.getIndex());
		SQLiteManager srcMan = srcPro.getSql();
		SQLiteManager tgtMan = tgtPro.getSql();
		
		OverallSimilarity osim = new OverallSimilarity();
		svm.initTest(TEST_SET_SIZE);
		
		for(int i=0; i<TEST_SET_SIZE; i++) {
			String s = src.get((int) (src.size() * Math.random()));
			ArrayList<Tuple> cbd1 = srcMan.getTuples(s);
			Instance inst1 = new Instance(s);
			inst1.setProcessing(srcPro);
			for(Tuple tu : cbd1) {
				if(tu.getS().equals(s))
					inst1.addTuple(tu);
				else
					inst1.addInverseTuple(tu);
			}
			String t = tgt.get((int) (tgt.size() * Math.random()));
			ArrayList<Tuple> cbd2 = tgtMan.getTuples(t);
			Instance inst2 = new Instance(t);
			inst2.setProcessing(tgtPro);
			for(Tuple tu : cbd2) {
				if(tu.getS().equals(t))
					inst2.addTuple(tu);
				else
					inst2.addInverseTuple(tu);
			}
			
			Example ex = new Example(inst1, inst2);
			LOGGER.info("Evaluating "+ex+"...");
			
			ex.setSim(osim.compute(ex));
			ex.spoil(featureNames);
			if(oracle.get(s) == null)
				ex.setLabel(false);
			else
				ex.setLabel(oracle.get(s).equals(t));
			
			svm.addTestInstance(ex);
				
		}
			
		svm.evaluate();
		
		
	}

}

/*
		int i=0;
		
		outer: for(String s : src) {
			ArrayList<Tuple> cbd1 = srcMan.getTuples(s);
			Instance inst1 = new Instance(s);
			inst1.setProcessing(srcPro);
			for(Tuple tu : cbd1) {
				if(tu.getS().equals(s))
					inst1.addTuple(tu);
				else
					inst1.addInverseTuple(tu);
			}
			for(String t : tgt) {
				ArrayList<Tuple> cbd2 = tgtMan.getTuples(t);
				Instance inst2 = new Instance(t);
				inst2.setProcessing(tgtPro);
				for(Tuple tu : cbd2) {
					if(tu.getS().equals(t))
						inst2.addTuple(tu);
					else
						inst2.addInverseTuple(tu);
				}
				
				Example ex = new Example(inst1, inst2);
				LOGGER.info("Evaluating "+ex+"...");
				
				ex.setSim(osim.compute(ex));
				ex.spoil(featureNames);
				if(oracle.get(s) == null)
					ex.setLabel(false);
				else
					ex.setLabel(oracle.get(s).equals(t));
				
				svm.addTestInstance(ex);
				
				if(++i == TEST_SET_SIZE)
					break outer;
			}
		}

*/