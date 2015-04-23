package org.aksw.tsoru.acids3.db;

/**
 * @author Tommaso Soru <tsoru@informatik.uni-leipzig.de>
 *
 */
public class Tuple {
	
	private String s, p, o, otype;

	public String getS() {
		return s;
	}

	public void setS(String s) {
		this.s = s;
	}

	public String getP() {
		return p;
	}

	public void setP(String p) {
		this.p = p;
	}

	public String getO() {
		return o;
	}

	public void setO(String o) {
		this.o = o;
	}

	public String getOtype() {
		return otype;
	}

	public void setOtype(String otype) {
		this.otype = otype;
	}

	public Tuple(String s, String p, String o, String otype) {
		super();
		this.s = s;
		this.p = p;
		this.o = o;
		this.otype = otype;
	}
	
	public String toString() {
		return s + " " + p + " " + o;
	}
	

}
