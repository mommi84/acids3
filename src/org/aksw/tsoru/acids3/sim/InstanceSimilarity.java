package org.aksw.tsoru.acids3.sim;

import java.util.ArrayList;

import org.aksw.tsoru.acids3.db.Tuple;
import org.aksw.tsoru.acids3.io.CBDBuilder;
import org.aksw.tsoru.acids3.model.Example;
import org.aksw.tsoru.acids3.model.GeneralNode;
import org.aksw.tsoru.acids3.model.Instance;
import org.apache.commons.math3.stat.descriptive.moment.Mean;

/**
 * Compute similarity among instances as the mean of all the node similarities.
 * 
 * @author Tommaso Soru <tsoru@informatik.uni-leipzig.de>
 *
 */
public class InstanceSimilarity implements NodeSimilarity {

	@Override
	public Double compute(GeneralNode s, GeneralNode t, Example ex, int depth) {
		
		depth = depth + 1;
		if(depth > MAX_DEPTH)
			return null;
		
		Instance src = (Instance) s;
		if(!src.isCrawled())
			CBDBuilder.build(src.getProcessing(), src);
		Instance tgt = (Instance) t;
		if(!tgt.isCrawled())
			CBDBuilder.build(tgt.getProcessing(), tgt);

		ArrayList<Double> features = new ArrayList<Double>();

		for (Tuple ts : src.getTuples()) {
			for (Tuple tt : tgt.getTuples()) {

				GeneralNode sObj = ts.getObj();
				GeneralNode tObj = tt.getObj();
				
				Double d = SimilarityController.compute(sObj, tObj, ex, depth);
				// if depth too deep, d is null
				if(d != null) {
					features.add(d);
					if(depth == 1)
						ex.setFeature(ts.getP()+"#"+tt.getP(), d);
				}

			}
		}
		
		LOGGER.trace("featsize("+src+","+tgt+") = "+features.size());
		// cannot say anything
		if(features.isEmpty())
			return null;

		Mean mean = new Mean();
		double[] feat = new double[features.size()];
		for (int i = 0; i < feat.length; i++)
			feat[i] = features.get(i);

		return mean.evaluate(feat);
	}

}
