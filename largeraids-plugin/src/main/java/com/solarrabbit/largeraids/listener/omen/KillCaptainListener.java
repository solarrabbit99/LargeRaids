package com.solarrabbit.largeraids.listener.omen;

import java.util.Optional;
import com.solarrabbit.largeraids.LargeRaids;
import com.solarrabbit.largeraids.listener.TriggerListener;
import com.solarrabbit.largeraids.util.VersionUtil;
import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Raider;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class KillCaptainListener extends TriggerListener {
    private static final int DEFAULT_EFFECT_DURATION = 100 * 60 * 20;
    private static final String OMEN_LEVEL_KEY = "bad-omen-level";
    private static final String OMEN_TICK_KEY = "bad-omen-tick";
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
        if (killer != null && canGiveOmen(raider))
            incrementBadOmenLevel(killer);
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent evt) {
        resetOmenLevel(evt.getEntity());
    }

    @EventHandler
    public void onDrinkMilk(PlayerItemConsumeEvent evt) {
        if (evt.getItem().getType() == Material.MILK_BUCKET)
            resetOmenLevel(evt.getPlayer());
    }

    @Override
    public void unregisterListener() {
        EntityDeathEvent.getHandlerList().unregister(this);
        PlayerDeathEvent.getHandlerList().unregister(this);
        PlayerItemConsumeEvent.getHandlerList().unregister(this);
    }

    private void incrementBadOmenLevel(Player player) {
        int recordedLevel = this.getRecordedOmenLevel(player);
        int maxAllowed = this.plugin.getConfig().getInt("trigger.omen.max-level");
        if (getCurrentOmenLevel(player) == 5)
            applyOmenLevel(player, Math.min(recordedLevel + 1, maxAllowed));
        syncOmenLevel(player);
    }

    protected void syncOmenLevel(Player player) {
        int currentLevel = this.getCurrentOmenLevel(player);
        PersistentDataContainer pdc = player.getPersistentDataContainer();
        pdc.set(this.plugin.getNamespacedKey(OMEN_LEVEL_KEY), PersistentDataType.INTEGER, currentLevel);
        pdc.set(this.plugin.getNamespacedKey(OMEN_TICK_KEY), PersistentDataType.INTEGER, player.getTicksLived());
    }

    protected void resetOmenLevel(Player player) {
        PersistentDataContainer pdc = player.getPersistentDataContainer();
        pdc.remove(this.plugin.getNamespacedKey(OMEN_LEVEL_KEY));
        pdc.remove(this.plugin.getNamespacedKey(OMEN_TICK_KEY));
    }

    protected int getRecordedOmenLevel(Player player) {
        Optional<PersistentDataContainer> pdc = Optional.of(player).map(Player::getPersistentDataContainer);
        Optional<Integer> validRecordedTick = pdc.map(
                container -> container.get(this.plugin.getNamespacedKey(OMEN_TICK_KEY), PersistentDataType.INTEGER))
                .filter(lastTick -> player.getTicksLived() - lastTick < getEffectDuration(player));
        int recordedLevel = pdc.map(
                container -> container.get(this.plugin.getNamespacedKey(OMEN_LEVEL_KEY), PersistentDataType.INTEGER))
                .orElse(0);
        return validRecordedTick.isPresent() ? recordedLevel : 0;
    }

    private boolean canGiveOmen(Raider raider) {
        return VersionUtil.fromRaider(raider).canGiveOmen();
    }

    private void applyOmenLevel(Player player, int level) {
        // Patch for v1_14_R1
        player.removePotionEffect(PotionEffectType.BAD_OMEN);
        player.addPotionEffect(new PotionEffect(PotionEffectType.BAD_OMEN, getEffectDuration(player), level - 1));
    }

    /**
     * Returns the default omen effect duration of 40 minutes (48000 ticks) if
     * player do not have an omen effect. Otherwise, it will either be the current
     * effect's duration or the default omen effect duration, whichever is higher.
     *
     * @param player to apply omen on
     * @return duration for applying the omen effect on the player
     */
    private int getEffectDuration(Player player) {
        PotionEffect effect = player.getPotionEffect(PotionEffectType.BAD_OMEN);
        return effect == null ? DEFAULT_EFFECT_DURATION : Math.max(effect.getDuration(), DEFAULT_EFFECT_DURATION);
    }

    public int getCurrentOmenLevel(Player player) {
        return Optional.ofNullable(player.getPotionEffect(PotionEffectType.BAD_OMEN))
                .map(effect -> effect.getAmplifier() + 1).orElse(0);
    }

}
