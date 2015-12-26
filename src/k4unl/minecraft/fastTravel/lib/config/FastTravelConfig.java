package k4unl.minecraft.fastTravel.lib.config;

import k4unl.minecraft.k4lib.lib.config.Config;
import k4unl.minecraft.k4lib.lib.config.ConfigOption;

public class FastTravelConfig extends Config {

    public static final FastTravelConfig INSTANCE = new FastTravelConfig();

    public void init() {

        super.init();
        configOptions.add(new ConfigOption("useExperienceOnTravel", true).setComment("Useful for survival."));
        configOptions.add(new ConfigOption("experienceMultiplier", 1.0).setComment("The amount of experience to use per meter"));
        configOptions.add(new ConfigOption("experienceUsageDimensionTravel", 50).setComment("The amount of experience to use when traveling "
          + "between dimensions"));
        configOptions.add(new ConfigOption("masterListForOpOnly", true).setComment("Whether or not only OP can use /fasttravel set|del"));
        configOptions.add(new ConfigOption("enablePrivateList", true).setComment("Allows users to set their own waypoints."));
        configOptions.add(new ConfigOption("allowBookList", true).setComment("Allows users to spawn a book with all the waypoints listed"));
    }
}
