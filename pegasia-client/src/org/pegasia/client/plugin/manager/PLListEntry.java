package org.pegasia.client.plugin.manager;

import java.util.Map;

import org.pegasia.api.PluginConfigPanel;
import org.pegasia.client.plugin.PluginEntry;
import org.pegasia.client.plugin.PluginLoader;
import org.pegasia.util.Version;
import org.pegasia.util.VersionMask;

class PLListEntry implements PluginEntry {
	final PluginLoader loader;

	String toolTip = null;
	PluginConfigPanel panel = null;
	Boolean enabled = false;
	boolean active = false;

	PLListEntry(PluginLoader plugin) {
		this.loader = plugin;

		if (plugin != null) {
			enabled = Boolean.valueOf(plugin.isEnabled());
			active = plugin.isActive();
		}
	}

	String getName() {
		return loader.getName();
	}

	String getToolTipText() {
		if (toolTip == null)
			toolTip = loader.getName() + " v" + loader.getVersion();
		return toolTip;
	}

	synchronized PluginConfigPanel getPanel() {
		if (panel == null)try {
			panel = loader.newConfig();
		} catch (Exception e) {
			System.err.println("Exception while creating config panel for plugin: " + loader.getName());
			e.printStackTrace();
		}
		return panel;
	}

	void applyChanges() {
		// Save any changes the config panel may have made
		if (panel != null)
			panel.save();
	}

	@Override
	public String toString() {
		return getName();
	}

	@Override
	public Version getVersion() {
		// TODO Auto-generated method stub
		return loader.getVersion();
	}

	@Override
	public Map<String, VersionMask> getDependencies() {
		return loader.getDependencies();
	}

	@Override
	public boolean isActive() {
		return active;
	}

	@Override
	public void setActive(boolean active) {
		this.active = active;
	}

	@Override
	public boolean isEnabled() {
		return enabled;
	}

	@Override
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}
}