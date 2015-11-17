package org.pegasia.plugins.xpdrops;

import java.io.File;
import java.io.IOException;

import org.pegasia.util.DataFile;
import org.pegasia.util.FileUtils;

public class XPDropsProperties extends DataFile {
	public static final File FILE = new File(FileUtils.PLUGIN_DIRECTORY, "xpdrops.conf");
	
	public static final String DURATION_KEY = "duration",
			THRESHOLD_KEY = "theshold";
	
	public int duration, threshold;
	
	XPDropsProperties() {
		super(FILE);
		
		duration = getInt(null, DURATION_KEY, 5);
		if (duration < 0)
			duration *= -1;
		
		threshold = getInt(null, THRESHOLD_KEY, -1);
		if (threshold < -1)
			threshold *= -1;
	}
	
	@Override
	public void save() throws IOException {
		put(null, DURATION_KEY, Integer.toString(duration));
		put(null, THRESHOLD_KEY, Integer.toString(threshold));
		
		super.save();
	}
}
