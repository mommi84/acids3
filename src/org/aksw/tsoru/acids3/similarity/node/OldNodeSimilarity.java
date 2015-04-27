package org.aksw.tsoru.acids3.similarity.node;

import java.util.ArrayList;

import org.aksw.tsoru.acids3.filters.AllowedFilter;
import org.aksw.tsoru.acids3.model.Example;

/**
 * @author Tommaso Soru <tsoru@informatik.uni-leipzig.de>
 *
 */
public interface OldNodeSimilarity {
	
	public Double compute(Example ex, ArrayList<AllowedFilter> filters);

}
