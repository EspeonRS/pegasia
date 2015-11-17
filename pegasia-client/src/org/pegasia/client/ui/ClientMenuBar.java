package org.pegasia.client.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;

import org.pegasia.client.Pegasia;
import org.pegasia.client.UIManager;
import org.pegasia.plugins.screenshot.ScreenShotPlugin;
import org.pegasia.plugins.screenshot.ScreenshotType;
import org.pegasia.plugins.screenshot.dialog.ScreenShotFrame;
import org.pegasia.plugins.worldmap.WorldMap;

public final class ClientMenuBar extends JMenuBar implements ActionListener {
	private final Pegasia client;
	private final UIManager ui;

	private JMenu viewMenu, pluginsMenu;
	private JCheckBoxMenuItem viewSideContainer, viewBottomContainer, viewResizable, viewFullscreen, viewAlwaysOnTop;
	private JMenuItem pluginManage;

	// TODO: Remove these and add to corresponding plugins
	private JMenuItem toolsWorldMap;
	private JMenu screenshotMenu;
	private JMenuItem screenshotGame, screenshotWorld, screenshotChat, screenshotTabs, screenshotViewer;

	public ClientMenuBar(Pegasia client, UIProperties properties) {
		super();
		this.client = client;
		this.ui = client.getUIManager();

		// Load properties
		boolean sideVisible, bottomVisible, resizable, alwaysOnTop;
		sideVisible = properties.getBool(null, "main.sideVisible", false);
		bottomVisible = properties.getBool(null, "main.bottomVisible", false);
		resizable = properties.getBool(null, "main.resizable", false);
		alwaysOnTop = properties.getBool(null, "main.alwaysOnTop", false);

		// View Menu
		viewMenu = new JMenu("View");
		viewMenu.setMnemonic(KeyEvent.VK_V);
		viewMenu.getPopupMenu().setLightWeightPopupEnabled(false);
		this.add(viewMenu);

		viewSideContainer = new JCheckBoxMenuItem("Side Panel");
		viewSideContainer.setMnemonic(KeyEvent.VK_S);
		viewSideContainer.addActionListener(this);
		viewMenu.add(viewSideContainer);

		viewBottomContainer = new JCheckBoxMenuItem("Bottom Panel");
		viewBottomContainer.setMnemonic(KeyEvent.VK_B);
		viewBottomContainer.addActionListener(this);
		viewMenu.add(viewBottomContainer);

		viewMenu.addSeparator();

		viewResizable = new JCheckBoxMenuItem("Resizable");
		viewResizable.setMnemonic(KeyEvent.VK_R);
		viewResizable.addActionListener(this);
		viewMenu.add(viewResizable);
		
		viewFullscreen = new JCheckBoxMenuItem("Fullscreen");
		viewFullscreen.setMnemonic(KeyEvent.VK_F);
		viewFullscreen.addActionListener(this);
		viewFullscreen.setEnabled(false);
		viewMenu.add(viewFullscreen);

		viewAlwaysOnTop = new JCheckBoxMenuItem("Always on Top");
		viewAlwaysOnTop.setMnemonic(KeyEvent.VK_A);
		viewAlwaysOnTop.addActionListener(this);
		viewMenu.add(viewAlwaysOnTop);
		
		viewMenu.addMenuListener(new MenuListener() {
			@Override
			public void menuSelected(MenuEvent e) {
				viewResizable.setEnabled(!ui.getFrame().isMaximized());
			}
			
			public void menuCanceled(MenuEvent e) {}
			public void menuDeselected(MenuEvent e) {}
		});

		// Plugins Menu
		pluginsMenu = new JMenu("Plugins");
		pluginsMenu.setMnemonic(KeyEvent.VK_P);
		pluginsMenu.getPopupMenu().setLightWeightPopupEnabled(false);
		this.add(pluginsMenu);

		pluginManage = new JMenuItem("Manage...");//, getImageIcon("settings.png"));
		pluginManage.addActionListener(this);
		pluginsMenu.add(pluginManage);

		pluginsMenu.addSeparator();

		toolsWorldMap = new JMenuItem("World Map");//, getImageIcon("worldmap.png"));
		toolsWorldMap.setMnemonic(KeyEvent.VK_W);
		toolsWorldMap.addActionListener(this);
		pluginsMenu.add(toolsWorldMap);

		// Screenshot Menu
		screenshotMenu = new JMenu("Screenshot");
		screenshotMenu.getPopupMenu().setLightWeightPopupEnabled(false);
		this.add(screenshotMenu);

		screenshotGame = new JMenuItem("Full Game");
		screenshotGame.setMnemonic(KeyEvent.VK_G);
		screenshotGame.addActionListener(this);
		screenshotMenu.add(screenshotGame);

		screenshotWorld = new JMenuItem("World");
		screenshotWorld.setMnemonic(KeyEvent.VK_W);
		screenshotWorld.addActionListener(this);
		screenshotMenu.add(screenshotWorld);

		screenshotChat = new JMenuItem("Chat");
		screenshotChat.setMnemonic(KeyEvent.VK_C);
		screenshotChat.addActionListener(this);
		screenshotMenu.add(screenshotChat);

		screenshotTabs = new JMenuItem("Tabs");
		screenshotTabs.setMnemonic(KeyEvent.VK_T);
		screenshotTabs.addActionListener(this);
		screenshotMenu.add(screenshotTabs);

		screenshotMenu.addSeparator();

		screenshotViewer = new JMenuItem("Viewer");//, getImageIcon("screenshot.png"));
		screenshotViewer.setMnemonic(KeyEvent.VK_V);
		screenshotViewer.addActionListener(this);
		screenshotMenu.add(screenshotViewer);
	}

	/**
	 * Handles events called when user selects an option from the menu.
	 * 
	 * @param e ActionEvent instance passed when user interacts with menu
	 */
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == viewSideContainer)
			ui.getFrame().setPanelVisible(ClientFrame.PANEL_SIDE, viewSideContainer.isSelected());
		else if (e.getSource() == viewBottomContainer)
			ui.getFrame().setPanelVisible(ClientFrame.PANEL_BOTTOM, viewBottomContainer.isSelected());
		else if (e.getSource() == viewResizable)
			ui.getFrame().setResizable(viewResizable.isSelected());
		else if (e.getSource() == viewAlwaysOnTop)
			ui.getFrame().setAlwaysOnTop(viewAlwaysOnTop.isSelected());
		else if (e.getSource() == pluginManage)
			client.openPlugindialog();

		// TODO: Remove these and add to corresponding plugins
		else if (e.getSource() == toolsWorldMap)
			WorldMap.showDialog();
		else if (e.getSource() == screenshotGame)
			takeScreenShot(ScreenshotType.GAME);
		else if (e.getSource() == screenshotWorld)
			takeScreenShot(ScreenshotType.WORLD);
		else if (e.getSource() == screenshotChat)
			takeScreenShot(ScreenshotType.CHAT);
		else if (e.getSource() == screenshotTabs)
			takeScreenShot(ScreenshotType.TABS);
		else if (e.getSource() == screenshotViewer)
			ScreenShotFrame.open();
	}

	private void takeScreenShot(ScreenshotType type) {
		ScreenShotPlugin.takeScreenshot(type);
	}

	/**
	 * Method to load in an icon from the internal resource folder.
	 * 
	 * @param name Filename of the menu icon
	 * @return Icon loaded in as an ImageIcon instance
	 */
	/*private ImageIcon getImageIcon(String filename) {
		return FileUtil.getImageIcon("resources/menu/" + filename);
	}*/
}
