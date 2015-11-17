
/* e(302,true,0,"oldschool2",1340,"United Kingdom","GB","Old School 2","Trade - Falador","");
 * e(338,true,0,"oldschool38",234,"United States","US","Old School 38","-","");
 * e(337,true,0,"oldschool37",160,"United States","US","Old School 37","PVP World - High Risk","pvpWorld");
 * 
 * world, members, , domain, population, , location, , activity, dangerous
 * 
 * "GB" "US" "DE"
 * 
 * 
 * <Star> World		|	Players		|	<Flag> Location		|	Activity
 * 
 */


package org.pegasia.api.runescape;

import javax.swing.ImageIcon;

import org.pegasia.util.FileUtils;

public class World implements Comparable<World> {
	public static final ImageIcon MEMBERS_ICON = new ImageIcon(FileUtils.getBufferedImage("resources/world/members.gif"), "Members");
	public static final ImageIcon FREE_ICON = new ImageIcon(FileUtils.getBufferedImage("resources/world/free.gif"), "Free");
	
	public static final ImageIcon AU_FLAG = new ImageIcon(FileUtils.getBufferedImage("resources/world/au.gif"), "AU");
	public static final ImageIcon GER_FLAG = new ImageIcon(FileUtils.getBufferedImage("resources/world/ger.gif"), "GER");
	public static final ImageIcon UK_FLAG = new ImageIcon(FileUtils.getBufferedImage("resources/world/uk.gif"), "UK");
	public static final ImageIcon US_FLAG = new ImageIcon(FileUtils.getBufferedImage("resources/world/us.gif"), "US");
	
	public final int world;
	public final boolean members, dangerous;
	public final String domain, location, activity;
	public final ImageIcon flag;
	
	public int population;
	
	public World(int world, boolean members, String domain, int population, String location, String flag, String activity, boolean dangerous) {
		this.world = world;
		this.members = members;
		this.domain = domain;
		this.population = population;
		this.location = location;
		this.activity = activity;
		this.dangerous = dangerous;
		
		switch (flag.toUpperCase()) {
		case "AU": this.flag = AU_FLAG; break;
		case "DE": this.flag = GER_FLAG; break;
		case "GB": this.flag = UK_FLAG; break;
		case "US": this.flag = US_FLAG; break;
		default: this.flag = null;
		}
	}

	@Override public boolean equals(Object other) {
		if (other instanceof World)
			return world == ((World)other).world;
		return false;
	}
	
	@Override public int compareTo(World other) {
		return world - ((World)other).world;
	}
	
	@Override public String toString() {
		return "World " + world + " (" + population + ')';
	}
}
