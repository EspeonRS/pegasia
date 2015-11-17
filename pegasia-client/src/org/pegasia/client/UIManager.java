package org.pegasia.client;

import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.Dimension;
import java.awt.KeyboardFocusManager;
import java.awt.Toolkit;
import java.io.IOException;
import java.util.ArrayDeque;

import org.pegasia.api.PegasiaComponent;
import org.pegasia.api.component.PegasiaOverlay;
import org.pegasia.api.component.PegasiaPanel;
import org.pegasia.client.ui.ClientFrame;
import org.pegasia.client.ui.ClientMenuBar;
import org.pegasia.client.ui.UIProperties;

public class UIManager {
	private static final KeyboardFocusManager keyboardFocusManager = KeyboardFocusManager.getCurrentKeyboardFocusManager();
	
	private static UIManager instance;
	
	public static boolean installComponent(PegasiaComponent component) {
		if (instance == null || instance.shutdown)
			return false;
		
		return instance.addComponent(component);
	}
	
	public static boolean uninstallComponent(PegasiaComponent component) {
		if (instance == null || instance.shutdown)
			return false;
		
		return instance.removeComponent(component);
	}

	private final Pegasia client;
	private final UIProperties properties = new UIProperties();
	private final ArrayDeque<PegasiaComponent> queue = new ArrayDeque<PegasiaComponent>();

	private ClientFrame frame;
	private ClientMenuBar menuBar;
	private boolean shutdown = false;

	public UIManager(Pegasia client) throws InstantiationException {
		if (client == null)
			throw new InstantiationException();
		this.client = client;
		
		instance = this;
	}
	
	public void shutdown() {
		shutdown = true;

		properties.put(null, UIProperties.BOUNDS_KEY, frame.getBounds());
		properties.put(null, UIProperties.SIDE_PANEL_KEY, frame.isPanelVisible(ClientFrame.PANEL_SIDE));
		properties.put(null, UIProperties.BOTTOM_PANEL_KEY, frame.isPanelVisible(ClientFrame.PANEL_SIDE));
		properties.put(null, UIProperties.RESIZABLE_KEY, frame.isResizable());
		properties.put(null, UIProperties.ALWAYS_ON_TOP_KEY, frame.isAlwaysOnTop());

		try {
			properties.save();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public synchronized void show() {
		if (frame != null)
			return;

		// Initialize the client frame
		frame = new ClientFrame(properties);
		menuBar = new ClientMenuBar(client, properties);
		frame.setJMenuBar(menuBar);
		frame.add(client.getRSClient().getPanel(), BorderLayout.CENTER);
		
		// Empty the queue of visible panels
		for (PegasiaComponent component: queue)
			addComponent(component);
		queue.clear();

		// Make the client visible
		frame.pack();
		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		frame.setLocation(dim.width/2 - frame.getSize().width/2, dim.height/2 - frame.getSize().height/2);
		frame.setVisible(true);
	}

	public synchronized boolean addComponent(PegasiaComponent component) {
		if (shutdown)
			return false;
		
		if (component instanceof PegasiaOverlay) {
			
		} else if (component instanceof PegasiaPanel) {
			if (frame == null)
				return queue.add(component);
			
			PegasiaPanel ppanel = (PegasiaPanel) component;
			// Check if properties holds saved position for panel
			// if not...
			switch (ppanel.getDefaultPosition()) {
			case PegasiaPanel.PANEL_RIGHT:
				return frame.panels[ClientFrame.PANEL_SIDE].addPanel(ppanel);
			case PegasiaPanel.PANEL_BOTTOM:
				return frame.panels[ClientFrame.PANEL_BOTTOM].addPanel(ppanel);
			default:
				// Add to menu
				break;
			}
		}
		
		return false;
	}
	
	public synchronized boolean removeComponent(PegasiaComponent component) {
		if (shutdown)
			return false;
		if (frame == null)
			return queue.remove(component);
		else if (component instanceof PegasiaPanel)
			return frame.panels[ClientFrame.PANEL_SIDE].removePanel((PegasiaPanel) component) ||
					frame.panels[ClientFrame.PANEL_BOTTOM].removePanel((PegasiaPanel) component);
		
		return false;
	}
	
	public ClientFrame getFrame() {
		return frame;
	}
	
	public ClientMenuBar getMenuBar() {
		return menuBar;
	}
	
	public UIProperties getProperties() {
		return properties;
	}

	public boolean isWindowFocus() {
		return !shutdown && keyboardFocusManager.getActiveWindow() == frame;
	}

	public boolean isGameFocus() {
		return isWindowFocus() && keyboardFocusManager.getFocusOwner() instanceof Canvas;
	}
}
