package org.aksw.tsoru.acids3.similarity.value;

import java.util.HashMap;

import org.aksw.tsoru.acids3.util.ValueParser;

/**
 * @author Tommaso Soru <tsoru@informatik.uni-leipzig.de>
 *
 */
public class LogarithmicSimilarity implements ValueSimilarity {

	private HashMap<String, Double> extrema = new HashMap<String, Double>();

	public LogarithmicSimilarity() {
		super();
	}
	
	@Override
	public Double compute(String term1, String term2) {
		if(extrema.isEmpty()) {
			System.err.println("LogarithmicSimilarity: compute extrema first!");
			return Double.NaN;
		}
		double sd = toLogScale(ValueParser.parse(term1));
		double td = toLogScale(ValueParser.parse(term2));
		return normalize((Math.abs(sd-td)));
	}
	
	private double toLogScale(double x) {
		return Math.log10(x - extrema.get("minMin") + 1);
	}

	private double normalize(double value) {
		// incomplete information
		if(Double.isNaN(value))
			return Double.NaN;
		
		double denom = extrema.get("denom");
		
		if(denom == 0.0)
			return 1.0;
		else
			return 1.0 - value / denom;
	}

	public void setMinMin(double d) {
		extrema.put("minMin", d);
	}
	
	public double getMinMin() {
		return extrema.get("minMin");
	}

	public double getDenomArg() {
		return extrema.get("denomArg");
	}
	
	public double getDenom() {
		return extrema.get("denom");
	}
	
	public void setDenomArg(double d) {
		extrema.put("denomArg", d);
		extrema.put("denom", toLogScale(d));
	}

	/**
	 * Test.
	 * @param args
	 */
	public static void main(String[] args) {
		
		LogarithmicSimilarity ls = new LogarithmicSimilarity();
		ls.extrema.put("minMin", 0.0);
		ls.extrema.put("denom", ls.toLogScale(30001.0));
		
		System.out.println(ls.compute("0", "1"));
		System.out.println(ls.compute("30000", "30001"));
		System.out.println(ls.compute("-2", "200"));
		
	}


}
