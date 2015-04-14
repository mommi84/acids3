package org.aksw.tsoru.acids3.similarity;

import java.util.ArrayList;

import org.aksw.tsoru.acids3.db.Tuple;
import org.aksw.tsoru.acids3.filters.ReededFilter;
import org.aksw.tsoru.acids3.model.Example;
import org.aksw.tsoru.acids3.model.Instance;
import org.apache.commons.math3.stat.descriptive.moment.Mean;
import org.apache.log4j.Logger;

/**
 * @author Tommaso Soru <t.soru@informatik.uni-leipzig.de>
 *
 */
public class OverallSimilarity {
	
	private static final Logger LOGGER = Logger.getLogger(OverallSimilarity.class);
	
	public OverallSimilarity() {
		super();
	}
	
	public Double compute(Example ex, final ReededFilter filter) {
		
		Instance src = ex.getSource();
		Instance tgt = ex.getTarget();
		
		WEDSimilarity wed = new WEDSimilarity();
		
		ArrayList<Double> features = new ArrayList<Double>();
		ArrayList<String> featureNames = new ArrayList<String>();
		
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
							src2.addTuple(t);
						else
							src2.addInverseTuple(t);
					}
					for(Tuple t : tgt2.getProcessing().getSql().getTuples(tt.getO())) {
						if(t.getS().equals(tt.getO()))
							tgt2.addTuple(t);
						else
							tgt2.addInverseTuple(t);
					}
					Double sim = this.compute(ex2, filter);
					// if some property values of a resource didn't make the cut
					if(sim == null) {
						if(ex.isParent())
							LOGGER.debug(" ---> didn't make the cut (URI)");
						return null;
					}
					LOGGER.debug("all("+ts.getO()+","+tt.getO()+") = "+sim);
					features.add(sim);
					featureNames.add("all("+ts.getP()+","+tt.getP()+")");

				} else if(!ts.getOtype().equals("URI") && !tt.getOtype().equals("URI")) {
					// classic similarity comparison
					try {
						Double.parseDouble(ts.getO());
						Double.parseDouble(tt.getO());
					} catch (NumberFormatException e) {
						// string similarity
						if(filter != null) {
							double threshold = 0.3;
							if(ex.isParent())
								LOGGER.debug(ts.getO()+", "+tt.getO()+", "+threshold);
							if(!filter.filter(ts.getO(), tt.getO(), threshold)) {
								if(ex.isParent())
									LOGGER.debug(" ---> didn't make the cut");
								// XXX check this one
								return null;
							}
						}
						// actual similarity computation
						Double sim = wed.compute(ts.getO(), tt.getO());
						if(ex.isParent())
							LOGGER.debug("wed("+ts.getO() +"," + tt.getO()+") = "+sim);
						features.add(sim);
						featureNames.add("wed("+ts.getP() +"," + tt.getP()+")");
						continue;
					}
					// double similarity
					Double minMin = Math.min(
							ex.getSource().getProcessing().getLogsim(ts.getP()).getMinMin(),
							ex.getTarget().getProcessing().getLogsim(tt.getP()).getMinMin());
					Double denomArg = Math.max(
							ex.getSource().getProcessing().getLogsim(ts.getP()).getDenomArg(),
							ex.getTarget().getProcessing().getLogsim(tt.getP()).getDenomArg());
					LogarithmicSimilarity logsim = new LogarithmicSimilarity();
					logsim.setMinMin(minMin);
					logsim.setDenomArg(denomArg);
					Double sim = logsim.compute(ts.getO(), tt.getO());
					if(ex.isParent())
						LOGGER.debug("lgs("+ts.getO() +"," + tt.getO()+") = "+sim);
					features.add(sim);
					featureNames.add("lgs("+ts.getP() +"," + tt.getP()+")");
				} else {
					// TODO The one is URI, the other is not.
					if(ex.isParent())
						LOGGER.debug("zero("+ts.getO() +"," + tt.getO()+") = 0.0");
					features.add(0.0);
					featureNames.add("zero("+ts.getP() +"," + tt.getP()+")");
				}
			}
		}
		
		Mean mean = new Mean();
		double[] feat = new double[features.size()];
		for(int i=0; i<feat.length; i++) {
			feat[i] = features.get(i);
			LOGGER.debug(feat[i]);
		}
		
		for(int i=0; i<featureNames.size(); i++)
			ex.setFeature(featureNames.get(i), features.get(i));
		
		return mean.evaluate(feat);
	}

}
