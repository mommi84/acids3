package org.aksw.tsoru.acids3.model;

import java.util.Collection;
import java.util.HashMap;
import java.util.TreeSet;

import org.aksw.tsoru.acids3.sim.SimType;


/**
 * @author Tommaso Soru <tsoru@informatik.uni-leipzig.de>
 *
 */
public class Example {

	private Instance source, target;
	
	private HashMap<String, Double> features;
	
	private Double sim;

	private boolean parent = true;
	
	private boolean unsupervised = false;

	public boolean getLabel() {
		return label;
	}

	private boolean label = false;

	public Example(Instance source, Instance target) {
		super();
		this.source = source;
		this.target = target;
		features = new HashMap<String, Double>();
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
		return "< " + source.getID() + ", " + target.getID() + ", " + sim + " >";
	}

	public void setParent(boolean parent) {
		this.parent = parent;
	}

	public boolean isParent() {
		return parent;
	}
	
	public void setLabel(boolean label) {
		this.label  = label;
	}

	public Collection<Double> getFeatureValues() {
//		ArrayList<Double> f = new ArrayList<Double>();
//		for(String key : features.keySet())
//			f.add(features.get(key));
//		return f;
		return features.values();
	}
	
	public HashMap<String, Double> getFeatures() {
		return features;
	}

//	public void setFeatures(ArrayList<Double> features) {
//		this.features = features;
//	}
//
//
//	public void setNames(ArrayList<String> names) {
//		this.names = names;
//	}
//
	public Collection<String> getFeatureNames() {
		return features.keySet();
	}
	
	public void setFeature(String name, Double value) {
		features.put(name, value);
	}

	/**
	 * Keep only features from featureNames, as other features have no SVM weight associated.
	 * 
	 * @param featureNames
	 */
	public void spoil(TreeSet<String> featureNames) {
		
		// remove futile features
		TreeSet<String> toRemove = new TreeSet<String>();
		for(String name : features.keySet())
			if(!featureNames.contains(name))
				toRemove.add(name);
		for(String name : toRemove)
			features.remove(name);
		
		// put zeroes for absent properties
		for(String name : featureNames)
			if(!features.containsKey(name))
				features.put(name, 0.0);
		
	}

	/**
	 * Forward request to the Processing.
	 * 
	 * @param fname
	 * @param type
	 */
	public void countFeatureRecord(String fname, SimType type) {
		source.getProcessing().countFeatureRecord(fname, type);
	}

	public boolean isUnsupervised() {
		return unsupervised;
	}

	public void setUnsupervised(boolean unsupervised) {
		this.unsupervised = unsupervised;
	}
	
}
