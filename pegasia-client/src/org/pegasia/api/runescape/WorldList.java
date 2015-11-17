package org.pegasia.api.runescape;

import java.io.IOException;
import java.util.Collection;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.pegasia.util.net.Webpage;

public class WorldList {
	private static TreeMap<Integer, World> worlds = null;
	private static int totalPopulation = 0;
	
	public static void load() {
		try {
			Webpage.WORLD_LIST.refreshContent();
			String lines = Webpage.WORLD_LIST.getContent();
			worlds = new TreeMap<Integer, World>();
			
			// Search for the line that states how many players are online
			Matcher matcher = Pattern.compile("There\\sare\\scurrently\\s(\\d+)\\speople\\splaying", Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE).matcher(lines);
			if (matcher.find())
				totalPopulation = Integer.parseInt(matcher.group(1));

			// Search for all of the functions describing various world details
			matcher = Pattern.compile("\\s+e\\(([^//)]+)\\);", Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE).matcher(lines);
			while (matcher.find()) {
				// Split the string up into its arguments
				String param[] = matcher.group(1).split(",");
				if (param.length < 10)
					continue;
				
				// Convert the split string into a World object
				World newWorld = new World(
						Integer.parseInt(param[0]),
						param[1].equals("true"),
						param[3].replaceAll("\"", ""),
						Integer.parseInt(param[4]),
						param[5].replaceAll("\"", ""),
						param[6].replaceAll("\"", ""),
						param[8].replaceAll("\"", ""),
						param[9].equals("\"pvpWorld\"") );
				
				// If the Map already contains this world, update its population
				if (worlds.containsKey(newWorld.world))
					worlds.get(newWorld.world).population = newWorld.population;
				// Else, add the world to the Map
				else
					worlds.put(newWorld.world, newWorld);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static Collection<World> getWorldList() {
		if (worlds == null)
			load();
		
		return worlds.values();
	}
	
	public static World getWorld(int worldNumber) {
		if (worlds == null)
			load();
		
		return worlds.get(worldNumber);
	}
	
	public static int getTotalPopulation() {
		if (worlds == null)
			load();
		
		return totalPopulation;
	}
}
