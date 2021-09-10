package com.solarrabbit.largeraids.listener.omen;

import java.util.Optional;
import com.solarrabbit.largeraids.LargeRaids;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Raider;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class KillCaptainListener implements Listener {
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

    private void incrementPlayerBadOmenLevel(Player player) {
        int recordedLevel = this.getRecordedOmenLevel(player);
        int currentLevel = this.getCurrentOmenLevel(player);
        if (currentLevel == 5 && recordedLevel >= 5) {
            PotionEffect effect = player.getPotionEffect(PotionEffectType.BAD_OMEN);
            new PotionEffect(PotionEffectType.BAD_OMEN, effect.getDuration(), recordedLevel).apply(player);
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

}
