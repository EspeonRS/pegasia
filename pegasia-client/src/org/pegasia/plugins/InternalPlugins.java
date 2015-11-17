package org.pegasia.plugins;

import org.pegasia.client.PluginManager;
import org.pegasia.client.plugin.PluginLoader;
import org.pegasia.plugins.hiscores.HiscoresPanel;
import org.pegasia.plugins.screenshot.ScreenShotPlugin;
import org.pegasia.plugins.worldmap.WorldMap;
import org.pegasia.plugins.xptable.XPTablePanel;
import org.pegasia.util.Version;

public final class InternalPlugins extends PluginLoader {
	private static final PluginLoader[] list = {
		new InternalPlugins(HiscoresPanel.class, "Hiscores", new Version("1.0"), null),
		new InternalPlugins(ScreenShotPlugin.class, "ScreenShot", new Version("1.0"), null),
		new InternalPlugins(WorldMap.class, "World Map", new Version("1.0"), null),
		new InternalPlugins(XPTablePanel.class, "XP Table", new Version("1.0"), null),
		new InternalPlugins(Dummy.class, "Dependent", new Version("1.0"), "middle"),
		new InternalPlugins(Dummy.class, "Middle", new Version("1.0"), "dependency"),
		new InternalPlugins(Dummy.class, "Dependency", new Version("1.0"), null),
	};
	
	public static PluginLoader[] get(PluginManager pm) {
		return pm != null ? list : null;
	}
	
	private final Class<?> pluginClass;
	
	private InternalPlugins(Class<?> pluginClass, String name, Version version, String dependencies) {
		super(name, version, dependencies);
		this.pluginClass = pluginClass;
	}
	
	public void load() throws NoSuchMethodException {
		super.initialize(pluginClass);
	}
}
