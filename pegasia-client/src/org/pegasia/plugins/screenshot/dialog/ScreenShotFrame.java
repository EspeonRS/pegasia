package org.pegasia.plugins.screenshot.dialog;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.SwingUtilities;

import org.pegasia.plugins.screenshot.ScreenShotPlugin;
import org.pegasia.util.ui.ImageViewer;

public class ScreenShotFrame extends JFrame {
	private static final Dimension size = new Dimension(1000, 600);
	private static ScreenShotFrame instance = null;
	
	private ScreenShotFileTree tree;
	private ScreenShotFileList list;
	private ImageViewer viewer;
	
	public static void open() {
		if (!ScreenShotPlugin.isActive())
			return;
		
		if (instance == null)
			instance = new ScreenShotFrame();
		else
			instance.toFront();
	}
	
	private ScreenShotFrame() {
		super("ScreenShot Viewer");
		this.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		this.setLayout(new BorderLayout());
		this.setSize(size);
		this.setResizable(true);

		// Initialize the upper and lower left panes
		list = new ScreenShotFileList(this);
		JScrollPane lowerLeftPane = new JScrollPane(list, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		lowerLeftPane.getVerticalScrollBar().setUnitIncrement(20);
		lowerLeftPane.getHorizontalScrollBar().setUnitIncrement(20);
		
		tree = new ScreenShotFileTree(this, ScreenShotPlugin.getCurrentScreenshotDirectory());
		JScrollPane upperLeftPane = new JScrollPane(tree, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		upperLeftPane.getVerticalScrollBar().setUnitIncrement(20);
		upperLeftPane.getHorizontalScrollBar().setUnitIncrement(20);
		
		JSplitPane sideSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, upperLeftPane, lowerLeftPane);
		sideSplitPane.setDividerLocation(150);
		upperLeftPane.setMinimumSize(new Dimension(0, 100));
		lowerLeftPane.setMinimumSize(new Dimension(0, 100));

		// Initialize the right pane, and the split between them
		JPanel rightPane = new JPanel(new BorderLayout());
		viewer = new ImageViewer(null, ImageViewer.ImageViewerFillType.FIT_FRAME);
		rightPane.add(viewer, BorderLayout.CENTER);
		
		JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, sideSplitPane, rightPane);
		splitPane.setDividerLocation(200);
		sideSplitPane.setMinimumSize(new Dimension(100, 400));
		rightPane.setMinimumSize(new Dimension(200, 400));
		this.add(splitPane);
		
		this.setVisible(true);
	}
	
	public void setDirectory(final File dir) {
		// Give the JList time to handle its ListModel
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				list.changeDirectory(dir);
			}
		});
	}
	
	public void setImage(BufferedImage image) {
		viewer.changeImage(image);
	}
	
	@Override
	public void dispose() {
		instance = null;
		super.dispose();
	}
}
