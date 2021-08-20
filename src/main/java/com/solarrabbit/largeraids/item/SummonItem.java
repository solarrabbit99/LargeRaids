package com.solarrabbit.largeraids.item;

import java.util.List;
import java.util.Optional;
import com.solarrabbit.largeraids.LargeRaids;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;

public class SummonItem extends ItemStack {

    public SummonItem() {
        super(Material.TOTEM_OF_UNDYING);
        ConfigurationSection conf = getPlugin().getConfig().getConfigurationSection("trigger.drop-item-in-lava.item");
        Optional.ofNullable(conf.getString("material", null)).map(name -> Material.matchMaterial(name))
                .ifPresent(material -> this.setType(material));

        ItemMeta meta = this.getItemMeta();
        Optional.ofNullable(conf.getString("display-name", null))
                .ifPresent(name -> meta.setDisplayName(formatColor(name)));
        List<String> lore = conf.getStringList("lore");
        lore.replaceAll(str -> formatColor(str));
        meta.setLore(lore);
        Optional.ofNullable(conf.getInt("custom-model-data")).filter(data -> !data.equals(0))
                .ifPresent(data -> meta.setCustomModelData(data));
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        PersistentDataContainer pdc = meta.getPersistentDataContainer();
        pdc.set(getNamespacedKey(), PersistentDataType.BYTE, (byte) 0);
        this.setItemMeta(meta);

        if (conf.getBoolean("enchantment-glint"))
            this.addUnsafeEnchantment(Enchantment.MENDING, 1);
    }

    public static boolean isSummonItem(ItemStack item) {
        return item != null && item.hasItemMeta()
                ? item.getItemMeta().getPersistentDataContainer().has(getNamespacedKey(), PersistentDataType.BYTE)
                : false;
    }

    private String formatColor(String string) {
        return ChatColor.translateAlternateColorCodes('&', "&f" + string);
    }

    private static LargeRaids getPlugin() {
        return JavaPlugin.getPlugin(LargeRaids.class);
    }

    private static NamespacedKey getNamespacedKey() {
        return getPlugin().getNamespacedKey("summon-item");
    }

}
