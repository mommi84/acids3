package org.aksw.tsoru.acids3.filters;

import java.util.ArrayList;
import java.util.Iterator;

import org.apache.commons.math3.stat.descriptive.moment.Mean;
import org.apache.commons.math3.stat.descriptive.moment.Variance;
import org.apache.commons.math3.stat.descriptive.rank.Max;
import org.apache.commons.math3.stat.descriptive.rank.Median;

/**
 * @author Tommaso Soru <tsoru@informatik.uni-leipzig.de>
 *
 */
public class AllowedFilter {
	
	private String measure;
	private ArrayList<Double> xs = new ArrayList<Double>();
		
	private boolean isNumeric = false;
	
	public AllowedFilter(String measure) {
		this.measure = measure;
	}
	
	private ArrayList<Double> stripZeros() {
		ArrayList<Double> dbl = new ArrayList<Double>(this.xs);
//		Iterator<Double> it = dbl.iterator();
//		while(it.hasNext())
//			if(it.next() == 0.0)
//				it.remove();
		return dbl;
	}

	public Double max() {
		ArrayList<Double> xs = stripZeros();
		Max m = new Max();
		double[] d = new double[xs.size()];
		for(int i=0; i<d.length; i++)
			d[i] = xs.get(i);
		return m.evaluate(d);
	}

	public Double median() {
		ArrayList<Double> xs = stripZeros();
		Median m = new Median();
		double[] d = new double[xs.size()];
		for(int i=0; i<d.length; i++)
			d[i] = xs.get(i);
		return m.evaluate(d);
	}
	
	/**
	 * A score for the property mapping.
	 * TODO use this.
	 * @return
	 */
	public Double score() {
		ArrayList<Double> xs = stripZeros();
		Median m = new Median();
		double[] d = new double[xs.size()];
		for(int i=0; i<d.length; i++)
			d[i] = xs.get(i);
		return m.evaluate(d) * xs.size();
	}

	public Double variance() {
		ArrayList<Double> xs = stripZeros();
		Variance var = new Variance();
		double[] d = new double[xs.size()];
		for(int i=0; i<d.length; i++)
			d[i] = xs.get(i);
		return var.evaluate(d);
	}

	public Double mean() {
		ArrayList<Double> xs = stripZeros();
		Mean m = new Mean();
		double[] d = new double[xs.size()];
		for(int i=0; i<d.length; i++)
			d[i] = xs.get(i);
		return m.evaluate(d);
	}

	public Integer count() {
		return stripZeros().size();
	}

	public void add(double x) {
		xs.add(x);
	}
	public String getMeasure() {
		return measure;
	}
	
	@Override
	public String toString() {
		return getMeasure() + "\t" + median() + "\t" + count();
	}

	public boolean isNumeric() {
		return isNumeric;
	}

	public void setNumeric(boolean isNumeric) {
		this.isNumeric = isNumeric;
	}
	
}