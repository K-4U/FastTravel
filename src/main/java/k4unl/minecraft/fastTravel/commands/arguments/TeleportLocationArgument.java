package k4unl.minecraft.fastTravel.commands.arguments;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;

import k4unl.minecraft.fastTravel.lib.Locations;
import k4unl.minecraft.fastTravel.lib.Users;
import k4unl.minecraft.k4lib.lib.Location;
import net.minecraft.client.multiplayer.ClientSuggestionProvider;
import net.minecraft.command.CommandSource;
import net.minecraft.command.ISuggestionProvider;
import net.minecraft.util.text.TranslationTextComponent;

/**
 * @author Koen Beckers (K-4U)
 */
public class TeleportLocationArgument implements ArgumentType<String> {

	public static final DynamicCommandExceptionType LOCATION_INVALID = new DynamicCommandExceptionType((p_208659_0_) -> {
		return new TranslationTextComponent("argument.location.invalid", p_208659_0_);
	});
	private static final Collection<String> EXAMPLES = Arrays.asList("home", "spawn");

	public static TeleportLocationArgument location() {
		return new TeleportLocationArgument();
	}

	public static Location getLocation(CommandContext<CommandSource> context, String name) throws CommandSyntaxException {
		String playerName = context.getSource().getEntity().getName().getUnformattedComponentText();
		String locationName = context.getArgument(name, String.class);
		if (Users.getUserByName(playerName).getLocations().containsKey(locationName)) {
			return Users.getUserByName(playerName).getLocations().get(locationName);
		} else if (Locations.getLocationMap().containsKey(locationName)) {
			return Locations.getLocationMap().get(locationName);
		}

		throw LOCATION_INVALID.create(locationName);
	}

	@Override
	public String parse(StringReader reader) {
		return reader.readUnquotedString();
	}

	@Override
	public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
		//Do we need to ask the server?
		if (context.getSource() instanceof ClientSuggestionProvider) {
			return ((ClientSuggestionProvider) context.getSource()).getSuggestionsFromServer((CommandContext<ISuggestionProvider>) context, builder);
		}
		String playerName = ((CommandSource) context.getSource()).getEntity().getName().getUnformattedComponentText();

		List<String> strings = new ArrayList<>(Locations.getLocationNames());
		if (!playerName.equals("")) {
			strings.addAll(Users.getUserByName(playerName).getLocations().keySet());
		}
		return ISuggestionProvider.suggest(strings, builder);
	}

	@Override
	public Collection<String> getExamples() {
		return EXAMPLES;
	}
}
