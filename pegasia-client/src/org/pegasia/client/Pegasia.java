//javax.swing.JOptionPane.showMessageDialog(null, "Debug");

package org.pegasia.client;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;

import javax.swing.SwingUtilities;

import org.pegasia.api.runescape.RuneScape;
import org.pegasia.client.config.ClientProperties;
import org.pegasia.client.plugin.manager.PluginDialog;
import org.pegasia.client.runescape.RSClient;
import org.pegasia.client.runescape.RSLoader;
import org.pegasia.client.ui.skin.ThemeManager;
import org.pegasia.util.FileUtils;
import org.pegasia.util.Version;

public final class Pegasia {
	public static final String TITLE = "Pegasia Alpha";
	public static final Version VERSION = new Version("3.04");

	private static String[] args = null;
	private static Pegasia instance = null;

	/**
	 * Main method of the program. But you already knew this. Hopefully.
	 * 
	 * This method handles the construction of the Pegasia instance, as well
	 * as instances of RSLoader, PluginManager, and UIManager.
	 * 
	 * @param args String arguments provided to the program when as it starts.
	 * @throws IOException 
	 */
	public static void main(String[] args) {
		// Only allow the main method to be called once
		if (instance != null)
			return;

		if (args.length == 0 || !args[0].equals("-debug"))
			try {
				OutputStream stream = new FileOutputStream(FileUtils.DEFAULT_DIRECTORY + "/errors.txt", true);
				System.setErr(new PrintStream(stream));
			} catch (FileNotFoundException e) {
				System.err.println("Unable to open error output file.");
			}

		Pegasia.args = args;
		instance = new Pegasia();
		try { instance.pegasiaProperties = new ClientProperties(instance); }
		catch (InstantiationException e) {}

		// Begin loading the RuneScape applet in a new thread
		instance.rs = new RSClient(instance.pegasiaProperties);
		RuneScape.setLoader(instance.rs);
		(new Thread(new Runnable() {
			@Override public void run() {
				RSLoader.load(instance.rs, false);
			}
		})).start();

		// Set up the Plugin and UI managers
		try {
			instance.pm = new PluginManager(instance);
			instance.ui = new UIManager(instance);
		} catch (InstantiationException e) {
			e.printStackTrace();
		}
		

		// MUST COME BEFORE PLUGINS ARE LOADED
		ThemeManager.loadTheme(instance.pegasiaProperties);

		// Load all plugins
		instance.pm.loadPlugins();

		// Add the shutdown hook
		Runtime.getRuntime().addShutdownHook( new Thread() {
			public void run() {
				instance.ui.shutdown();
				instance.pm.shutdown();
			}
		} );

		// Show the client
		SwingUtilities.invokeLater( new Runnable() {
			public void run() {
				instance.ui.show();
			}
		} );
	}

	/**
	 * Returns the array of arguments provided to the client upon startup.
	 * 
	 * @return Arguments provided to the client upon startup.
	 */
	public static String[] getArguments() {
		return args.clone();
	}


	private ClientProperties pegasiaProperties;
	private RSClient rs;
	private org.pegasia.client.PluginManager pm;
	private org.pegasia.client.UIManager ui;

	private Pegasia() {}

	public ClientProperties getPegasiaProperties() {
		return pegasiaProperties;
	}
	
	public RSClient getRSClient() {
		return rs;
	}

	public PluginManager getPluginManager() {
		return pm;
	}

	public UIManager getUIManager() {
		return ui;
	}

	public void openPlugindialog() {
		PluginDialog.openDialog(this);
	}
}
