package com.solarrabbit.largeraids.util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class ItemCreator {

    public static ItemStack getItemFromConfig(ConfigurationSection config) {
        ItemStack item = Optional.ofNullable(config.getString("material", null))
                .map(name -> Material.matchMaterial(name)).map(material -> new ItemStack(material))
                .orElse(new ItemStack(Material.STICK));

        ItemMeta meta = item.getItemMeta();
        Optional.ofNullable(config.getString("display-name", null))
                .ifPresent(name -> meta.setDisplayName(formatColor(name)));
        List<String> lore = config.getStringList("lore");
        lore.replaceAll(str -> formatColor(str));
        if (!lore.isEmpty())
            meta.setLore(lore);
        Optional.ofNullable(config.getInt("custom-model-data")).filter(data -> !data.equals(0))
                .ifPresent(data -> meta.setCustomModelData(data));
        item.setItemMeta(meta);

        Optional.ofNullable(config.getInt("amount")).filter(amt -> !amt.equals(0))
                .ifPresent(amt -> item.setAmount(amt));
        item.addUnsafeEnchantments(getEnchantments(config.getConfigurationSection("enchantments")));

        return item;
    }

    private static String formatColor(String string) {
        return ChatColor.translateAlternateColorCodes('&', "&f" + string);
    }

    private static Map<Enchantment, Integer> getEnchantments(ConfigurationSection config) {
        Map<Enchantment, Integer> map = new HashMap<>();

        if (config == null)
            return map;

        Set<String> keys = config.getKeys(false);
        keys.forEach(section -> {
            int level = config.getInt(section + ".level");
            Optional.ofNullable(config.getString(section + ".type", null)).map(str -> Enchantment.getByName(str))
                    .ifPresent(enchant -> map.put(enchant, level));
        });
        return map;
    }

}
