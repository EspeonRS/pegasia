package org.pegasia.plugins.worldmap;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;

import org.pegasia.util.FileUtils;
import org.pegasia.util.SleepTicker;
import org.pegasia.util.net.DownloadListener;
import org.pegasia.util.net.WebUtil;
import org.pegasia.util.net.Webpage;
import org.pegasia.util.ui.ImageViewer;
import org.pegasia.util.ui.RSProgressBar;

public class WorldMap extends JFrame {
	private static final File file = new File(FileUtils.DEFAULT_DIRECTORY, "worldmap.jpg");
	private static final Dimension size = new Dimension(720, 600);
	
	private static boolean active = false;
	private static volatile WorldMap instance = null;
	
	private RSProgressBar progressBar;
	
	public static void start() {
		active = true;
	}

	public static void stop() {
		active = false;
	}
	
	public static void showDialog() {
		if (active == false)
			return;
		
		if (instance == null) {
			instance = new WorldMap();
			
			(new SwingWorker<Void,Void>(){

				@Override
				protected Void doInBackground() throws Exception {
					instance.load();
					return null;
				}
				
			}).execute();
		} else
			instance.toFront();
	}
	
	private WorldMap() {
		super("RuneScape World Map");
		this.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		
		this.add(new JLabel("hi"));
		this.getContentPane().setLayout(new BorderLayout());
		this.getContentPane().setBackground(Color.BLACK);
		this.setSize(size);
		this.setResizable(true);
		
		progressBar = new RSProgressBar();
		setComponent(progressBar);

		this.setVisible(true);
		System.out.println(isVisible());
	}
	
	private void load() {
		if (!file.exists()) try {
			progressBar.update(0, "Connecting to website");
			
			String lines = Webpage.GAME_PAGE.getContent();
			
			String loc = null;
			Matcher matcher = Pattern.compile("<a\\s+href=([^\\s]+)\\s+target=\"WorldMap\"\\s+").matcher(lines);
			while (matcher.find())
				loc = matcher.group(1).replaceAll("\"", "");

			if ( loc == null)
				throw new IOException("Unable to find WorldMap link on page.");
			
			progressBar.update(0, "Opening image URL");
			
			WebUtil.webToFile(new URL(loc), file, new DownloadListener(){

				@Override public int getNotificationInterval() {
					return 250;
				}

				@Override public void notifyDownloadUpdate(int progress, int total) {
					progressBar.update(1.0*progress/total,
							"Downloading World Map - "
							+ ((int)(100.0*progress/total)) + "% of "
							+ FileUtils.bytesToMegabytes(total) + "MB");
				}

				@Override public void notifyDownloadEnd(boolean success) {
					if (success) {
						load();
					} else
						retry("Connection error. Retrying in ");
				}

			});
		} catch (IOException e) {
			retry("Connection error. Retrying in ");
		}
		
		progressBar.update(0, "Loading image");
		
		BufferedImage image = FileUtils.toCompatibleImage(FileUtils.getBufferedImage(getClass(), file.getAbsolutePath()));
		if (image == null) {
			file.delete();
			retry("Unable to open image. Reloading in ");
		}
		
		SwingUtilities.invokeLater(new Runnable(){

			@Override
			public void run() {
				ImageViewer imageViewer = new ImageViewer(image, ImageViewer.ImageViewerFillType.FILL_FRAME);
				setComponent(imageViewer);
			}
			
		});
		
	}
	
	private void retry(final String message) {
		(new SleepTicker() {
			@Override
			protected boolean tick(int ticksRemaining) {
				progressBar.update(0, message + ticksRemaining);
				return true;
			}
		}).start(1000, 15);
		load();
	}
	
	private void setComponent(final Component c) {
		final Container pane = getContentPane();
		final Insets insets = pane.getInsets();

		pane.removeAll();
		
		pane.add(c);
		c.setBounds(insets.left, insets.top, size.width, size.height);
		pane.revalidate();
	}
	
	@Override
	public void dispose() {
		instance = null;
		super.dispose();
	}
}
