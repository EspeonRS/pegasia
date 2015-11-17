package org.pegasia.client.ui.container;

import org.pegasia.api.component.PegasiaPanel;
import org.pegasia.util.ui.tabbedpane.DnDJTabbedPane;

public class TabbedPanelContainer extends DnDJTabbedPane implements PanelContainer {
	private final boolean largeIcons;

	public TabbedPanelContainer(int orientation, boolean largeIcons) {
		super(orientation);
		this.largeIcons = largeIcons;
	}

	@Override
	public PegasiaPanel getSelectedPanel() {
		return (PegasiaPanel) getSelectedComponent();
	}

	@Override
	public boolean hasPanel(PegasiaPanel panel) {
		return indexOfComponent(panel) != -1;
	}

	@Override
	public boolean addPanel(PegasiaPanel panel) {
		if (indexOfComponent(panel) != -1)
			return false;

		add(panel);
		updatePanel(panel);
		System.out.println("Added panel to container: " + panel.getName() + "[" + panel.getID() + "]");
		return true;
	}

	@Override
	public boolean removePanel(PegasiaPanel info) {
		int index = indexOfComponent(info);

		if (index == -1)
			return false;

		remove(index);
		return true;
	}

	public void updatePanel(PegasiaPanel panel) {
		int index = indexOfComponent(panel);

		if (index == -1)
			return;

		setTitleAt(index, "");

		try {
			if (largeIcons)
				setIconAt(index, panel.getIcon32());
			else
				setIconAt(index, panel.getIcon16());
		} catch (Exception e) {
			System.err.println("Error setting icon for panel: " + panel.getName());
		}
	}
}
