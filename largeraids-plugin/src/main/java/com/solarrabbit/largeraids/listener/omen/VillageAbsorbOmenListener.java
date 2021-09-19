package com.solarrabbit.largeraids.listener.omen;

import java.util.function.Consumer;
import com.solarrabbit.largeraids.AbstractLargeRaid;
import com.solarrabbit.largeraids.LargeRaids;
import com.solarrabbit.largeraids.listener.RaidListener;
import com.solarrabbit.largeraids.listener.TriggerListener;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.raid.RaidTriggerEvent;

public class VillageAbsorbOmenListener extends TriggerListener {
    private final KillCaptainListener killCaptainListener;
    private final IteratePlayersInRaidTask task;

    public VillageAbsorbOmenListener(LargeRaids plugin) {
        this.killCaptainListener = new KillCaptainListener(plugin);
        Bukkit.getPluginManager().registerEvents(this.killCaptainListener, plugin);
        this.task = new IteratePlayersInRaidTask();
        RaidListener.registerTickTask(task);
    }

    @EventHandler
    public void onRaidCreation(RaidTriggerEvent evt) {
        if (RaidListener.matchingLargeRaid(evt.getRaid()).isPresent())
            return;
        evt.setCancelled(true);
        Player player = evt.getPlayer();
        int recordedLevel = this.killCaptainListener.getRecordedOmenLevel(player);
        this.triggerRaid(player, recordedLevel);
        killCaptainListener.resetOmenLevel(player);
    }

    @Override
    public void unregisterListener() {
        RaidListener.unregisterTickTask(task);
        RaidTriggerEvent.getHandlerList().unregister(this);
        this.killCaptainListener.unregisterListener();
    }

    private class IteratePlayersInRaidTask implements Consumer<AbstractLargeRaid> {

        @Override
        public void accept(AbstractLargeRaid raid) {
            for (Player player : raid.getPlayersInInnerRadius()) {
                int omenLevel = killCaptainListener.getRecordedOmenLevel(player);
                if (omenLevel != 0) {
                    raid.absorbOmenLevel(omenLevel);
                    killCaptainListener.resetOmenLevel(player);
                }
            }
        }

    }

}
