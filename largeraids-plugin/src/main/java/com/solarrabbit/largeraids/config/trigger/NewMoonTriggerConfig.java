package com.solarrabbit.largeraids.config.trigger;

import org.bukkit.configuration.ConfigurationSection;

public class NewMoonTriggerConfig implements TriggerConfig {
    private final boolean isEnabled;

    NewMoonTriggerConfig(ConfigurationSection config) {
        isEnabled = config.getBoolean("enabled");
    }

    @Override
    public boolean isEnabled() {
        return isEnabled;
    }
}
