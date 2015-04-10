package org.aksw.tsoru.acids3.similarity;

import org.aksw.tsoru.acids3.db.Tuple;
import org.aksw.tsoru.acids3.model.Example;
import org.aksw.tsoru.acids3.model.Instance;
import org.apache.log4j.Logger;

/**
 * @author Tommaso Soru <t.soru@informatik.uni-leipzig.de>
 *
 */
public class OverallSimilarity {
	
	private static final Logger LOGGER = Logger.getLogger(OverallSimilarity.class);
	
	public Double compute(Example ex) {
		
		Instance src = ex.getSource();
		Instance tgt = ex.getTarget();
		
		// XXX tests ahead
		WEDSimilarity wed = new WEDSimilarity();
		
		Double dtot = 0.0;
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
					for(Tuple t : tgt2.getProcessing().getSql().getTuples(ts.getO())) {
						if(t.getS().equals(tt.getO()))
							tgt2.addTuple(t);
						else
							tgt2.addInverseTuple(t);
					}
					dtot += this.compute(ex2);
					
				} else if(!ts.getOtype().equals("URI") && !tt.getOtype().equals("URI")) {
					// classic similarity comparison
					try {
						Double.parseDouble(ts.getO());
						Double.parseDouble(tt.getO());
					} catch (NumberFormatException e) {
						// string similarity
						Double sim = wed.compute(ts.getO(), tt.getO());
						LOGGER.debug("wed("+ts.getO() +"," + tt.getO()+") = "+sim);
						dtot += sim;
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
					LOGGER.debug("lgs("+ts.getO() +"," + tt.getO()+") = "+sim);
					dtot += sim;
				} else {
					// TODO The one is URI, the other is not.
				}
			}
		}
		
		return dtot / (src.getTuples().size() + tgt.getTuples().size());
	}

}
