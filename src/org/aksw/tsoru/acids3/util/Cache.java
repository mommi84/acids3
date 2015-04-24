package org.aksw.tsoru.acids3.util;

import org.aksw.tsoru.acids3.model.Instance;

/**
 * @author Tommaso Soru <tsoru@informatik.uni-leipzig.de>
 *
 */
public class Cache {

	public int i = 0;
	public Integer nTriples = null;
	public Integer pick = null;
	public Instance instance = null;
	
	// default values
	public Double max = Double.NEGATIVE_INFINITY;
	public Double min = Double.POSITIVE_INFINITY;
	
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
