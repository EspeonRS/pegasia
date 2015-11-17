package org.pegasia.client.ui;

import org.pegasia.client.Pegasia;
import org.pegasia.client.ui.container.TabbedPanelContainer;
import org.pegasia.util.FileUtils;

import javax.swing.*;

import java.awt.*;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;

public final class ClientFrame extends JFrame {
	public static final int PANEL_SIDE = 0, PANEL_BOTTOM = 1;

	private final BorderLayout layout;
	public final TabbedPanelContainer[] panels;
	// TODO: Some sort of seperate panel manager?

	/**
	 * Constructs a new instance of the client. The client should only be created
	 * once, from the main method of the program.
	 */
	public ClientFrame(UIProperties properties) {
		super(Pegasia.TITLE + " v" + Pegasia.VERSION);
		setLayout(layout = new BorderLayout());

		// Load the application icon set
		BufferedImage iconSet = FileUtils.getBufferedImage("resources/pegasia-icons.png");
		setIconImages(FileUtils.getIconList(iconSet));

		// Initialize the side containers
		panels = new TabbedPanelContainer[2];
		panels[PANEL_SIDE] = new TabbedPanelContainer(JTabbedPane.RIGHT, true);
		panels[PANEL_SIDE].setPreferredSize(new Dimension(256, 0));
		panels[PANEL_BOTTOM] = new TabbedPanelContainer(JTabbedPane.BOTTOM, false);
		panels[PANEL_BOTTOM].setPreferredSize(new Dimension(0, 196));

		// Load settings from the provided Properties instance
		//set

		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		setResizable(false);
	}

	public boolean isMaximized() {
		return (getExtendedState() & JFrame.MAXIMIZED_BOTH) == JFrame.MAXIMIZED_BOTH;
	}

	public void setMaximized(boolean maximized) {
		if (maximized)
			setExtendedState(getExtendedState() | JFrame.MAXIMIZED_BOTH);
		else
			setExtendedState(getExtendedState() & ~JFrame.MAXIMIZED_BOTH);
	}

	public boolean isPanelVisible(int panel) {
		return layout.getLayoutComponent(panelLayoutPosition(panel)) != null;
	}

	public void setPanelVisible(int panel, boolean visible) {
		String layoutPosition = panelLayoutPosition(panel);
		Component current = layout.getLayoutComponent(layoutPosition);
		Dimension size = getSize();
		int width = size.width, height = size.height;

		if (current != null) {
			switch (panel) {
			case PANEL_SIDE:
				width -= current.getSize().width;
				break;
			case PANEL_BOTTOM:
				height -= current.getSize().height;
				break;
			}
			
			remove(current);
		}

		if (visible) { 
			add(panels[panel], layoutPosition);

			switch (panel) {
			case PANEL_SIDE:
				width += panels[panel].getPreferredSize().width;
				break;
			case PANEL_BOTTOM:
				height += panels[panel].getPreferredSize().height;
				break;
			}
		}

		if (!isMaximized()) {
			size.width = width;
			size.height = height;
			setSize(size);
		}

		revalidate();
		repaint();
	}

	private String panelLayoutPosition(int panel) {
		switch (panel) {
		default:
		case PANEL_SIDE:
			return BorderLayout.EAST;
		case PANEL_BOTTOM:
			return BorderLayout.SOUTH;
		}
	}

	/**
	 * Method to call when attempting to close the client. This method will
	 * prompt the user before closing the client to prevent miss-clicks.
	 */
	public void close() {
		Object[] options = {"Exit","Cancel"};
		if (JOptionPane.showOptionDialog(this,
				"Are you certain you wish to exit?",
				"Confirm Exit",
				JOptionPane.OK_CANCEL_OPTION,
				JOptionPane.WARNING_MESSAGE,
				(Icon)null,
				options,
				options[1]) == JOptionPane.YES_OPTION)
			System.exit(0);
	}

	/**
	 * Handles event called when user attempts to close the window using
	 * the close button at the upper right-hand corner of the screen.
	 * 
	 * @param e WindowEvent instance passed when user interacts with window
	 */
	@Override protected void processWindowEvent(WindowEvent e) {
		if (e.getID() == WindowEvent.WINDOW_CLOSING)
			close();
		else
			super.processWindowEvent(e);
	}
}
