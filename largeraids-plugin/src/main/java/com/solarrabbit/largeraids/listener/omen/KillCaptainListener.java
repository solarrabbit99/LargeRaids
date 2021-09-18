package com.solarrabbit.largeraids.listener.omen;

import java.util.Optional;
import com.solarrabbit.largeraids.LargeRaids;
import com.solarrabbit.largeraids.listener.TriggerListener;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Raider;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class KillCaptainListener extends TriggerListener {
    private final LargeRaids plugin;

    public KillCaptainListener(LargeRaids plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onKillCaptain(EntityDeathEvent evt) {
        LivingEntity dead = evt.getEntity();
        if (!(dead instanceof Raider))
            return;

        Raider raider = (Raider) dead;
        Player killer = dead.getKiller();
        if (raider.isPatrolLeader() && killer != null)
            incrementPlayerBadOmenLevel(killer);
    }

    @Override
    public void unregisterListener() {
        EntityDeathEvent.getHandlerList().unregister(this);
    }

    private void incrementPlayerBadOmenLevel(Player player) {
        int recordedLevel = this.getRecordedOmenLevel(player);
        int currentLevel = this.getCurrentOmenLevel(player);
        if (currentLevel == 5 && recordedLevel >= 5) {
            int maxAllowed = this.plugin.getConfig().getInt("trigger.omen.max-level");
            applyOmenLevel(player, Math.min(recordedLevel + 1, maxAllowed));
        }
        this.syncOmenLevel(player);
    }

    private void syncOmenLevel(Player player) {
        int currentLevel = this.getCurrentOmenLevel(player);
        player.getPersistentDataContainer().set(this.plugin.getNamespacedKey("bad-omen"), PersistentDataType.INTEGER,
                currentLevel);
    }

    private int getRecordedOmenLevel(Player player) {
        return Optional.of(player).map(Player::getPersistentDataContainer)
                .map(pdc -> pdc.get(this.plugin.getNamespacedKey("bad-omen"), PersistentDataType.INTEGER)).orElse(0);
    }

    private int getCurrentOmenLevel(Player player) {
        return Optional.ofNullable(player.getPotionEffect(PotionEffectType.BAD_OMEN))
                .map(effect -> effect.getAmplifier() + 1).orElse(0);
    }

    /**
     * Player must already have an omen effect for the plugin to take reference of
     * its duration.
     *
     * @param player to apply level on
     * @param level  to apply to the player
     */
    private void applyOmenLevel(Player player, int level) {
        PotionEffect effect = player.getPotionEffect(PotionEffectType.BAD_OMEN);
        new PotionEffect(PotionEffectType.BAD_OMEN, effect.getDuration(), level - 1).apply(player);
    }

}
