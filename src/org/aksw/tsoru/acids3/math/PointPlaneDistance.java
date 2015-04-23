package org.aksw.tsoru.acids3.math;

import java.util.ArrayList;

import org.aksw.tsoru.acids3.model.Example;

/**
 * @author Tommaso Soru <tsoru@informatik.uni-leipzig.de>
 *
 */
public class PointPlaneDistance {
	
	public static double compute(Example ex, double[] w, double b) {
		ArrayList<Double> x = new ArrayList<Double>( ex.getFeatures() );
		double sum = 0.0, q = 0.0;
		for(int i=0; i<w.length; i++) {
			sum += x.get(i) * w[i];
			q += Math.pow(w[i], 2);
		}
		return (sum - b) / Math.sqrt(q);
	}

}
