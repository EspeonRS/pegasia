package org.pegasia.client.ui.container;

import org.pegasia.api.component.PegasiaPanel;

public interface PanelContainer {
	/**
	 * Adds the panel to the container.
	 * 
	 * @param panel Panel to be added to the container.
	 * @return Whether or not the panel was successfully added.
	 */
	boolean addPanel(PegasiaPanel panel);
	
	/**
	 * Returns the currently active panel in the container.
	 * 
	 * @return Current panel.
	 */
	PegasiaPanel getSelectedPanel();
	
	/**
	 * Checks if the container currently holds the specific panel.
	 * 
	 * @param panel Which panel to check that the container holds.
	 * @return Whether or not the container holds the specified panel.
	 */
	boolean hasPanel(PegasiaPanel panel);
	
	
	boolean removePanel(PegasiaPanel panel);
}
