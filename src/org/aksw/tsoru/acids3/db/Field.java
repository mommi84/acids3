package org.aksw.tsoru.acids3.db;

/**
 * @author Tommaso Soru <tsoru@informatik.uni-leipzig.de>
 *
 */
public enum Field {
	
	OUTCOMING("outcoming"),
	INCOMING("incoming");
	
	private String name;
	
	Field(String name) {
		this.name = name;
	}
	
	public String getName() {
		return name;
	}

}
