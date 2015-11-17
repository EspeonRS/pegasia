package org.pegasia.api.runescape;

import java.io.IOException;
import java.net.URL;
import java.util.EnumMap;

import org.pegasia.util.net.WebUtil;

public class SkillMap extends EnumMap<Skill, int[]> {

	public SkillMap() {
		super(Skill.class);
		for (Skill skill : Skill.values())
			put(skill, new int[3]);
	}

	public SkillMap(SkillMap s) {
		super(Skill.class);
		putAll(s);
	}

	@Override
	public SkillMap clone() {
		return new SkillMap(this);
	}

	@Override
	public void clear() {
		for (Skill skill : Skill.values())
			remove(skill);
	}

	public void remove(Skill skill) {
		int[] data = get(skill);
		data[0] = -1;
		data[1] = -1;
		data[2] = -1;
	}

	@Override
	public int[] remove(Object skill) {
		throw new UnsupportedOperationException();
	}

	public int getRank(Skill skill) {
		return get(skill)[0];
	}

	public int getLevel(Skill skill) {
		return get(skill)[1];
	}

	public int getXP(Skill skill) {
		return get(skill)[2];
	}

	public double getCombatLevel() {
		return getCombatLevel(
				getLevel(Skill.ATTACK),
				getLevel(Skill.STRENGTH),
				getLevel(Skill.DEFENCE),
				getLevel(Skill.RANGED),
				getLevel(Skill.MAGIC),
				getLevel(Skill.HITPOINTS),
				getLevel(Skill.PRAYER));
	}

	public void loadName(String name) throws IOException {
		name = name.replaceAll("[^" + RuneScape.PLAYER_NAME_CHARACTERS + "]", "");
		URL url = new URL("http://services.runescape.com/m=hiscore_oldschool/index_lite.ws?player=" + name);
		parseString(WebUtil.webToString(url));
	}

	public void parseString(String str) {
		clear();

		Skill[] skills = Skill.values();
		String[] split = str.split("\n");
		for (int i = 0; i < skills.length && i < split.length; i++) {
			String[] subSplit = split[i].split(",");
			int[] data = get(skills[i]);

			for (int j = 0; j < data.length && j < subSplit.length; j++)
				try {
					data[j] = Integer.parseInt(subSplit[j]);
				} catch (NumberFormatException e) {}
		}
	}

	// http://forum.tip.it/topic/199687-runescape-formulas-revealed/
	public static double getCombatLevel(int attack, int strength, int defence, int ranged, int magic, int hitpoints, int prayer) {
		double base = 0.25 * (defence + hitpoints + prayer/2);
		int combat = (int) (1.5 * Math.max(ranged, magic));
		combat = Math.max(combat, attack + strength);

		return base + 0.325 * combat;
	}
}
