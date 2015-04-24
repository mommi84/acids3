package org.aksw.tsoru.acids3.filters;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;

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

	public ArrayList<AllowedFilter> build() {
		
		LOGGER.info("Building filters using "+RANDOM_SAMPLES+" random samples...");
		
		OverallSimilarity osim = new OverallSimilarity();
		HashMap<String, AllowedFilter> ranks = new HashMap<String, AllowedFilter>();
		
		ArrayList<Instance> sources = srcPro.randomPick(RANDOM_SAMPLES);
		ArrayList<Instance> targets = tgtPro.randomPick(RANDOM_SAMPLES);
		
		for(int i=0; i<RANDOM_SAMPLES; i++) {
			
			Instance src = sources.get(i);
			Instance tgt = targets.get(i);
			
			src.setProcessing(srcPro);
			tgt.setProcessing(tgtPro);
			
			Example ex = new Example(src, tgt);
			
			for(Tuple ts : src.getTuples()) {
				for(Tuple tt : tgt.getTuples()) {
					String measure = ts.getP()+", "+tt.getP();
					Double sim = osim.tupleCompute(ts, tt, srcPro.getLogsim(ts.getP()), tgtPro.getLogsim(tt.getP()), ex);
					LOGGER.trace("Value for sim("+measure+") is "+sim);
					if(sim != null) {
						AllowedFilter rank;
						if(ranks.containsKey(measure))
							rank = ranks.get(measure);
						else {
							rank = new AllowedFilter(measure);
							ranks.put(measure, rank);
						}
						rank.add(sim);
					}
				}
			}
		}
		// important!
		srcPro.getCache().resetInstances();
		tgtPro.getCache().resetInstances();
		
		ArrayList<AllowedFilter> rankList = new ArrayList<AllowedFilter>(ranks.values());
		LOGGER.debug("Filter list (size="+rankList.size()+"): "+rankList);
		
		// allowed filter size
		final int AF_SIZE = (int) (Math.ceil(Math.sqrt(Math.sqrt(ranks.size()))));
		
		// sort and cut away low-ranked measures
		Collections.sort(rankList, new Comparator<AllowedFilter>() {
			@Override
			public int compare(AllowedFilter o1, AllowedFilter o2) {
				return o2.getMean().compareTo(o1.getMean());
			}
		});
		Iterator<AllowedFilter> it = rankList.iterator();
		for(int i=0; it.hasNext(); i++) {
			it.next();
			if(i>=AF_SIZE)
				it.remove();
		}
		LOGGER.debug("Allowed filter list (size="+AF_SIZE+"): "+rankList);
		LOGGER.info("Filters done.");
		
		return rankList; 
	}

}