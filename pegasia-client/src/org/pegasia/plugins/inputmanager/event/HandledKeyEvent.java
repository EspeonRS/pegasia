package org.pegasia.plugins.inputmanager.event;

import java.awt.Component;
import java.awt.event.KeyEvent;

public class HandledKeyEvent extends KeyEvent implements HandledInputEvent {
	public HandledKeyEvent(Component source, int id, long when, int modifiers, int keyCode, char keyChar) {
		super(source, id, when, modifiers, keyCode, keyChar);
	}
}
