package org.pegasia.client.runescape;

import java.awt.Graphics;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.HashMap;

import org.pegasia.api.component.PegasiaOverlay;
import org.pegasia.client.config.ClientProperties;
import org.pegasia.util.ui.RSProgressBar;

public class RSClient {
	final ClientProperties properties;
	final RSProgressBar progressBar = new RSProgressBar();
	final RSPanel panel = new RSPanel();
	final ArrayList<PegasiaOverlay> overlays = new ArrayList<PegasiaOverlay>();
	
	RSLoader loader = null;
	
	public RSClient(ClientProperties properties) {
		this.properties = properties;
		
		overlays.add(new FPSPlugin());
		panel.add(progressBar);
	}
	
	public void start(RSLoader loader) {
		this.loader = loader;
		panel.add(loader.applet);
	}
	
	public boolean isRunning() {
		return loader != null;
	}
	
	public boolean isCanvasHooked() {
		return loader != null && loader.graphics != null;
	}
	
	public ClientProperties getProperties() {
		return properties;
	}
	
	public int getWidth() {
		if (isCanvasHooked())
			return loader.graphics.origCanvas.getWidth();
		return panel.getWidth();
	}
	
	public int getHeight() {
		if (loader.gameCanvas != null)
			return loader.gameCanvas.getHeight();
		return panel.getHeight();
	}
	
	public Rectangle getBounds(boolean factorLocationOnScreen) {
		Rectangle bounds;
		if (loader.gameCanvas != null) {
			bounds = loader.gameCanvas.getBounds();
			if (factorLocationOnScreen)
				bounds.setLocation(loader.gameCanvas.getLocationOnScreen());
		} else {
			bounds = panel.getBounds();
			if (factorLocationOnScreen)
				bounds.setLocation(panel.getLocationOnScreen());
		}
		return bounds;
	}
	
	public void drawImage(Graphics g, int xOffset, int yOffset, int width, int height) {
		if (width < 0)
			width = getWidth();
		if (height < 0)
			height = getHeight();
		
		if (isCanvasHooked())
				loader.graphics.renderSurface(g, xOffset, yOffset, width, height);
		/*
		 * if applet exists, either use hooked canvas or use the robot
		 * else, get the image directly from the rspanel.
		 */
	}
	
	public HashMap<String, Class<?>> getClassMap() {
		if (loader == null)
			return null;
		return loader.classMap;
	}
	
	public RSPanel getPanel() {
		return panel;
	}
}
