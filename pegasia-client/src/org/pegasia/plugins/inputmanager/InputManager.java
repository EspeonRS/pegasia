package org.pegasia.plugins.inputmanager;

import java.awt.AWTEvent;
import java.awt.Canvas;
import java.awt.KeyboardFocusManager;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.AWTEventListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.Arrays;

import org.pegasia.api.PluginConfigPanel;
import org.pegasia.plugins.inputmanager.config.InputConfigPanel;
import org.pegasia.plugins.inputmanager.event.HandledInputEvent;
import org.pegasia.plugins.inputmanager.event.HandledKeyEvent;
import org.pegasia.plugins.inputmanager.event.HandledMouseEvent;

public class InputManager implements AWTEventListener {
	private static final KeyboardFocusManager manager = KeyboardFocusManager.getCurrentKeyboardFocusManager();

	private static InputManager instance = null;
	private static InputProperties properties = null;

	public static void start() {
		if (properties == null)
			properties = new InputProperties();
		
		instance = new InputManager();
		Toolkit.getDefaultToolkit().addAWTEventListener(instance, AWTEvent.KEY_EVENT_MASK+AWTEvent.MOUSE_EVENT_MASK);
	}

	public static void stop() {
		Toolkit.getDefaultToolkit().removeAWTEventListener(instance);
		instance = null;
	}
	
	public static PluginConfigPanel newConfig() {
		if (properties == null)
			properties = new InputProperties();
		
		return new InputConfigPanel(properties);
	}

	@Override public void eventDispatched(final AWTEvent event) {
		// The InputManager will only handle an event if it meets the following
		// three conditions:
		//		1. The event is not an instance of HandledInputEvent, meaning that
		//			the InputManager has not already handled it.
		//		2. The event takes place on a Canvas (likely the RuneScape client).
		//		3. The event is an instance of InputEvent, meaning it is either a
		//			KeyEvent or MouseEvent.
		if ( !(event instanceof HandledInputEvent) &&
				event.getSource() instanceof Canvas &&
				event instanceof InputEvent ) {

			// The InputManager then checks if the event is of a type in which it will handle,
			// while also gathering default values for the final output. This second screening
			// will check to ensure that the event meets the following:
			//		1. It must be either a MouseEvent or a KeyEvent
			//		2. If it is a mouse event, it must be either a press or release event, and
			//			it cannot be a left or right click event.
			final int inType, inCode, inID = event.getID();

			if (event instanceof MouseEvent) {
				if (inID != MouseEvent.MOUSE_PRESSED && inID != MouseEvent.MOUSE_RELEASED)
					return;

				inType = InputConfig.TYPE_MOUSE;
				inCode = ((MouseEvent)event).getButton();

				if (inCode == MouseEvent.BUTTON1 || inCode == MouseEvent.BUTTON3)
					return;
			} else if (event instanceof KeyEvent) {
				inType = InputConfig.TYPE_KEY;
				inCode = getStandardizedKeycode((KeyEvent) event);
			} else return;

			// Once the event is confirmed to be a type of event that can be handled by the
			// InputManager, begin checking if there is any user-defined configurations that
			// alters how the KeyEvent or MouseEvent is suppose to function.
			boolean handled = false;
			int outType = InputConfig.TYPE_UNDEFINED;
			int outID = inID;
			int outCode = inCode;
			int outX = 0, outY = 0; // Changes to these variables will move the mouse position relatively

			// Check if the key is mapped to any hot keys
			for (InputConfig hotkeyConfig: InputConfig.values())
				if (inCode == hotkeyConfig.code && inType == hotkeyConfig.type) {
					outType = InputConfig.TYPE_KEY;
					outCode = hotkeyConfig.osrsDefaultCode;
					handled = true;
					break;
				}

			// If the key was originally a hotkey, but was not mapped to any different key,
			// set the outType to undefined so that the event is consumed.
			if (!handled && inType == InputConfig.TYPE_KEY)
				for (InputConfig hotkeyConfig: InputConfig.values())
					if (inCode == hotkeyConfig.osrsDefaultCode) {
						outType = InputConfig.TYPE_UNDEFINED;
						handled = true;
						break;
					}

			// If the event was altered in any way, consume it and fire a new one
			if (handled) {
				InputEvent in = (InputEvent) event;

				if (outType == InputConfig.TYPE_KEY) {
					// If the original event was a MouseEvent, convert the event ID
					// from the MouseEvent value to the KeyEvent value.
					if (inType == InputConfig.TYPE_MOUSE) {
						if (inID == MouseEvent.MOUSE_PRESSED)
							outID = KeyEvent.KEY_PRESSED;
						else if (inID == MouseEvent.MOUSE_RELEASED)
							outID = KeyEvent.KEY_RELEASED;
					}
					
					// Only fire a new event if something was altered
					if ( outCode != inCode || outID != inID || inType != InputConfig.TYPE_KEY ) {
						// Dispatch a new KeyEvent
						manager.dispatchEvent(new HandledKeyEvent(in.getComponent(), outID, in.getWhen(), in.getModifiers(), outCode, (char)outCode));
						
						// Consume the original InputEvent
						in.consume();
					}
				} else if (outType == InputConfig.TYPE_MOUSE) {
					// If the original event was a KeyEvent...
					if (inType == InputConfig.TYPE_KEY) {
						// Get the position of the mouse from the component in which the
						// KeyEvent takes place.
						Point p = ((KeyEvent)in).getComponent().getMousePosition();
						if (p != null) {
							outX += p.x;
							outY += p.y;
						}
						
						// Convert the event ID from the KeyEvent Value to the MouseEvent value.
						if (inID == KeyEvent.KEY_PRESSED)
							outID = MouseEvent.MOUSE_PRESSED;
						else if (inID == KeyEvent.KEY_RELEASED)
							outID = MouseEvent.MOUSE_RELEASED;
					} else {
						// Add the absolute position of the MouseEvent to the relative position
						// given by the InputManager.
						outX += ((MouseEvent)in).getX();
						outY += ((MouseEvent)in).getY();
					}

					// Only fire a new event if something was altered
					// If there are any changes to the final event...
					if ( outCode != inCode || outID != inID || inType != InputConfig.TYPE_MOUSE ||
							outX != ((MouseEvent)in).getX() || outY != ((MouseEvent)in).getY() ) {
						// Dispatch a new MouseEvent
						manager.dispatchEvent(new HandledMouseEvent(in.getComponent(), outID, in.getWhen(), in.getModifiers(), outX, outY, 1, false, outCode));
						
						// Consume the original InputEvent
						in.consume();
					}
				} else
					in.consume(); // If we're ending with an undefined event, always consume
			}
		}
	}

	private static int getStandardizedKeycode(KeyEvent e) {
		int code = e.getKeyCode();
		if (code != 0)
			return code;
		return KeyEvent.getExtendedKeyCodeForChar(e.getKeyChar());
	}

	private static final int whitelist[] = getWhiteList();
	private static int[] getWhiteList() {
		int whitelist[] = {
				KeyEvent.VK_ESCAPE,
				KeyEvent.VK_F1,
				KeyEvent.VK_F2,
				KeyEvent.VK_F3,
				KeyEvent.VK_F4,
				KeyEvent.VK_F5,
				KeyEvent.VK_F6,
				KeyEvent.VK_F7,
				KeyEvent.VK_F8,
				KeyEvent.VK_F9,
				KeyEvent.VK_F10,
				KeyEvent.VK_F11,
				KeyEvent.VK_F12,
				KeyEvent.VK_CONTROL,
				KeyEvent.VK_PRINTSCREEN,
				KeyEvent.VK_INSERT,
				KeyEvent.VK_HOME,
				KeyEvent.VK_PAGE_UP,
				KeyEvent.VK_PAGE_DOWN,
				KeyEvent.VK_END,
				KeyEvent.VK_PAUSE,
				KeyEvent.VK_CLEAR
		};
		Arrays.sort(whitelist);
		return whitelist;
	}

	public static boolean isWhitelisted(int type, int code) {
		if (type == InputConfig.TYPE_KEY) {
			for (int i = 0; i < whitelist.length; i++) {
				if (whitelist[i] == code)
					return true;
				if (whitelist[i] > code)
					return false;
			}
			return false;
		}
		if (type == InputConfig.TYPE_MOUSE)
			return !(code == MouseEvent.BUTTON1 || code == MouseEvent.BUTTON3);
		return false;
	}
}