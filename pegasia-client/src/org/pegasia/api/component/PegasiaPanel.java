package org.pegasia.api.component;

import javax.swing.ImageIcon;
import javax.swing.JPanel;

import org.pegasia.api.PegasiaComponent;
import org.pegasia.client.UIManager;
import org.pegasia.client.ui.container.PanelContainer;

//getName
//getMinimumSize

public abstract class PegasiaPanel extends JPanel implements PegasiaComponent {
	public static final int NONE = 0;
	public static final int PANEL_MENU = 0, PANEL_RIGHT = 1, PANEL_BOTTOM = 2;
	
	private final String id;
	private final int defaultPosition;
	
	private PanelContainer container = null;
	
	public PegasiaPanel(String name, String id, int defaultPosition) {
		this.id = id;
		this.defaultPosition = defaultPosition;
		
		setName(name);
		setOpaque(false);
	}
	
	public void install() {
		UIManager.installComponent(this);
	}
	
	public void uninstall() {
		UIManager.uninstallComponent(this);
	}
	
	public final String getID() {
		return id;
	}
	
	public final int getDefaultPosition() {
		return defaultPosition;
	}
	
	/**
	 * Suggest lazy initialization.
	 * @return
	 */
	public abstract ImageIcon getIcon16() throws Exception;
	
	public abstract ImageIcon getIcon32() throws Exception;
	
	public void setContainer(PanelContainer container) {
		this.container = container;
	}
	
	protected void updateContainer() {
		//if (container != null)
		//	container.updatePanel(this);
	}
}