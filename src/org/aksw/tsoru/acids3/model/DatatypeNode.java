package org.aksw.tsoru.acids3.model;

/**
 * @author Tommaso Soru <tsoru@informatik.uni-leipzig.de>
 *
 */
public class DatatypeNode extends GeneralNode {
	
	private String value;
	
	public DatatypeNode(String value) {
		super();
		this.setValue(value);
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
	
}
