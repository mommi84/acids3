package org.aksw.tsoru.acids3.similarity;

import org.aksw.tsoru.acids3.model.Example;

/**
 * @author Tommaso Soru <t.soru@informatik.uni-leipzig.de>
 *
 */
public class OverallSimilarity {
	
	public Double compute(Example ex) {
//		return Math.random();
		
		// XXX tests ahead
		WEDSimilarity wed = new WEDSimilarity();
		return wed.compute(ex.getSource().getURI(), ex.getTarget().getURI());
		
	}

}
