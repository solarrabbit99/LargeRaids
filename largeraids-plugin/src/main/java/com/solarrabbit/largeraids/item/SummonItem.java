package com.solarrabbit.largeraids.item;

import com.solarrabbit.largeraids.LargeRaids;
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
        super(ItemCreator
                .getItemFromConfig(getPlugin().getConfig().getConfigurationSection("trigger.drop-item-in-lava.item")));

        ConfigurationSection conf = getPlugin().getConfig().getConfigurationSection("trigger.drop-item-in-lava.item");
        ItemMeta meta = this.getItemMeta();
        PersistentDataContainer pdc = meta.getPersistentDataContainer();
        pdc.set(getNamespacedKey(), PersistentDataType.BYTE, (byte) 0);

        if (conf.getBoolean("enchantment-glint")) {
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            this.setItemMeta(meta);
            this.addUnsafeEnchantment(Enchantment.MENDING, 1);
        } else {
            this.setItemMeta(meta);
        }
    }

    public static boolean isSummonItem(ItemStack item) {
        return item != null && item.hasItemMeta()
                ? item.getItemMeta().getPersistentDataContainer().has(getNamespacedKey(), PersistentDataType.BYTE)
                : false;
    }

    private static LargeRaids getPlugin() {
        return JavaPlugin.getPlugin(LargeRaids.class);
    }

    private static NamespacedKey getNamespacedKey() {
        return getPlugin().getNamespacedKey("summon-item");
    }

}
