package org.aksw.tsoru.acids3.filters;

import java.util.HashMap;
import java.util.Vector;

import org.aksw.tsoru.acids3.sim.value.WEDSimilarity;
import org.aksw.tsoru.acids3.util.Transform;
import org.apache.log4j.Logger;

/**
 * REEDED string similarity join filtering, as described in Tommaso Soru and
 * Axel-Cyrille Ngonga Ngomo, "Rapid execution of weighted edit distances." 
 * In OM, pp. 1-12. 2013.
 * 
 * @author Tommaso Soru <tsoru@informatik.uni-leipzig.de>
 *
 */
public class ReededFilter {

	private static final Logger LOGGER = Logger.getLogger(ReededFilter.class);

	private static HashMap<String, Vector<Character>> index = new HashMap<String, Vector<Character>>();
	
	private static double tau;
	
	public ReededFilter() {
		super();
	}

	/**
	 * Runs indexing (if needed) and filtering. 
	 * 
	 * @param sp source string
	 * @param tp target string
	 * @param theta similarity threshold
	 * @return whether the similarity join passes the filtering
	 */
	public boolean filter(String sp, String tp, double theta) {

		double tau = Transform.toDistance(theta) / WEDSimilarity.getMinWeight();
		
		LOGGER.trace("Threshold for weighted edit distance is "+tau);

		Vector<Character> cs, ct;
		
		if(index.containsKey(sp))
			cs = index.get(sp);
		else {
			cs = new Vector<Character>();
			for(int i=0; i<sp.length(); i++)
				cs.add(sp.charAt(i));
			index.put(sp, cs);
		}
		if(index.containsKey(tp))
			ct = index.get(tp);
		else {
			ct = new Vector<Character>();
			for(int i=0; i<tp.length(); i++)
				ct.add(tp.charAt(i));
			index.put(tp, ct);
		}
		
		if (Math.abs(sp.length() - tp.length()) <= tau
				&& Math.ceil(exclDisjSize(cs, ct) / 2.0) <= tau)
			return true;

		return false;
	}
	
	public Vector<Character> indexSource(String sp) {
		Vector<Character> cs;
		if(index.containsKey(sp))
			cs = index.get(sp);
		else {
			cs = new Vector<Character>();
			for(int i=0; i<sp.length(); i++)
				cs.add(sp.charAt(i));
			index.put(sp, cs);
		}
		return cs;
	}
	
	public void computeTau(double theta) {
		tau = Transform.toDistance(theta) / WEDSimilarity.getMinWeight();
	}
	
	public boolean filterTargets(String sp, Vector<Character> cs, String tp) {
		Vector<Character> ct;
		if(index.containsKey(tp))
			ct = index.get(tp);
		else {
			ct = new Vector<Character>();
			for(int i=0; i<tp.length(); i++)
				ct.add(tp.charAt(i));
			index.put(tp, ct);
		}
		
		if (Math.abs(sp.length() - tp.length()) <= tau
				&& Math.ceil(exclDisjSize(cs, ct) / 2.0) <= tau)
			return true;

		return false;
	}

	private static double exclDisjSize(Vector<Character> cs, Vector<Character> ct) {
		Vector<Character> cs2 = new Vector<Character>(cs);
		Vector<Character> ct2 = new Vector<Character>(ct);
		for (Character c1 : ct)
			if (cs2.remove(c1))
				ct2.remove(c1);
		return cs2.size() + ct2.size();
	}
	
	

} 