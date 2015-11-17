package org.pegasia.plugins.xpdrops;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;

import javax.swing.Timer;

import org.pegasia.api.runescape.RuneScape;

public class XPDropsPlugin {
	private static XPDropsPanel panel;
	private static XPDropsProperties properties;
	
	private static Field field;

	private static Timer timer;
	private static int ticks;
	private static long oldTotal;
	
	public static void start() {
		if (properties == null)
			properties = new XPDropsProperties();
		
		panel = new XPDropsPanel(properties);
		panel.install();
		
		timer = new Timer(20, new XPListener());
		
		ticks = 1;
		oldTotal = 0;
		timer.start();
	}

	public static void stop() {
		System.out.println("Remove component startie.");
		panel.uninstall();
		System.out.println("Remove component endie.");
		panel = null;
		
		timer.stop();
		timer = null;
	}
	
	public static XPDropsConfigPanel newConfig() {
		if (properties == null)
			properties = new XPDropsProperties();
		
		return new XPDropsConfigPanel(properties);
	}

	private static Class<?> getClientClass() {
		HashMap<String, Class<?>> classMap = RuneScape.getClassMap();
		if (classMap == null)
			return null;
		return classMap.get("client");
	}

	private static boolean isValidField(Field field) {
		if (field.getType().isArray() && field.getType().getComponentType() == int.class && Modifier.isStatic(field.getModifiers()) ) try {
			boolean accessible = field.isAccessible();
			field.setAccessible(true);
			int[] values = (int[]) field.get(null);
			field.setAccessible(accessible);

			if (values.length >= 23 && values.length <= 30 && values[3] > 1153)
				return true;
		} catch (NullPointerException |
				IndexOutOfBoundsException |
				IllegalArgumentException |
				IllegalAccessException e) {}

		return false;
	}

	private static Field search() {
		Class<?> clientClass = getClientClass();

		if (clientClass != null)
			for (Field f: clientClass.getDeclaredFields())
				if (isValidField(f))
					return f;

		return null;
	}
	
	private static class XPListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent arg0) {
			ticks++;

			if (ticks >= 5 && field != null) {
				ticks = 1;

				long newTotal = 0;

				try {
					// Get the array of skill XP from the game
					int[] skills = (int[]) field.get(null);

					// Iterate over the array to get the total XP
					if (skills == null)
						newTotal = 0;
					else
						for (int i = 0; i < skills.length; i++)
							newTotal += skills[i];
				} catch (IllegalArgumentException | IllegalAccessException e1) {
					e1.printStackTrace();
				}

				if (newTotal != oldTotal) {
					int amount = (int)(newTotal - oldTotal);
					panel.addDrop(amount, properties.threshold != -1 && amount >= properties.threshold);
				}

				oldTotal = newTotal;
			} else if (ticks >= 20 && field == null) {
				ticks = 1;

				field = search();
				if (field != null)
					field.setAccessible(true);
			}

			if (panel != null)
				panel.repaint();
		}
		
	}
}
