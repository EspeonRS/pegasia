package org.pegasia.api.component;

import java.awt.Graphics2D;

import org.pegasia.api.PegasiaComponent;
import org.pegasia.client.UIManager;

public abstract class PegasiaOverlay implements PegasiaComponent {
	public void install() {
		UIManager.installComponent(this);
	}
	
	public void uninstall() {
		UIManager.uninstallComponent(this);
	}
	
	public abstract void paintOverlay(Graphics2D g) throws Exception;
}
