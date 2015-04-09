package org.aksw.tsoru.acids3;

import org.aksw.tsoru.acids3.algorithm.Algorithm;
import org.aksw.tsoru.acids3.algorithm.Parameters;
import org.apache.log4j.Logger;

/**
 * @author Tommaso Soru <t.soru@informatik.uni-leipzig.de>
 *
 */
public class Acids3 implements Runnable {
	
	private static final Logger LOGGER = Logger.getLogger(Acids3.class);
	private String sourcePath, targetPath, oraclePath;
	
	public Acids3(String sourcePath, String targetPath, String oraclePath) {
		super();
		this.sourcePath = sourcePath;
		this.targetPath = targetPath;
		this.oraclePath = oraclePath;
	}

	public void run() {
		LOGGER.info("Acids3 started.");
		Parameters param = new Parameters();
		param.setSourcePath(sourcePath);
		param.setTargetPath(targetPath);
		param.setOraclePath(oraclePath);
		new Algorithm(param).run();
		LOGGER.info("Acids3 finished.");
	}

	public String getSourcePath() {
		return sourcePath;
	}

	public void setSourcePath(String sourcePath) {
		this.sourcePath = sourcePath;
	}

	public String getTargetPath() {
		return targetPath;
	}

	public void setTargetPath(String targetPath) {
		this.targetPath = targetPath;
	}

	public String getOraclePath() {
		return oraclePath;
	}

	public void setOraclePath(String oraclePath) {
		this.oraclePath = oraclePath;
	}

	public static void main(String[] args) {
		new Acids3("data/ceur-ws.ttl", "data/colinda.nt", "data/oracle-person1.csv").run();
	}

}
