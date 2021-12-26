package com.solarrabbit.largeraids.util;

import org.bukkit.ChatColor;

public class ChatColorUtil {
    public static String colorize(String str) {
        return ChatColor.translateAlternateColorCodes('&', str);
    }
}
