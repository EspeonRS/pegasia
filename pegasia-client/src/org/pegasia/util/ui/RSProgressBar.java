package org.pegasia.util.ui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.geom.Rectangle2D;

import javax.swing.JComponent;

public class RSProgressBar extends JComponent {
	private static final Dimension size = new Dimension(303, 33);
	private static final Color color = new Color(140, 17, 17);
	private static final Font font = new Font("Helvetica", 1, 13);
	
	private double progress;
	private String text;
	
	public RSProgressBar() {
		super();
		
		progress = 0;
		text = "";
	}
	
	public void update(final double updProgress, final String updText) {
		EventQueue.invokeLater ( new Runnable() {
			public void run() {
				progress = updProgress;
				text = updText;
				repaint();
			}
		} );
	}
	
	public void update(String text) {
		update(this.progress, text);
	}
	
	public void update(double progress) {
		update(progress, this.text);
	}
	
	@Override public void paintComponent(Graphics g) {
		super.paintComponent(g);
		
		RSProgressBar.paint(g, this, progress, text);
	}
	
	public static void paint(Graphics g, Component c, double percent, String text) {
		final int x = (c.getWidth() - size.width)/2 - 1;
		final int y = (c.getHeight() - size.height)/2 - 2;
		
		g.setColor(color);
		g.drawRect(x, y, size.width, size.height);
		if (percent > 0)
			g.fillRect(x+2, y+2, (int) ((size.width-3) * percent), size.height-3);
		
		g.setColor(Color.WHITE);
		g.setFont(font);
		Rectangle2D textSize = g.getFontMetrics().getStringBounds(text, g);
		g.drawString(text, x + size.width/2 - (int)(textSize.getWidth()/2), y + 22);
	}
}
