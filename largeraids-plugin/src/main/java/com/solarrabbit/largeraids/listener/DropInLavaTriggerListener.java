package com.solarrabbit.largeraids.listener;

import java.util.UUID;
import com.solarrabbit.largeraids.item.SummonItem;
import org.bukkit.Bukkit;
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
            UUID uuid = UUID.fromString(
                    entity.getPersistentDataContainer().get(this.getNamespacedKey(), PersistentDataType.STRING));
            entity.remove();
            this.triggerRaid(Bukkit.getPlayer(uuid));
        }
    }

    @Override
    public void unregisterListener() {
        EntityDamageEvent.getHandlerList().unregister(this);
        PlayerDropItemEvent.getHandlerList().unregister(this);
    }

    private NamespacedKey getNamespacedKey() {
        return this.getPlugin().getNamespacedKey("dropped-summon-item");
    }

}
