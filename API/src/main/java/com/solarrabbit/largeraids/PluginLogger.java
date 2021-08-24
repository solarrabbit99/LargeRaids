package com.solarrabbit.largeraids;

import org.bukkit.ChatColor;

public class PluginLogger {
    public enum Level {
        FAIL(ChatColor.RED), INFO(ChatColor.AQUA), WARN(ChatColor.YELLOW), SUCCESS(ChatColor.GREEN);

        Level(ChatColor color) {
        }
    }

    public void sendMessage(String message, Level type) {
    }
}
