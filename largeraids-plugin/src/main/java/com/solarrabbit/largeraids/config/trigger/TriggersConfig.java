package com.solarrabbit.largeraids.config.trigger;

import org.bukkit.configuration.ConfigurationSection;

public class TriggersConfig {
    private final OmenTriggerConfig omenConfig;
    private final DropInLavaTriggerConfig dropInLavaConfig;
    private final NewMoonTriggerConfig newMoonConfig;
    private final boolean canNormalRaid;
    private final boolean isArtificialOnly;

    public TriggersConfig(ConfigurationSection config) {
        omenConfig = new OmenTriggerConfig(config.getConfigurationSection("omen"));
        dropInLavaConfig = new DropInLavaTriggerConfig(config.getConfigurationSection("drop-item-in-lava"));
        newMoonConfig = new NewMoonTriggerConfig(config.getConfigurationSection("new-moon"));
        canNormalRaid = config.getBoolean("enable-normal-raids");
        isArtificialOnly = config.getBoolean("artificial-only");
    }

    public boolean canNormalRaid() {
        return canNormalRaid;
    }

    public boolean isArtificialOnly() {
        return isArtificialOnly;
    }

    public OmenTriggerConfig getOmenConfig() {
        return omenConfig;
    }

    public DropInLavaTriggerConfig getDropInLavaConfig() {
        return dropInLavaConfig;
    }

    public NewMoonTriggerConfig getNewMoonConfig() {
        return newMoonConfig;
    }
}
