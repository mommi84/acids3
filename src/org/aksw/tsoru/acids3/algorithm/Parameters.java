package org.aksw.tsoru.acids3.algorithm;

import org.apache.log4j.Logger;

/**
 * @author Tommaso Soru <t.soru@informatik.uni-leipzig.de>
 *
 */
public class Parameters {

	private static final Logger LOGGER = Logger.getLogger(Parameters.class);

	private String sourcePath, targetPath, oraclePath;

	/**
	 * Number of rounds for the active learning setting.
	 */
	public final int ROUNDS_ACTIVE = 1;

	/**
	 * Maximum number of examples per round. The minimum per kind (positive and
	 * negative) is one, however the research will end after this number of
	 * examples.
	 */
	public final int MAX_EX_ROUND = 5;

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

}
