package com.solarrabbit.largeraids.v1_15;

import com.solarrabbit.largeraids.AbstractRaider;
import org.bukkit.craftbukkit.v1_15_R1.entity.CraftRaider;
import net.minecraft.server.v1_15_R1.EntityRaider;
import net.minecraft.server.v1_15_R1.WorldServer;

public class Raider implements AbstractRaider {
    private final EntityRaider raider;

    public Raider(org.bukkit.entity.Raider raider) {
        this.raider = ((CraftRaider) raider).getHandle();
    }

    @Override
    public boolean canGiveOmen() {
        return raider.world instanceof WorldServer && raider.isPatrolLeader() && raider.eE() == null
                && ((WorldServer) raider.world).c_(raider.getChunkCoordinates()) == null;
    }

}
