package org.pegasia.util;

import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

public final class FileUtils {
	public static final File DEFAULT_DIRECTORY, THEME_DIRECTORY, PLUGIN_DIRECTORY, DEFAULT_RUNESCAPE_DIRECTORY;

	static {
		// Obtain and format the client's current directory
		String path = ClassLoader.getSystemClassLoader().getResource(".").getPath();
		try { path = URLDecoder.decode(path, "UTF-8"); }
		catch (UnsupportedEncodingException e) { throw new AssertionError("UTF-8 is undefined"); }

		// Attempt to load/create the client's subdirectory
		File subdirectory = new File(path+"Pegasia-Data/");
		if ( (subdirectory.exists() && subdirectory.isDirectory()) || subdirectory.mkdirs() )
			DEFAULT_DIRECTORY = subdirectory;
		else
			DEFAULT_DIRECTORY = new File(path);

		// Attempt to create a subdirectory for themes
		subdirectory = new File(DEFAULT_DIRECTORY, "themes/");
		if ( (subdirectory.exists() && subdirectory.isDirectory()) || subdirectory.mkdirs() )
			THEME_DIRECTORY = subdirectory;
		else
			THEME_DIRECTORY = null;

		// Attempt to create a subdirectory for plugins
		subdirectory = new File(DEFAULT_DIRECTORY, "plugins/");
		if ( (subdirectory.exists() && subdirectory.isDirectory()) || subdirectory.mkdirs() )
			PLUGIN_DIRECTORY = subdirectory;
		else
			PLUGIN_DIRECTORY = null;

		// Locate the default directory for RuneScape's files
		String directory = System.getProperty("user.home");

		if (directory == null) {
			String os = System.getProperty("os.name");
			if ( os == null )
				os = "Unknown";
			if ( os.toLowerCase().startsWith("win") )
				directory = System.getenv("USERPROFILE");
			else
				directory = System.getenv("HOME");
		}

		if ( directory != null )
			DEFAULT_RUNESCAPE_DIRECTORY = new File(directory);
		else
			DEFAULT_RUNESCAPE_DIRECTORY = new File("~/");
	}

	/**
	 * http://www.journaldev.com/861/4-ways-to-copy-file-in-java
	 * 
	 * @param src Location of file to be copied.
	 * @param dest Destination of the copy of the file.
	 * @throws IOException
	 */
	public static void copyFile2(File source, File destination) {
		try {
			InputStream is = new FileInputStream(source);
			OutputStream os = new FileOutputStream(destination);
			byte[] buffer = new byte[1024];
			int length;

			while ( (length = is.read(buffer)) > 0 )
				os.write(buffer, 0, length);

			is.close();
			os.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static double bytesToMegabytes(int bytes) {
		bytes = (int)(bytes / 104857.6);
		return bytes / 10d;
	}

	/**
	 * Method to load in an image (from the internal resource folder?).
	 * 
	 * @param name Filename of the menu icon
	 * @return Icon loaded in as an ImageIcon instance
	 */
	public static BufferedImage getBufferedImage(Class<?> loadingClass, String filename) {
		BufferedImage image = null;
		InputStream is = loadingClass.getClassLoader().getResourceAsStream(filename);

		try {
			if (is != null)
				image = ImageIO.read(is);
			else
				image = ImageIO.read(new File(filename));
		} catch (IOException e) {
			System.err.println("Unable to load image: " + filename);
		}

		return image;
	}

	/**
	 * Method to load in an image (from the internal resource folder?).
	 * 
	 * @param name Filename of the menu icon
	 * @return Icon loaded in as an ImageIcon instance
	 */
	public static BufferedImage getBufferedImage(String filename) {
		return getBufferedImage(FileUtils.class, filename);
	}

	public static List<? extends Image> getIconList(BufferedImage image) {
		int width = image.getWidth(), height = image.getHeight();
		ArrayList<Image> list = new ArrayList<Image>();

		if (width >= 16 && height >= 16) {
			list.add(image.getSubimage(0,0,16,16));

			if (width >= 32 && height >= 64) {
				list.add(image.getSubimage(0,32,32,32));

				if (width >= 72)
					list.add(image.getSubimage(32,0,40,40));

				if (width >= 128)
					list.add(image.getSubimage(80,0,48,48));

				if (width >= 192)
					list.add(image.getSubimage(128,0,64,64));
			}
		}

		return list;
	}

	/**
	 * http://stackoverflow.com/questions/196890/java2d-performance-issues
	 * 
	 * @param image Old Image to be converted into a compatible format
	 * @return Image best compatible with the system's graphics configuration
	 */
	public static BufferedImage toCompatibleImage(BufferedImage image) {
		// Obtain the current system graphical settings
		GraphicsConfiguration gfx_config = GraphicsEnvironment.
				getLocalGraphicsEnvironment().getDefaultScreenDevice().
				getDefaultConfiguration();

		// Return the image if it is already compatible
		if (image.getColorModel().equals(gfx_config.getColorModel()))
			return image;

		// If the image is not compatible, create a new image that is
		BufferedImage newImage = gfx_config.createCompatibleImage(
				image.getWidth(), image.getHeight(), image.getTransparency());

		// Draw the old image onto the new
		Graphics2D g2d = (Graphics2D) newImage.getGraphics();
		g2d.drawImage(image, 0, 0, null);
		g2d.dispose();

		// Return the new image
		return newImage; 
	}
}
