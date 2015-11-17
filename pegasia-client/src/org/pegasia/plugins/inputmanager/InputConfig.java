package org.pegasia.plugins.inputmanager;

import java.awt.event.KeyEvent;

public enum InputConfig {
	INVENTORY		(KeyEvent.VK_ESCAPE,	KeyEvent.VK_F1),
	COMBAT_OPTIONS	(KeyEvent.VK_F1,		KeyEvent.VK_F5),
	STATS			(KeyEvent.VK_F2,		-1),
	QUEST_LIST		(KeyEvent.VK_F3,		-1),
	WORN_EQUIPMENT	(KeyEvent.VK_F4,		KeyEvent.VK_F2),
	PRAYER			(KeyEvent.VK_F5,		KeyEvent.VK_F3),
	MAGIC			(KeyEvent.VK_F6,		KeyEvent.VK_F4),
	CLAN_CHAT		(KeyEvent.VK_F7,		-1),
	FRIENDS_LIST	(KeyEvent.VK_F8,		-1),
	IGNORE_LIST		(KeyEvent.VK_F9,		-1),
	OPTIONS			(KeyEvent.VK_F10,		-1),
	EMOTES			(KeyEvent.VK_F11,		-1),
	MUSIC_PLAYER	(KeyEvent.VK_F12,		-1),
	QUICK_RUN		(KeyEvent.VK_CONTROL,	KeyEvent.VK_CONTROL),
	;

	public static final int TYPE_UNDEFINED = -1, TYPE_KEY = 0, TYPE_MOUSE = 1;
	
	public final String name, formattedName;
	public final int osrsDefaultCode, preeocDefaultCode;
	public int type, code;

	private InputConfig(int osrsDefaultCode, int preeocDefaultCode) {
		this.osrsDefaultCode = osrsDefaultCode;
		this.preeocDefaultCode = preeocDefaultCode;
		type = TYPE_KEY;
		code = osrsDefaultCode;
		formattedName = name().toLowerCase().replaceAll("_", "-");
		
		StringBuilder sb = new StringBuilder();
		boolean caps = true;
		for (int i = 0; i < formattedName.length(); i++ ) {
			char c = formattedName.charAt(i);
			if (c == '-') {
				c = ' ';
				caps = true;
			} else if (caps) {
				c = Character.toTitleCase(c);
				caps = false;
			}
			sb.append(c);
		}
		name = sb.toString();
	}

	public String getKey() {
		return toString().toLowerCase();
	}

	public String getDefaultValue() {
		return TYPE_KEY + ":" + osrsDefaultCode;
	}

	public String getValue() {
		return type + ":" + code;
	}

	public void setValue(String value) {
		if ( value == null ) {
			type = TYPE_UNDEFINED;
			return;
		}

		String split[] = value.split(":");
		if (split == null || split.length < 2) {
			type = TYPE_UNDEFINED;
			return;
		}

		type = Integer.valueOf(split[0]);
		code = Integer.valueOf(split[1]);
	}
}
