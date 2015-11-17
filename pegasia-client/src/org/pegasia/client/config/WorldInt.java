package org.pegasia.client.config;

import org.pegasia.api.runescape.World;

public class WorldInt {
	int value;
	World world;

	WorldInt(World world) {
		this.world = world;
		this.value = world.world;
	}

	WorldInt(int value) {
		this.world = null;
		this.value = value;
	}

	@Override public String toString() {
		return Integer.toString(value);
	}
}
