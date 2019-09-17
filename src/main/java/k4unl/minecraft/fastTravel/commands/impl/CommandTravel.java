package k4unl.minecraft.fastTravel.commands.impl;

import java.util.Random;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import k4unl.minecraft.fastTravel.commands.arguments.TeleportLocationArgument;
import k4unl.minecraft.fastTravel.lib.config.FastTravelConfig;
import k4unl.minecraft.k4lib.commands.Command;
import k4unl.minecraft.k4lib.lib.Functions;
import k4unl.minecraft.k4lib.lib.Location;
import k4unl.minecraft.k4lib.lib.TeleportHelper;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;

/**
 * @author Koen Beckers (K-4U)
 */
public class CommandTravel implements Command {

	@Override
	public void register(LiteralArgumentBuilder<CommandSource> argumentBuilder) {
		argumentBuilder.then(Commands.argument("to", TeleportLocationArgument.location()).executes(this::teleport));
	}

	private int teleport(CommandContext<CommandSource> context) throws CommandSyntaxException {

		Location target = TeleportLocationArgument.getLocation(context, "to");

		ServerPlayerEntity player = context.getSource().asPlayer();
		Entity sourceEntity = context.getSource().getEntity();
		if (FastTravelConfig.useExperienceOnTravel.get() && !((PlayerEntity) sourceEntity).abilities.isCreativeMode) {

			Location startLocation = new Location(player);
			int distance = startLocation.getDifference(target);
			int experienceCost = (int) (FastTravelConfig.experienceMultiplier.get() * distance);
			if (startLocation.getDimension() != target.getDimension()) {
				experienceCost += FastTravelConfig.experienceUsageOnDimensionTravel.get();
			}

			if (player.experienceTotal < experienceCost) {
				context.getSource().sendFeedback(new StringTextComponent(TextFormatting.RED + "You don't have enough experience to make this journey"), false);
				return 0;
			}

			experienceCost = experienceCost * -1;
			Functions.addPlayerXP(player, experienceCost);
		}

		TeleportHelper.teleportEntity(player, target);
		Random rnd = new Random(System.currentTimeMillis() / 1000);
		float dx;
		float dy;
		float dz;
		double x = context.getSource().getPos().getX();
		double y = context.getSource().getPos().getY();
		double z = context.getSource().getPos().getZ();
		for (int i = 0; i <= 5; i++) {
			dx = (rnd.nextFloat() - 0.6F) * 0.1F;
			dy = (rnd.nextFloat() - 0.6F) * 0.2F;
			dz = (rnd.nextFloat() - 0.6F) * 0.1F;

			context.getSource().getWorld().spawnParticle(ParticleTypes.POOF, x + .5D, y + .5D, z + .5D, 25, dx, dy, dz, 0.02D);
			context.getSource().getWorld().spawnParticle(ParticleTypes.SMOKE, x + .5D, y + .5D, z + .5D, 100, dx, dy, dz, 0.04D);
			context.getSource().getWorld().spawnParticle(ParticleTypes.PORTAL, x + .5D, y + .5D, z + .5D, 30, dx, dy, dz, 0.5D);
		}

		context.getSource().getWorld().playSound((PlayerEntity) sourceEntity, x, y, z, SoundEvents.ENTITY_ENDERMAN_TELEPORT, SoundCategory.PLAYERS, 1.0F, 1.0F);
		sourceEntity.playSound(SoundEvents.ENTITY_ENDERMAN_TELEPORT, 1.0F, 1.0F);

		context.getSource().sendFeedback(new StringTextComponent(TextFormatting.GRAY + "Woosh"), false);
		return 0;
	}

	@Override
	public String getName() {

		return "travel";
	}

	@Override
	public boolean canUse(CommandSource commandSource) {
		return true;
	}
}
