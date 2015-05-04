package org.aksw.tsoru.acids3.math;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.TreeSet;

import org.aksw.tsoru.acids3.algorithm.Parameters;
import org.apache.log4j.Logger;

/**
 * Scalable implementation of the Otsu's Method (see <a
 * href="http://en.wikipedia.org/wiki/Otsu%27s_method">here</a>) for finding
 * hubs and authorities.
 * 
 * @author Tommaso Soru <tsoru@informatik.uni-leipzig.de>
 *
 */
public class OtsuMethod {

	private static final Logger LOGGER = Logger.getLogger(OtsuMethod.class);

	public static void otsu(Statement statement, String q,
			TreeSet<String> results, String name) throws SQLException {
		
		LOGGER.info("Processing "+name+"...");

		ResultSet rs = statement.executeQuery(q);
		// compute threshold (thr2)
		rs.next();
		int max = rs.getInt("c");
		double thr1 = max / 2.0;
		LOGGER.debug("max = " + max + ", thr1 = " + thr1);
		int sumHigh = max, occHigh = 1, sumLow = 0, occLow = 0;
		while (rs.next()) {
			int c = rs.getInt("c");
			if (c >= thr1) {
				sumHigh += c;
				occHigh++;
			} else {
				sumLow += c;
				occLow++;
			}
		}
		LOGGER.debug("values = [" + sumHigh + ", " + occHigh + ", " + sumLow
				+ ", " + occLow + "]");
		double avgHigh = (double) sumHigh / occHigh;
		double avgLow = (double) sumLow / occLow;
		LOGGER.debug("avg = [" + avgHigh + ", " + avgLow + "]");
		double thr2 = (avgHigh + avgLow) / 2 * Parameters.HUB_AUTH_COEFF;
		LOGGER.debug("thr2 = " + thr2);

		// collect IDs
		ResultSet rs2 = statement.executeQuery(q);
		while (rs2.next()) {
			String id = rs2.getString("id");
			int count = rs2.getInt("c");
			LOGGER.trace("Comparing <" + id + "> having " + count + " <=> " + thr2);
			if (count < thr2)
				break;
			results.add(id);
		}

		LOGGER.info(name + " = " + results);
	}

}
