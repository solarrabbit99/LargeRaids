package com.solarrabbit.largeraids.config.trigger;

import com.solarrabbit.largeraids.LargeRaids;
import com.solarrabbit.largeraids.util.ItemCreator;

import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;

public class DropInLavaTriggerConfig implements TriggerConfig {
    private final boolean isEnabled;
    private final int contributeOmenLevel;
    private final ItemStack item;

    DropInLavaTriggerConfig(ConfigurationSection config) {
        isEnabled = config.getBoolean("enabled");
        contributeOmenLevel = config.getInt("contribute-omen-level");

        ConfigurationSection itemConf = config.getConfigurationSection("item");
        item = ItemCreator.getItemFromConfig(itemConf);
        ItemMeta meta = item.getItemMeta();
        PersistentDataContainer pdc = meta.getPersistentDataContainer();
        pdc.set(getNamespacedKey(), PersistentDataType.BYTE, (byte) 0);

        if (itemConf.getBoolean("enchantment-glint")) {
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            item.setItemMeta(meta);
            item.addUnsafeEnchantment(Enchantment.MENDING, 1);
        } else
            item.setItemMeta(meta);
    }

    @Override
    public boolean isEnabled() {
        return isEnabled;
    }

    public int getContributeOmenLevel() {
        return contributeOmenLevel;
    }

    public ItemStack getItem() {
        return item.clone();
    }

    public static boolean isSummonItem(ItemStack item) {
        return item != null && item.hasItemMeta()
                && item.getItemMeta().getPersistentDataContainer().has(getNamespacedKey(), PersistentDataType.BYTE);
    }

    private static NamespacedKey getNamespacedKey() {
        LargeRaids plugin = JavaPlugin.getPlugin(LargeRaids.class);
        return new NamespacedKey(plugin, "summon-item");
    }
}
