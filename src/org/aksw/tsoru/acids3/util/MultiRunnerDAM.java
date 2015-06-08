package org.aksw.tsoru.acids3.util;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * 
 * @author http://www.emarx.org
 *
 */
public class MultiRunnerDAM {

	Runnable runnables[];
	ExecutorService executor;
	boolean isMultiThread = true;

	public MultiRunnerDAM(Runnable... runnables) {
		this.runnables = runnables;
		this.executor = Executors.newFixedThreadPool(runnables.length);
	}
	
	public MultiRunnerDAM(boolean isMultiThread, Runnable... runnables) {
		this(runnables);
		this.isMultiThread = isMultiThread;
	}

	public MultiRunnerDAM(List<? extends Runnable> runnables) {
		Runnable[] runnableArray = new Runnable[runnables.size()];
		runnableArray = runnables.toArray(runnableArray);
		this.runnables = runnableArray;
		this.executor = Executors.newFixedThreadPool(runnableArray.length);
	}
	
	public MultiRunnerDAM(boolean isMultiThread, List<? extends Runnable> runnables) {
		this(runnables);
		this.isMultiThread = isMultiThread;
	}
	
	public void run() {
		if(isMultiThread) {
			runMultiThread();
		} else {
			runSingleThread();
		}
	}
	
	public void runSingleThread() {
		for(Runnable runnable: runnables) {
			runnable.run();
		}
	}

	public void runMultiThread() {
		for(Runnable runnable: runnables) {
			executor.execute(runnable);
		}		
		// This will make the executor do not accept new threads
	    // and finish all existing threads in the queue
	    executor.shutdown();
	    try {
	    	// Wait until all threads are finished
			executor.awaitTermination(Long.MAX_VALUE, TimeUnit.DAYS); // wait forever :)
		} catch (InterruptedException e) {
			// (Re-)Cancel if current thread also interrupted
			executor.shutdownNow();
		    // Preserve interrupt status
		    Thread.currentThread().interrupt();
		}	    
	}
}
