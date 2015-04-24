package org.aksw.tsoru.acids3.filters;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.aksw.tsoru.acids3.db.Tuple;
import org.aksw.tsoru.acids3.io.Processing;
import org.aksw.tsoru.acids3.model.Example;
import org.aksw.tsoru.acids3.model.Instance;
import org.aksw.tsoru.acids3.similarity.OverallSimilarity;
import org.apache.log4j.Logger;

/**
 * @author Tommaso Soru <tsoru@informatik.uni-leipzig.de>
 *
 */
public class FilterBuilder {
	
	private static final Logger LOGGER = Logger.getLogger(FilterBuilder.class);

	private static final int RANDOM_SAMPLES = 50;
	
	private Processing srcPro, tgtPro;
	
	public FilterBuilder(Processing srcPro, Processing tgtPro) {
		super();
		this.srcPro = srcPro;
		this.tgtPro = tgtPro;
	}

	public void build() {
		LOGGER.info("Building filters...");
		
		OverallSimilarity osim = new OverallSimilarity();
		HashMap<String, Rank> ranks = new HashMap<String, Rank>();
		
		for(int i=0; i<RANDOM_SAMPLES; i++) {
		
			Instance src = srcPro.randomPick();
			src.setProcessing(srcPro);
			Instance tgt = tgtPro.randomPick();
			tgt.setProcessing(tgtPro);
			
			Example ex = new Example(src, tgt);
			
			for(Tuple ts : src.getTuples()) {
				for(Tuple tt : tgt.getTuples()) {
					String measure = ts.getP()+", "+tt.getP();
					Double sim = osim.tupleCompute(ts, tt, srcPro.getLogsim(ts.getP()), tgtPro.getLogsim(tt.getP()), ex);
					LOGGER.debug("Value for sim("+measure+") is "+sim);
					if(sim != null) {
						Rank rank;
						if(ranks.containsKey(measure))
							rank = ranks.get(measure);
						else {
							rank = new Rank(measure);
							ranks.put(measure, rank);
						}
						rank.add(sim);
					}
				}
			}
		}
		
		ArrayList<Rank> rankList = new ArrayList<Rank>(ranks.values());
		System.out.println(rankList);
		
		// allowed filter size
		int afsize = (int) (Math.ceil(Math.sqrt(ranks.size())));
		Collections.sort(rankList, new Comparator<Rank>() {
			@Override
			public int compare(Rank o1, Rank o2) {
				return o2.getMean().compareTo(o1.getMean());
			}
		});
		Iterator<Rank> it = rankList.iterator();
		for(int i=0; it.hasNext(); i++) {
			it.next();
			if(i>=afsize)
				it.remove();
		}
		System.out.println(rankList);
		System.exit(0);
	}

}

class Rank {
	
	private String measure;
	private double sum;
	private int occur;
	
	Rank(String measure) {
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