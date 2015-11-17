package org.pegasia.client.ui.skin;

import java.io.IOException;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.LookAndFeel;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

final class ThemeEntry {
	private final ThemeLoader loader;
	private final String name, className;

	private Class<? extends LookAndFeel> classObject;
	private LookAndFeel laf;

	@SuppressWarnings("unchecked")
	ThemeEntry(ThemeLoader loader, String name, String className) throws ClassNotFoundException {
		this.loader = loader;
		this.name = name;
		this.className = className;

		if (loader == null)
			try {
				if ("javax.swing.plaf.metal.MetalLookAndFeel".equals(className))
					classObject = javax.swing.plaf.metal.MetalLookAndFeel.class;
				else
					classObject = (Class<? extends LookAndFeel>) Class.forName(className);
			} catch (ClassCastException e) {
				throw new ClassNotFoundException(e.getMessage());
			}
	}

	void apply(boolean decorated) throws InstantiationException, IllegalAccessException, UnsupportedLookAndFeelException {
		if (laf == null) {
			if (classObject == null)
				try {
					classObject = loader.load(className);
				} catch (ClassCastException | ClassNotFoundException | IOException e) {
					System.err.println("Unable to load class: " + className);
				}
			
			UIManager.put("ClassLoader", classObject.getClassLoader());

			LookAndFeel currLaF = UIManager.getLookAndFeel();
			if (classObject.equals(currLaF.getClass()))
				laf = currLaF;
			else
				laf = classObject.newInstance();
		} else if (classObject != null)
			UIManager.put("ClassLoader", classObject.getClassLoader());

		decorated = decorated && laf.getSupportsWindowDecorations();
		JFrame.setDefaultLookAndFeelDecorated(decorated);
		JDialog.setDefaultLookAndFeelDecorated(decorated);
		UIManager.setLookAndFeel(laf);
	}

	String getClassName() {
		return className;
	}

	String getName() {
		return name;
	}

	@Override
	public String toString() {
		return name;
	}
}
