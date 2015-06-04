package org.aksw.tsoru.acids3.db;

import org.aksw.tsoru.acids3.io.Processing;
import org.aksw.tsoru.acids3.model.DatatypeNode;
import org.aksw.tsoru.acids3.model.GeneralNode;
import org.aksw.tsoru.acids3.model.Instance;

/**
 * @author Tommaso Soru <tsoru@informatik.uni-leipzig.de>
 *
 */
public class Tuple {
	
	private String s, p, o, otype;
	
	private Instance subj;
	private GeneralNode obj;

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

	public Tuple(String s, String p, String o, String otype, Processing pro) {
		super();
		this.s = s;
		this.p = p;
		this.o = o;
		this.otype = otype;
		
		this.subj = new Instance(s);
		this.subj.setProcessing(pro);
		
		if(otype.equals("URI"))
			this.obj = new Instance(o);
		else
			this.obj = new DatatypeNode(o);
		this.obj.setProcessing(pro);
	}
	
	public String toString() {
		return s + " " + p + " " + o;
	}

	public Instance getSubj() {
		return subj;
	}

	public GeneralNode getObj() {
		return obj;
	}
	
	

}
