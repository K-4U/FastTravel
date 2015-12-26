package k4unl.minecraft.fastTravel.commands;

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
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
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
        if(Functions.isPlayerOpped(sender.getCommandSenderName())){
            result += "|save|load";
        }
        result += "|list";

        if ((!FastTravelConfig.INSTANCE.getBool("masterListForOpOnly") || Functions.isPlayerOpped(sender.getCommandSenderName()))) {
            result += "|set|del";
        }
        if(FastTravelConfig.INSTANCE.getBool("enablePrivateList")){
            result += "|listprivate|setprivate|delprivate";
        }
        if(FastTravelConfig.INSTANCE.getBool("allowBookList")){
            result += "|book";
        }

        return result;
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) {

        if (args.length >= 1) {
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
                if(FastTravelConfig.INSTANCE.getBool("enablePrivateList")) {
                    for (Map.Entry<String, Location> entry : Users.getUserByName(sender.getCommandSenderName()).getLocations().entrySet()) {
                        sender.addChatMessage(new ChatComponentText("- " + entry.getKey()));
                    }
                }
            } else if (args[0].toLowerCase().equals("set")) {
                if((FastTravelConfig.INSTANCE.getBool("masterListForOpOnly") && !Functions.isPlayerOpped(sender.getCommandSenderName()))){
                    sender.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "You do not have permission to use this command."));
                    return;
                }
                if (args.length == 2) {
                    FastTravel.instance.locations.put(args[1], new Location(sender.getPosition()));
                    sender.addChatMessage(new ChatComponentText(EnumChatFormatting.GREEN + "Location " + args[1] + " saved!"));
                } else {
                    sender.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "Usage: /fasttravel set <name>"));
                }
            } else if (args[0].toLowerCase().equals("setprivate")) {
                if(FastTravelConfig.INSTANCE.getBool("enablePrivateList")) {
                    if (args.length == 2) {
                        Users.getUserByName(sender.getCommandSenderName()).addLocation(args[1], new Location(sender.getPosition()));
                        sender.addChatMessage(new ChatComponentText(EnumChatFormatting.GREEN + "Location " + args[1] + " saved!"));
                    } else {
                        sender.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "Usage: /fasttravel setprivate <name>"));
                    }
                }
            } else if (args[0].toLowerCase().equals("del")) {
                if((FastTravelConfig.INSTANCE.getBool("masterListForOpOnly") && !Functions.isPlayerOpped(sender.getCommandSenderName()))){
                    sender.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "You do not have permission to use this command."));
                    return;
                }
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
                if(FastTravelConfig.INSTANCE.getBool("enablePrivateList")) {
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
            }else if(args[0].toLowerCase().equals("book")){
                if(FastTravelConfig.INSTANCE.getBool("allowBookList")){
                    spawnBook((EntityPlayer)sender.getCommandSenderEntity());
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

    private void spawnBook(EntityPlayer player){

        ItemStack book = new ItemStack(Items.written_book, 1);
        NBTTagCompound tCompound = new NBTTagCompound();
        tCompound.setString("author", "K4Unl");
        tCompound.setString("title", "Waypoints");
        NBTTagList tList = new NBTTagList();

        SimpleDateFormat dateFormat = new SimpleDateFormat(("dd-MM-yyyy"));
        String page1 = "";
        page1 += EnumChatFormatting.BOLD + "K4Unl's \n";
        page1 += EnumChatFormatting.BOLD + "Fast Travel Guide\n\n";
        page1 += EnumChatFormatting.DARK_GREEN + "This book lists all the \n";
        page1 += EnumChatFormatting.DARK_GREEN + "waypoints as of \n";
        page1 += EnumChatFormatting.DARK_GREEN + "" + EnumChatFormatting.BOLD + dateFormat.format(new Date()) + "\n";
        page1 += EnumChatFormatting.DARK_GREEN + "It will not update\n";
        page1 += EnumChatFormatting.DARK_GREEN + "automatically!\n";

        tList.appendTag(new NBTTagString(page1));

        String p = "";
        int i = 0;
        if(FastTravel.instance.locations.size() > 0) {
            for (Map.Entry<String, Location> entry : FastTravel.instance.locations.entrySet()) {
                i++;
                if(!p.equals("")){
                    p+= ",";
                }
                p += "{\"clickEvent\":{\"action\":\"run_command\",\"value\":\"/travel " + entry.getKey() + "\"}, \"text\":\"" + entry.getKey() +
                  "\n\",\"hoverEvent\":{\"action\":\"show_text\",\"value\":{\"text\":\"\",\"extra\":[{\"text\":\"Click to travel\"}]}}}";
                if (i == 14) {
                    tList.appendTag(new NBTTagString("{\"extra\":[" + p + "]}"));
                    i = 0;
                    p = "";
                }
            }
        }else{
            p = "No locations defined yet!";
        }
        if(i < 14){
            tList.appendTag(new NBTTagString(p));
        }


        tCompound.setTag("pages", tList);

        book.setTagCompound(tCompound);

        EntityItem EItem = new EntityItem(player.worldObj, player.getPosition().getX(), player.getPosition().getY(), player.getPosition().getZ(),
          book);
        player.worldObj.spawnEntityInWorld(EItem);
    }
}
