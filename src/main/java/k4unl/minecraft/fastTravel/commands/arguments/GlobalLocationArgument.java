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

import k4unl.minecraft.fastTravel.lib.Locations;
import net.minecraft.command.CommandSource;
import net.minecraft.command.ISuggestionProvider;
import net.minecraft.util.text.TranslationTextComponent;

/**
 * @author Koen Beckers (K-4U)
 */
public class GlobalLocationArgument implements ArgumentType<String> {

	public static final DynamicCommandExceptionType LOCATION_INVALID = new DynamicCommandExceptionType((p_208659_0_) -> {
		return new TranslationTextComponent("argument.location.invalid", p_208659_0_);
	});
	private static final Collection<String> EXAMPLES = Arrays.asList("home", "spawn");

	public static GlobalLocationArgument location() {
		return new GlobalLocationArgument();
	}

	public static String getLocation(CommandContext<CommandSource> context, String name) {
		return context.getArgument(name, String.class);
	}

	@Override
	public String parse(StringReader reader) throws CommandSyntaxException {
		String s = reader.readUnquotedString();
		if (!Locations.getLocationNames().contains(s)) {
			throw LOCATION_INVALID.create(s);
		}
		return s;
	}

	@Override
	public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
		return ISuggestionProvider.suggest(Locations.getLocationNames(), builder);
	}

	@Override
	public Collection<String> getExamples() {
		return EXAMPLES;
	}
}
