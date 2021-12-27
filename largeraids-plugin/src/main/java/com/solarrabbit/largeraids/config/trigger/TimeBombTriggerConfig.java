package com.solarrabbit.largeraids.config.trigger;

import java.util.List;

import org.bukkit.configuration.ConfigurationSection;

public class TimeBombTriggerConfig implements TriggerConfig {
    private final boolean isEnabled;
    private final List<Integer> ticks;

    TimeBombTriggerConfig(ConfigurationSection config) {
        isEnabled = config.getBoolean("enabled");
        ticks = config.getIntegerList("ticks");
    }

    @Override
    public boolean isEnabled() {
        return isEnabled;
    }

    public List<Integer> getTicks() {
        return ticks;
    }
}
