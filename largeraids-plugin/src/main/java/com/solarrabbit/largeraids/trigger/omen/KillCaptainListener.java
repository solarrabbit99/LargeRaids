package com.solarrabbit.largeraids.trigger.omen;

import com.solarrabbit.largeraids.LargeRaids;
import com.solarrabbit.largeraids.trigger.Trigger;
import com.solarrabbit.largeraids.util.VersionUtil;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityPotionEffectEvent;
import org.bukkit.event.entity.EntityPotionEffectEvent.Cause;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class KillCaptainListener extends Trigger {
    private static final int DEFAULT_EFFECT_DURATION = 100 * 60 * 20;
    private int prevAmplifier;

    public KillCaptainListener(LargeRaids plugin) {
        super(plugin);
        prevAmplifier = -1;
    }

    /**
     * Handles event where server intends to apply/increase players' omen levels on
     * patrol captain kills.
     *
     * @param evt event described above
     */
    @EventHandler
    public void onOmenIncrease(EntityPotionEffectEvent evt) {
        PotionEffectType type = evt.getModifiedType();
        if (type == null || !type.equals(PotionEffectType.BAD_OMEN))
            return;
        PotionEffect effect = evt.getOldEffect();
        switch (evt.getAction()) {
            case REMOVED:
                if (evt.getCause() != Cause.UNKNOWN)
                    return;
                prevAmplifier = effect == null ? -1 : effect.getAmplifier();
                break;
            case ADDED:
                Cause cause = VersionUtil.getServerMinorVersion() == 14 ? Cause.UNKNOWN : Cause.PATROL_CAPTAIN;
                if (evt.getCause() != cause) // Could be added by commands or the plugin itself
                    return;
                if (evt.getNewEffect().getAmplifier() == 0) // Ignore newly applied effect
                    return;
                evt.setCancelled(true);
                Player killer = (Player) evt.getEntity();
                int maxAllowed = plugin.getTriggerConfig().getOmenConfig().getMaxLevel();
                killer.addPotionEffect(
                        new PotionEffect(PotionEffectType.BAD_OMEN, DEFAULT_EFFECT_DURATION,
                                Math.min(maxAllowed - 1, prevAmplifier + 1)));
                break;
            default:
                break;
        }
    }

    @Override
    public void unregisterListener() {
        EntityPotionEffectEvent.getHandlerList().unregister(this);
    }

}
