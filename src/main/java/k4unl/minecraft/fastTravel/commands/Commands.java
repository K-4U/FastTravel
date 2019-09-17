package k4unl.minecraft.fastTravel.commands;


import com.mojang.brigadier.CommandDispatcher;

import k4unl.minecraft.fastTravel.commands.arguments.GlobalLocationArgument;
import k4unl.minecraft.fastTravel.commands.arguments.PrivateLocationArgument;
import k4unl.minecraft.fastTravel.commands.arguments.TeleportLocationArgument;
import k4unl.minecraft.fastTravel.commands.impl.CommandFastTravel;
import k4unl.minecraft.fastTravel.commands.impl.CommandTravel;
import k4unl.minecraft.k4lib.commands.CommandsRegistry;
import net.minecraft.command.CommandSource;
import net.minecraft.command.arguments.ArgumentSerializer;
import net.minecraft.command.arguments.ArgumentTypes;

public class Commands extends CommandsRegistry {

	public Commands(boolean isDedicatedServer, CommandDispatcher<CommandSource> dispatcher) {
		try {
			ArgumentTypes.register("globallocations", GlobalLocationArgument.class, new ArgumentSerializer<>(GlobalLocationArgument::location));
			ArgumentTypes.register("privatelocations", PrivateLocationArgument.class, new ArgumentSerializer<>(PrivateLocationArgument::location));
			ArgumentTypes.register("teleportlocations", TeleportLocationArgument.class, new ArgumentSerializer<>(TeleportLocationArgument::location));

		} catch (IllegalArgumentException e) {
			//Already registered these.
		}


		register(dispatcher, new CommandFastTravel());
		register(dispatcher, new CommandTravel());
	}
}
