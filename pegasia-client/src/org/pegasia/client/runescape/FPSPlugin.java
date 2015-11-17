package org.pegasia.client.runescape;

import java.awt.Color;
import java.awt.Graphics2D;
import java.text.DecimalFormat;

import org.pegasia.api.component.PegasiaOverlay;
import org.pegasia.api.runescape.RuneScape;

public class FPSPlugin extends PegasiaOverlay {
	static final DecimalFormat oneDigit = new DecimalFormat("#,##0.0");
	
	long prevTime = System.nanoTime();
	double avgTime = 0;

	@Override
	public void paintOverlay(Graphics2D g) throws Exception {
		long currTime = System.nanoTime();
		avgTime = 0.95*avgTime + 0.05*(currTime-prevTime);
		prevTime = currTime;
		
		if (g == null)
			return;
		
		String str = oneDigit.format(1000000000/avgTime);
		int strWidth = g.getFontMetrics().stringWidth(str);
		g.setColor(Color.RED);
		g.drawString(str, RuneScape.getWidth() - strWidth - 4, 16);
	}
	
	public void printFPS() {		
		System.out.println(oneDigit.format(1000000000/avgTime));
	}
}
