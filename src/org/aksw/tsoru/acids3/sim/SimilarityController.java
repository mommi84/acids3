package org.aksw.tsoru.acids3.sim;

import org.aksw.tsoru.acids3.model.Example;
import org.aksw.tsoru.acids3.model.GeneralNode;
import org.aksw.tsoru.acids3.model.Instance;

/**
 * @author Tommaso Soru <tsoru@informatik.uni-leipzig.de>
 *
 */
public class SimilarityController {
	
	private static final InstanceSimilarity NODE_SIM = new InstanceSimilarity();
	private static final ValueSimilarity VALUE_SIM = new ValueSimilarity();
	private static final HybridSimilarity HYBRID_SIM = new HybridSimilarity();
	
	/**
	 * Handle the node natures and call the respective similarity class.
	 * 
	 * @param s source node
	 * @param t target node
	 * @param depth visit iteration depth
	 * @return similarity value
	 */
	public static Double compute(GeneralNode s, GeneralNode t, int depth) {
		if(s instanceof Instance) {
			if(t instanceof Instance)
				return NODE_SIM.compute(s, t, depth);
			else
				return HYBRID_SIM.compute(s, t, depth);
		} else {
			if(t instanceof Instance)
				return HYBRID_SIM.compute(s, t, depth);
			else
				return VALUE_SIM.compute(s, t, depth);
		}
	}
	
	/**
	 * Comfortable call for the Example class.
	 * 
	 * @param e
	 * @return
	 */
	public static Double compute(Example e) {
		return compute(e.getSource(), e.getTarget(), 0);
	}

}
