package com.solarrabbit.largeraids.event;

import com.solarrabbit.largeraids.raid.LargeRaid;

import org.bukkit.event.Cancellable;

public class LargeRaidTriggerEvent extends LargeRaidEvent implements Cancellable {
    private boolean isCancelled;

    public LargeRaidTriggerEvent(LargeRaid raid) {
        super(raid);
    }

    @Override
    public boolean isCancelled() {
        return isCancelled;
    }

    @Override
    public void setCancelled(boolean cancel) {
        isCancelled = cancel;
    }

}
