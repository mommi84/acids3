package org.aksw.tsoru.acids3.model;

import org.aksw.tsoru.acids3.io.Processing;

/**
 * @author Tommaso Soru <tsoru@informatik.uni-leipzig.de>
 *
 */
public abstract class GeneralNode {
	
	protected String id;
	protected Processing processing;

	public GeneralNode(String id) {
		super();
		this.id = id;
	}
	
	public String getID() {
		return id;
	}
	
	@Override
	public String toString() {
		return getID();
	}

	public Processing getProcessing() {
		return processing;
	}

	public void setProcessing(Processing processing) {
		this.processing = processing;
	}
	
	public boolean isSuperconnected() {
		if(processing.getHubs().contains(id))
			return true;
		if(processing.getAuths().contains(id))
			return true;
		return false;
	}


}
