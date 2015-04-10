package org.aksw.tsoru.acids3.model;

import org.aksw.tsoru.acids3.io.Processing;

/**
 * @author Tommaso Soru <t.soru@informatik.uni-leipzig.de>
 *
 */
public class Example {

	private Instance source, target;
	
	private Processing processing;
	
	private Double sim;

	public Example(Instance source, Instance target) {
		super();
		this.source = source;
		this.target = target;
	}

	public Instance getSource() {
		return source;
	}

	public void setSource(Instance source) {
		this.source = source;
	}

	public Instance getTarget() {
		return target;
	}

	public void setTarget(Instance target) {
		this.target = target;
	}

	public Double getSim() {
		return sim;
	}

	public void setSim(Double sim) {
		this.sim = sim;
	}
	
	public String toString() {
		return "<" + source.getURI() + ", " + target.getURI() + ", " + sim + ">";
	}

	public Processing getProcessing() {
		return processing;
	}

	public void setProcessing(Processing processing) {
		this.processing = processing;
	}
	
}
