package com.solarrabbit.largeraids.event;

import com.solarrabbit.largeraids.raid.LargeRaid;

/**
 * This event is called when a {@link LargeRaid}'s omen level is increased.
 */
public class LargeRaidExtendEvent extends LargeRaidEvent {
    private final int oldLevel;
    private final int newLevel;

    public LargeRaidExtendEvent(LargeRaid raid, int oldLevel, int newLevel) {
        super(raid);
        this.oldLevel = oldLevel;
        this.newLevel = newLevel;
    }

    public int getOldLevel() {
        return oldLevel;
    }

    public int getNewLevel() {
        return newLevel;
    }

}
