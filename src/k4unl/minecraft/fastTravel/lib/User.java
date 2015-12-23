package k4unl.minecraft.fastTravel.lib;

import k4unl.minecraft.k4lib.lib.Location;

import java.util.HashMap;
import java.util.Map;

public class User {

    private String userName;
    private Map<String, Location> locations;

	public User(String _username) {
		userName = _username;
        locations = new HashMap<String, Location>();
	}

	public String getUserName() {
		return userName;
	}

    public Map<String, Location> getLocations() {
        return locations;
    }

    public void addLocation(String key, Location location) {
        locations.put(key, location);
    }

    public void removeLocation(String key) {
        locations.remove(key);
    }
}
