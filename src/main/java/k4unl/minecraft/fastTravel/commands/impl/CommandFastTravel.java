package k4unl.minecraft.fastTravel.commands.impl;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import k4unl.minecraft.fastTravel.commands.arguments.GlobalLocationArgument;
import k4unl.minecraft.fastTravel.commands.arguments.PrivateLocationArgument;
import k4unl.minecraft.fastTravel.lib.Locations;
import k4unl.minecraft.fastTravel.lib.Users;
import k4unl.minecraft.fastTravel.lib.config.FastTravelConfig;
import k4unl.minecraft.k4lib.commands.Command;
import k4unl.minecraft.k4lib.lib.Functions;
import k4unl.minecraft.k4lib.lib.Location;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.StringNBT;
import net.minecraft.util.Hand;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraft.util.text.event.HoverEvent;

public class CommandFastTravel implements Command {


	@Override
	public void register(LiteralArgumentBuilder<CommandSource> argumentBuilder) {
		LiteralArgumentBuilder<CommandSource> setCommand = Commands.literal("set");
		LiteralArgumentBuilder<CommandSource> delCommand = Commands.literal("del");
		LiteralArgumentBuilder<CommandSource> listCommand = Commands.literal("list");
		if (FastTravelConfig.masterListForOpOnly.get()) {
			argumentBuilder.then(
					setCommand.then(Commands.literal("global").requires(this::isPlayerOpped)
							.then(Commands.argument("name", StringArgumentType.word()).executes(this::setGlobalLocation)
							)
					)
			);
			//TODO: Replace StringArgumentType with GlobalListArgumentType
			argumentBuilder.then(
					delCommand.then(Commands.literal("global").requires(this::isPlayerOpped)
							.then(Commands.argument("name", GlobalLocationArgument.location()).executes(this::delGlobalLocation)
							)
					)
			);
		}
		if (FastTravelConfig.enablePrivateList.get()) {
			argumentBuilder.then(setCommand.then(Commands.argument("name", StringArgumentType.word()).executes(this::setPrivateLocation)));
			argumentBuilder.then(delCommand.then(Commands.argument("name", PrivateLocationArgument.location()).executes(this::delPrivateLocation)));
			argumentBuilder.then(listCommand.then(Commands.literal("private").executes(this::listPrivateList)));
		}
		argumentBuilder.then(listCommand.executes(this::listGlobalList));

		if (FastTravelConfig.allowBook.get()) {
			argumentBuilder.then(Commands.literal("book").executes(this::spawnBook));
		}

	}

	private int spawnBook(CommandContext<CommandSource> context) throws CommandSyntaxException {
		//Search the players current held item for the book.
		ServerPlayerEntity player = context.getSource().asPlayer();
		boolean foundBook = false;
		ItemStack book = new ItemStack(Items.WRITTEN_BOOK, 1);
		CompoundNBT tCompound = new CompoundNBT();
		if (player.getHeldItem(Hand.MAIN_HAND) != null) {
			if (player.getHeldItem(Hand.MAIN_HAND).getItem() == Items.WRITTEN_BOOK) {

				//Find the NBT:
				tCompound = player.getHeldItem(Hand.MAIN_HAND).getTag();
				if (tCompound.getString("author").equals("K4Unl") && tCompound.getString("title").equals("Waypoints")) {
					//Oh yeah!
					book = player.getHeldItem(Hand.MAIN_HAND).copy();
					foundBook = true;
				}
			}
		}

		tCompound.putString("author", "K4Unl");
		tCompound.putString("title", "Waypoints");
		ListNBT tList = new ListNBT();

		SimpleDateFormat dateFormat = new SimpleDateFormat(("HH:mm dd-MM-yyyy"));
		ITextComponent page1 = new StringTextComponent("");

		page1.appendText(TextFormatting.BOLD + "K4Unl's \n");
		page1.appendText(TextFormatting.BOLD + "Fast Travel Guide\n\n");
		page1.appendText(TextFormatting.DARK_GREEN + "This book lists all the \n");
		page1.appendText(TextFormatting.DARK_GREEN + "waypoints as of \n");
		page1.appendText(TextFormatting.DARK_GREEN + "" + TextFormatting.BOLD + dateFormat.format(new Date()) + "\n");
		page1.appendText(TextFormatting.DARK_GREEN + "It will not update\n");
		page1.appendText(TextFormatting.DARK_GREEN + "automatically!\n\n");

		ITextComponent updateLink = new StringTextComponent("Update\n");
		updateLink.getStyle().setColor(TextFormatting.BLUE);
		updateLink.getStyle().setUnderlined(true);
		updateLink.getStyle().setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new StringTextComponent("Click here to update the book!")));
		updateLink.getStyle().setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/fasttravel book"));

		page1.appendSibling(updateLink);

		ITextComponent publicListLink = new StringTextComponent("Public list\n");
		publicListLink.getStyle().setColor(TextFormatting.BLUE);
		publicListLink.getStyle().setUnderlined(true);
		publicListLink.getStyle().setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new StringTextComponent("Click here to go to the public list.")));
		publicListLink.getStyle().setClickEvent(new ClickEvent(ClickEvent.Action.CHANGE_PAGE, "2"));
		page1.appendSibling(publicListLink);

		ITextComponent privateListLink = new StringTextComponent("Private list\n");
		if (FastTravelConfig.enablePrivateList.get()) {
			privateListLink.getStyle().setColor(TextFormatting.BLUE);
			privateListLink.getStyle().setUnderlined(true);
			privateListLink.getStyle().setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new StringTextComponent("Click here to go to your private list.")));
		}

		tList.add(new StringNBT(ITextComponent.Serializer.toJson(page1)));

		HoverEvent backButtonHover = new HoverEvent(HoverEvent.Action.SHOW_TEXT, new StringTextComponent("Back to title"));
		ClickEvent backButtonClick = new ClickEvent(ClickEvent.Action.CHANGE_PAGE, "1");
		StringTextComponent backButton = new StringTextComponent("Back\n");
		backButton.getStyle().setColor(TextFormatting.BLUE);
		backButton.getStyle().setUnderlined(true);
		backButton.getStyle().setHoverEvent(backButtonHover);
		backButton.getStyle().setClickEvent(backButtonClick);

		ITextComponent p = new StringTextComponent("");
		p.appendSibling(backButton);
		int i = 0;
		int page = 2;
		if (Locations.getLocationMap().size() > 0) {
			for (Map.Entry<String, Location> entry : Locations.getLocationMap().entrySet()) {
				i++;
				p = p.appendSibling(parseEntry(entry.getKey()));

				if (i == 13) {
					tList.add(new StringNBT(ITextComponent.Serializer.toJson(p)));
					i = 0;
					p = new StringTextComponent("");
					p.appendSibling(backButton);
					page++;
				}
			}
		} else {
			p.appendSibling(new StringTextComponent(TextFormatting.RESET + "No locations defined yet."));
		}
		if (i < 13) {
			tList.add(new StringNBT(ITextComponent.Serializer.toJson(p)));
			page++;
		}

		if (FastTravelConfig.enablePrivateList.get()) {
			p = new StringTextComponent("");
			p.appendSibling(backButton);
			i = 0;
			if (Users.getUserByName(getSenderName(context.getSource())).getLocations().size() > 0) {
				for (Map.Entry<String, Location> entry : Users.getUserByName(getSenderName(context.getSource())).getLocations().entrySet()) {
					i++;
					p = p.appendSibling(parseEntry(entry.getKey()));

					if (i == 13) {
						tList.add(new StringNBT(ITextComponent.Serializer.toJson(p)));
						i = 0;
						p = new StringTextComponent("");
						p.appendSibling(backButton);
					}
				}
			} else {
				p.appendSibling(new StringTextComponent(TextFormatting.RESET + "No locations defined yet."));
			}
			if (i < 13) {
				tList.add(new StringNBT(ITextComponent.Serializer.toJson(p)));
			}

			privateListLink.getStyle().setClickEvent(new ClickEvent(ClickEvent.Action.CHANGE_PAGE, "" + page));

			page1.appendSibling(privateListLink);
		}

		tList.set(0, new StringNBT(ITextComponent.Serializer.toJson(page1)));

		tCompound.put("pages", tList);

		book.setTag(tCompound);

		if (!foundBook) {
			ItemEntity itemEntity = new ItemEntity(player.getEntityWorld(), player.getPosition().getX(), player.getPosition().getY(), player.getPosition().getZ(), book);
			context.getSource().getWorld().addEntity(itemEntity);
		}
		return 0;
	}

	private int listGlobalList(CommandContext<CommandSource> context) {
		for (Map.Entry<String, Location> entry : Locations.getLocationMap().entrySet()) {
			context.getSource().sendFeedback(new StringTextComponent("- " + entry.getKey()), false);
		}
		return 0;
	}

	private String getSenderName(CommandSource source) {
		return source.getEntity().getName().getUnformattedComponentText();
	}

	private int listPrivateList(CommandContext<CommandSource> context) {
		for (Map.Entry<String, Location> entry : Users.getUserByName(getSenderName(context.getSource())).getLocations().entrySet()) {
			context.getSource().sendFeedback(new StringTextComponent("- " + entry.getKey()), false);
		}
		return 0;
	}

	private int delPrivateLocation(CommandContext<CommandSource> context) throws CommandSyntaxException {
		String tag = PrivateLocationArgument.getLocation(context, "name");
		Users.getUserByName(getSenderName(context.getSource())).removeLocation(tag);
		context.getSource().sendFeedback(new StringTextComponent(TextFormatting.GREEN + "Location removed"), false);
		return 0;
	}

	private int delGlobalLocation(CommandContext<CommandSource> context) {
		String tag = GlobalLocationArgument.getLocation(context, "name");
		Locations.remove(tag);
		context.getSource().sendFeedback(new StringTextComponent(TextFormatting.GREEN + "Location removed"), false);
		return 0;
	}

	private int setGlobalLocation(CommandContext<CommandSource> context) {
		String tag = StringArgumentType.getString(context, "name");
		Locations.put(tag, new Location(context.getSource().getEntity().getPosition()));
		context.getSource().sendFeedback(new StringTextComponent(TextFormatting.GREEN + "Location " + tag + " saved!"), false);
		return 0;
	}

	private int setPrivateLocation(CommandContext<CommandSource> context) {
		String tag = StringArgumentType.getString(context, "name");
		Entity entity = context.getSource().getEntity();
		Users.getUserByName(getSenderName(context.getSource())).addLocation(tag, new Location(entity.getPosition()));
		context.getSource().sendFeedback(new StringTextComponent(TextFormatting.GREEN + "Location " + tag + " saved!"), false);
		return 0;
	}

	private boolean isPlayerOpped(CommandSource source) {
		if (source.getEntity() instanceof ServerPlayerEntity) {
			return Functions.isPlayerOpped(((ServerPlayerEntity) source.getEntity()).getGameProfile());
		} else {
			return true;
		}
	}

	@Override
	public String getName() {

		return "fasttravel";
	}

	@Override
	public boolean canUse(CommandSource commandSource) {
		return true;
	}
/*
	@Override
	public String getUsage(ICommandSender sender) {

		String result = "fasttravel version";
		boolean isOpped = sender.getName().equals("Server") || Functions.isPlayerOpped(((ServerPlayerEntity) sender).getGameProfile());
		if (isOpped) {
			result += "|save|load";
		}
		result += "|list";

		if ((!FastTravelConfig.INSTANCE.getBool("masterListForOpOnly") || isOpped)) {
			result += "|set|del";
		}
		if (FastTravelConfig.INSTANCE.getBool("enablePrivateList")) {
			result += "|listprivate|setprivate|delprivate";
		}
		if (FastTravelConfig.INSTANCE.getBool("allowBookList")) {
			result += "|book";
		}

		return result;
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {

		if (args.length >= 1) {
			List<String> args_ = new ArrayList<>();
			Collections.addAll(args_, args);

			boolean isOpped = sender.getName().equals("Server") || Functions.isPlayerOpped(((ServerPlayerEntity) sender).getGameProfile());

			args_.remove(args[0]);
			String tag = Joiner.on(" ").join(args_);
			if (args[0].toLowerCase().equals("version")) {
				sender.sendMessage(new StringTextComponent("Fast Travel version " + ModInfo.VERSION));
			} else if (args[0].toLowerCase().equals("save")) {
				if (isOpped) {
					Users.saveToFile(DimensionManager.getCurrentSaveRootDirectory());
					FastTravel.instance.saveLocationsToFile(DimensionManager.getCurrentSaveRootDirectory());

					sender.sendMessage(new StringTextComponent("Locations and user settings saved to world dir!"));
				}
			} else if (args[0].toLowerCase().equals("load")) {
				if (isOpped) {
					Users.readFromFile(DimensionManager.getCurrentSaveRootDirectory());
					FastTravel.instance.readLocationsFile(DimensionManager.getCurrentSaveRootDirectory());

					sender.sendMessage(new StringTextComponent("Locations and user settings loaded from world dir!"));
				}
			} else if (args[0].toLowerCase().equals("list")) {
				for (Map.Entry<String, Location> entry : FastTravel.instance.locations.entrySet()) {
					sender.sendMessage(new StringTextComponent("- " + entry.getKey()));
				}
			} else if (args[0].toLowerCase().equals("listprivate")) {
				if (FastTravelConfig.INSTANCE.getBool("enablePrivateList")) {
					for (Map.Entry<String, Location> entry : Users.getUserByName(sender.getName()).getLocations().entrySet()) {
						sender.sendMessage(new StringTextComponent("- " + entry.getKey()));
					}
				}
			} else if (args[0].toLowerCase().equals("set")) {
				if ((FastTravelConfig.INSTANCE.getBool("masterListForOpOnly") && !isOpped)) {
					sender.sendMessage(new StringTextComponent(TextFormatting.RED + "You do not have permission to use this command."));
					return;
				}
				if (args.length >= 2) {
					FastTravel.instance.locations.put(tag, new Location(sender.getPosition()));
					sender.sendMessage(new StringTextComponent(TextFormatting.GREEN + "Location " + tag + " saved!"));
				} else {
					sender.sendMessage(new StringTextComponent(TextFormatting.RED + "Usage: /fasttravel set <name>"));
				}
			} else if (args[0].toLowerCase().equals("setprivate")) {
				if (FastTravelConfig.INSTANCE.getBool("enablePrivateList")) {
					if (args.length >= 2) {
						Users.getUserByName(sender.getName()).addLocation(tag, new Location(sender.getPosition()));
						sender.sendMessage(new StringTextComponent(TextFormatting.GREEN + "Location " + tag + " saved!"));
					} else {
						sender.sendMessage(new StringTextComponent(TextFormatting.RED + "Usage: /fasttravel setprivate <name>"));
					}
				}
			} else if (args[0].toLowerCase().equals("del")) {
				if ((FastTravelConfig.INSTANCE.getBool("masterListForOpOnly") && !isOpped)) {
					sender.sendMessage(new StringTextComponent(TextFormatting.RED + "You do not have permission to use this command."));
					return;
				}
				if (args.length >= 2) {
					if (FastTravel.instance.locations.containsKey(tag)) {
						FastTravel.instance.locations.remove(tag);
						sender.sendMessage(new StringTextComponent(TextFormatting.GREEN + "Location removed"));
					} else {
						sender.sendMessage(new StringTextComponent(TextFormatting.RED + "This location does not exist"));
					}
				} else {
					sender.sendMessage(new StringTextComponent(TextFormatting.RED + "Usage: /fasttravel del <name>"));
				}
			} else if (args[0].toLowerCase().equals("delprivate")) {
				if (FastTravelConfig.INSTANCE.getBool("enablePrivateList")) {
					if (args.length >= 2) {
						if (Users.getUserByName(sender.getName()).getLocations().containsKey(tag)) {
							Users.getUserByName(sender.getName()).removeLocation(tag);
							sender.sendMessage(new StringTextComponent(TextFormatting.GREEN + "Location removed"));
						} else {
							sender.sendMessage(new StringTextComponent(TextFormatting.RED + "This location does not exist"));
						}
					} else {
						sender.sendMessage(new StringTextComponent(TextFormatting.RED + "Usage: /fasttravel del <name>"));
					}
				}
			} else if (args[0].toLowerCase().equals("book")) {
				if (FastTravelConfig.INSTANCE.getBool("allowBookList")) {
					spawnBook((PlayerEntity) sender.getCommandSenderEntity());
				}
			}
		} else {
			sender.sendMessage(new StringTextComponent("Usage: " + getUsage(sender)));
		}
	}*/


	private ITextComponent parseEntry(String key) {

		ITextComponent chatComponent = new StringTextComponent(key + "\n");
		chatComponent.getStyle().setColor(TextFormatting.DARK_BLUE);
		chatComponent.getStyle().setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/travel " + key));
		chatComponent.getStyle().setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new StringTextComponent("Click to travel")));

		return chatComponent;
	}
}
