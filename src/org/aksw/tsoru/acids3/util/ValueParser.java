package org.aksw.tsoru.acids3.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Tommaso Soru <tsoru@informatik.uni-leipzig.de>
 *
 */
public class ValueParser {

	/**
	 * Find the double value in a string.
	 * @param s the input string
	 * @return the double value
	 */
	public static double parse(String s) {
		if(s.equals(""))
			return Double.NaN;
		Matcher m = Pattern.compile("\\d+").matcher(s);
		if(m.find()) {
			double i = Double.parseDouble(m.group());
			return i;
		}
		return Double.parseDouble(s);
	}

}