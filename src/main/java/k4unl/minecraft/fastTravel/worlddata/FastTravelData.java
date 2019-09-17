package k4unl.minecraft.fastTravel.worlddata;

import java.util.Map;

import k4unl.minecraft.fastTravel.lib.Locations;
import k4unl.minecraft.fastTravel.lib.User;
import k4unl.minecraft.fastTravel.lib.Users;
import k4unl.minecraft.fastTravel.lib.config.ModInfo;
import k4unl.minecraft.k4lib.lib.Location;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.storage.DimensionSavedDataManager;
import net.minecraft.world.storage.WorldSavedData;

/**
 * @author Koen Beckers (K-4U)
 */
public class FastTravelData extends WorldSavedData {
	public FastTravelData() {
		super(ModInfo.ID);
	}

	public static FastTravelData get(ServerWorld world) {
		DimensionSavedDataManager storage = world.getSavedData();
		return storage.getOrCreate(FastTravelData::new, ModInfo.ID);
	}

	@Override
	public void read(CompoundNBT nbt) {
		ListNBT users = nbt.getList("users", 10);
		for (int i = 0; i < users.size(); ++i) {
			CompoundNBT compoundnbt = users.getCompound(i);
			User user = new User(compoundnbt);
			Users.addUser(user);
		}

		ListNBT locations = nbt.getList("locations", 10);
		for (int i = 0; i < locations.size(); i++) {
			CompoundNBT compoundNBT = locations.getCompound(i);
			Location location = new Location(compoundNBT);
			String name = compoundNBT.getString("name");
			Locations.getLocationMap().put(name, location);
		}
	}

	@Override
	public boolean isDirty() {
		return true;
	}

	@Override
	public CompoundNBT write(CompoundNBT compound) {

		ListNBT users = new ListNBT();
		for (User user : Users.getUserList()) {
			CompoundNBT nbt = user.save();
			users.add(nbt);
		}
		compound.put("users", users);

		ListNBT locations = new ListNBT();
		for (Map.Entry<String, Location> s : Locations.getLocationMap().entrySet()) {
			CompoundNBT nbt = s.getValue().getNBT();
			nbt.putString("name", s.getKey());
			locations.add(nbt);
		}
		compound.put("locations", locations);

		return compound;
	}
}
