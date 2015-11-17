package org.pegasia.util;

public abstract class SleepTicker {
	/**
	 * Puts the current thread to sleep while still calling a tick
	 * at a set interval.
	 * <p>
	 * 
	 * 
	 * @param delay the amount of time between ticks
	 * @param count how many ticks the loop lasts
	 */
	public void start(int delay, int count) {
		final long startTime = System.currentTimeMillis();
		
		for (int i = 0; i < count; i++) {
			tick(count-i);
			
			long sleepTime = startTime + (i+1) * delay - System.currentTimeMillis();
			if (sleepTime > 0)
				try {
					Thread.sleep(sleepTime);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
		}
	}
	
	protected abstract boolean tick(int ticksRemaining);
}
