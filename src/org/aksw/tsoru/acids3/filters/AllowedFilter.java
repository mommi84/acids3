package org.aksw.tsoru.acids3.filters;

import java.util.ArrayList;

import org.apache.commons.math3.stat.descriptive.rank.Median;

/**
 * @author Tommaso Soru <tsoru@informatik.uni-leipzig.de>
 *
 */
public class AllowedFilter {
	
	private String measure;
//	private double sum;
//	private int occur;
	private ArrayList<Double> xs = new ArrayList<Double>();
	
	private boolean isNumeric = false;
	
	public AllowedFilter(String measure) {
		this.measure = measure;
	}
	
	public Double getMean() {
//		return sum / occur;
		Median m = new Median();
		double[] d = new double[xs.size()];
		for(int i=0; i<d.length; i++)
			d[i] = xs.get(i);
		return m.evaluate(d);
	}
	public void add(double x) {
		xs.add(x);
//		this.sum += x;
//		this.occur++;
	}
	public String getMeasure() {
		return measure;
	}
	
	@Override
	public String toString() {
		return getMeasure() + " => " + getMean();
	}

	public boolean isNumeric() {
		return isNumeric;
	}

	public void setNumeric(boolean isNumeric) {
		this.isNumeric = isNumeric;
	}
	
}