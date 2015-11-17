package org.pegasia.plugins.inputmanager;

import java.io.File;
import java.io.IOException;

import org.pegasia.util.DataFile;
import org.pegasia.util.FileUtils;

public class InputProperties extends DataFile {
	public static final File FILE = new File(FileUtils.DEFAULT_DIRECTORY, "input.conf");

	InputProperties() {
		super(FILE);
		
		for (InputConfig key: InputConfig.values()) {
			String value = get(null, key.formattedName, null);
			if (value != null)
				key.setValue(value);
		}
	}
	
	@Override
	public void save() {
		for (InputConfig key: InputConfig.values())
			put(null, key.formattedName, key.getValue());
		
		try {
			super.save();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
