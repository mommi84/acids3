package org.aksw.tsoru.acids3.experimental.concurrence;

/**
 * MyRunnable will count the sum of the number from 1 to the parameter
 * countUntil and then write the result to the console.
 * <p>
 * MyRunnable is the task which will be performed
 * 
 * @author Lars Vogel
 * 
 */
public class MyRunnable implements Runnable {
	private final long countUntil;
	private MyObject o;

	MyRunnable(long countUntil, MyObject o) {
		this.countUntil = countUntil;
		this.o = o;
	}

	@Override
	public void run() {
		long sum = 0;
		for (long i = 1; i < countUntil; i++) {
			sum += i;
		}
		o.done++;
		System.out.println(sum + "(" + o.done + ")");
	}
}