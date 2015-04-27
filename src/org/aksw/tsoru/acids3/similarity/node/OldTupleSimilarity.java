package org.aksw.tsoru.acids3.similarity.node;

import java.util.ArrayList;

import org.aksw.tsoru.acids3.db.Tuple;
import org.aksw.tsoru.acids3.filters.AllowedFilter;
import org.aksw.tsoru.acids3.model.Example;
import org.aksw.tsoru.acids3.model.Instance;
import org.aksw.tsoru.acids3.similarity.value.LogarithmicSimilarity;
import org.aksw.tsoru.acids3.similarity.value.WEDSimilarity;
import org.apache.commons.math3.stat.descriptive.moment.Mean;
import org.apache.log4j.Logger;

/**
 * @author Tommaso Soru <tsoru@informatik.uni-leipzig.de>
 *
 */
public class OldTupleSimilarity {
	
	private static final Logger LOGGER = Logger.getLogger(OldTupleSimilarity.class);
	private static WEDSimilarity wed = new WEDSimilarity();

	public OldTupleSimilarity() {
		super();
	}
	
	public Double compute(Tuple ts, Tuple tt, LogarithmicSimilarity srcLogsim, LogarithmicSimilarity tgtLogsim, Example ex, AllowedFilter rank) {
		
		String sObj = ts.getO(), tObj = tt.getO();
		
		Double sim = null;
		
		if(ts.getOtype().equals("URI") && tt.getOtype().equals("URI") && ex.isParent()) {
			
			Instance src2 = new Instance(sObj);
			src2.setProcessing(ex.getSource().getProcessing());
			Instance tgt2 = new Instance(tObj);
			tgt2.setProcessing(ex.getTarget().getProcessing());
			Example ex2 = new Example(src2, tgt2);
			ex2.setParent(false);
			for(Tuple t : src2.getProcessing().getSql().getTuples(sObj)) {
				if(t.getS().equals(sObj))
					src2.add(t);
				else
					src2.addInverse(t);
			}
			for(Tuple t : tgt2.getProcessing().getSql().getTuples(tObj)) {
				if(t.getS().equals(tObj))
					tgt2.add(t);
				else
					tgt2.addInverse(t);
			}
			
			ArrayList<Double> features = new ArrayList<Double>();
			for(Tuple ts2 : src2.getTuples())
				for(Tuple tt2 : tgt2.getTuples())
					features.add(this.compute(ts2, tt2, src2.getProcessing().getLogsim(ts2.getP()), tgt2.getProcessing().getLogsim(tt2.getP()), ex2, null));
			
			Mean mean = new Mean();
			double[] feat = new double[features.size()];
			if(feat.length > 10000) {
				LOGGER.warn("For '"+"all("+sObj+","+tObj+"' feature length is "+feat.length+"!");
			}
			for(int i=0; i<feat.length; i++)
				feat[i] = features.get(i);
			
			sim = mean.evaluate(feat);
			
			LOGGER.trace("all("+sObj+","+tObj+") = "+sim);
			
		} else if(!ts.getOtype().equals("URI") && !tt.getOtype().equals("URI")) {
			boolean isNumeric = true;
			// classic similarity comparison
			try {
				Double.parseDouble(sObj);
				Double.parseDouble(tObj);
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
				sim = logsim.compute(sObj, tObj);
				if(ex.isParent()) {
					LOGGER.trace("lgs("+sObj +"," + tObj+") = "+sim);
					rank.setNumeric(true);
				}
			} else {
				// string similarity
				sim = wed.compute(sObj, tObj);
				if(ex.isParent())
					LOGGER.trace("wed("+sObj +"," + tObj+") = "+sim);
			}
		} else {
			// TODO The one is URI, the other is not.
			if(ex.isParent())
				LOGGER.trace("zero("+sObj +"," + tObj+") = 0.0");
			sim = 0.0;
		}
		
		return sim;
	}

}
