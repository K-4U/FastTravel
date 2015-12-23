package k4unl.minecraft.fastTravel.commands;

import k4unl.minecraft.fastTravel.FastTravel;
import k4unl.minecraft.k4lib.commands.CommandK4Base;
import k4unl.minecraft.k4lib.lib.TeleportHelper;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
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
    public void processCommand(ICommandSender sender, String[] args) {

        if (args.length == 1) {
            if (FastTravel.instance.locations.containsKey(args[0])) {
                TeleportHelper.teleportEntity(sender.getCommandSenderEntity(), FastTravel.instance.locations.get(args[0]));
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

                sender.addChatMessage(new ChatComponentText(EnumChatFormatting.GRAY + "Woosh"));
            } else {
                sender.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "Location does not exist"));
            }

        } else {
            sender.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "Usage: /travel <location>"));
        }
    }
}
