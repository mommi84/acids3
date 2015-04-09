package org.aksw.tsoru.acids3.algorithm;

import java.io.IOException;

import org.aksw.tsoru.acids3.model.Example;
import org.aksw.tsoru.acids3.util.Oracle;
import org.apache.log4j.Logger;

/**
 * @author Tommaso Soru <t.soru@informatik.uni-leipzig.de>
 *
 */
public class Algorithm implements Runnable {
	
	private static final Logger LOGGER = Logger.getLogger(Algorithm.class);
	
	private Parameters param;
	
	public Algorithm(Parameters param) {
		super();
		this.param = param;
	}

	public Parameters getParam() {
		return param;
	}

	public void run() {
		LOGGER.info("Algorithm bound.");
		
		Oracle oracle = new Oracle(param.getOraclePath());
		try {
			oracle.build();
		} catch (IOException e) {
			LOGGER.fatal("No oracle's answers found.");
			e.printStackTrace();
			return;
		} finally {
			LOGGER.info("Oracle's answers loaded.");
		}
		
		for(int round = 1; round <= param.ROUNDS_ACTIVE; round ++) {
			LOGGER.info("Round #"+round+" of questions has started.");
			
			// get (pseudo-)random source example
			Example src = null;
			
		}
	}

}
