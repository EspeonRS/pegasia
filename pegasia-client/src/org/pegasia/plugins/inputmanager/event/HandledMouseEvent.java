package org.pegasia.plugins.inputmanager.event;

import java.awt.Component;
import java.awt.event.MouseEvent;

public class HandledMouseEvent extends MouseEvent implements HandledInputEvent {
	public HandledMouseEvent(Component source, int id, long when, int modifiers, int x, int y, int clickCount, boolean popupTrigger, int button) {
		super(source, id, when, modifiers, x, y, clickCount, popupTrigger, button);
	}
}
