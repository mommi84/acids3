package org.aksw.tsoru.acids3.util;

import java.util.ArrayList;

import org.aksw.tsoru.acids3.model.Instance;

/**
 * @author Tommaso Soru <tsoru@informatik.uni-leipzig.de>
 *
 */
public class Cache {

	public int i = 0;
	public Integer nTriples = null;
	public ArrayList<Integer> pick = null;
	public ArrayList<Instance> instances = null;
	
	// default values
	public Double max = Double.NEGATIVE_INFINITY;
	public Double min = Double.POSITIVE_INFINITY;
	
	public Cache() {
		super();
		pick = new ArrayList<Integer>();
		instances = new ArrayList<Instance>();
	}

	public void saveCount() {
		nTriples = new Integer(i);
		this.iReset();
	}

	public void iReset() {
		i = 0;
	}
	
	public void resetInstances() {
		pick.clear();
		instances.clear();
	}

}
