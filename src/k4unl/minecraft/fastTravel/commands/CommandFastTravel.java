package k4unl.minecraft.fastTravel.commands;

import k4unl.minecraft.fastTravel.FastTravel;
import k4unl.minecraft.fastTravel.lib.Users;
import k4unl.minecraft.fastTravel.lib.config.ModInfo;
import k4unl.minecraft.k4lib.commands.CommandK4Base;
import k4unl.minecraft.k4lib.lib.Functions;
import k4unl.minecraft.k4lib.lib.Location;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.common.DimensionManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CommandFastTravel extends CommandK4Base {

    public CommandFastTravel() {

        aliases.add("ft");
    }

    @Override
    public String getCommandName() {

        return "fasttravel";
    }

    @Override
    public String getCommandUsage(ICommandSender p_71518_1_) {

        return "fasttravel version|save|load|set|setprivate|del|delprivate|list|listprivate";
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) {

        if (args.length >= 1) {
            if (args[0].toLowerCase().equals("version")) {
                sender.addChatMessage(new ChatComponentText("Fast Travel version " + ModInfo.VERSION));
            } else if (args[0].toLowerCase().equals("save")) {
                if (Functions.isPlayerOpped(((EntityPlayerMP)sender).getGameProfile())) {
                    Users.saveToFile(DimensionManager.getCurrentSaveRootDirectory());
                    FastTravel.instance.saveLocationsToFile(DimensionManager.getCurrentSaveRootDirectory());

                    sender.addChatMessage(new ChatComponentText("Locations and user settings saved to world dir!"));
                }
            } else if (args[0].toLowerCase().equals("load")) {
                if (Functions.isPlayerOpped(((EntityPlayerMP)sender).getGameProfile())) {
                    Users.readFromFile(DimensionManager.getCurrentSaveRootDirectory());
                    FastTravel.instance.readLocationsFile(DimensionManager.getCurrentSaveRootDirectory());

                    sender.addChatMessage(new ChatComponentText("Locations and user settings loaded from world dir!"));
                }
            } else if (args[0].toLowerCase().equals("list")) {
                for (Map.Entry<String, Location> entry : FastTravel.instance.locations.entrySet()) {
                    sender.addChatMessage(new ChatComponentText("- " + entry.getKey()));
                }
            } else if (args[0].toLowerCase().equals("listprivate")) {
                for (Map.Entry<String, Location> entry : Users.getUserByName(sender.getCommandSenderName()).getLocations().entrySet()) {
                    sender.addChatMessage(new ChatComponentText("- " + entry.getKey()));
                }
            } else if (args[0].toLowerCase().equals("set")) {
                if (args.length == 2) {
                    FastTravel.instance.locations.put(args[1], new Location(sender.getPlayerCoordinates()));
                    sender.addChatMessage(new ChatComponentText(EnumChatFormatting.GREEN + "Location " + args[1] + " saved!"));
                } else {
                    sender.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "Usage: /fasttravel set <name>"));
                }
            } else if (args[0].toLowerCase().equals("setprivate")) {
                if (args.length == 2) {
                    Users.getUserByName(sender.getCommandSenderName()).addLocation(args[1], new Location(sender.getPlayerCoordinates()));
                    sender.addChatMessage(new ChatComponentText(EnumChatFormatting.GREEN + "Location " + args[1] + " saved!"));
                } else {
                    sender.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "Usage: /fasttravel setprivate <name>"));
                }
            } else if (args[0].toLowerCase().equals("del")) {
                if (args.length == 2) {
                    if (FastTravel.instance.locations.containsKey(args[1])) {
                        FastTravel.instance.locations.remove(args[1]);
                        sender.addChatMessage(new ChatComponentText(EnumChatFormatting.GREEN + "Location removed"));
                    } else {
                        sender.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "This location does not exist"));
                    }
                } else {
                    sender.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "Usage: /fasttravel del <name>"));
                }
            } else if (args[0].toLowerCase().equals("delprivate")) {
                if (args.length == 2) {
                    if (Users.getUserByName(sender.getCommandSenderName()).getLocations().containsKey(args[1])) {
                        Users.getUserByName(sender.getCommandSenderName()).removeLocation(args[1]);
                        sender.addChatMessage(new ChatComponentText(EnumChatFormatting.GREEN + "Location removed"));
                    } else {
                        sender.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "This location does not exist"));
                    }
                } else {
                    sender.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "Usage: /fasttravel del <name>"));
                }
            }
        } else {
            sender.addChatMessage(new ChatComponentText("Usage: " + getCommandUsage(sender)));
        }
    }

    @Override
    public List addTabCompletionOptions(ICommandSender sender, String[] args) {

        List<String> ret = new ArrayList<String>();

        if (args.length == 1) {
            ret.add("load");
            ret.add("save");
            ret.add("version");
            ret.add("list");
            ret.add("listprivate");
            ret.add("set");
            ret.add("setprivate");
            ret.add("del");
            ret.add("delprivate");
        }

        return ret;
    }
}
