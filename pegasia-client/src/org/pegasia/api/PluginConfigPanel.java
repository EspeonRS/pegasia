package org.pegasia.api;

import java.awt.LayoutManager;

import javax.swing.JPanel;

public abstract class PluginConfigPanel extends JPanel {

	public PluginConfigPanel() {
		super();
	}
	
	public PluginConfigPanel(LayoutManager layout) {
		super(layout);
	}
	
	public abstract void save();
}
