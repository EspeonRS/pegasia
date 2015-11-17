package org.pegasia.client;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Set;

import org.pegasia.client.config.ClientProperties;
import org.pegasia.client.plugin.PluginEntry;
import org.pegasia.client.plugin.PluginList;
import org.pegasia.client.plugin.PluginLoader;
import org.pegasia.plugins.InternalPlugins;
import org.pegasia.util.FileUtils;
import org.pegasia.util.StringUtils;

public final class PluginManager implements PluginList<PluginLoader> {
	private final Pegasia client;
	
	public final HashMap<String, PluginLoader> plugins;
	public final HashMap<String, ArrayList<PluginLoader>> dependents;

	public PluginManager(Pegasia client) throws InstantiationException {
		if (client == null)
			throw new InstantiationException();
		this.client = client;
		
		plugins = new HashMap<String, PluginLoader>();
		dependents = new HashMap<String, ArrayList<PluginLoader>>();
	}

	public void loadPlugins() {
		// Get the list of disabled plugins from ClientProperties
		Set<String> disabled = client.getPegasiaProperties().keySet(ClientProperties.DISABLED_SECTION);
		
		// Load all external plugins
		File[] files = FileUtils.PLUGIN_DIRECTORY.listFiles(new FileFilter() {
			@Override public boolean accept(File file) {
				return file.isFile() && file.getName().endsWith(".jar");
			}
		});

		for (File file: files)
			try {
				registerPlugin(PluginLoader.createExternal(file), disabled);
			} catch (Exception e) {
				e.printStackTrace();
			}
		
		for (PluginLoader loader: InternalPlugins.get(this))
			registerPlugin(loader, disabled);
	}
	
	public void shutdown() {
		for (PluginLoader loader: plugins.values())
			if (loader.isActive())
				deactivatePlugin(this, loader);
	}

	/**
	 * Adds the given class to the client as a plugin.
	 * 
	 * @param c The plugin's main class.
	 * @return Whether the plugin was successfully added.
	 */
	private boolean registerPlugin(PluginLoader loader, Set<String> disabled) {
		final String name = loader.getFormattedName();
		
		// If the plugin with the same name is already registered,
		// do not continue registering this plugin.
		if (plugins.containsKey(name))
			return false;
		
		// Attempt to load the plugin. If this fails, do not
		// continue registering it.
		try {
			loader.load();
		} catch (NoSuchMethodException | ClassNotFoundException | IOException e) {
			System.err.println("Exception while loading plugin \"" + name + "\"");
			return false;
		}
		
		// Add the plugin to the list of loaded plugins
		plugins.put(name, loader);

		// Add this plugin to the list of dependents for all plugins it depends on
		for (String dependency: loader.getDependencies().keySet()) {
			// Initialize the dependency's entry if it does not already exist
			ArrayList<PluginLoader> list = dependents.get(dependency);

			// If the list does not exist, initialize it
			if (list == null) {
				list = new ArrayList<PluginLoader>();
				dependents.put(dependency, list);
			}

			// Add this plugin as a dependent
			list.add(loader);
		}
		
		// If the plugin is not on the list of
		// disabled plugins, enable it.
		if (!disabled.contains(name))
			setPluginEnabled(this, loader, true);
		
		return true;
	}

	@Override
	public PluginLoader getPlugin(String name) {
		return plugins.get(StringUtils.formatName(name));
	}
	
	@Override public Collection<PluginLoader> getDependents(PluginLoader pl) {
		return dependents.get(pl.getFormattedName());
	}
	
	public static <E extends PluginEntry> void setPluginEnabled(PluginList<E> list, E entry, boolean enabled) {
		entry.setEnabled(enabled);

		if (enabled)
			activatePlugin(list, entry);
		else
			deactivatePlugin(list, entry);
	}
	
	public static <E extends PluginEntry> void activatePlugin(PluginList<E> list, E entry) {
		if (entry.isEnabled() && !entry.isActive()) {
			// Check that all dependencies are active
			for (String name: entry.getDependencies().keySet()) {
				PluginEntry dependency = list.getPlugin(name);
				if (dependency == null || !dependency.isActive() || !dependency.getVersion().matches(entry.getDependencies().get(name)))
					return;
			}

			// Activate plugin
			entry.setActive(true);

			// Attempt to activate any plugins that are dependent upon this
			Collection<E> dependents = list.getDependents(entry);
			if (dependents != null)
				for (E dependent: dependents)
					activatePlugin(list, dependent);
		}
	}

	public static <E extends PluginEntry> void deactivatePlugin(PluginList<E> list, E entry) {
		if (entry.isActive()) {
			// Deactivate any plugins that are dependent upon this
			Collection<E> dependents = list.getDependents(entry);
			if (dependents != null)
				for (E dependent: dependents)
					deactivatePlugin(list, dependent);

			// Deactivate plugin
			entry.setActive(false);
		}
	}
}
