package org.pegasia.api.runescape;

import java.awt.Graphics;
import java.awt.Rectangle;
import java.util.HashMap;

import org.pegasia.client.runescape.RSClient;

public final class RuneScape {
	public static final String PLAYER_NAME_CHARACTERS = "a-zA-Z0-9 _-";
	
	private static RSClient rs;
	
	/** Instances of RuneScape cannot be instantiated. */
	private RuneScape() {}
	
	/**
	 * Setter method used internally to set the client's {@link RSClient} instance.
	 * <p>
	 * This client will call this method once to set the RuneScape interface's {@code RSClient} instance.
	 * It will throw an IllegalStateException when called by a plugin.
	 * 
	 * @param rs the {@code RSClient} instance for the RuneScape interface to use
	 * @throws IllegalStateException if the {@code RSClient} instance has already been set by the client
	 */
	public static void setLoader(RSClient rs) throws IllegalStateException {
		if (RuneScape.rs != null)
			throw new IllegalStateException("RSClient has already been set.");
		RuneScape.rs = rs;
	}
	
	/**
	 * Returns whether the RuneScape applet is currently running.
	 * <p>
	 * Plugins can be running while the RuneScape applet is not, so it may be necessary to check if
	 * it is running before performing further actions.
	 * 
	 * @return whether or not the RuneScape applet is current running
	 */
	public static boolean isRunning() {
		return rs.isRunning();
	}
	
	/**
	 * Returns whether RuneScape's canvas has been successfully hooked.
	 * <p>
	 * The canvas may not be hooked under numerous circumstances. If the canvas was unable to be hooked,
	 * then no overlays will be visible.
	 * 
	 * @return whether RuneScape's canvas has been successfully hooked
	 */
	public static boolean isCanvasHooked() {
		return rs.isCanvasHooked();
	}
	
	/**
	 * Returns whether reflection features are enabled.
	 * <p>
	 * Reflection being disabled means that the canvas will not be hooked, and any plugins relying on
	 * reflection will be unable to function.
	 * 
	 * @return whether reflection is enabled
	 */
	public static boolean isReflectionEnabled() {
		return rs.getProperties().reflection;
	}
	
	/**
	 * Returns the current width of the game canvas.
	 * 
	 * @return the width of the game canvas
	 */
	public static int getWidth() {
		return rs.getWidth();
	}
	
	/**
	 * Returns the current height of the game canvas.
	 * 
	 * @return the height of the game canvas
	 */
	public static int getHeight() {
		return rs.getHeight();
	}
	
	/**
	 * Calculates a {@link Rectangle} with same width and height as the game canvas.
	 * <p>
	 * If factorLocationOnScreen is true, then the x and y coordinates of the {@code Rectangle} will
	 * reflect the game canvas's positioning on the user's screen.
	 * 
	 * @param factorLocationOnScreen whether the x and y positioning of the {@code Rectangle} should match the canvas's position
	 * @return A rectangle with the same width and height as the game canvas.
	 */
	public static Rectangle getBounds(boolean factorLocationOnScreen) {
		return rs.getBounds(factorLocationOnScreen);
	}
	
	/**
	 * Draws the game canvas onto the provided {@link Graphics} instance.
	 * <p>
	 * 
	 * 
	 * @param graphics
	 */
	public static void drawImage(Graphics graphics) {
		rs.drawImage(graphics, 0, 0, -1, -1);
	}
	
	public static void drawImage(Graphics graphics, int xOffset, int yOffset, int width, int height) {
		rs.drawImage(graphics, xOffset, yOffset, width, height);
	}
	
	
	public static HashMap<String, Class<?>> getClassMap() {
		return rs.getClassMap();
	}
}
