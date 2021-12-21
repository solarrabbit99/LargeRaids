package com.solarrabbit.largeraids.v1_18.nms;

import com.solarrabbit.largeraids.nms.AbstractPlayerEntityWrapper;
import com.solarrabbit.largeraids.nms.AbstractRaidsWrapper;

import net.minecraft.world.entity.raid.Raids;

public class RaidsWrapper implements AbstractRaidsWrapper {
    private final Raids raids;

    RaidsWrapper(Raids raids) {
        this.raids = raids;
    }

    @Override
    public RaidWrapper createOrExtendRaid(AbstractPlayerEntityWrapper player) {
        return new RaidWrapper(this.raids.createOrExtendRaid(((PlayerEntityWrapper) player).player));
    }

    @Override
    public void setDirty() {
        this.raids.setDirty();
    }

}
