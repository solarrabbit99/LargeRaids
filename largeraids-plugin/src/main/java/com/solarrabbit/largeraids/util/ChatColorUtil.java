package com.solarrabbit.largeraids.util;

import org.bukkit.ChatColor;

public class ChatColorUtil {
    public static String translate(String str) {
        return ChatColor.translateAlternateColorCodes('&', str);
    }
}
