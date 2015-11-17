package org.pegasia.client.plugin;

import java.util.Map;

import org.pegasia.util.Version;
import org.pegasia.util.VersionMask;

public interface PluginEntry {
	Version getVersion();
	Map<String, VersionMask> getDependencies();
	
	boolean isActive();
	void setActive(boolean active);
	
	boolean isEnabled();
	void setEnabled(boolean enabled);
}
