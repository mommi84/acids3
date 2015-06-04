package org.aksw.tsoru.acids3.util;

/**
 * @author Tommaso Soru <tsoru@informatik.uni-leipzig.de>
 *
 */
public class Conventions {

	/**
	 * The use of "::" should be forbidden in URLs.
	 * 
	 * @param p1
	 * @param p2
	 * @return
	 */
	public static String toFeatureName(String p1, String p2) {
		return p1 + "::" + p2;
	}
	
	public static String toFirstProperty(String fname) {
		return fname.split("::")[0];
	}
	
	public static String toSecondProperty(String fname) {
		return fname.split("::")[1];
	}

}
