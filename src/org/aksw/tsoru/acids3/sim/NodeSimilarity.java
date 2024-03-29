package org.aksw.tsoru.acids3.sim;

import org.aksw.tsoru.acids3.model.Example;
import org.aksw.tsoru.acids3.model.GeneralNode;
import org.apache.log4j.Logger;

/**
 * @author Tommaso Soru <tsoru@informatik.uni-leipzig.de>
 *
 */
public interface NodeSimilarity {
	
	public static final int MAX_DEPTH = 2;
	
	static final Logger LOGGER = Logger.getLogger(NodeSimilarity.class);
	
	public SimilarityBean compute(GeneralNode s, GeneralNode t, Example ex, int depth);
	
}
