package org.pegasia.client.ui.skin;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import javax.swing.LookAndFeel;
import javax.swing.UIManager;

final class ThemeLoader {
	private final File file;

	private ClassLoader classLoader;

	ThemeLoader(File file, ArrayList<ThemeEntry> entries) throws IOException, InstantiationException {
		this.file = file;

		JarFile jar = null;
		InputStream is = null;
		Properties properties;

		// Open the jar files and load in "plugin.info" as a
		// new Properties instance.
		try {
			jar = new JarFile(file);

			JarEntry infoFile = jar.getJarEntry("skins.info");
			if (infoFile == null)
				throw new IOException("\"skins.info\" not found.");

			properties = new Properties();
			properties.load(jar.getInputStream(infoFile));
		} finally {
			if (is != null)
				is.close();
			if (jar != null)
				jar.close();
		}

		for (Entry<Object, Object> entry: properties.entrySet()) {
			String key = entry.getKey().toString().trim();

			if (key.startsWith("UIManager.")) {
				key = key.substring("UIManager.".length());
				String value = entry.getValue().toString().trim();
				
				if ("[true]".equals(value))
					UIManager.put(key, true);
				else if ("[false]".equals(value))
					UIManager.put(key, false);
				else
					UIManager.put(key, value);
			}

			else try {
				entries.add(new ThemeEntry(this, key, entry.getValue().toString().trim()));
			} catch (ClassNotFoundException e) {
				System.err.println("Unable to load class: " + entry.getValue().toString().trim());
			}
		}
	}

	@SuppressWarnings("unchecked")
	Class<? extends LookAndFeel> load(String className) throws IOException, ClassNotFoundException {
		if (classLoader == null)
			classLoader = new URLClassLoader(new URL[]{file.toURI().toURL()});
		//UIManager.put("ClassLoader", classLoader);
		return (Class<? extends LookAndFeel>) classLoader.loadClass(className);
	}
}
