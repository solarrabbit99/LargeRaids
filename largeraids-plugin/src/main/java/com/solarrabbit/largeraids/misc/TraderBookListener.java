package com.solarrabbit.largeraids.misc;

import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;

public class TraderBookListener implements Listener {
    private static final double DROP_CHANCE = 0.1f;
    private final BookGenerator bookGen;

    public TraderBookListener(BookGenerator bookGen) {
        this.bookGen = bookGen;
    }

    @EventHandler
    public void onTraderDeath(EntityDeathEvent evt) {
        if (evt.getEntityType() != EntityType.WANDERING_TRADER)
            return;
        LivingEntity entity = evt.getEntity();
        if (hasDrop())
            entity.getWorld().dropItem(entity.getLocation(), bookGen.getBook());
    }

    private boolean hasDrop() {
        return Math.random() <= DROP_CHANCE;
    }
}
