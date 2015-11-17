package org.pegasia.client.ui.skin;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;

import org.pegasia.client.config.ClientProperties;
import org.pegasia.util.FileUtils;

public class ThemeManager {
	private static final ArrayList<ThemeEntry> themes = new ArrayList<ThemeEntry>();

	static {
		// Populate the list with the default LaFs provided with Java
		LookAndFeelInfo[] orig = UIManager.getInstalledLookAndFeels();
		for (int i = 0; i < orig.length; i++)
			try {
				themes.add(new ThemeEntry(null, orig[i].getName(), orig[i].getClassName()));
			} catch (ClassNotFoundException e) {}
	}

	public static void loadTheme(ClientProperties properties) {
		// Load all external themes
		File[] files = FileUtils.THEME_DIRECTORY.listFiles(new FileFilter() {
			@Override public boolean accept(File file) {
				return file.isFile() && file.getName().endsWith(".jar");
			}
		});

		for (File file: files)
			try {
				new ThemeLoader(file, themes);
			} catch (Exception e) {
				e.printStackTrace();
			}

		// Set the system LaF as the client's default skin
		String systemTheme = UIManager.getSystemLookAndFeelClassName();
		boolean tryDecorated = properties.themeDecorated;

		// Attempt to use the skin specified in the client properties
		ThemeEntry theme = null;
		System.out.println("Attempting load: " + properties.themeClass);
		for (ThemeEntry entry: themes)
			if (properties.themeClass.equals(entry.getClassName())) {
				theme = entry;
				break;
			}

		try {
			if (theme == null)
				UIManager.setLookAndFeel(systemTheme);
			else
				theme.apply(tryDecorated);
		} catch (Exception e) {
			e.printStackTrace();
			if (properties.themeSelection == ClientProperties.HIDE_THEME_SELECTION)
				properties.themeSelection = ClientProperties.SHOW_THEME_SELECTION_ONCE;
		}

		if (properties.themeSelection > 0) {
			// Show the theme selection
			ThemeSelector.showThemeSelection(themes);

			// Remove the theme selection flag if showing only once
			if (properties.themeSelection == ClientProperties.SHOW_THEME_SELECTION_ONCE)
				properties.themeSelection = ClientProperties.HIDE_THEME_SELECTION;

			properties.themeClass = UIManager.getLookAndFeel().getClass().getName();
			System.out.println("Attempting save: " + properties.themeClass);
			properties.themeDecorated = JFrame.isDefaultLookAndFeelDecorated();
			properties.save();
		}
	}
}
