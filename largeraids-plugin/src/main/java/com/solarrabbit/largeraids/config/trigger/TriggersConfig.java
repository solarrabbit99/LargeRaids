package com.solarrabbit.largeraids.config.trigger;

import javax.annotation.Nullable;

import com.solarrabbit.largeraids.util.ChatColorUtil;

import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;

public class TriggersConfig {
    private final OmenTriggerConfig omenConfig;
    private final DropInLavaTriggerConfig dropInLavaConfig;
    private final TimeBombTriggerConfig timeBombConfig;
    private final boolean canNormalRaid;
    private final boolean isArtificialOnly;
    private final String broadcastMessage;

    public TriggersConfig(ConfigurationSection config) {
        omenConfig = new OmenTriggerConfig(config.getConfigurationSection("omen"));
        dropInLavaConfig = new DropInLavaTriggerConfig(config.getConfigurationSection("drop-item-in-lava"));
        timeBombConfig = new TimeBombTriggerConfig(config.getConfigurationSection("time-bomb"));
        canNormalRaid = config.getBoolean("enable-normal-raids");
        isArtificialOnly = config.getBoolean("artificial-only.enabled");
        broadcastMessage = config.getString("artificial-only.broadcast-message", null);
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

    public TimeBombTriggerConfig getTimeBombConfig() {
        return timeBombConfig;
    }

    @Nullable
    public String getBroadcastMessage(CommandSender triggerer, String center) {
        if (broadcastMessage == null)
            return null;
        String rawMessage = broadcastMessage.replaceAll("<player>", triggerer.getName()).replaceAll("<center>", center);
        return ChatColorUtil.colorize(rawMessage);
    }
}
