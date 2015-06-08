package org.aksw.tsoru.acids3.evaluation;

import java.util.ArrayList;
import java.util.List;

import org.aksw.tsoru.acids3.util.MultiRunnerDAM;
import org.apache.log4j.Logger;

public class MultiRunEvaluation {

	public void start() {
		
		String[] files = {"blah"};
		
		List<CandidateSelectionRunnable> selectionThreads = new ArrayList<CandidateSelectionRunnable>();			
		for(String file : files) {
			CandidateSelectionRunnable candidateSelectionRunnable = new CandidateSelectionRunnable(
					file);
			selectionThreads.add(candidateSelectionRunnable);
		}
		
		if (selectionThreads.size() > 0) {
			MultiRunnerDAM candidateSelectionDam = new MultiRunnerDAM(
					selectionThreads);
			candidateSelectionDam.run();
		}
	}
}

class CandidateSelectionRunnable implements Runnable {

	Logger logger = Logger.getLogger(CandidateSelectionRunnable.class);

	volatile String file;

	public CandidateSelectionRunnable(String file) {
		super();
		this.file = file;
	}

	@Override
	public void run() {
		try {
			logger.debug("Processing file " + file);
			
			// stuff here...
			
		} catch (Exception e) {
			logger.error("Error reading file " + file, e);
		}
	}

	@Override
	protected void finalize() throws Throwable {
		super.finalize();
	}
}