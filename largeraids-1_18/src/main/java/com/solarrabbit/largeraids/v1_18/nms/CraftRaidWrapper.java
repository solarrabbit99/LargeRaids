package com.solarrabbit.largeraids.v1_18.nms;

import com.solarrabbit.largeraids.nms.AbstractCraftRaidWrapper;
import com.solarrabbit.largeraids.nms.AbstractRaidWrapper;

import org.bukkit.craftbukkit.v1_18_R1.CraftRaid;

public class CraftRaidWrapper extends AbstractCraftRaidWrapper {

    public CraftRaidWrapper(AbstractRaidWrapper nmsRaid) {
        super(new CraftRaid(((RaidWrapper) nmsRaid).raid));
    }

}
