package k4unl.minecraft.fastTravel.commands;


import net.minecraftforge.fml.common.event.FMLServerStartingEvent;

public class Commands {
    public static void init(FMLServerStartingEvent event) {

        event.registerServerCommand(new CommandFastTravel());
        event.registerServerCommand(new CommandTravel());
    }
}
