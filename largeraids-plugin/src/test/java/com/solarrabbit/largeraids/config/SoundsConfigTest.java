package com.solarrabbit.largeraids.config;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;

import org.bukkit.Sound;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.junit.Test;

public class SoundsConfigTest {
    private static final File SRC_DIR = new File("src");
    private static final File TEST_DIR = new File(SRC_DIR, "test");
    private static final File RESOURCE_DIR = new File(TEST_DIR, "resources");
    private static SoundsConfig instance;

    private SoundsConfig getSoundConfigOne() throws IOException, InvalidConfigurationException {
        if (instance != null)
            return instance;
        File file = new File(RESOURCE_DIR, "raidconfig1.yml");
        Reader reader = new InputStreamReader(new FileInputStream(file));
        FileConfiguration fileConfig = new YamlConfiguration();
        fileConfig.load(reader);
        ConfigurationSection configSection = fileConfig.getConfigurationSection("raid.sounds");
        instance = new SoundsConfig(configSection);
        return instance;
    }

    @Test
    public void getSummonSound_blockBreak_success() throws IOException, InvalidConfigurationException {
        SoundsConfig config = getSoundConfigOne();
        assertEquals(Sound.BLOCK_STONE_BREAK, config.getSummonSound());
    }

    @Test
    public void getVictorySound_blockBreak_success() throws IOException, InvalidConfigurationException {
        SoundsConfig config = getSoundConfigOne();
        assertEquals(Sound.BLOCK_GLASS_BREAK, config.getVictorySound());
    }

    @Test
    public void getDefeatSound_portal_success() throws IOException, InvalidConfigurationException {
        SoundsConfig config = getSoundConfigOne();
        assertEquals(Sound.BLOCK_END_PORTAL_SPAWN, config.getDefeatSound());
    }
}
