package org.aksw.tsoru.acids3.algorithm;

import java.io.IOException;

import org.aksw.tsoru.acids3.io.Arg;
import org.aksw.tsoru.acids3.io.Processing;
import org.aksw.tsoru.acids3.model.Instance;
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
		
		Processing srcPro = new Processing(Arg.SOURCE, param);
		Processing tgtPro = new Processing(Arg.TARGET, param);
		
		tgtPro.index();
		
		for(int round = 1; round <= param.ROUNDS_ACTIVE; round ++) {
			LOGGER.info("Round #"+round+" of questions has started.");
			
			// get (pseudo-)random source example
			Instance src = (Instance) srcPro.randomPick();
			
			tgtPro.topMatches(src);
		}
		
		srcPro.close();
		tgtPro.close();
	}

}
