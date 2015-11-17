package org.pegasia.plugins.screenshot;

import java.io.File;
import java.io.IOException;

import org.pegasia.util.DataFile;
import org.pegasia.util.FileUtils;

public class ScreenShotProperties extends DataFile {
	public static final File SCREENSHOT_FILE = new File(FileUtils.PLUGIN_DIRECTORY, "screenshot.conf");
	
	public static final String SOUND_KEY = "use-sound",
			HOTKEY_KEY = "use-hotkey",
			SORT_KEY = "sort-type";
	
	public static final int SORT_YEAR_AND_MONTH = 0,
			SORT_YEAR = 1,
			SORT_NONE = 2;
	
	public boolean useSound, useHotkey;
	public int sortType;
	
	ScreenShotProperties() {
		super(SCREENSHOT_FILE);
		
		useSound = getBool(null, SOUND_KEY, true);
		useHotkey = getBool(null, HOTKEY_KEY, true);
		sortType = getInt(null, SORT_KEY, SORT_YEAR_AND_MONTH);
	}
	
	@Override
	public void save() {
		put(null, SOUND_KEY, Boolean.toString(useSound));
		put(null, HOTKEY_KEY, Boolean.toString(useHotkey));
		put(null, SORT_KEY, Integer.toString(sortType));
		
		try {
			super.save();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
