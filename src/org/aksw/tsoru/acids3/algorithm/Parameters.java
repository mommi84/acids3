package org.aksw.tsoru.acids3.algorithm;

import org.aksw.tsoru.acids3.io.Arg;
import org.apache.log4j.Logger;

/**
 * @author Tommaso Soru <tsoru@informatik.uni-leipzig.de>
 *
 */
public class Parameters {

	private static final Logger LOGGER = Logger.getLogger(Parameters.class);

	/**
	 * Paths.
	 */
	private String sourcePath, targetPath, oraclePath;

	/**
	 * Number of rounds for the active learning setting.
	 */
	public static final int ROUNDS_ACTIVE = 1;
	
	/**
	 * Number of queries to the oracle per round.
	 */
	public static final int QUERIES_PER_ROUND = 5;

	/**
	 * Maximum number of examples per query. The minimum per kind (positive and
	 * negative) is one, however the research will end after this number of
	 * examples.
	 */
	public static final int EX_PER_QUERY = 5;

	/**
	 * A node is a hub (respectively, an authority) if exists one property for
	 * which the number of connected objects is at least HUB_AUTH_COEFF times the
	 * Otsu threshold.
	 */
	public static final double HUB_AUTH_COEFF = .5;

	public Parameters() {
		super();
	}

	public String getSourcePath() {
		return sourcePath;
	}

	public void setSourcePath(String sourcePath) {
		LOGGER.info("sourcePath = " + sourcePath);
		this.sourcePath = sourcePath;
	}

	public String getTargetPath() {
		return targetPath;
	}

	public void setTargetPath(String targetPath) {
		LOGGER.info("targetPath = " + targetPath);
		this.targetPath = targetPath;
	}

	public String getOraclePath() {
		return oraclePath;
	}

	public void setOraclePath(String oraclePath) {
		LOGGER.info("oraclePath = " + oraclePath);
		this.oraclePath = oraclePath;
	}

	public String getPath(Arg arg) {
		if (arg == Arg.SOURCE)
			return getSourcePath();
		if (arg == Arg.TARGET)
			return getTargetPath();
		return null;
	}

	public String getDir(Arg arg) {
		if (arg == Arg.SOURCE)
			return getSourcePath().substring(0,
					getSourcePath().lastIndexOf('/'));
		if (arg == Arg.TARGET)
			return getTargetPath().substring(0,
					getTargetPath().lastIndexOf('/'));
		return null;
	}

}
