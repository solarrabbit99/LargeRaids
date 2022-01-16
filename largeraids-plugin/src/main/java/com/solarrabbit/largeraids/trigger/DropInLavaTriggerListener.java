package com.solarrabbit.largeraids.trigger;

import java.util.Optional;
import java.util.UUID;

import com.solarrabbit.largeraids.LargeRaids;
import com.solarrabbit.largeraids.config.trigger.DropInLavaTriggerConfig;
import com.solarrabbit.largeraids.raid.LargeRaid;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

public class DropInLavaTriggerListener extends Trigger {

    public DropInLavaTriggerListener(LargeRaids plugin) {
        super(plugin);
    }

    @EventHandler
    public void onDropTotem(PlayerDropItemEvent evt) {
        Item entity = evt.getItemDrop();
        if (!DropInLavaTriggerConfig.isSummonItem(entity.getItemStack()))
            return;

        PersistentDataContainer pdc = entity.getPersistentDataContainer();
        pdc.set(getNamespacedKey(), PersistentDataType.STRING, evt.getPlayer().getUniqueId().toString());
    }

    @EventHandler
    public void onItemBurnInLava(EntityDamageEvent evt) {
        if (evt.getEntityType() != EntityType.DROPPED_ITEM)
            return;
        Item entity = (Item) evt.getEntity();
        if (!entity.getPersistentDataContainer().has(getNamespacedKey(), PersistentDataType.STRING))
            return;
        if (evt.getCause() != DamageCause.LAVA)
            return;

        UUID uuid = UUID.fromString(
                entity.getPersistentDataContainer().get(getNamespacedKey(), PersistentDataType.STRING));
        entity.remove();
        Player player = Bukkit.getPlayer(uuid);
        Location location = entity.getLocation();
        Optional<LargeRaid> currentRaid = plugin.getRaidManager().getLargeRaid(location);
        int contributeLevel = plugin.getTriggerConfig().getDropInLavaConfig().getContributeOmenLevel();
        if (!currentRaid.isPresent())
            if (contributeLevel <= 0)
                triggerRaid(player, location);
            else
                triggerRaid(player, location, contributeLevel);
        else
            plugin.getRaidManager().extendRaid(currentRaid.get(), contributeLevel);
    }

    @Override
    public void unregisterListener() {
        EntityDamageEvent.getHandlerList().unregister(this);
        PlayerDropItemEvent.getHandlerList().unregister(this);
    }

    private NamespacedKey getNamespacedKey() {
        return new NamespacedKey(plugin, "dropped-summon-item");
    }

}
