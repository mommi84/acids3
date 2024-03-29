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
	
	private static final InstanceSimilarity INSTANCE_SIM = new InstanceSimilarity();
	private static final ValueSimilarity VALUE_SIM = new ValueSimilarity();
	private static final HybridSimilarity HYBRID_SIM = new HybridSimilarity();

	
	/**
	 * Handle the node natures and call the respective similarity class.
	 * 
	 * @param s source node
	 * @param t target node
	 * @param depth visit iteration depth
	 * @return similarity bean
	 */
	public static SimilarityBean compute(GeneralNode s, GeneralNode t, Example ex, int depth) {
		
		if(s.isSuperconnected() || t.isSuperconnected()) {
			LOGGER.trace(s+" or "+t+" -> superconnected");
			SimilarityBean bean = new SimilarityBean();
			bean.setValue(s.getID().equals(t.getID()) ? 1.0 : 0.0);
			bean.setType(SimType.INSTANCE_SIM);
			return bean;
		}
		
		// TODO handle for evaluation...
		
		NodeSimilarity sim;
		if(s instanceof Instance) {
			if(t instanceof Instance) {
				sim = INSTANCE_SIM;
			} else {
				sim = HYBRID_SIM;
			}
		} else {
			if(t instanceof Instance) {
				sim = HYBRID_SIM;
			} else {
				sim = VALUE_SIM;
			}
		}

		SimilarityBean bean = sim.compute(s, t, ex, depth);
//		LOGGER.trace(sim.getClass().getSimpleName()+"("+s+", "+t+ ", depth="+depth+") = "+d);
		return bean;
	
	}
	
	/**
	 * Comfortable call for the Example class.
	 * 
	 * @param e
	 * @return
	 */
	public static SimilarityBean compute(Example e) {
		return compute(e.getSource(), e.getTarget(), e, 0);
	}

}
