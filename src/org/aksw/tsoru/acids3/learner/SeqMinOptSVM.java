package org.aksw.tsoru.acids3.learner;

import java.util.ArrayList;

import org.aksw.tsoru.acids3.model.Example;
import org.apache.log4j.Logger;

import weka.classifiers.functions.SMO;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instances;

/**
 * @author Tommaso Soru <t.soru@informatik.uni-leipzig.de>
 *
 */
public class SeqMinOptSVM {
	
	private static final Logger LOGGER = Logger.getLogger(SeqMinOptSVM.class);
	
	private Instances train, test;
	private Attribute classAttribute;
	
	private ArrayList<Attribute> attributes;
	private ArrayList<Attribute> fvWekaAttributes;
	
	private ArrayList<String> names;
	
	private double[] weights;
	private double bias;
	
	private SMO cModel;

	public SeqMinOptSVM() {
		super();
	}

	public void init(Example ex, int nInst) {
		
		int nAttr = ex.getFeatures().size();
		names = ex.getNames();
		
		attributes = new ArrayList<Attribute>();
		for (String name : names)
			attributes.add(new Attribute(name));

		// Declare the class attribute along with its values
		ArrayList<String> fvClassVal = new ArrayList<String>(2);
		fvClassVal.add("true");
		fvClassVal.add("false");
		classAttribute = new Attribute("theClass", fvClassVal);

		// Declare the feature vector
		fvWekaAttributes = new ArrayList<Attribute>(nAttr + 1);
		for (Attribute attr : attributes)
			fvWekaAttributes.add(attr);
		fvWekaAttributes.add(classAttribute);

		// Create an empty training set
		train = new Instances("sameAs", fvWekaAttributes, nInst);
		// Set class index
		train.setClassIndex(nAttr);
		
		cModel = new SMO();
		
	}

	public void addInstance(Example ex) {

		ArrayList<Double> features = ex.getFeatures();
		DenseInstance inst = new DenseInstance(features.size() + 1);
		for (int i = 0; i < features.size(); i++)
			inst.setValue(attributes.get(i), features.get(i));
		inst.setValue(classAttribute, "" + ex.getLabel());
		
		train.add(inst);
		
	}

	public void train(boolean printMatrix) {
		try {
			LOGGER.info("Training classifier with "+train.size()+" examples...");
			cModel.buildClassifier(train);
			LOGGER.info("Classifier built.");
		} catch (Exception e) {
			LOGGER.fatal(e.getMessage());
			return;
		}
		double[] w_ = cModel.sparseWeights()[0][1];
		int[] indices = cModel.sparseIndices()[0][1];
		bias = cModel.bias()[0][1];
		weights = new double[attributes.size()];
		for(int i=0; i<indices.length; i++)
			weights[indices[i]] = w_[i];
		int radix = (int) Math.sqrt(weights.length);
		if(printMatrix) {
			for(int i=0; i<weights.length; i++) {
				System.out.print(names.get(i)+"\t");
				if(i % radix == radix - 1)
					System.out.println("");
			}
			for(int i=0; i<weights.length; i++) {
				System.out.print(weights[i]+"\t");
				if(i % radix == radix - 1)
					System.out.println("");
			}
			System.out.println("bias = " + bias);
		}
	}
	
	public boolean classify(Example ex) {
		test = new Instances("sameAs", fvWekaAttributes, 1);
		test.setClassIndex(fvWekaAttributes.size() - 1);
		ArrayList<Double> features = ex.getFeatures();
		DenseInstance inst = new DenseInstance(features.size());
		for (int i = 0; i < features.size(); i++)
			inst.setValue(attributes.get(i), features.get(i));
		inst.setDataset(test);
		double cl;
		try {
			cl = cModel.classifyInstance(inst);
			LOGGER.info("classify: " + cl);
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
			return false;
		}
		if(cl == 0.0) // 'true' was set as first [0] element
			return true;
		return false;
	}

	public double[] getWeights() {
		return weights;
	}

	public double getBias() {
		return bias;
	}
	
	
}
