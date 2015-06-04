package org.aksw.tsoru.acids3.sim;

/**
 * @author Tommaso Soru <tsoru@informatik.uni-leipzig.de>
 *
 */
public class SimilarityBean {
	
	private SimType type;
	private Double value;
	
	public SimilarityBean() {
		super();
	}
	
	public SimType getType() {
		return type;
	}
	public void setType(SimType type) {
		this.type = type;
	}
	public Double getValue() {
		return value;
	}
	public void setValue(Double value) {
		this.value = value;
	}

}
