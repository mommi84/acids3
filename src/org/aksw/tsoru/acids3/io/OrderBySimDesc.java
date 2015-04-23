package org.aksw.tsoru.acids3.io;

import java.util.Comparator;

import org.aksw.tsoru.acids3.model.Example;

/**
 * @author Tommaso Soru <tsoru@informatik.uni-leipzig.de>
 *
 */
public class OrderBySimDesc implements Comparator<Example> {

	@Override
	public int compare(Example arg0, Example arg1) {
		// swap for decreasing order
		return arg1.getSim().compareTo(arg0.getSim());
	}

}
