package com.solarrabbit.largeraids.listener.omen;

import java.util.function.Consumer;
import com.solarrabbit.largeraids.LargeRaids;
import com.solarrabbit.largeraids.listener.TriggerListener;
import com.solarrabbit.largeraids.raid.LargeRaid;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.raid.RaidTriggerEvent;
import org.bukkit.potion.PotionEffectType;

public class VillageAbsorbOmenListener extends TriggerListener {
    private final KillCaptainListener killCaptainListener;
    private final IteratePlayersInRaidTask task;

    public VillageAbsorbOmenListener(LargeRaids plugin) {
        super(plugin);
        this.killCaptainListener = new KillCaptainListener(plugin);
        Bukkit.getPluginManager().registerEvents(killCaptainListener, plugin);
        this.task = new IteratePlayersInRaidTask();
        plugin.getBukkitRaidListener().registerTickTask(task);
    }

    @EventHandler
    public void onRaidCreation(RaidTriggerEvent evt) {
        if (plugin.getBukkitRaidListener().isIdle())
            return;
        evt.setCancelled(true);
        Player player = evt.getPlayer();
        int recordedLevel = this.killCaptainListener.getRecordedOmenLevel(player);
        triggerRaid(player, player.getLocation(), recordedLevel);
        killCaptainListener.resetOmenLevel(player);
    }

    @Override
    public void unregisterListener() {
        plugin.getBukkitRaidListener().unregisterTickTask(task);
        RaidTriggerEvent.getHandlerList().unregister(this);
        this.killCaptainListener.unregisterListener();
    }

    private class IteratePlayersInRaidTask implements Consumer<LargeRaid> {

        @Override
        public void accept(LargeRaid raid) {
            boolean hasReleasedOmen = raid.releaseOmen();
            for (Player player : raid.getPlayersInInnerRadius()) {
                int omenLevel = killCaptainListener.getRecordedOmenLevel(player);
                int actualOmenLevel = killCaptainListener.getCurrentOmenLevel(player);
                if (omenLevel != 0 || actualOmenLevel != 0) {
                    player.removePotionEffect(PotionEffectType.BAD_OMEN);
                    killCaptainListener.resetOmenLevel(player);
                    if (hasReleasedOmen || actualOmenLevel != 0)
                        raid.absorbOmenLevel(omenLevel);
                }
            }
        }

    }

}
