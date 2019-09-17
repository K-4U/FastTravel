package k4unl.minecraft.fastTravel.lib.config;

import k4unl.minecraft.k4lib.lib.config.Config;
import net.minecraftforge.common.ForgeConfigSpec;

public class FastTravelConfig extends Config {

	public static ForgeConfigSpec.BooleanValue useExperienceOnTravel;
	public static ForgeConfigSpec.ConfigValue<Double> experienceMultiplier;
	public static ForgeConfigSpec.ConfigValue<Integer> experienceUsageOnDimensionTravel;
	public static ForgeConfigSpec.BooleanValue masterListForOpOnly;
	public static ForgeConfigSpec.BooleanValue enablePrivateList;
	public static ForgeConfigSpec.BooleanValue allowBook;

	@Override
	protected void buildCommon(ForgeConfigSpec.Builder builder) {
		useExperienceOnTravel = builder.comment("Userful for survival.").define("useExperienceOnTravel", true);
		experienceMultiplier = builder.comment("The amount of experience to use per block traveled.").define("experienceMultiplier", 1.0);
		experienceUsageOnDimensionTravel = builder.comment("The amount of experience to use when traveling between dimensions.").define("experienceUsageOnDimensionTravel", 50);
		masterListForOpOnly = builder.comment("Whether or not only OP can use /fasttravel set|del").define("masterListForOpOnly", true);
		enablePrivateList = builder.comment("Allows users to set their own waypoints").define("enablePrivateList", true);
		allowBook = builder.comment("Allows users to spawn a book with all the waypoints listed").define("allowBook", true);
	}

	@Override
	protected void buildServer(ForgeConfigSpec.Builder builder) {

	}

	@Override
	protected void buildClient(ForgeConfigSpec.Builder builder) {

	}
}
