package org.aksw.tsoru.acids3.model;

/**
 * @author Tommaso Soru <tsoru@informatik.uni-leipzig.de>
 *
 */
public class DatatypeNode extends GeneralNode {
	
	public DatatypeNode(String value) {
		super(value);
	}

	/**
	 * Value is the ID itself.
	 * 
	 * @return
	 */
	public String getValue() {
		return this.getID();
	}
	
}
