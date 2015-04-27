package org.aksw.tsoru.acids3.model;

/**
 * @author Tommaso Soru <tsoru@informatik.uni-leipzig.de>
 *
 */
public abstract class GeneralNode {
	
	protected String id;
	
	public GeneralNode() {
		super();
	}
	
	public String getID() {
		return id;
	}
	
	@Override
	public String toString() {
		return getID();
	}
	
}