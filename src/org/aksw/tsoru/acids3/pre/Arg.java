package org.aksw.tsoru.acids3.pre;

/**
 * @author Tommaso Soru <t.soru@informatik.uni-leipzig.de>
 *
 */
public enum Arg {
	
	SOURCE(0, "source"),
	TARGET(1, "target");
	
	public int getPos() {
		return pos;
	}

	public String getName() {
		return name;
	}

	int pos;
	String name;

	Arg(int pos, String name) {
		this.pos = pos;
		this.name = name;
	}
	
}
