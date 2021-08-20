package com.solarrabbit.largeraids.listener;

import com.solarrabbit.largeraids.item.SummonItem;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

public class DropInLavaTriggerListener extends TriggerListener {

    @EventHandler
    public void onDropTotem(PlayerDropItemEvent evt) {
        Item entity = evt.getItemDrop();
        if (!SummonItem.isSummonItem(entity.getItemStack()))
            return;

        PersistentDataContainer pdc = entity.getPersistentDataContainer();
        pdc.set(this.getNamespacedKey(), PersistentDataType.STRING, evt.getPlayer().getUniqueId().toString());
    }

    @EventHandler
    public void onItemBurnInLava(EntityDamageEvent evt) {
        if (evt.getEntityType() != EntityType.DROPPED_ITEM)
            return;
        Item entity = (Item) evt.getEntity();
        if (!entity.getPersistentDataContainer().has(this.getNamespacedKey(), PersistentDataType.STRING))
            return;
        if (evt.getCause() == DamageCause.LAVA) {
            entity.remove();
            this.triggerRaid(entity.getLocation());
        }
    }

    private NamespacedKey getNamespacedKey() {
        return this.getPlugin().getNamespacedKey("dropped-summon-item");
    }

}