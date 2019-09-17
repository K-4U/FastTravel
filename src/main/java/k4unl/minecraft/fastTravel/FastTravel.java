package k4unl.minecraft.fastTravel;


import k4unl.minecraft.fastTravel.commands.Commands;
import k4unl.minecraft.fastTravel.lib.Locations;
import k4unl.minecraft.fastTravel.lib.Log;
import k4unl.minecraft.fastTravel.lib.Users;
import k4unl.minecraft.fastTravel.lib.config.FastTravelConfig;
import k4unl.minecraft.fastTravel.lib.config.ModInfo;
import k4unl.minecraft.fastTravel.worlddata.FastTravelData;
import k4unl.minecraft.k4lib.commands.CommandsRegistry;
import k4unl.minecraft.k4lib.lib.config.Config;
import net.minecraft.server.dedicated.DedicatedServer;
import net.minecraft.world.dimension.DimensionType;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.server.FMLServerStartedEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(ModInfo.ID)
public class FastTravel {
	public static FastTravel instance;

	public FastTravel() {
		instance = this;

		Log.init();
		Users.init();
		Locations.init();

		Config config = new FastTravelConfig();
		config.load(ModInfo.ID);

		FMLJavaModLoadingContext.get().getModEventBus().register(this);
		MinecraftForge.EVENT_BUS.addListener(this::onServerStart);
	}

	@SubscribeEvent
	public void onServerStart(FMLServerStartedEvent event) {

		FastTravelData fastTravelData = FastTravelData.get(event.getServer().getWorld(DimensionType.OVERWORLD));

		boolean b = event.getServer() instanceof DedicatedServer;
		CommandsRegistry commandsRegistry = new Commands(b, event.getServer().getCommandManager().getDispatcher());
	}

}
