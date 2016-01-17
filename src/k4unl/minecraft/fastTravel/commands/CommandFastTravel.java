package k4unl.minecraft.fastTravel.commands;

import com.google.common.base.Joiner;
import k4unl.minecraft.fastTravel.FastTravel;
import k4unl.minecraft.fastTravel.lib.Users;
import k4unl.minecraft.fastTravel.lib.config.FastTravelConfig;
import k4unl.minecraft.fastTravel.lib.config.ModInfo;
import k4unl.minecraft.k4lib.commands.CommandK4Base;
import k4unl.minecraft.k4lib.lib.Functions;
import k4unl.minecraft.k4lib.lib.Location;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.event.ClickEvent;
import net.minecraft.event.HoverEvent;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;
import net.minecraftforge.common.DimensionManager;

import java.text.SimpleDateFormat;
import java.util.*;

public class CommandFastTravel extends CommandK4Base {

    public CommandFastTravel() {

        aliases.add("ft");
    }

    @Override
    public String getCommandName() {

        return "fasttravel";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        String result = "fasttravel version";
        if (Functions.isPlayerOpped(sender.getCommandSenderName())) {
            result += "|save|load";
        }
        result += "|list";

        if ((!FastTravelConfig.INSTANCE.getBool("masterListForOpOnly") || Functions.isPlayerOpped(sender.getCommandSenderName()))) {
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
    public void processCommand(ICommandSender sender, String[] args) {

        if (args.length >= 1) {
            List<String> args_ = new ArrayList<String>();
            Collections.addAll(args_, args);

            args_.remove(args[0]);
            String tag = Joiner.on(" ").join(args_);
            if (args[0].toLowerCase().equals("version")) {
                sender.addChatMessage(new ChatComponentText("Fast Travel version " + ModInfo.VERSION));
            } else if (args[0].toLowerCase().equals("save")) {
                if (Functions.isPlayerOpped(sender.getCommandSenderName())) {
                    Users.saveToFile(DimensionManager.getCurrentSaveRootDirectory());
                    FastTravel.instance.saveLocationsToFile(DimensionManager.getCurrentSaveRootDirectory());

                    sender.addChatMessage(new ChatComponentText("Locations and user settings saved to world dir!"));
                }
            } else if (args[0].toLowerCase().equals("load")) {
                if (Functions.isPlayerOpped(sender.getCommandSenderName())) {
                    Users.readFromFile(DimensionManager.getCurrentSaveRootDirectory());
                    FastTravel.instance.readLocationsFile(DimensionManager.getCurrentSaveRootDirectory());

                    sender.addChatMessage(new ChatComponentText("Locations and user settings loaded from world dir!"));
                }
            } else if (args[0].toLowerCase().equals("list")) {
                for (Map.Entry<String, Location> entry : FastTravel.instance.locations.entrySet()) {
                    sender.addChatMessage(new ChatComponentText("- " + entry.getKey()));
                }
            } else if (args[0].toLowerCase().equals("listprivate")) {
                if (FastTravelConfig.INSTANCE.getBool("enablePrivateList")) {
                    for (Map.Entry<String, Location> entry : Users.getUserByName(sender.getCommandSenderName()).getLocations().entrySet()) {
                        sender.addChatMessage(new ChatComponentText("- " + entry.getKey()));
                    }
                }
            } else if (args[0].toLowerCase().equals("set")) {
                if ((FastTravelConfig.INSTANCE.getBool("masterListForOpOnly") && !Functions.isPlayerOpped(sender.getCommandSenderName()))) {
                    sender.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "You do not have permission to use this command."));
                    return;
                }
                if (args.length >= 2) {
                    FastTravel.instance.locations.put(tag, new Location(sender.getPosition()));
                    sender.addChatMessage(new ChatComponentText(EnumChatFormatting.GREEN + "Location " + tag + " saved!"));
                } else {
                    sender.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "Usage: /fasttravel set <name>"));
                }
            } else if (args[0].toLowerCase().equals("setprivate")) {
                if (FastTravelConfig.INSTANCE.getBool("enablePrivateList")) {
                    if (args.length >= 2) {
                        Users.getUserByName(sender.getCommandSenderName()).addLocation(tag, new Location(sender.getPosition()));
                        sender.addChatMessage(new ChatComponentText(EnumChatFormatting.GREEN + "Location " + tag + " saved!"));
                    } else {
                        sender.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "Usage: /fasttravel setprivate <name>"));
                    }
                }
            } else if (args[0].toLowerCase().equals("del")) {
                if ((FastTravelConfig.INSTANCE.getBool("masterListForOpOnly") && !Functions.isPlayerOpped(sender.getCommandSenderName()))) {
                    sender.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "You do not have permission to use this command."));
                    return;
                }
                if (args.length >= 2) {
                    if (FastTravel.instance.locations.containsKey(tag)) {
                        FastTravel.instance.locations.remove(tag);
                        sender.addChatMessage(new ChatComponentText(EnumChatFormatting.GREEN + "Location removed"));
                    } else {
                        sender.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "This location does not exist"));
                    }
                } else {
                    sender.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "Usage: /fasttravel del <name>"));
                }
            } else if (args[0].toLowerCase().equals("delprivate")) {
                if (FastTravelConfig.INSTANCE.getBool("enablePrivateList")) {
                    if (args.length >= 2) {
                        if (Users.getUserByName(sender.getCommandSenderName()).getLocations().containsKey(tag)) {
                            Users.getUserByName(sender.getCommandSenderName()).removeLocation(tag);
                            sender.addChatMessage(new ChatComponentText(EnumChatFormatting.GREEN + "Location removed"));
                        } else {
                            sender.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "This location does not exist"));
                        }
                    } else {
                        sender.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "Usage: /fasttravel del <name>"));
                    }
                }
            } else if (args[0].toLowerCase().equals("book")) {
                if (FastTravelConfig.INSTANCE.getBool("allowBookList")) {
                    spawnBook((EntityPlayer) sender.getCommandSenderEntity());
                }
            }
        } else {
            sender.addChatMessage(new ChatComponentText("Usage: " + getCommandUsage(sender)));
        }
    }


    @Override
    public List addTabCompletionOptions(ICommandSender sender, String[] args, BlockPos pos) {

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

    private void spawnBook(EntityPlayer player) {
        //Search the players current held item for the book.
        boolean foundBook = false;
        ItemStack book = new ItemStack(Items.written_book, 1);
        NBTTagCompound tCompound = new NBTTagCompound();
        if (player.getHeldItem() != null) {
            if (player.getHeldItem().getItem() == Items.written_book) {

                //Find the NBT:
                tCompound = player.getHeldItem().getTagCompound();
                if (tCompound.getString("author").equals("K4Unl") && tCompound.getString("title").equals("Waypoints")) {
                    //Oh yeah!
                    book = player.getHeldItem().copy();
                    foundBook = true;
                }
            }
        }

        tCompound.setString("author", "K4Unl");
        tCompound.setString("title", "Waypoints");
        NBTTagList tList = new NBTTagList();

        SimpleDateFormat dateFormat = new SimpleDateFormat(("HH:mm dd-MM-yyyy"));
        IChatComponent page1 = new ChatComponentText("");

        page1.appendText(EnumChatFormatting.BOLD + "K4Unl's \n");
        page1.appendText(EnumChatFormatting.BOLD + "Fast Travel Guide\n\n");
        page1.appendText(EnumChatFormatting.DARK_GREEN + "This book lists all the \n");
        page1.appendText(EnumChatFormatting.DARK_GREEN + "waypoints as of \n");
        page1.appendText(EnumChatFormatting.DARK_GREEN + "" + EnumChatFormatting.BOLD + dateFormat.format(new Date()) + "\n");
        page1.appendText(EnumChatFormatting.DARK_GREEN + "It will not update\n");
        page1.appendText(EnumChatFormatting.DARK_GREEN + "automatically!\n\n");

        IChatComponent updateLink = new ChatComponentText("Update\n");
        updateLink.getChatStyle().setColor(EnumChatFormatting.BLUE);
        updateLink.getChatStyle().setUnderlined(true);
        updateLink.getChatStyle().setChatHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ChatComponentText("Click here to update the book!")));
        updateLink.getChatStyle().setChatClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/fasttravel book"));

        page1.appendSibling(updateLink);

        IChatComponent publicListLink = new ChatComponentText("Public list\n");
        publicListLink.getChatStyle().setColor(EnumChatFormatting.BLUE);
        publicListLink.getChatStyle().setUnderlined(true);
        publicListLink.getChatStyle().setChatHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ChatComponentText("Click here to go to the public list.")));
        publicListLink.getChatStyle().setChatClickEvent(new ClickEvent(ClickEvent.Action.CHANGE_PAGE, "2"));
        page1.appendSibling(publicListLink);

        IChatComponent privateListLink = new ChatComponentText("Private list\n");
        if (FastTravelConfig.INSTANCE.getBool("enablePrivateList")) {
            privateListLink.getChatStyle().setColor(EnumChatFormatting.BLUE);
            privateListLink.getChatStyle().setUnderlined(true);
            privateListLink.getChatStyle().setChatHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ChatComponentText("Click here to go to your private list.")));
        }

        tList.appendTag(new NBTTagString(IChatComponent.Serializer.componentToJson(page1)));

        HoverEvent backButtonHover = new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ChatComponentText("Back to title"));
        ClickEvent backButtonClick = new ClickEvent(ClickEvent.Action.CHANGE_PAGE, "1");
        ChatComponentText backButton = new ChatComponentText("Back\n");
        backButton.getChatStyle().setColor(EnumChatFormatting.BLUE);
        backButton.getChatStyle().setUnderlined(true);
        backButton.getChatStyle().setChatHoverEvent(backButtonHover);
        backButton.getChatStyle().setChatClickEvent(backButtonClick);

        IChatComponent p = new ChatComponentText("");
        p.appendSibling(backButton);
        int i = 0;
        int page = 2;
        NBTTagList extra = new NBTTagList();
        if (FastTravel.instance.locations.size() > 0) {
            for (Map.Entry<String, Location> entry : FastTravel.instance.locations.entrySet()) {
                i++;
                p = p.appendSibling(parseEntry(entry.getKey()));

                if (i == 13) {
                    tList.appendTag(new NBTTagString(IChatComponent.Serializer.componentToJson(p)));
                    i = 0;
                    p = new ChatComponentText("");
                    p.appendSibling(backButton);
                    page++;
                }
            }
        } else {
            p.appendSibling(new ChatComponentText(EnumChatFormatting.RESET + "No locations defined yet."));
        }
        if (i < 13) {
            tList.appendTag(new NBTTagString(IChatComponent.Serializer.componentToJson(p)));
            page++;
        }

        if (FastTravelConfig.INSTANCE.getBool("enablePrivateList")) {
            p = new ChatComponentText("");
            p.appendSibling(backButton);
            i = 0;
            if (Users.getUserByName(player.getDisplayNameString()).getLocations().size() > 0) {
                for (Map.Entry<String, Location> entry : Users.getUserByName(player.getDisplayNameString()).getLocations().entrySet()) {
                    i++;
                    p = p.appendSibling(parseEntry(entry.getKey()));

                    if (i == 13) {
                        tList.appendTag(new NBTTagString(IChatComponent.Serializer.componentToJson(p)));
                        i = 0;
                        p = new ChatComponentText("");
                        p.appendSibling(backButton);
                    }
                }
            } else {
                p.appendSibling(new ChatComponentText(EnumChatFormatting.RESET + "No locations defined yet."));
            }
            if (i < 13) {
                tList.appendTag(new NBTTagString(IChatComponent.Serializer.componentToJson(p)));
            }

            privateListLink.getChatStyle().setChatClickEvent(new ClickEvent(ClickEvent.Action.CHANGE_PAGE, "" + page));

            page1.appendSibling(privateListLink);
        }

        tList.set(0, new NBTTagString(IChatComponent.Serializer.componentToJson(page1)));

        tCompound.setTag("pages", tList);

        book.setTagCompound(tCompound);

        if (!foundBook) {
            EntityItem EItem = new EntityItem(player.worldObj, player.getPosition().getX(), player.getPosition().getY(), player.getPosition().getZ(),
                    book);
            player.worldObj.spawnEntityInWorld(EItem);
        }
    }

    private IChatComponent parseEntry(String key) {
        IChatComponent chatComponent = new ChatComponentText(key + "\n");
        chatComponent.getChatStyle().setColor(EnumChatFormatting.DARK_BLUE);
        chatComponent.getChatStyle().setChatClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/travel " + key));
        chatComponent.getChatStyle().setChatHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ChatComponentText("Click to travel")));

        return chatComponent;
    }
}
