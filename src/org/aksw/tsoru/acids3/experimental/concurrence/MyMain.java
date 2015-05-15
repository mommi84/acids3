package org.aksw.tsoru.acids3.experimental.concurrence;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * @author Tommaso Soru <tsoru@informatik.uni-leipzig.de>
 *
 */
public class MyMain {
	private static final int NTHREADS = 3;

	public static void main(String[] args) throws InterruptedException {
		ExecutorService executor = Executors.newFixedThreadPool(NTHREADS);
		MyObject o = new MyObject();
		for (int i = 0; i < 500; i++) {
			Runnable worker = new MyRunnable(10000000L + i, o);
			executor.execute(worker);
		}
		// This will make the executor accept no new threads
		// and finish all existing threads in the queue
		executor.shutdown();
		// Wait until all threads are finish
		executor.awaitTermination(0, TimeUnit.MINUTES);
		System.out.println("Finished all threads");
	}
}
class MyObject {
	int done = 0;
}