package com.solarrabbit.largeraids;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.ConsoleCommandSender;

/**
 * Standardized logger for the plugin.
 */
public class PluginLogger {
    /**
     * Level of log message.
     */
    public enum Level {
        FAIL(ChatColor.RED), INFO(ChatColor.AQUA), WARN(ChatColor.YELLOW), SUCCESS(ChatColor.GREEN);

        private ChatColor color;

        Level(ChatColor color) {
            this.color = color;
        }

        @Override
        public String toString() {
            return this.color.toString();
        }
    }

    /**
     * Uses {@link ConsoleCommandSender} to log color-coded messages.
     * 
     * @param message message to send to console
     * @param type    {@link PluginLogger.Level} associated with the message
     */
    public void sendMessage(String message, Level type) {
        Bukkit.getConsoleSender().sendMessage(type.toString() + ChatColor.BOLD + "[LargeRaids] "
                + ChatColor.translateAlternateColorCodes('&', message));
    }
}
