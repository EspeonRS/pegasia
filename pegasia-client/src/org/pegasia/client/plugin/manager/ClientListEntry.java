package org.pegasia.client.plugin.manager;

import org.pegasia.client.Pegasia;
import org.pegasia.client.config.ClientConfigPanel;

class ClientListEntry extends PLListEntry {
	ClientListEntry(Pegasia client) {
		super(null);
		
		panel = new ClientConfigPanel(client);
		setEnabled(true);
		setActive(true);
	}
	
	@Override
	String getName() {
		return "Pegasia";
	}
	
	@Override
	String getToolTipText() {
		return "Pegasia v" + Pegasia.VERSION;
	}
}
