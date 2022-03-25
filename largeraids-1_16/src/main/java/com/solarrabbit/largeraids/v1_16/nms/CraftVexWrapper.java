package com.solarrabbit.largeraids.v1_16.nms;

import com.solarrabbit.largeraids.nms.AbstractCraftVexWrapper;

import org.bukkit.craftbukkit.v1_16_R3.entity.CraftVex;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Vex;

public class CraftVexWrapper implements AbstractCraftVexWrapper {
    private final Vex vex;

    public CraftVexWrapper(Vex vex) {
        this.vex = vex;
    }

    @Override
    public LivingEntity getOwner() {
        return (LivingEntity) ((CraftVex) vex).getHandle().eK().getBukkitEntity();
    }
}
