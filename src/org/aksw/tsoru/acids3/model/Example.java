package org.aksw.tsoru.acids3.model;

import java.util.ArrayList;


/**
 * @author Tommaso Soru <t.soru@informatik.uni-leipzig.de>
 *
 */
public class Example {

	private Instance source, target;
	
	private ArrayList<Double> features;
	private ArrayList<String> names;
	
	private Double sim;

	private boolean parent = true;

	public boolean getLabel() {
		return label;
	}

	private boolean label = false;

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

	public void setParent(boolean parent) {
		this.parent = parent;
	}

	public boolean isParent() {
		return parent;
	}

	public ArrayList<Double> getFeatures() {
		return features;
	}

	public void setFeatures(ArrayList<Double> features) {
		this.features = features;
	}

	public void setLabel(boolean label) {
		this.label  = label;
	}

	public void setNames(ArrayList<String> names) {
		this.names = names;
	}

	public ArrayList<String> getNames() {
		return names;
	}
	
}
