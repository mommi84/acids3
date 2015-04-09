package org.aksw.tsoru.acids3.io;

import java.util.Comparator;

import org.aksw.tsoru.acids3.model.Example;

/**
 * @author Tommaso Soru <t.soru@informatik.uni-leipzig.de>
 *
 */
public class OrderBySimDesc implements Comparator<Example> {

	@Override
	public int compare(Example arg0, Example arg1) {
		return arg0.getSim().compareTo(arg1.getSim());
	}

}
