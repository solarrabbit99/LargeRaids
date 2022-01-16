package com.solarrabbit.largeraids.trigger.omen;

import java.util.Set;
import java.util.stream.Collectors;

import com.solarrabbit.largeraids.LargeRaids;
import com.solarrabbit.largeraids.raid.LargeRaid;
import com.solarrabbit.largeraids.trigger.Trigger;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityPotionEffectEvent;
import org.bukkit.event.entity.EntityPotionEffectEvent.Action;
import org.bukkit.event.entity.EntityPotionEffectEvent.Cause;
import org.bukkit.event.raid.RaidTriggerEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class VillageAbsorbOmenListener extends Trigger {
    private final KillCaptainListener killCaptainListener;

    public VillageAbsorbOmenListener(LargeRaids plugin) {
        super(plugin);
        killCaptainListener = new KillCaptainListener(plugin);
        Bukkit.getPluginManager().registerEvents(killCaptainListener, plugin);
    }

    /**
     * Converts player's omen-triggered normal raids into {@link LargeRaid}s.
     */
    @EventHandler
    public void onVanillaRaidCreation(RaidTriggerEvent evt) {
        if (evt.getRaid().getBadOmenLevel() != 0) // Raid is getting extended
            return;
        if (plugin.getRaidManager().isIdle()) // LargeRaid triggering
            return;
        evt.setCancelled(true);
        Player player = evt.getPlayer();
        int amplifier = player.getPotionEffect(PotionEffectType.BAD_OMEN).getAmplifier();
        triggerRaid(player, player.getLocation(), amplifier + 1);
    }

    @EventHandler
    public void onEffectRemoval(EntityPotionEffectEvent evt) {
        PotionEffectType type = evt.getModifiedType();
        if (type == null || !type.equals(PotionEffectType.BAD_OMEN) || evt.getAction() != Action.REMOVED
                || evt.getCause() != Cause.UNKNOWN)
            return;
        Set<LargeRaid> affectedLargeRaids = plugin.getRaidManager().currentRaids.stream()
                .filter(LargeRaid::releaseOmen).collect(Collectors.toSet());
        if (affectedLargeRaids.size() > 1)
            throw new MultipleLargeRaidReleaseOmenException();
        PotionEffect effect = evt.getOldEffect();
        int absorbLevel = effect == null ? 0 : effect.getAmplifier() + 1;
        for (LargeRaid lr : affectedLargeRaids)
            plugin.getRaidManager().extendRaid(lr, absorbLevel);
    }

    @Override
    public void unregisterListener() {
        RaidTriggerEvent.getHandlerList().unregister(this);
        EntityPotionEffectEvent.getHandlerList().unregister(this);
        killCaptainListener.unregisterListener();
    }

    private class MultipleLargeRaidReleaseOmenException extends RuntimeException {

    }

}
