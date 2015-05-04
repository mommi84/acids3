package org.aksw.tsoru.acids3.sim;

/**
 * @author Tommaso Soru <tsoru@informatik.uni-leipzig.de>
 *
 */
public enum SimType {
	
	INSTANCE_SIM("instance similarity", new InstanceSimilarity()),
	HYBRID_SIM("hybrid similarity", new HybridSimilarity()),
	STRING_SIM("string similarity", new ValueSimilarity()),
	DOUBLE_SIM("double similarity", new ValueSimilarity());
	
	SimType(String name, NodeSimilarity sim) {
		this.name = name;
		this.sim = sim;
	}
	
	public String getName() {
		return name;
	}

	public NodeSimilarity getSim() {
		return sim;
	}

	private String name;
	private NodeSimilarity sim;

}
