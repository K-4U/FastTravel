package k4unl.minecraft.fastTravel;


import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.event.*;
import k4unl.minecraft.fastTravel.commands.Commands;
import k4unl.minecraft.fastTravel.lib.Log;
import k4unl.minecraft.fastTravel.lib.Users;
import k4unl.minecraft.fastTravel.lib.config.FastTravelConfig;
import k4unl.minecraft.fastTravel.lib.config.ModInfo;
import k4unl.minecraft.k4lib.lib.Location;
import k4unl.minecraft.k4lib.lib.config.ConfigHandler;
import net.minecraftforge.common.DimensionManager;

import java.io.*;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

@Mod(
  modid = ModInfo.ID,
  name = ModInfo.NAME,
  version = ModInfo.VERSION,
  acceptableRemoteVersions = "*"
)

public class FastTravel {
    @Mod.Instance(value = ModInfo.ID)
    public static k4unl.minecraft.fastTravel.FastTravel instance;

    private ConfigHandler fastTravelConfigHandler = new ConfigHandler();

    public Map<String, Location> locations = new HashMap<String, Location>();

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {

        Log.init();
        Users.init();
        FastTravelConfig.INSTANCE.init();
        fastTravelConfigHandler.init(FastTravelConfig.INSTANCE, event.getSuggestedConfigurationFile());
        locations = new HashMap<String, Location>();
    }

    @Mod.EventHandler
    public void load(FMLInitializationEvent event) {


    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event) {

    }

    @Mod.EventHandler
    public void onServerStart(FMLServerStartingEvent event) {

        Commands.init(event);
    }

    @Mod.EventHandler
    public void serverStart(FMLServerStartingEvent event) {

        Users.readFromFile(DimensionManager.getCurrentSaveRootDirectory());
        readLocationsFile(DimensionManager.getCurrentSaveRootDirectory());
    }

    @Mod.EventHandler
    public void serverStop(FMLServerStoppingEvent event) {

        Users.saveToFile(DimensionManager.getCurrentSaveRootDirectory());
        readLocationsFile(DimensionManager.getCurrentSaveRootDirectory());

    }

    public void readLocationsFile(File dir) {

        locations.clear();
        if (dir != null) {
            Gson gson = new Gson();
            String p = dir.getAbsolutePath();
            p += "/fasttravel.locations.json";
            File f = new File(p);
            if (!f.exists()) {
                try {
                    f.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            try {
                FileInputStream ipStream = new FileInputStream(f);
                InputStreamReader reader = new InputStreamReader(ipStream);
                BufferedReader bReader = new BufferedReader(reader);
                String json = bReader.readLine();
                reader.close();
                ipStream.close();
                bReader.close();

                Type myTypeMap = new TypeToken<Map<String, Location>>() {
                }.getType();
                locations = gson.fromJson(json, myTypeMap);
                if (locations == null) {
                    locations = new HashMap<String, Location>();
                }

                //Log.info("Read from file: " + json);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }


        }
    }

    public void saveLocationsToFile(File dir) {

        if (dir != null) {
            Gson gson = new Gson();
            String json = gson.toJson(locations);
            //Log.info("Saving: " + json);
            String p = dir.getAbsolutePath();
            p += "/fasttravel.locations.json";
            File f = new File(p);
            if (!f.exists()) {
                try {
                    f.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            try {
                PrintWriter opStream = new PrintWriter(f);
                opStream.write(json);
                opStream.flush();
                opStream.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }
}
