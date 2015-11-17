package org.pegasia.plugins.screenshot;

import java.awt.AWTEvent;
import java.awt.AWTException;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.event.AWTEventListener;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Calendar;

import javax.imageio.ImageIO;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

import org.pegasia.api.PluginConfigPanel;
import org.pegasia.api.runescape.RuneScape;
import org.pegasia.util.FileUtils;
import org.pegasia.util.StringUtils;

/**
 * 
 * <p>Sources:<br />
 * http://stackoverflow.com/questions/4854185/jlist-right-click-shows-menu-use-drop-cancel <br />
 * http://stackoverflow.com/tags/javasound/info <br />
 * 
 * @author Espeon
 * @since 1.0
 */

public class ScreenShotPlugin {
	public static final File SCREENSHOT_DIRECTORY;
	static {
		File subdirectory = new File(FileUtils.DEFAULT_DIRECTORY, "screenshots");
		if ( subdirectory.exists() && subdirectory.isDirectory()
				|| subdirectory.mkdirs() )
			SCREENSHOT_DIRECTORY = subdirectory;
		else
			SCREENSHOT_DIRECTORY = FileUtils.DEFAULT_DIRECTORY;
	}

	private static boolean active = false;
	private static ScreenShotProperties properties = null;
	private static AWTEventListener listener = null;

	private static Robot robot = null;
	private static Clip clip = null;

	public static void start() {
		if (properties == null)
			properties = new ScreenShotProperties();

		listener = new AWTEventListener() {
			@Override
			public void eventDispatched(AWTEvent event) {
				if (active && properties.useHotkey &&
						((KeyEvent)event).getKeyCode() == KeyEvent.VK_PRINTSCREEN &&
						event.getID() == KeyEvent.KEY_RELEASED ) {
					takeScreenshot(ScreenshotType.GAME);
					((KeyEvent)event).consume();
				}
			}
		};

		Toolkit.getDefaultToolkit().addAWTEventListener(listener, AWTEvent.KEY_EVENT_MASK);
		active = true;
	}

	public static void stop() {
		active = false;

		if (listener != null) {
			Toolkit.getDefaultToolkit().removeAWTEventListener(listener);
			listener = null;
		}
	}

	public static PluginConfigPanel newConfig() {
		if (properties == null)
			properties = new ScreenShotProperties();

		return new ScreenShotConfigPanel(properties);
	}

	public static boolean isActive() {
		return active;
	}

	public static void takeScreenshot(ScreenshotType type) {
		if (!active)
			return;

		// Get screenshot dimension modifiers based on the type of
		// screenshot being taken.
		int xOffset = 0, yOffset = 0, width = RuneScape.getWidth(), height = RuneScape.getHeight();

		switch (type) {
		case GAME: // Do not use any modifiers
			break;
		case WORLD:
			xOffset = 4;
			yOffset = 4;
			width -= 253;
			height -= 169;
			break;
		case CHAT:
			yOffset = height - 165;
			width = 519;
			height = 142;
			break;
		case TABS:
			xOffset = width - 243;
			yOffset = height - 335;
			width = 241;
			height = 335;
			break;
		}

		// If the canvas was hooked, grab the frame directly
		BufferedImage image;
		if (RuneScape.isCanvasHooked()) {
			image = new BufferedImage(width, height, BufferedImage.OPAQUE);
			Graphics imageGraphics = image.getGraphics();
			RuneScape.drawImage(imageGraphics, xOffset, yOffset, width, height);
			imageGraphics.dispose();
		}
		// Else, use a Robot to capture it from the display
		else {
			// Get the bounds of the RuneScape canvas
			Rectangle bounds = RuneScape.getBounds(true);

			// Apply the modifiers to these dimensions
			bounds.x += xOffset;
			bounds.y += yOffset;
			bounds.width = width;
			bounds.height = height;

			// Create the robot if it has not yet been initialized
			if (robot == null)
				try {
					robot = new Robot();
				} catch (AWTException e) {
					e.printStackTrace();
					return;
				}

			// Take the screenshot
			image = robot.createScreenCapture(bounds);
		}

		// If the image could not be captured, don't save a screenshot
		if (image == null) {
			System.err.println("Unable to capture screenshot.");
			return;
		}

		// Determine the file name to save it as
		File file = new File(getCurrentScreenshotDirectory(), getTimestamp()+".png");
		int count = 0;
		while (file.exists()) {
			count += 1;
			file = new File(getCurrentScreenshotDirectory(), getTimestamp()+" ("+count+").png");
		}

		try {
			// Attempt to save the screenshot
			ImageIO.write(image, "png", file);

			// If successful, play a sound
			if (properties.useSound)
				try {
					if (clip == null) {
						clip = AudioSystem.getClip();
						InputStream is = ScreenShotPlugin.class.getClassLoader().getResourceAsStream("resources/plugin/screenshot/screenshot.wav");
						AudioInputStream ais = AudioSystem.getAudioInputStream(new BufferedInputStream(is));
						clip.open(ais);
					}
					clip.setFramePosition(0);
					clip.start();
				} catch (LineUnavailableException | UnsupportedAudioFileException | IOException e) {
					System.err.println("Unable to open screen shot audio clip.");
				}
		} catch(Exception e) {
			System.err.println("Unable to save screen shot.");
		}
	}

	/**
	 * Gets the directory in which a screenshot should be placed if it
	 * were to be taken at the current time. This method should be called
	 * on the spot, to ensure the path is updated for changes in month/year.
	 * 
	 * @return File representation of the directory for screenshots at the current time.
	 */
	public static File getCurrentScreenshotDirectory() {
		String path;
		switch(properties.sortType) {
		case 0:
			path = StringUtils.getYear() + "/" + StringUtils.getMonth();
			break;
		case 1:
			path = StringUtils.getYear();
			break;
		default:
			return SCREENSHOT_DIRECTORY;
		}

		// Attempt to load/create screenshot directory
		File subdirectory = new File(SCREENSHOT_DIRECTORY, path);
		if ( subdirectory.exists() && subdirectory.isDirectory()
				|| subdirectory.mkdirs() )
			return subdirectory;

		// If it couldn't be created, use the default directory
		return FileUtils.DEFAULT_DIRECTORY;
	}

	private static String getTimestamp() {
		Calendar cal = Calendar.getInstance();
		StringBuilder str = new StringBuilder();

		str.append(StringUtils.fillNumber(cal.get(Calendar.YEAR), 4) + "-");
		str.append(StringUtils.fillNumber(cal.get(Calendar.MONTH), 2) + "-");
		str.append(StringUtils.fillNumber(cal.get(Calendar.DATE), 2) + " ");
		str.append(StringUtils.fillNumber(cal.get(Calendar.HOUR_OF_DAY), 2) + ".");
		str.append(StringUtils.fillNumber(cal.get(Calendar.MINUTE), 2) + ".");
		str.append(StringUtils.fillNumber(cal.get(Calendar.SECOND), 2));

		return str.toString();
	}
}
