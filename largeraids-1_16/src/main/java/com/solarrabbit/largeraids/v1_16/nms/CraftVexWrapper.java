package com.solarrabbit.largeraids.v1_16.nms;

import com.solarrabbit.largeraids.nms.AbstractCraftVexWrapper;

import org.bukkit.craftbukkit.v1_16_R3.entity.CraftVex;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Vex;

import net.minecraft.server.v1_16_R3.EntityInsentient;

public class CraftVexWrapper implements AbstractCraftVexWrapper {
    private final Vex vex;

    public CraftVexWrapper(Vex vex) {
        this.vex = vex;
    }

    @Override
    public LivingEntity getOwner() {
        EntityInsentient owner = ((CraftVex) vex).getHandle().eK();
        return owner == null ? null : (LivingEntity) owner.getBukkitEntity();
    }
}
