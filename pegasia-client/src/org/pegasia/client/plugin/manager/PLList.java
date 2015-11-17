package org.pegasia.client.plugin.manager;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map.Entry;

import javax.swing.table.AbstractTableModel;

import org.pegasia.Global;
import org.pegasia.client.Pegasia;
import org.pegasia.client.PluginManager;
import org.pegasia.client.config.ClientProperties;
import org.pegasia.client.plugin.PluginList;
import org.pegasia.client.plugin.PluginLoader;
import org.pegasia.util.VersionMask;

class PLList extends AbstractTableModel implements PluginList<PLListEntry> {
	final PluginManager pm;
	final ClientProperties properties;
	final PluginDialog dialog;
	final PLListEntry[] entries;

	final HashMap<String, PLListEntry> pluginMap = new HashMap<String, PLListEntry>();;
	final HashMap<PLListEntry, Collection<PLListEntry>> dependents = new HashMap<PLListEntry, Collection<PLListEntry>>();

	PLList(Pegasia client, PluginDialog dialog) {
		this.pm = client.getPluginManager();
		this.properties = client.getPegasiaProperties();
		this.dialog = dialog;

		// Create the HashMap that will store all of the registered plugins
		Collection<PluginLoader> loaders = pm.plugins.values();

		// Create an ArrayList to retain the order in which the plugins are added
		ArrayList<PLListEntry> list = new ArrayList<PLListEntry>(loaders.size());

		// Add the special Pegasia client entry
		ClientListEntry pegasiaEntry = new ClientListEntry(client);
		pluginMap.put("", pegasiaEntry);
		list.add(pegasiaEntry);

		// Populate the data structures
		for (PluginLoader loader: loaders) {
			PLListEntry entry = new PLListEntry(loader);

			pluginMap.put(loader.getFormattedName(), entry);
			list.add(entry);
		}

		// Convert the ArrayList to an array to be used
		entries = list.toArray(new PLListEntry[list.size()]);
	}

	String getPluginStatus(PLListEntry entry) {
		if (entry.isActive())
			return Global.PLUGIN_ACTIVE.toString();

		if (!entry.isEnabled())
			return Global.PLUGIN_DISABLED.toString();

		HashMap<String, VersionMask> dependencies = entry.loader.getDependencies();
		StringBuilder str = new StringBuilder("<html><div style='text-align:center'>" + Global.PLUGIN_INACTIVE + "<br><br>" + Global.REQUIRED_PLUGINS);
		for (String name: dependencies.keySet()) {
			PLListEntry dependency = pluginMap.get(name);
			VersionMask mask = dependencies.get(name);

			// If the dependency cannot be found or is inactive, then it must be preventing
			// this plugin from being active
			if (dependency == null || !dependency.isActive()|| !dependency.loader.getVersion().matches(mask)) {
				str.append("<br>" + name);

				if (mask != null)
					str.append("[" + dependencies.get(name) + "]" );
			}
		}
		return str.toString();
	}

	public PLListEntry getPlugin(String name) {
		return pluginMap.get(name);
	}

	public Collection<PLListEntry> getDependents(PLListEntry plugin) {
		Collection<PLListEntry> list = null;
		if (!dependents.containsKey(plugin)) {
			Collection<PluginLoader> d = pm.dependents.get(plugin.loader.getFormattedName());
			if (d != null) {
				list = new ArrayList<PLListEntry>();
				for (PluginLoader pl: d)
					((ArrayList<PLListEntry>) list).add(getPlugin(pl.getFormattedName()));

			}
			dependents.put(plugin, list);
		} else
			list = dependents.get(plugin);
		return list;
	}

	void applyChanges() {
		for (PLListEntry entry: pluginMap.values()) {
			entry.applyChanges();

			if (entry.loader != null && entry.isEnabled() != entry.loader.isEnabled())
				PluginManager.setPluginEnabled(pm, entry.loader, entry.isEnabled());
		}

		for (Entry<String, PluginLoader> entry: pm.plugins.entrySet()) {
			if (entry.getValue().isEnabled())
				properties.removeKey(ClientProperties.DISABLED_SECTION, entry.getKey());
			else
				properties.put(ClientProperties.DISABLED_SECTION, entry.getKey(), null);
		}

		properties.save();
	}

	@Override public void setValueAt(Object value, int row, int col) {
		// The event should not be processed if it takes place in the very
		// first row, if it is not in bounds of the internal array, or if it
		// is attempting to modify anything except for the "active" column.
		if (row < 1 || row >= entries.length || col != PLTable.BOOL_COLUMN)
			return;

		// Only process value updates of type Boolean
		if (value instanceof Boolean) {
			// Get the list entry contained in this row
			PLListEntry entry = (PLListEntry) getValueAt(row, PLTable.ENTRY_COLUMN);
			
			if (entry != null && entry.loader != null)
				PluginManager.setPluginEnabled(this, entry, (Boolean) value);

			dialog.refresh();
		}
	}

	@Override public int getColumnCount() {
		return 2;
	}

	@Override public int getRowCount() {
		return entries.length;
	}

	@Override public Object getValueAt(int row, int col) {
		if (row < 0 || row >= entries.length)
			return null;

		if (col == PLTable.BOOL_COLUMN)
			return entries[row].enabled;
		return entries[row];
	}

	@Override public Class<?> getColumnClass(int col) {
		if (col == PLTable.BOOL_COLUMN)
			return Boolean.class;
		return PLListEntry.class;
	}

	@Override public String getColumnName(int col) {
		if (col == PLTable.BOOL_COLUMN)
			return "Enabled";
		return "Plugin";
	}

	@Override public boolean isCellEditable(int row, int column) {
		return column == PLTable.BOOL_COLUMN && row > 0;
	}
}