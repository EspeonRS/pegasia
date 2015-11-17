package org.pegasia.client.config;

import java.io.File;
import java.io.IOException;
import java.util.Map.Entry;

import org.pegasia.client.Pegasia;
import org.pegasia.client.plugin.PluginLoader;
import org.pegasia.util.DataFile;
import org.pegasia.util.FileUtils;

public final class ClientProperties extends DataFile {
	public static final File FILE = new File(FileUtils.DEFAULT_DIRECTORY, "pegasia.config");

	public static final String MAIN_SECTION = null,
			DISABLED_SECTION = "disabled-plugins",
			WHITELIST_SECTION = "whitelisted-plugins";
	
	public static final String HOMEWORLD_KEY = "homeworld",
			USE_HOMEWORLD_KEY = "use-homeworld",
			LIMIT_SIZE_KEY = "limit-size",
			REFLECTION_KEY = "use-reflection",
			LAYOUT_FIX_KEY = "use-layout-fix",

			THEME_CLASS_KEY = "theme-class",
			THEME_DECORATED_KEY = "theme-decorated",
			THEME_SELECTION_KEY = "theme-selection";
	
	public static final int HIDE_THEME_SELECTION = 0,
			SHOW_THEME_SELECTION_ONCE = 1,
			SHOW_THEME_SELECTION = 2;

	private final Pegasia client;
	
	public int homeworld, themeSelection;
	public boolean useHomeworld, limitSize, themeDecorated;
	public String themeClass;

	public final boolean reflection, layoutFix;

	public ClientProperties(Pegasia client) throws InstantiationException {
		super(FILE);
		
		if (client == null)
			throw new InstantiationException();
		this.client = client;

		homeworld = getInt(MAIN_SECTION, HOMEWORLD_KEY, 330);
		useHomeworld = getBool(MAIN_SECTION, USE_HOMEWORLD_KEY, false);
		limitSize = getBool(MAIN_SECTION, LIMIT_SIZE_KEY, true);
		reflection = getBool(MAIN_SECTION, REFLECTION_KEY, true);
		layoutFix = getBool(MAIN_SECTION, REFLECTION_KEY, true);
		
		themeClass = get(MAIN_SECTION, THEME_CLASS_KEY, "");
		themeDecorated = getBool(MAIN_SECTION, THEME_DECORATED_KEY, true);
		themeSelection = getInt(MAIN_SECTION, THEME_SELECTION_KEY, 1);
	}

	@Override
	public void save() {
		put(MAIN_SECTION, HOMEWORLD_KEY, homeworld);
		put(MAIN_SECTION, USE_HOMEWORLD_KEY, useHomeworld);
		put(MAIN_SECTION, LIMIT_SIZE_KEY, limitSize);
		
		put(MAIN_SECTION, THEME_CLASS_KEY, themeClass);
		put(MAIN_SECTION, THEME_DECORATED_KEY, themeDecorated);
		put(MAIN_SECTION, THEME_SELECTION_KEY, themeSelection);
		
		for (Entry<String, PluginLoader> entry: client.getPluginManager().plugins.entrySet()) {
			if (entry.getValue().isEnabled())
				removeKey(ClientProperties.DISABLED_SECTION, entry.getKey());
			else
				put(ClientProperties.DISABLED_SECTION, entry.getKey(), null);
		}

		try {
			super.save();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
