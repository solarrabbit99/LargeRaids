package com.solarrabbit.largeraids;

import org.bukkit.ChatColor;

public class PluginLogger {
    public enum Level {
        FAIL(ChatColor.RED), INFO(ChatColor.AQUA), WARN(ChatColor.YELLOW), SUCCESS(ChatColor.GREEN);

        private ChatColor color;

        Level(ChatColor color) {
            this.color = color;
        }
    }

    public void sendMessage(String message, Level type) {
    }
}
