package org.aksw.tsoru.acids3.util;

import org.aksw.tsoru.acids3.model.Instance;

/**
 * @author Tommaso Soru <t.soru@informatik.uni-leipzig.de>
 *
 */
public class Cache {

	public int i = 0;
	public Integer nTriples = null;
	public Integer pick = null;
	public Instance instance = null;
	
	public Cache() {
		super();
	}

	public void saveCount() {
		nTriples = new Integer(i);
		this.iReset();
	}

	public void iReset() {
		i = 0;
	}

}
