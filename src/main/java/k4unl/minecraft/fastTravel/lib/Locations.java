package k4unl.minecraft.fastTravel.lib;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import k4unl.minecraft.k4lib.lib.Location;

public class Locations {

	private static Map<String, Location> locationMap;

	public static void init() {
		locationMap = new HashMap<>();
	}

	public static Map<String, Location> getLocationMap() {
		return locationMap;
	}

	public static Location put(String tag, Location location) {
		return locationMap.put(tag, location);
	}

	public static void remove(String tag) {
		locationMap.remove(tag);
	}

	public static Set<String> getLocationNames() {
		return locationMap.keySet();
	}
}
