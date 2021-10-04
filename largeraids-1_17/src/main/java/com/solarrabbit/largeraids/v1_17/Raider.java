package com.solarrabbit.largeraids.v1_17;

import com.solarrabbit.largeraids.raid.mob.AbstractRaider;
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftRaider;
import net.minecraft.server.level.ServerLevel;

public class Raider implements AbstractRaider {
    private final net.minecraft.world.entity.raid.Raider raider;

    public Raider(org.bukkit.entity.Raider raider) {
        this.raider = ((CraftRaider) raider).getHandle();
    }

    @Override
    public boolean canGiveOmen() {
        return raider.level instanceof ServerLevel && raider.isPatrolLeader() && raider.getCurrentRaid() == null
                && ((ServerLevel) raider.level).getRaidAt(raider.blockPosition()) == null;
    }

}
