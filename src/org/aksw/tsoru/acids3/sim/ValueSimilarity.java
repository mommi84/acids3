package org.aksw.tsoru.acids3.sim;

import org.aksw.tsoru.acids3.model.DatatypeNode;
import org.aksw.tsoru.acids3.model.GeneralNode;
import org.aksw.tsoru.acids3.similarity.value.WEDSimilarity;

/**
 * @author Tommaso Soru <tsoru@informatik.uni-leipzig.de>
 *
 */
public class ValueSimilarity implements NodeSimilarity {

	private static final WEDSimilarity WED_SIM = new WEDSimilarity();
	
	@Override
	public Double compute(GeneralNode s, GeneralNode t, int depth) {

		DatatypeNode sObj = (DatatypeNode) s;
		DatatypeNode tObj = (DatatypeNode) t;

		String sValue = sObj.getValue();
		String tValue = tObj.getValue();

		// TODO Implement better double similarity.
		Double sVal, tVal;
		try {
			sVal = Math.abs(Double.parseDouble(sValue));
			tVal = Math.abs(Double.parseDouble(tValue));
		} catch (NumberFormatException e) {
			// string similarity
			Double d = WED_SIM.compute(sValue, tValue);
			return d;
		}
		double max = Math.max(sVal, tVal);
		if(max == 0.0) // both are 0.0
			return 1.0;
		Double v = 1.0 - Math.abs(sVal - tVal) / max;
		if(v.isNaN())
			System.out.println(sObj + " & " + tObj);
		return v;
		
	}

}
