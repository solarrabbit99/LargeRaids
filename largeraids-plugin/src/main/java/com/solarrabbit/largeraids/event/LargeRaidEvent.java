package com.solarrabbit.largeraids.event;

import com.solarrabbit.largeraids.raid.LargeRaid;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public abstract class LargeRaidEvent extends Event {
    private static final HandlerList HANDLERS = new HandlerList();
    private final LargeRaid raid;

    protected LargeRaidEvent(LargeRaid raid) {
        this.raid = raid;
    }

    public LargeRaid getLargeRaid() {
        return raid;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
}
