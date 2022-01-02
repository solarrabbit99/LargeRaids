package com.solarrabbit.largeraids.config;

import javax.annotation.Nullable;

import org.bukkit.configuration.ConfigurationSection;

public class PlaceholderConfig {
    private final String notInRangeString;

    public PlaceholderConfig(ConfigurationSection conf) {
        notInRangeString = conf.getString("not-in-range-string", null);
    }

    @Nullable
    public String getNotInRangeString() {
        return notInRangeString;
    }
}
