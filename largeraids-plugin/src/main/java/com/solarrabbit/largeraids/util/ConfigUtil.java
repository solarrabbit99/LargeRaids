package com.solarrabbit.largeraids.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

public class ConfigUtil {
    public static YamlConfiguration getYamlConfig(InputStream source) {
        YamlConfiguration config = new YamlConfiguration();
        try {
            config.load(new InputStreamReader(source));
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }
        return config;
    }
}
