package org.aksw.tsoru.acids3.sim;

import org.aksw.tsoru.acids3.model.Example;
import org.aksw.tsoru.acids3.model.GeneralNode;

/**
 * @author Tommaso Soru <tsoru@informatik.uni-leipzig.de>
 *
 */
public class HybridSimilarity implements NodeSimilarity {

	@Override
	public Double compute(GeneralNode s, GeneralNode t, Example ex, int depth) {
		// TODO Similarity among URI and datatype...
		return 0.0;
	}

}
