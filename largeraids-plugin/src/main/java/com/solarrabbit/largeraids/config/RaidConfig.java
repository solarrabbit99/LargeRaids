package com.solarrabbit.largeraids.config;

import javax.annotation.Nullable;

import com.solarrabbit.largeraids.util.ChatColorUtil;

import org.bukkit.configuration.ConfigurationSection;

/**
 * Encapsulates raid related configurations in {@code config.yml}.
 */
public class RaidConfig {
    private final SoundsConfig soundsConfig;
    private final RaiderConfig raiderConfig;

    private final int maximumWaves;
    private final boolean isAlwaysMax;
    private final boolean isTitleEnabled;
    private final String defaultWaveTitle;
    private final String finalWaveTitle;
    private final boolean isMessageEnabled;
    private final String defaultWaveMessage;
    private final String finalWaveMessage;

    public RaidConfig(ConfigurationSection config) {
        soundsConfig = new SoundsConfig(config.getConfigurationSection("sounds"));
        raiderConfig = new RaiderConfig(config.getConfigurationSection("mobs"));
        maximumWaves = config.getInt("waves");
        isAlwaysMax = config.getBoolean("always-max-waves");
        ConfigurationSection waveAnnouncementConfig = config.getConfigurationSection("announce-waves");
        ConfigurationSection titleConfig = waveAnnouncementConfig.getConfigurationSection("title");
        isTitleEnabled = titleConfig.getBoolean("enabled");
        defaultWaveTitle = titleConfig.getString("default", null);
        finalWaveTitle = titleConfig.getString("final", null);
        ConfigurationSection messageConfig = waveAnnouncementConfig.getConfigurationSection("message");
        isMessageEnabled = messageConfig.getBoolean("enabled");
        defaultWaveMessage = messageConfig.getString("default", null);
        finalWaveMessage = messageConfig.getString("final", null);
    }

    public SoundsConfig getSounds() {
        return soundsConfig;
    }

    public RaiderConfig getRaiders() {
        return raiderConfig;
    }

    public int getMaximumWaves() {
        return maximumWaves;
    }

    public boolean isAlwaysMaxWaves() {
        return isAlwaysMax;
    }

    public boolean isTitleEnabled() {
        return isTitleEnabled;
    }

    @Nullable
    public String getDefaultWaveTitle(int wave) {
        return defaultWaveTitle == null ? null : ChatColorUtil.colorize(String.format(defaultWaveTitle, wave));
    }

    @Nullable
    public String getFinalWaveTitle() {
        return finalWaveTitle == null ? null : ChatColorUtil.colorize(finalWaveTitle);
    }

    public boolean isMessageEnabled() {
        return isMessageEnabled;
    }

    @Nullable
    public String getDefaultWaveMessage(int wave) {
        return defaultWaveMessage == null ? null : ChatColorUtil.colorize(String.format(defaultWaveMessage, wave));
    }

    @Nullable
    public String getFinalWaveMessage() {
        return finalWaveMessage == null ? null : ChatColorUtil.colorize(finalWaveMessage);
    }
}
