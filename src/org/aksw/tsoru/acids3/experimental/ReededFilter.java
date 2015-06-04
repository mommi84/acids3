package org.aksw.tsoru.acids3.experimental;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Vector;

/**
 * @author Tommaso Soru <tsoru@informatik.uni-leipzig.de>
 *
 */
public class ReededFilter extends WeightedEditDistanceFilter {
	
	public ReededFilter() {
		super();
	}
	
	public ArrayList<Pair> filter(ArrayList<Pair> intersection,
			String propertyName, double theta) {
		
		ArrayList<Pair> results = new ArrayList<Pair>();

		double tau = theta / getMinWeight();
		
		long start = System.currentTimeMillis();
		
		HashMap<String, Vector<Character>> index = new HashMap<String, Vector<Character>>();
		for(Pair c : intersection) {
			String sp = c.getS();
			Vector<Character> cs;
			if(!index.containsKey(sp)) {
				cs = new Vector<Character>();
				for(int i=0; i<sp.length(); i++)
					cs.add(sp.charAt(i));
				index.put(sp, cs);
			} else
				cs = index.get(sp);
			String tp = c.getT();
			Vector<Character> ct;
			if(!index.containsKey(tp)) {
				ct = new Vector<Character>();
				for(int i=0; i<tp.length(); i++)
					ct.add(tp.charAt(i));
				index.put(tp, ct);
			} else
				ct = index.get(tp);
			reededCore(sp, tp, cs, ct, tau, theta, results);
		}
		
		double compTime = (double)(System.currentTimeMillis()-start)/1000.0;
		System.out.println("REEDED: Join done in "+compTime+" seconds.");
	
		return results;
	}

	public ArrayList<Pair> filter(ArrayList<String> sources,
			ArrayList<String> targets, double theta) {
		
		ArrayList<Pair> results = new ArrayList<Pair>();

		double tau = theta / getMinWeight();
		
		long start = System.currentTimeMillis();
		
		HashMap<String, Vector<Character>> index = new HashMap<String, Vector<Character>>();
		for(String sp : sources) {
			Vector<Character> cs = new Vector<Character>();
			for(int i=0; i<sp.length(); i++)
				cs.add(sp.charAt(i));
			index.put(sp, cs);
		}
		for(String tp : targets) {
			Vector<Character> ct = new Vector<Character>();
			for(int i=0; i<tp.length(); i++)
				ct.add(tp.charAt(i));
			index.put(tp, ct);
		}
		int i=0, j=sources.size()/100;
		for(String sp : sources) {
			if(++i % j == 0)
				System.out.print(".");
			for(String tp : targets) {
				reededCore(sp, tp, index.get(sp), index.get(tp), tau, theta, results);
			}
		}
				
		double compTime = (System.currentTimeMillis()-start)/1000.0;
		System.out.println("REEDED: Join done in "+compTime+" seconds.");
		
		return results;
	}
	
	private double exclDisjSize(Vector<Character> cs, Vector<Character> ct) {
		Vector<Character> cs2 = new Vector<Character>(cs);
		Vector<Character> ct2 = new Vector<Character>(ct);
		for(Character c1 : ct)
			if(cs2.remove(c1))
				ct2.remove(c1);
		return cs2.size()+ct2.size();
	}
	
	private void reededCore(String sp, String tp, Vector<Character> cs, Vector<Character> ct, 
			double tau, double theta, ArrayList<Pair> results) {
		
		if(Math.abs(sp.length() - tp.length()) <= tau) {
			// (...) + (size % 2);
			if(Math.ceil(exclDisjSize(cs, ct) / 2.0) <= tau) {
				//  Verification.
				double d = this.getDistance(sp, tp);
				if(d <= theta) {
					Pair pair = new Pair(sp, tp);
					pair.setDistance(d);
					results.add(pair);
				}
			}
		}
		
	}


}

class Pair {
	private String s, t;
	private Double distance;
	Pair(String s, String t) {
		this.s = s;
		this.t = t;
	}
	public Double getDistance() {
		return distance;
	}
	public void setDistance(Double distance) {
		this.distance = distance;
	}
	public String getS() {
		return s;
	}
	public String getT() {
		return t;
	}
}
