package org.aksw.tsoru.acids3.sim;

import org.aksw.tsoru.acids3.model.Example;
import org.aksw.tsoru.acids3.model.GeneralNode;
import org.aksw.tsoru.acids3.model.Instance;
import org.apache.log4j.Logger;

/**
 * @author Tommaso Soru <tsoru@informatik.uni-leipzig.de>
 *
 */
public class SimilarityController {
	
	private static final Logger LOGGER = Logger.getLogger(SimilarityController.class);
	
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
	public static Double compute(GeneralNode s, GeneralNode t, Example ex, int depth) {
		NodeSimilarity sim;
		if(s instanceof Instance) {
			if(t instanceof Instance)
				sim = NODE_SIM;
			else
				sim = HYBRID_SIM;
		} else {
			if(t instanceof Instance)
				sim = HYBRID_SIM;
			else
				sim = VALUE_SIM;
		}

		Double d = sim.compute(s, t, ex, depth);
//		LOGGER.trace(sim.getClass().getSimpleName()+"("+s+", "+t+ ", depth="+depth+") = "+d);
		return d;
	
	}
	
	/**
	 * Comfortable call for the Example class.
	 * 
	 * @param e
	 * @return
	 */
	public static Double compute(Example e) {
		return compute(e.getSource(), e.getTarget(), e, 0);
	}

}
