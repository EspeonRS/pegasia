package org.pegasia.client.runescape;

import java.applet.Applet;
import java.awt.Canvas;
import java.awt.Component;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Collection;
import java.util.HashMap;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;

import org.pegasia.util.FileUtils;
import org.pegasia.util.SleepTicker;
import org.pegasia.util.net.DownloadListener;
import org.pegasia.util.net.WebUtil;

public final class RSLoader {
	public static final File GAME_FILE = new File(FileUtils.DEFAULT_DIRECTORY, "runescape.jar");

	public static void load(RSClient client, boolean downloadApplet) {
		// Set the location of the RuneScape cache
		System.setProperty("user.home", FileUtils.DEFAULT_DIRECTORY.getAbsolutePath());

		System.setProperty("sun.awt.noerasebackground", "true");

		// Download the client parameters
		RSParameters parameters;
		try {
			// Update the progress bar
			client.progressBar.update(0, "Connecting to website");

			// Load the parameters
			parameters = RSParameters.loadParameters(client.properties.useHomeworld ? client.properties.homeworld : -1);
		} catch (IOException e) {
			retry(client, downloadApplet, 15);
			return;
		}

		// Check if the applet needs to be downloaded
		if (downloadApplet || !GAME_FILE.exists()) try {
			// Update the progress bar
			client.progressBar.update(0, "Connecting to download server");

			// Create the applet's URL
			URL gameJar = new URL(parameters.getValue("codebase") + parameters.getValue("initial_jar"));

			// Download the file
			WebUtil.webToFile(gameJar, GAME_FILE, new DownloadListener(){
				@Override public int getNotificationInterval() {
					return 500;
				}

				@Override public void notifyDownloadUpdate(int progress, int total) {
					// Update the progress bar when it notifies with its progress
					double percent = (double) progress / total;
					client.progressBar.update( percent,
							"Downloading applet - "
									+ ((int)(100 * percent)) + "% of "
									+ FileUtils.bytesToMegabytes(total) + "MB" );
				}

				@Override public void notifyDownloadEnd(boolean success) {
					// If successful, attempt to start again without downloading anything else
					if (success) {
						client.progressBar.update(1);
						finish(client, parameters);
						// If unsuccessful, redownload the parameters and try again
					} else {
						System.err.println("Error download applet from server.");
						retry(client, downloadApplet, 15);
					}
				}
			});

			// Since the file will be downloaded in a new thread, end the
			// start event call in this thread
			return;
		} catch (IOException e) {
			System.err.println("Error download applet from server.");
			retry(client, downloadApplet, 15);
			return;
		}

		// If the applet does not need to be downloaded, launch it
		finish(client, parameters);
	}

	private static void retry(final RSClient client, final boolean downloadApplet, final int timeInSeconds) {
		(new SleepTicker() {
			@Override
			protected boolean tick(int ticksRemaining) {
				client.progressBar.update(0, "Unable to connect. Retrying in " + ticksRemaining);
				return true;
			}
		}).start(1000, 15);

		load(client, downloadApplet);
	}
	
	private static RSGraphics hookCanvas(RSClient client, Canvas gameCanvas, Collection<Class<?>> classList) throws ReflectiveOperationException {
		RSGraphics graphics = new RSGraphics(client, gameCanvas);
		boolean hooked = false;

		// Attempt to find any references to the game's internal canvas
		// and hook it with the custom canvas		
		for (Class<?> c: classList)
			for (Field f: c.getDeclaredFields())
				if (Modifier.isStatic(f.getModifiers()) && Canvas.class.isAssignableFrom(f.getType()) ) {
					f.setAccessible(true);
					f.set(null, graphics.canvas);
					hooked = true;
				}

		// If the canvas could not be hooked, dispose our custom canvas
		if (!hooked)
			throw new NoSuchFieldException("Unable to find \"static java.awt.Canvas\" field.");
		
		return graphics;
	}
	
	private static void finish(RSClient client, RSParameters parameters) {
		try {
			client.progressBar.update("Launching applet");
			
			client.start(new RSLoader(client, parameters));
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println(e.getMessage());
			System.err.println("Unable to launch RuneScape client, redownloading from server.");
			load(client, true);
		}
	}

	final RSClient client;
	final URLClassLoader classLoader;
	final HashMap<String, Class<?>> classMap;
	final Applet applet;
	final Canvas gameCanvas;
	final RSGraphics graphics;

	private RSLoader(RSClient client, RSParameters parameters) throws Exception {
		this.client = client;
		try {
			// Create the classloader
			classLoader = new URLClassLoader(new URL[]{RSLoader.GAME_FILE.toURI().toURL()});

			// Create a list of classes
			FileInputStream fstream = new FileInputStream(RSLoader.GAME_FILE);
			JarInputStream jstream = new JarInputStream(fstream);
			classMap = new HashMap<String, Class<?>>();

			JarEntry entry;
			while ((entry = jstream.getNextJarEntry()) != null)
				if (entry.getName().endsWith(".class")) {
					String name = entry.getName().replace(".class", "");
					classMap.put(name, classLoader.loadClass(name));
				}
			jstream.close();

			// Load and initiate the runner class
			applet = (Applet) classMap.get("client").newInstance();
			applet.setStub(parameters);
			if (client.properties.layoutFix)
				applet.setLayout(null);

			// Start running the applet
			applet.init();
			applet.start();

			// Wait until the applet finished loading, which will likely be
			// once it makes itself visible.
			while (!applet.isVisible() || applet.getComponentCount() == 0 )
				try { Thread.sleep(100); } catch (InterruptedException e) {}

			// Try to find the game's canvas
			RSGraphics graphics = null;
			Canvas gameCanvas = null;
			Component component = applet.getComponent(0);
			if (component instanceof Canvas) {
				gameCanvas = (Canvas) component;

				// Setting ignore repaint helps solve issues with flickering
				gameCanvas.setIgnoreRepaint(true);

				// If the client is using reflection, hook the canvas
				if (client.properties.reflection) try {
					graphics = hookCanvas(client, gameCanvas, classMap.values());
				} catch (ReflectiveOperationException e) {
					System.err.println(e.getMessage());
				}
			}
			this.graphics = graphics;
			this.gameCanvas = gameCanvas;
		} catch (Exception e) {
			destroy();
			throw e;
		}
	}

	void destroy() {
		// Clean up the applet in a new thread
		(new Thread(new Runnable(){
			@Override
			public void run() {
				applet.stop();
				applet.destroy();
			}
		})).start();

		// Close the ClassLoader
		try {
			classLoader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}