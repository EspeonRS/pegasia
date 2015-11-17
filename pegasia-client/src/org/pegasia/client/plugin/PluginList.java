package org.pegasia.client.plugin;

import java.util.Collection;

public interface PluginList<E extends PluginEntry> {
	E getPlugin(String name);
	Collection<E> getDependents(E plugin);
}
