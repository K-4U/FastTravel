package k4unl.minecraft.fastTravel.commands;

import com.google.common.base.Joiner;
import k4unl.minecraft.fastTravel.FastTravel;
import k4unl.minecraft.fastTravel.lib.Users;
import k4unl.minecraft.fastTravel.lib.config.FastTravelConfig;
import k4unl.minecraft.fastTravel.lib.config.ModInfo;
import k4unl.minecraft.k4lib.commands.CommandK4Base;
import k4unl.minecraft.k4lib.lib.Functions;
import k4unl.minecraft.k4lib.lib.Location;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.EnumHand;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraft.util.text.event.HoverEvent;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.common.DimensionManager;

import java.text.SimpleDateFormat;
import java.util.*;

public class CommandFastTravel extends CommandK4Base {
    
    public CommandFastTravel() {
        
        aliases.add("ft");
    }
    
    @Override
    public String getName() {
        
        return "fasttravel";
    }
    
    @Override
    public String getUsage(ICommandSender sender) {
        
        String result = "fasttravel version";
        boolean isOpped = sender.getName().equals("Server") || Functions.isPlayerOpped(((EntityPlayerMP) sender).getGameProfile());
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
            
            boolean isOpped = sender.getName().equals("Server") || Functions.isPlayerOpped(((EntityPlayerMP) sender).getGameProfile());
            
            args_.remove(args[0]);
            String tag = Joiner.on(" ").join(args_);
            if (args[0].toLowerCase().equals("version")) {
                sender.sendMessage(new TextComponentString("Fast Travel version " + ModInfo.VERSION));
            } else if (args[0].toLowerCase().equals("save")) {
                if (isOpped) {
                    Users.saveToFile(DimensionManager.getCurrentSaveRootDirectory());
                    FastTravel.instance.saveLocationsToFile(DimensionManager.getCurrentSaveRootDirectory());
                    
                    sender.sendMessage(new TextComponentString("Locations and user settings saved to world dir!"));
                }
            } else if (args[0].toLowerCase().equals("load")) {
                if (isOpped) {
                    Users.readFromFile(DimensionManager.getCurrentSaveRootDirectory());
                    FastTravel.instance.readLocationsFile(DimensionManager.getCurrentSaveRootDirectory());
                    
                    sender.sendMessage(new TextComponentString("Locations and user settings loaded from world dir!"));
                }
            } else if (args[0].toLowerCase().equals("list")) {
                for (Map.Entry<String, Location> entry : FastTravel.instance.locations.entrySet()) {
                    sender.sendMessage(new TextComponentString("- " + entry.getKey()));
                }
            } else if (args[0].toLowerCase().equals("listprivate")) {
                if (FastTravelConfig.INSTANCE.getBool("enablePrivateList")) {
                    for (Map.Entry<String, Location> entry : Users.getUserByName(sender.getName()).getLocations().entrySet()) {
                        sender.sendMessage(new TextComponentString("- " + entry.getKey()));
                    }
                }
            } else if (args[0].toLowerCase().equals("set")) {
                if ((FastTravelConfig.INSTANCE.getBool("masterListForOpOnly") && !isOpped)) {
                    sender.sendMessage(new TextComponentString(TextFormatting.RED + "You do not have permission to use this command."));
                    return;
                }
                if (args.length >= 2) {
                    FastTravel.instance.locations.put(tag, new Location(sender.getPosition()));
                    sender.sendMessage(new TextComponentString(TextFormatting.GREEN + "Location " + tag + " saved!"));
                } else {
                    sender.sendMessage(new TextComponentString(TextFormatting.RED + "Usage: /fasttravel set <name>"));
                }
            } else if (args[0].toLowerCase().equals("setprivate")) {
                if (FastTravelConfig.INSTANCE.getBool("enablePrivateList")) {
                    if (args.length >= 2) {
                        Users.getUserByName(sender.getName()).addLocation(tag, new Location(sender.getPosition()));
                        sender.sendMessage(new TextComponentString(TextFormatting.GREEN + "Location " + tag + " saved!"));
                    } else {
                        sender.sendMessage(new TextComponentString(TextFormatting.RED + "Usage: /fasttravel setprivate <name>"));
                    }
                }
            } else if (args[0].toLowerCase().equals("del")) {
                if ((FastTravelConfig.INSTANCE.getBool("masterListForOpOnly") && !isOpped)) {
                    sender.sendMessage(new TextComponentString(TextFormatting.RED + "You do not have permission to use this command."));
                    return;
                }
                if (args.length >= 2) {
                    if (FastTravel.instance.locations.containsKey(tag)) {
                        FastTravel.instance.locations.remove(tag);
                        sender.sendMessage(new TextComponentString(TextFormatting.GREEN + "Location removed"));
                    } else {
                        sender.sendMessage(new TextComponentString(TextFormatting.RED + "This location does not exist"));
                    }
                } else {
                    sender.sendMessage(new TextComponentString(TextFormatting.RED + "Usage: /fasttravel del <name>"));
                }
            } else if (args[0].toLowerCase().equals("delprivate")) {
                if (FastTravelConfig.INSTANCE.getBool("enablePrivateList")) {
                    if (args.length >= 2) {
                        if (Users.getUserByName(sender.getName()).getLocations().containsKey(tag)) {
                            Users.getUserByName(sender.getName()).removeLocation(tag);
                            sender.sendMessage(new TextComponentString(TextFormatting.GREEN + "Location removed"));
                        } else {
                            sender.sendMessage(new TextComponentString(TextFormatting.RED + "This location does not exist"));
                        }
                    } else {
                        sender.sendMessage(new TextComponentString(TextFormatting.RED + "Usage: /fasttravel del <name>"));
                    }
                }
            } else if (args[0].toLowerCase().equals("book")) {
                if (FastTravelConfig.INSTANCE.getBool("allowBookList")) {
                    spawnBook((EntityPlayer) sender.getCommandSenderEntity());
                }
            }
        } else {
            sender.sendMessage(new TextComponentString("Usage: " + getUsage(sender)));
        }
    }
    
    
    @Override
    public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, BlockPos pos) {
        
        List<String> ret = new ArrayList<>();
        
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
        ItemStack book = new ItemStack(Items.WRITTEN_BOOK, 1);
        NBTTagCompound tCompound = new NBTTagCompound();
        if (player.getHeldItem(EnumHand.MAIN_HAND) != null) {
            if (player.getHeldItem(EnumHand.MAIN_HAND).getItem() == Items.WRITTEN_BOOK) {
                
                //Find the NBT:
                tCompound = player.getHeldItem(EnumHand.MAIN_HAND).getTagCompound();
                if (tCompound.getString("author").equals("K4Unl") && tCompound.getString("title").equals("Waypoints")) {
                    //Oh yeah!
                    book = player.getHeldItem(EnumHand.MAIN_HAND).copy();
                    foundBook = true;
                }
            }
        }
        
        tCompound.setString("author", "K4Unl");
        tCompound.setString("title", "Waypoints");
        NBTTagList tList = new NBTTagList();
        
        SimpleDateFormat dateFormat = new SimpleDateFormat(("HH:mm dd-MM-yyyy"));
        ITextComponent page1 = new TextComponentString("");
        
        page1.appendText(TextFormatting.BOLD + "K4Unl's \n");
        page1.appendText(TextFormatting.BOLD + "Fast Travel Guide\n\n");
        page1.appendText(TextFormatting.DARK_GREEN + "This book lists all the \n");
        page1.appendText(TextFormatting.DARK_GREEN + "waypoints as of \n");
        page1.appendText(TextFormatting.DARK_GREEN + "" + TextFormatting.BOLD + dateFormat.format(new Date()) + "\n");
        page1.appendText(TextFormatting.DARK_GREEN + "It will not update\n");
        page1.appendText(TextFormatting.DARK_GREEN + "automatically!\n\n");
        
        ITextComponent updateLink = new TextComponentString("Update\n");
        updateLink.getStyle().setColor(TextFormatting.BLUE);
        updateLink.getStyle().setUnderlined(true);
        updateLink.getStyle().setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TextComponentString("Click here to update the book!")));
        updateLink.getStyle().setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/fasttravel book"));
        
        page1.appendSibling(updateLink);
        
        ITextComponent publicListLink = new TextComponentString("Public list\n");
        publicListLink.getStyle().setColor(TextFormatting.BLUE);
        publicListLink.getStyle().setUnderlined(true);
        publicListLink.getStyle().setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TextComponentString("Click here to go to the public list.")));
        publicListLink.getStyle().setClickEvent(new ClickEvent(ClickEvent.Action.CHANGE_PAGE, "2"));
        page1.appendSibling(publicListLink);
        
        ITextComponent privateListLink = new TextComponentString("Private list\n");
        if (FastTravelConfig.INSTANCE.getBool("enablePrivateList")) {
            privateListLink.getStyle().setColor(TextFormatting.BLUE);
            privateListLink.getStyle().setUnderlined(true);
            privateListLink.getStyle().setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TextComponentString("Click here to go to your private list.")));
        }
        
        tList.appendTag(new NBTTagString(ITextComponent.Serializer.componentToJson(page1)));
        
        HoverEvent backButtonHover = new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TextComponentString("Back to title"));
        ClickEvent backButtonClick = new ClickEvent(ClickEvent.Action.CHANGE_PAGE, "1");
        TextComponentString backButton = new TextComponentString("Back\n");
        backButton.getStyle().setColor(TextFormatting.BLUE);
        backButton.getStyle().setUnderlined(true);
        backButton.getStyle().setHoverEvent(backButtonHover);
        backButton.getStyle().setClickEvent(backButtonClick);
        
        ITextComponent p = new TextComponentString("");
        p.appendSibling(backButton);
        int i = 0;
        int page = 2;
        if (FastTravel.instance.locations.size() > 0) {
            for (Map.Entry<String, Location> entry : FastTravel.instance.locations.entrySet()) {
                i++;
                p = p.appendSibling(parseEntry(entry.getKey()));
                
                if (i == 13) {
                    tList.appendTag(new NBTTagString(ITextComponent.Serializer.componentToJson(p)));
                    i = 0;
                    p = new TextComponentString("");
                    p.appendSibling(backButton);
                    page++;
                }
            }
        } else {
            p.appendSibling(new TextComponentString(TextFormatting.RESET + "No locations defined yet."));
        }
        if (i < 13) {
            tList.appendTag(new NBTTagString(ITextComponent.Serializer.componentToJson(p)));
            page++;
        }
        
        if (FastTravelConfig.INSTANCE.getBool("enablePrivateList")) {
            p = new TextComponentString("");
            p.appendSibling(backButton);
            i = 0;
            if (Users.getUserByName(player.getName()).getLocations().size() > 0) {
                for (Map.Entry<String, Location> entry : Users.getUserByName(player.getName()).getLocations().entrySet()) {
                    i++;
                    p = p.appendSibling(parseEntry(entry.getKey()));
                    
                    if (i == 13) {
                        tList.appendTag(new NBTTagString(ITextComponent.Serializer.componentToJson(p)));
                        i = 0;
                        p = new TextComponentString("");
                        p.appendSibling(backButton);
                    }
                }
            } else {
                p.appendSibling(new TextComponentString(TextFormatting.RESET + "No locations defined yet."));
            }
            if (i < 13) {
                tList.appendTag(new NBTTagString(ITextComponent.Serializer.componentToJson(p)));
            }
            
            privateListLink.getStyle().setClickEvent(new ClickEvent(ClickEvent.Action.CHANGE_PAGE, "" + page));
            
            page1.appendSibling(privateListLink);
        }
        
        tList.set(0, new NBTTagString(ITextComponent.Serializer.componentToJson(page1)));
        
        tCompound.setTag("pages", tList);
        
        book.setTagCompound(tCompound);
        
        if (!foundBook) {
            EntityItem EItem = new EntityItem(player.getEntityWorld(), player.getPosition().getX(), player.getPosition().getY(), player.getPosition().getZ(),
                    book);
            player.getEntityWorld().spawnEntity(EItem);
        }
    }
    
    private ITextComponent parseEntry(String key) {
        
        ITextComponent chatComponent = new TextComponentString(key + "\n");
        chatComponent.getStyle().setColor(TextFormatting.DARK_BLUE);
        chatComponent.getStyle().setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/travel " + key));
        chatComponent.getStyle().setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TextComponentString("Click to travel")));
        
        return chatComponent;
    }
}
