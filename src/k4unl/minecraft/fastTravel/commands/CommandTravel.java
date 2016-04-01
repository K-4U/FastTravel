package k4unl.minecraft.fastTravel.commands;

import com.google.common.base.Joiner;
import k4unl.minecraft.fastTravel.FastTravel;
import k4unl.minecraft.fastTravel.lib.Users;
import k4unl.minecraft.fastTravel.lib.config.FastTravelConfig;
import k4unl.minecraft.k4lib.commands.CommandK4Base;
import k4unl.minecraft.k4lib.lib.Functions;
import k4unl.minecraft.k4lib.lib.Location;
import k4unl.minecraft.k4lib.lib.TeleportHelper;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.EnumParticleTypes;

import java.util.Random;

/**
 * @author Koen Beckers (K-4U)
 */
public class CommandTravel extends CommandK4Base {

    public CommandTravel() {

        aliases.add("t");
    }

    @Override
    public String getCommandName() {

        return "travel";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {

        return "travel <location>";
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {

        if (args.length >= 1) {
            String tag = Joiner.on(" ").join(args);
            
            if (!FastTravel.instance.locations.containsKey(tag) && !Users.getUserByName(sender.getName()).getLocations().containsKey(tag)) {
                sender.addChatMessage(new TextComponentString(TextFormatting.RED + "Location does not exist"));
            }else{
                Location target;
                if(FastTravel.instance.locations.containsKey(tag)){
                    target = FastTravel.instance.locations.get(tag);
                }else{
                    target = Users.getUserByName(sender.getName()).getLocations().get(tag);
                }

                if (FastTravelConfig.INSTANCE.getBool("useExperienceOnTravel") && !((EntityPlayer)sender.getCommandSenderEntity()).capabilities.isCreativeMode) {
                    Location startLocation = new Location((EntityPlayer) sender.getCommandSenderEntity());
                    int distance = startLocation.getDifference(target);
                    int experienceCost = (int)(FastTravelConfig.INSTANCE.getDouble("experienceMultiplier") * distance);
                    if (startLocation.getDimension() != target.getDimension()) {
                        experienceCost += FastTravelConfig.INSTANCE.getInt("experienceUsageDimensionTravel");
                    }

                    EntityPlayer player = (EntityPlayer) sender.getCommandSenderEntity();

                    if (player.experienceTotal < experienceCost) {
                        sender.addChatMessage(new TextComponentString(TextFormatting.RED + "You don't have enough experience to make this "
                          + "journey"));
                        return;
                    }

                    experienceCost = experienceCost * -1;
                    Functions.addPlayerXP(player, experienceCost);
                }

                TeleportHelper.teleportEntity(sender.getCommandSenderEntity(), target);
                Random rnd = new Random(System.currentTimeMillis() / 1000);
                float dx;
                float dy;
                float dz;
                float x = sender.getPosition().getX();
                float y = sender.getPosition().getY();
                float z = sender.getPosition().getZ();
                for (int i = 0; i <= 5; i++) {
                    dx = (rnd.nextFloat() - 0.6F) * 0.1F;
                    dy = (rnd.nextFloat() - 0.6F) * 0.1F;
                    dz = (rnd.nextFloat() - 0.6F) * 0.1F;

                    sender.getEntityWorld().spawnParticle(EnumParticleTypes.SMOKE_NORMAL, x + .5F, y + .5F, z + .5F, dx, dy, dz);
                }

                sender.addChatMessage(new TextComponentString(TextFormatting.GRAY + "Woosh"));
            }

        } else {
            sender.addChatMessage(new TextComponentString(TextFormatting.RED + "Usage: /travel <location>"));
        }
    }
}
