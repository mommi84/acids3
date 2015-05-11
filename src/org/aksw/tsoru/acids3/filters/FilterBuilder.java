package org.aksw.tsoru.acids3.filters;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;

import org.aksw.tsoru.acids3.db.Tuple;
import org.aksw.tsoru.acids3.io.Arg;
import org.aksw.tsoru.acids3.io.CBDBuilder;
import org.aksw.tsoru.acids3.io.Processing;
import org.aksw.tsoru.acids3.model.Example;
import org.aksw.tsoru.acids3.model.Instance;
import org.aksw.tsoru.acids3.sim.SimType;
import org.aksw.tsoru.acids3.sim.SimilarityController;
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

	/**
	 * Build the set of filters which may be applied on data by comparing the
	 * similarity statistical distributions.
	 * 
	 * @return
	 */
	public ArrayList<AllowedFilter> build() {

		LOGGER.info("Building filters using " + RANDOM_SAMPLES
				+ " random samples...");

		HashMap<String, AllowedFilter> ranks = new HashMap<String, AllowedFilter>();

		ArrayList<Instance> sources = srcPro.randomPick(RANDOM_SAMPLES);
		ArrayList<Instance> targets = tgtPro.randomPick(RANDOM_SAMPLES);
		
//		ArrayList<Instance> sources = getTestSamples(Arg.SOURCE);
//		ArrayList<Instance> targets = getTestSamples(Arg.TARGET);
		
		for (int i = 0; i < sources.size(); i++) {

			Instance src = sources.get(i);
			Instance tgt = targets.get(i);

			src.setProcessing(srcPro);
			tgt.setProcessing(tgtPro);

			Example ex = new Example(src, tgt);
			ex.setUnsupervised(true);
			
			for(Tuple t : src.getTuples())
				LOGGER.trace("src: "+t);
			for(Tuple t : tgt.getTuples())
				LOGGER.trace("tgt: "+t);
			
			Double sim = SimilarityController.compute(ex).getValue();
			ex.setSim(sim);
			LOGGER.info("sim: "+ex);
			
			LOGGER.info(ex.getFeatures());
			
			HashMap<String, Double> features = ex.getFeatures();
			
			for(String name : features.keySet()) {
				AllowedFilter rank;
				if(ranks.containsKey(name))
					rank = ranks.get(name);
				else {
					rank = new AllowedFilter(name);
					ranks.put(name, rank);
				}
				rank.add(features.get(name));
			}
			
		}
		
		// remove non-string features
		Iterator<String> it = ranks.keySet().iterator();
		while(it.hasNext())
			if(srcPro.simTypeOf(it.next()) != SimType.STRING_SIM)
				it.remove();
		
		// important!
		srcPro.getCache().resetInstances();
		tgtPro.getCache().resetInstances();

		ArrayList<AllowedFilter> rankList = new ArrayList<AllowedFilter>(
				ranks.values());
		LOGGER.debug("Filter list (size=" + rankList.size() + "): " + rankList);
		
		// export rank list (map learning)
		exportRankList(rankList);

		// allowed filter size
		final int AF_SIZE = reduceFilterSize(ranks.size());

		// sort and cut away low-ranked measures
		Collections.sort(rankList, new Comparator<AllowedFilter>() {
			@Override
			public int compare(AllowedFilter o1, AllowedFilter o2) {
				return o2.median().compareTo(o1.median());
			}
		});
		// reduce
		Iterator<AllowedFilter> it2 = rankList.iterator();
		for (int i = 0; it2.hasNext(); i++) {
			it2.next();
			if (i >= AF_SIZE)
				it2.remove();
		}
		LOGGER.debug("Allowed filter list (size=" + AF_SIZE + "): " + rankList);
		LOGGER.info("Filters done.");

		return rankList;
	}

	/**
	 * Warning: experimental!<br>
	 * Export rank list to make the ground for a training set (map learning).
	 * 
	 * @param rankList
	 */
	private void exportRankList(ArrayList<AllowedFilter> rankList) {
		PrintWriter pw = null;
		try {
			pw = new PrintWriter(new File("local/measures.csv"));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		for(AllowedFilter af : rankList) {
			pw.write(af.getMeasure()+","+af.median()+","+af.mean()+","+af.variance()+","+af.max()+","+(double) af.count() / RANDOM_SAMPLES+"\n");
		}
		pw.close();
	}

	@SuppressWarnings("unused")
	private ArrayList<Instance> getTestSamples(Arg arg) {
		ArrayList<Instance> resources = new ArrayList<Instance>();
		Instance res;
		if(arg == Arg.SOURCE) {
			res = new Instance("http://dblp.rkbexplorer.com/id/conf/sigmod/GuhaJKSY02");
			res.setProcessing(srcPro);
			CBDBuilder.build(srcPro, resources);
		} else {
			res = new Instance("http://acm.rkbexplorer.com/id/313897");
			res.setProcessing(tgtPro);
			CBDBuilder.build(tgtPro, resources);
		}
		resources.add(res);
		return resources;
	}

	/**
	 * The number of filters that should be selected among the top ranked.
	 * Default is sqrt(size).
	 * 
	 * @param size
	 * @return
	 */
	private int reduceFilterSize(int size) {
		return (int) (Math.floor(Math.sqrt(size)));
	}

}