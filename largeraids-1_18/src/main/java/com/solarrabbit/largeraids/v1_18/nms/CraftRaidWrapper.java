package com.solarrabbit.largeraids.v1_18.nms;

import java.lang.reflect.Field;

import com.solarrabbit.largeraids.nms.AbstractCraftRaidWrapper;
import com.solarrabbit.largeraids.nms.AbstractRaidWrapper;

import org.bukkit.Raid;
import org.bukkit.craftbukkit.v1_18_R1.CraftRaid;

public class CraftRaidWrapper extends AbstractCraftRaidWrapper {

    public CraftRaidWrapper(AbstractRaidWrapper nmsRaid) {
        super(new CraftRaid(((RaidWrapper) nmsRaid).raid));
    }

    public CraftRaidWrapper(Raid raid) {
        super(raid);
    }

    @Override
    public RaidWrapper getHandle() {
        net.minecraft.world.entity.raid.Raid nmsRaid;
        try {
            Field field = CraftRaid.class.getDeclaredField("handle");
            field.setAccessible(true);
            nmsRaid = (net.minecraft.world.entity.raid.Raid) field.get(raid);
        } catch (ReflectiveOperationException e) {
            e.printStackTrace();
            nmsRaid = null;
        }
        return new RaidWrapper(nmsRaid);
    }

}
