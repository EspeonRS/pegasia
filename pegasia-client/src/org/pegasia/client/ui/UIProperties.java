package org.pegasia.client.ui;

import java.awt.Rectangle;
import java.io.File;

import org.pegasia.util.DataFile;
import org.pegasia.util.FileUtils;

public class UIProperties extends DataFile {
	public static final File FILE = new File(FileUtils.DEFAULT_DIRECTORY, "interface.config");

	public static final String BOUNDS_KEY = "bounds",
			SIDE_PANEL_KEY = "side-panel",
			BOTTOM_PANEL_KEY = "bottom-panel",
			RESIZABLE_KEY = "resizable",
			FULLSCREEN_KEY = "fullscreen",
			ALWAYS_ON_TOP_KEY = "always-on-top";

	public UIProperties() {
		super(FILE);
	}
	
	public boolean getRect(String section, String key, Rectangle rect) {
		String value = get(section, key);
		String[] split;
		
		if (value != null && (split = value.split(",")).length >= 4) try {
			rect.x = Integer.valueOf(split[0]);
			rect.y = Integer.valueOf(split[1]);
			rect.width = Integer.valueOf(split[2]);
			rect.height = Integer.valueOf(split[3]);
		} catch (NumberFormatException e) {}
		return false;
	}

	public void put(String section, String key, Rectangle value) {
		put(section, key, value.x + "," + value.y + "," + value.width + "," + value.height);
	}
}