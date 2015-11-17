package org.pegasia.util;

public class CodeTimer {
	boolean enabled = true;
	long prevTime = 0;
	String prevComment = "";

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public void time(String comment) {
		long endTime = System.nanoTime();

		if (enabled && prevTime != 0)
			System.out.println(prevComment + " - " + (endTime - prevTime));

		prevComment = comment;
		prevTime = System.nanoTime();
	}
}
