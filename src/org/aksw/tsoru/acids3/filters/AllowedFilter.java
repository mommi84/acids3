package org.aksw.tsoru.acids3.filters;

/**
 * @author Tommaso Soru <tsoru@informatik.uni-leipzig.de>
 *
 */
public class AllowedFilter {
	
	private String measure;
	private double sum;
	private int occur;
	
	AllowedFilter(String measure) {
		this.measure = measure;
	}
	
	public Double getMean() {
		return sum / occur;
	}
	public void add(double x) {
		this.sum += x;
		this.occur++;
	}
	public String getMeasure() {
		return measure;
	}
	
	@Override
	public String toString() {
		return getMeasure() + " => " + getMean();
	}
	
}