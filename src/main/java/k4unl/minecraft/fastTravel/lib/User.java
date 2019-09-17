package k4unl.minecraft.fastTravel.lib;

import java.util.HashMap;
import java.util.Map;

import k4unl.minecraft.k4lib.lib.Location;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;

public class User {

	private String userName;
	private Map<String, Location> locations;

	public User(String _username) {
		userName = _username;
		locations = new HashMap<>();
	}

	public User(CompoundNBT compoundnbt) {
		this.userName = compoundnbt.getString("name");
		this.locations = new HashMap<>();
		ListNBT locations = compoundnbt.getList("locations", 10);
		for (int i = 0, locationsSize = locations.size(); i < locationsSize; i++) {
			CompoundNBT locationNbt = locations.getCompound(i);
			String name = locationNbt.getString("name");
			Location location = new Location(locationNbt.getCompound("location"));
			this.locations.put(name, location);
		}
	}


	public CompoundNBT save() {
		CompoundNBT nbt = new CompoundNBT();
		nbt.putString("name", this.userName);
		ListNBT locationList = new ListNBT();
		for (Map.Entry<String, Location> stringLocationEntry : locations.entrySet()) {
			CompoundNBT locationNbt = new CompoundNBT();
			locationNbt.putString("name", stringLocationEntry.getKey());
			locationNbt.put("location", stringLocationEntry.getValue().getNBT());
		}
		return nbt;
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
