package org.aksw.tsoru.acids3.similarity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeSet;

import org.aksw.tsoru.acids3.db.Tuple;
import org.aksw.tsoru.acids3.filters.AllowedFilter;
import org.aksw.tsoru.acids3.filters.ReededFilter;
import org.aksw.tsoru.acids3.model.Example;
import org.aksw.tsoru.acids3.model.Instance;
import org.apache.commons.math3.stat.descriptive.moment.Mean;
import org.apache.log4j.Logger;

/**
 * @author Tommaso Soru <tsoru@informatik.uni-leipzig.de>
 *
 */
public class OverallSimilarity {
	
	private static final Logger LOGGER = Logger.getLogger(OverallSimilarity.class);
	private static WEDSimilarity wed = new WEDSimilarity();
	
	public OverallSimilarity() {
		super();
	}
	
	public Double compute(Example ex, final ArrayList<AllowedFilter> allowedFilters) {
		
		// collect measures which allow the filtering
		HashMap<String, AllowedFilter> afMeasures = new HashMap<String, AllowedFilter>();
		for(AllowedFilter af : allowedFilters)
			afMeasures.put(af.getMeasure(), af);
		
		double threshold = 0.5;
//		LOGGER.debug("thr_edit = "+Transform.toDistance(threshold));

		Instance src = ex.getSource();
		Instance tgt = ex.getTarget();
		
		
		ArrayList<Double> features = new ArrayList<Double>();
		ArrayList<String> featureNames = new ArrayList<String>();
		
		// first check allowed filters
		ReededFilter rf = new ReededFilter();
		for(Tuple ts : src.getTuples()) {
			for(Tuple tt : tgt.getTuples()) {
				String measure = "["+ts.getP()+", "+tt.getP()+"]";
				if(afMeasures.keySet().contains(measure)) {
					LOGGER.debug(measure + " was found among allowed filters.");
					if(!afMeasures.get(measure).isNumeric()) {
						// if compares strings...
						boolean pass = rf.filter(ts.getO(), tt.getO(), threshold);
						LOGGER.debug("Oh Filter, are '"+ts.getO()+"' and '"+tt.getO()+"' above thr="+threshold+"? "+pass);
						if(!pass)
							return null;
					} else {
						// TODO if compares doubles...
						/*
						 * TODO extend allowed filter list (no string filtering here!)
						 */
					}
				}
			}
		}
		
		
		for(Tuple ts : src.getTuples()) {
			
			for(Tuple tt : tgt.getTuples()) {
				
				if(ts.getOtype().equals("URI") && tt.getOtype().equals("URI") && ex.isParent()) {
					
					Instance src2 = new Instance(ts.getO());
					src2.setProcessing(ex.getSource().getProcessing());
					Instance tgt2 = new Instance(tt.getO());
					tgt2.setProcessing(ex.getTarget().getProcessing());
					Example ex2 = new Example(src2, tgt2);
					ex2.setParent(false);
					for(Tuple t : src2.getProcessing().getSql().getTuples(ts.getO())) {
						if(t.getS().equals(ts.getO()))
							src2.add(t);
						else
							src2.addInverse(t);
					}
					for(Tuple t : tgt2.getProcessing().getSql().getTuples(tt.getO())) {
						if(t.getS().equals(tt.getO()))
							tgt2.add(t);
						else
							tgt2.addInverse(t);
					}
					Double sim = this.compute(ex2, allowedFilters);
					LOGGER.trace("all("+ts.getO()+","+tt.getO()+") = "+sim);
					// if some property values of a resource didn't make the cut
					if(sim == null) {
						if(ex.isParent())
							LOGGER.trace("Similarity didn't make the cut (parent)");
						else
							LOGGER.trace("Similarity didn't make the cut");
						return null;
					}
					features.add(sim);
					featureNames.add("all("+ts.getP()+","+tt.getP()+")");

				} else if(!ts.getOtype().equals("URI") && !tt.getOtype().equals("URI")) {
					// classic similarity comparison
					try {
						Double.parseDouble(ts.getO());
						Double.parseDouble(tt.getO());
					} catch (NumberFormatException e) {
						// string similarity
//						if(allowedFilters != null) {
//							if(ex.isParent())
//								LOGGER.trace(ts.getO()+", "+tt.getO()+", "+threshold);
//							if(!allowedFilters.filter(ts.getO(), tt.getO(), threshold)) {
//								if(ex.isParent())
//									LOGGER.trace("Similarity didn't make the cut (parent)");
//								else
//									LOGGER.trace("Similarity didn't make the cut");
//								LOGGER.debug(allowedFilters.getClass().getSimpleName()+" discarded ("+ts.getS()+", "+tt.getS()+")");
//								return null;
//							}
//						}
						// actual similarity computation
						Double sim = wed.compute(ts.getO(), tt.getO());
						if(ex.isParent())
							LOGGER.trace("wed("+ts.getO() +"," + tt.getO()+") = "+sim);
						features.add(sim);
						featureNames.add("wed("+ts.getP() +"," + tt.getP()+")");
						continue;
					}
					LogarithmicSimilarity srcLogsim = src.getProcessing().getLogsim(ts.getP());
					LogarithmicSimilarity tgtLogsim = tgt.getProcessing().getLogsim(tt.getP());
					// double similarity
					LogarithmicSimilarity logsim = new LogarithmicSimilarity();
					logsim.setMinMin(Math.min(srcLogsim.getMinMin(), tgtLogsim.getMinMin()));
					logsim.setDenomArg(Math.max(srcLogsim.getDenomArg(), tgtLogsim.getDenomArg()));
					Double sim = logsim.compute(ts.getO(), tt.getO());
					if(ex.isParent())
						LOGGER.trace("lgs("+ts.getO() +"," + tt.getO()+") = "+sim);
					features.add(sim);
					featureNames.add("lgs("+ts.getP() +"," + tt.getP()+")");
				} else {
					// TODO The one is URI, the other is not.
					if(ex.isParent())
						LOGGER.trace("zero("+ts.getO() +"," + tt.getO()+") = 0.0");
					features.add(0.0);
					featureNames.add("zero("+ts.getP() +"," + tt.getP()+")");
				}
			}
		}
		
		Mean mean = new Mean();
		double[] feat = new double[features.size()];
		for(int i=0; i<feat.length; i++) {
			feat[i] = features.get(i);
			LOGGER.trace("Feature ["+i+"] = "+feat[i]);
		}
		
		for(int i=0; i<featureNames.size(); i++)
			ex.setFeature(featureNames.get(i), features.get(i));
		
		return mean.evaluate(feat);
	}

	public Double tupleCompute(Tuple ts, Tuple tt, LogarithmicSimilarity srcLogsim, LogarithmicSimilarity tgtLogsim, Example ex, AllowedFilter rank) {

		Double sim = null;
		
		if(ts.getOtype().equals("URI") && tt.getOtype().equals("URI") && ex.isParent()) {
			
			Instance src2 = new Instance(ts.getO());
			src2.setProcessing(ex.getSource().getProcessing());
			Instance tgt2 = new Instance(tt.getO());
			tgt2.setProcessing(ex.getTarget().getProcessing());
			Example ex2 = new Example(src2, tgt2);
			ex2.setParent(false);
			for(Tuple t : src2.getProcessing().getSql().getTuples(ts.getO())) {
				if(t.getS().equals(ts.getO()))
					src2.add(t);
				else
					src2.addInverse(t);
			}
			for(Tuple t : tgt2.getProcessing().getSql().getTuples(tt.getO())) {
				if(t.getS().equals(tt.getO()))
					tgt2.add(t);
				else
					tgt2.addInverse(t);
			}
			
			ArrayList<Double> features = new ArrayList<Double>();
			for(Tuple ts2 : src2.getTuples())
				for(Tuple tt2 : tgt2.getTuples())
					features.add(this.tupleCompute(ts2, tt2, src2.getProcessing().getLogsim(ts2.getP()), tgt2.getProcessing().getLogsim(tt2.getP()), ex2, null));
			
			Mean mean = new Mean();
			double[] feat = new double[features.size()];
			if(feat.length > 10000) {
				LOGGER.warn("For '"+"all("+ts.getO()+","+tt.getO()+"' feature length is "+feat.length+"!");
			}
			for(int i=0; i<feat.length; i++) {
				feat[i] = features.get(i);
//				LOGGER.trace("Feature ["+i+"] = "+feat[i]);
			}
			
			sim = mean.evaluate(feat);
			
			LOGGER.trace("all("+ts.getO()+","+tt.getO()+") = "+sim);
			
		} else if(!ts.getOtype().equals("URI") && !tt.getOtype().equals("URI")) {
			boolean isNumeric = true;
			// classic similarity comparison
			try {
				Double.parseDouble(ts.getO());
				Double.parseDouble(tt.getO());
				// NullPointerException if values are believed to be double, but generally they are not
				srcLogsim.getMinMin();
				tgtLogsim.getMinMin();
			} catch (NumberFormatException | NullPointerException e) {
				isNumeric = false;
			}
			if(isNumeric) {
				// double similarity
				LogarithmicSimilarity logsim = new LogarithmicSimilarity();
				logsim.setMinMin(Math.min(srcLogsim.getMinMin(), tgtLogsim.getMinMin()));
				logsim.setDenomArg(Math.max(srcLogsim.getDenomArg(), tgtLogsim.getDenomArg()));
				sim = logsim.compute(ts.getO(), tt.getO());
				if(ex.isParent()) {
					LOGGER.trace("lgs("+ts.getO() +"," + tt.getO()+") = "+sim);
					rank.setNumeric(true);
				}
			} else {
				// string similarity
				sim = wed.compute(ts.getO(), tt.getO());
				if(ex.isParent())
					LOGGER.trace("wed("+ts.getO() +"," + tt.getO()+") = "+sim);
			}
		} else {
			// TODO The one is URI, the other is not.
			if(ex.isParent())
				LOGGER.trace("zero("+ts.getO() +"," + tt.getO()+") = 0.0");
			sim = 0.0;
		}
		
		return sim;
	}

}
