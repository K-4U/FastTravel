package k4unl.minecraft.fastTravel.commands.arguments;

import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;

import k4unl.minecraft.fastTravel.lib.Users;
import net.minecraft.command.CommandSource;
import net.minecraft.command.ISuggestionProvider;
import net.minecraft.util.text.TranslationTextComponent;

/**
 * @author Koen Beckers (K-4U)
 */
public class PrivateLocationArgument implements ArgumentType<String> {

	public static final DynamicCommandExceptionType LOCATION_INVALID = new DynamicCommandExceptionType((p_208659_0_) -> {
		return new TranslationTextComponent("argument.location.invalid", p_208659_0_);
	});
	private static final Collection<String> EXAMPLES = Arrays.asList("home", "spawn");

	public static PrivateLocationArgument location() {
		return new PrivateLocationArgument();
	}

	public static String getLocation(CommandContext<CommandSource> context, String name) throws CommandSyntaxException {
		String playerName = context.getSource().getEntity().getName().getUnformattedComponentText();
		String locationName = context.getArgument(name, String.class);
		if (Users.getUserByName(playerName).getLocations().containsKey(locationName)) {
			return locationName;
		}
		throw LOCATION_INVALID.create(locationName);
	}

	@Override
	public String parse(StringReader reader) {
		return reader.readUnquotedString();
	}

	@Override
	public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
		if (context.getSource() instanceof CommandSource) {
			String playerName = ((CommandSource) context.getSource()).getEntity().getName().getUnformattedComponentText();
			return ISuggestionProvider.suggest(Users.getUserByName(playerName).getLocations().keySet(), builder);
		}
		return ISuggestionProvider.suggest(new String[]{}, builder);
	}

	@Override
	public Collection<String> getExamples() {
		return EXAMPLES;
	}
}
