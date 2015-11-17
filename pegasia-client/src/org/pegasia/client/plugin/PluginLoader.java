package org.pegasia.client.plugin;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.HashMap;
import java.util.Properties;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.pegasia.api.PluginConfigPanel;
import org.pegasia.util.StringUtils;
import org.pegasia.util.Version;
import org.pegasia.util.VersionMask;

public abstract class PluginLoader implements PluginEntry {
	private final String name, formattedName;
	private final Version version;
	private final HashMap<String, VersionMask> dependencies;

	private Class<?> plugin;
	private Method startMethod, stopMethod, configMethod;
	private boolean enabled, active;

	/**
	 * Constructs a new PluginLoader instance. See ExternalPluginLoader or
	 * InternalPluginLoader depending on which type of plugin must be loaded.
	 * 
	 * @param name Unique name identifying the plugin.
	 * @param version Current version of the plugin.
	 * @param dependencies The list of dependencies as a string. See PluginLoader.processDependencies().
	 */
	public PluginLoader(String name, Version version, String dependencies) {
		this.name = name;
		this.formattedName = StringUtils.formatName(name);
		this.version = version;
		this.dependencies = processDependencies(dependencies);

		enabled = false;
		active = false;
	}
	
	public abstract void load() throws IOException, NoSuchMethodException, ClassNotFoundException;

	/**
	 * When the PluginManager decides to load the plugin, this method will
	 * load the plugin's class. This method must be called by any subclasses.
	 * 
	 * @param plugin Class of the plugin that is being loaded.
	 * @throws NoSuchMethodException If the plugin class does not contain static "start" and "stop" methods.
	 */
	protected void initialize(Class<?> plugin) throws NoSuchMethodException {
		startMethod = plugin.getDeclaredMethod("start");
		stopMethod = plugin.getDeclaredMethod("stop");

		if (!Modifier.isStatic(startMethod.getModifiers()) || !Modifier.isStatic(stopMethod.getModifiers()))
			throw new NoSuchMethodException();

		try {
			Method configMethod = plugin.getDeclaredMethod("newConfig");
			if (Modifier.isStatic(configMethod.getModifiers()) &&
					PluginConfigPanel.class.isAssignableFrom(configMethod.getReturnType()) )
				this.configMethod = configMethod;
		} catch (NoSuchMethodException | NullPointerException | SecurityException e) {}

		this.plugin = plugin;
	}

	/**
	 * Starts or stops the plugin, calling the plugin's "start" or "stop" method and modifying active.
	 * 
	 * @throws Exception If any exceptions occurred in the plugin's "start" method.
	 */
	@Override
	public final void setActive(boolean active) {
		if (active) try {
			startMethod.invoke(null);
			this.active = true;
		} catch (Exception e) {
			System.err.println("Exception while starting plugin: " + name);
			e.printStackTrace();
		} else try {
			this.active = false;
			stopMethod.invoke(null);
		} catch (Exception e) {
			System.err.println("Exception while stopping plugin: " + name);
			e.printStackTrace();
		}
	}

	/**
	 * Gets the plugin's config panel as provided by the "newConfig" method.
	 * 
	 * @return The PegasiaConfigPanel instance created by the plugin's "newConfig" method.
	 * 		Returns null if the plugin does not contain a "newConfig" method.
	 * @throws Exception If any exceptions occurred in the plugin's "newConfig" method.
	 */
	public final PluginConfigPanel newConfig() throws Exception {
		if (configMethod == null)
			return null;
		return (PluginConfigPanel) configMethod.invoke(null);
	}

	public final Class<?> getPlugin() {
		return plugin;
	}

	@Override
	public final boolean isEnabled() {
		return enabled;
	}

	@Override
	public final void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	@Override
	public final boolean isActive() {
		return active;
	}

	public String getName() {
		return name;
	}

	public final String getFormattedName() {
		return formattedName;
	}

	public Version getVersion() {
		return version;
	}

	/**
	 * HashMap returned will not be null, but it may be empty.
	 * No keys on the dependency map are null. Entries, however, may be null if
	 * no version requirements are provided.
	 * 
	 * @return
	 */
	public HashMap<String, VersionMask> getDependencies() {
		return dependencies;
	}

	private static HashMap<String, VersionMask> processDependencies(String str) {
		HashMap<String, VersionMask> dependencies = new HashMap<String, VersionMask>();

		if (str != null) {
			String[] split = str.split(",");

			for (int i = 0; i < split.length; i++) {
				String name = split[i];
				VersionMask versionMask = null;

				// A split position other than -1 indicates that a version mask was found
				int splitPos = split[i].indexOf('[');
				if (splitPos != -1) {
					// Remove the version string from name
					name = name.substring(0, splitPos);

					// Attempt to create a VersionMask instance from the mask string
					try {
						int closeBracketPos = split[i].indexOf(']', splitPos + 1);
						if (closeBracketPos != -1)
							versionMask = new VersionMask(split[i].substring(splitPos+1, closeBracketPos));
						else
							versionMask = new VersionMask(split[i].substring(splitPos+1));
					} catch (IllegalArgumentException e) {
						// Do nothing, versionMask will remain null
					}
				}

				// Format the dependency's name
				name = StringUtils.formatName(name);				

				// Make sure the length is not blank before adding to dependencies
				if (name.length() > 0)
					dependencies.put(name, versionMask);
			}
		}

		return dependencies;
	}
	
	/**
	 * Constructs a new PluginLoader instance for a plugin that must be
	 * loaded externally from a separate JAR file.
	 *  
	 * @param file File specifying the location of the plugin's JAR file.
	 * @throws IOException If unable to read from the provided JAR file.
	 * @throws InstantiationException If the JAR's plugin.info file is missing information needed to load the plugin.
	 */
	public static PluginLoader createExternal(File file) throws IOException, InstantiationException {
		JarFile jar = null;
		InputStream is = null;
		Properties properties;
		
		// Open the jar files and load in "plugin.info" as a
		// new Properties instance.
		try {
			jar = new JarFile(file);

			JarEntry infoFile = jar.getJarEntry("plugin.info");
			if (infoFile == null)
				throw new IOException("\"plugin.info\" not found.");

			properties = new Properties();
			properties.load(jar.getInputStream(infoFile));
		} finally {
			if (is != null)
				is.close();
			if (jar != null)
				jar.close();
		}

		// Load each property from the file as a separate string.		
		String	name = properties.getProperty("name"),
				version = properties.getProperty("version"),
				className = properties.getProperty("main-class"),
				dependencies = properties.getProperty("dependencies");

		// "name", "version" and "main-class" are all required fields,
		// so throw an exception if they are not found. "dependencies"
		// is optional, so it may be null.
		if (name == null || version == null | className == null)
			throw new InstantiationException("Property missing from \"plugin.info\"");

		// Create and return a new instance.
		return new PluginLoader(name, new Version(version), dependencies) {
			public void load() throws IOException, NoSuchMethodException, ClassNotFoundException {
				@SuppressWarnings("resource")
				ClassLoader classLoader = new URLClassLoader(new URL[]{file.toURI().toURL()});

				// Call the parent method with the loaded plugin class.
				super.initialize(classLoader.loadClass(className));
			}
		};
	}
}