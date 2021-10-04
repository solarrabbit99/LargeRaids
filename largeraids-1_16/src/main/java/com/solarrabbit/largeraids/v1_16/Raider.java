package com.solarrabbit.largeraids.v1_16;

import com.solarrabbit.largeraids.raid.AbstractRaider;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftRaider;
import net.minecraft.server.v1_16_R3.EntityRaider;
import net.minecraft.server.v1_16_R3.WorldServer;

public class Raider implements AbstractRaider {
    private final EntityRaider raider;

    public Raider(org.bukkit.entity.Raider raider) {
        this.raider = ((CraftRaider) raider).getHandle();
    }

    @Override
    public boolean canGiveOmen() {
        return raider.world instanceof WorldServer && raider.isPatrolLeader() && raider.fa() == null
                && ((WorldServer) raider.world).b_(raider.getChunkCoordinates()) == null;
    }

}
