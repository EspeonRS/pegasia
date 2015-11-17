package org.pegasia.api.runescape;

import java.awt.image.BufferedImage;

import javax.swing.ImageIcon;

import org.pegasia.util.FileUtils;

public enum Skill {
	OVERALL,
	ATTACK,
	DEFENCE,
	STRENGTH,
	HITPOINTS,
	RANGED,
	PRAYER,
	MAGIC,
	COOKING,
	WOODCUTTING,
	FLETCHING,
	FISHING,
	FIREMAKING,
	CRAFTING,
	SMITHING,
	MINING,
	HERBLORE,
	AGILITY,
	THIEVING,
	SLAYER,
	FARMING,
	RUNECRAFT,
	HUNTER,
	CONSTRUCTION,
	SAILING;
	
	private static BufferedImage images = null;
	private ImageIcon icon = null;
	
	public ImageIcon getIcon() {
		if (icon == null) {
			if (images == null)
				images = FileUtils.getBufferedImage("resources/runescape/skills.gif");
			if (images != null)
				icon = new ImageIcon(images.getSubimage(ordinal()%9*25, ordinal()/9*25, 25, 25));
		}
		return icon;
	}
	
	//Cite: http://forum.tip.it/topic/263459-runescape-experience-formula/
	public static int levelToExp(int level) {
		int exp = 0;
		for (int i = 1; i < level; i++)
			exp += i + 300 * Math.pow(2, i / 7.0);
		return exp / 4;
	}
	
	public static int expToLevel(int experience) {
		int level;
		long exp = 0;
		for (level = 1; exp / 4 <= experience; level++)
			exp += level + 300 * Math.pow(2, level / 7.0);
		return level - 1;
	}
}
