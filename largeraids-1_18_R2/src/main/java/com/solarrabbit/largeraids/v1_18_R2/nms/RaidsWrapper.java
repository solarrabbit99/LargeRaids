package com.solarrabbit.largeraids.v1_18_R2.nms;

import com.solarrabbit.largeraids.nms.AbstractPlayerEntityWrapper;
import com.solarrabbit.largeraids.nms.AbstractRaidsWrapper;

import net.minecraft.world.entity.raid.PersistentRaid;

public class RaidsWrapper implements AbstractRaidsWrapper {
    private final PersistentRaid raids;

    RaidsWrapper(PersistentRaid raids) {
        this.raids = raids;
    }

    @Override
    public RaidWrapper createOrExtendRaid(AbstractPlayerEntityWrapper player) {
        return new RaidWrapper(this.raids.a(((PlayerEntityWrapper) player).player));
    }

    @Override
    public void setDirty() {
        this.raids.b();
    }

}
