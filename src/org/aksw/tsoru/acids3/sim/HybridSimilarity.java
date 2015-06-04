package org.aksw.tsoru.acids3.sim;

import org.aksw.tsoru.acids3.model.Example;
import org.aksw.tsoru.acids3.model.GeneralNode;

/**
 * @author Tommaso Soru <tsoru@informatik.uni-leipzig.de>
 *
 */
public class HybridSimilarity implements NodeSimilarity {

	@Override
	public SimilarityBean compute(GeneralNode s, GeneralNode t, Example ex, int depth) {
		SimilarityBean bean = new SimilarityBean();
		bean.setType(SimType.HYBRID_SIM);
		// TODO Similarity among URI and datatype...
		bean.setValue(0.0);
		return bean;
	}

}
