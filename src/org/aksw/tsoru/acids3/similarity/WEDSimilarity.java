package org.aksw.tsoru.acids3.similarity;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Scanner;

import org.aksw.tsoru.acids3.similarity.functions.WeightedEditDistanceExtended;
import org.aksw.tsoru.acids3.util.Transform;

/**
 * @author Tommaso Soru <tsoru@informatik.uni-leipzig.de>
 *
 */
public class WEDSimilarity implements Similarity {
	
	public static final double INIT_FULL_WEIGHT = 1.0;
	public static final double INIT_CASE_WEIGHT = 0.5;
	private static HashMap<String, Double> weights = new HashMap<String, Double>();
	
	public WEDSimilarity() {
		super();
		// load weights once
		if(weights.isEmpty())
			loadWeightsFromFile("etc/confusion-matrix.txt");
	}
	
	@Override
	public Double compute(String term1, String term2) {
		return Transform.toSimilarity(wed.proximity(term1, term2));
	}

	private WeightedEditDistanceExtended wed = new WeightedEditDistanceExtended() {
		@Override
		public double transposeWeight(char cFirst, char cSecond) {
			return Double.POSITIVE_INFINITY;
		}
		@Override
		public double substituteWeight(char cDeleted, char cInserted) {
			Double d = weights.get(cDeleted+","+cInserted);
			if(d == null)
				return INIT_FULL_WEIGHT;
			else
				return d;
		}
		@Override
		public double matchWeight(char cMatched) {
			return 0.0;
		}
		@Override
		public double insertWeight(char cInserted) {
			Double d = weights.get("&,"+cInserted);
			if(d == null)
				return INIT_FULL_WEIGHT;
			else
				return d;
		}
		@Override
		public double deleteWeight(char cDeleted) {
			Double d = weights.get(cDeleted+",&");
			if(d == null)
				return INIT_FULL_WEIGHT;
			else
				return d;
		}
	};

	/**
	 * Here, `&` is used instead of `epsilon`, meaning the empty string.
	 */
	private static void loadWeightsFromFile(String filePath) {
		
		// load case-transformation weights
		for(char c='A'; c<='Z'; c++) {
			weights.put(c+","+(char)(c+32), INIT_CASE_WEIGHT);
			weights.put((char)(c+32)+","+c, INIT_CASE_WEIGHT);
		}
		
		// load confusion matrix
		Scanner in = null;
		try {
			in = new Scanner(new File(filePath)); // "etc/confusion-matrix.txt"
		} catch (FileNotFoundException e) {
			System.err.println("Missing file `./" + filePath + "`!");
			return;
		}
		for(char c1='a'; c1<='{'; c1++) {
			for(char c2='a'; c2<='{'; c2++) {
				double d = in.nextDouble();
				if(d != 1) {
					if(c1 == '{') {
						weights.put("&,"+c2, d);
						weights.put("&,"+(char)(c2-32), d);
					} else 	if(c2 == '{') {
						weights.put(c1+",&", d);
						weights.put((char)(c1-32)+",&", d);
					} else {
						weights.put(c1+","+c2, d);
						weights.put((char)(c1-32)+","+(char)(c2-32), d);
						// crossing weights (e.g., <A,b>, <a,B>)
						double dcross = (1.0 + d) / 2.0;
						weights.put((char)(c1-32)+","+c2, dcross);
						weights.put(c1+","+(char)(c2-32), dcross);
					}
				}
			}
		}
		in.close();
	}

	public HashMap<String, Double> getWeights() {
		return weights;
	}
	
}


