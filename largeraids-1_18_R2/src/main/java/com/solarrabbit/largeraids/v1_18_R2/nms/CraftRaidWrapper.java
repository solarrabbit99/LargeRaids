package com.solarrabbit.largeraids.v1_18_R2.nms;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Field;

import com.solarrabbit.largeraids.nms.AbstractCraftRaidWrapper;
import com.solarrabbit.largeraids.nms.AbstractRaidWrapper;

import org.bukkit.Raid;
import org.bukkit.craftbukkit.v1_18_R2.CraftRaid;

public class CraftRaidWrapper extends AbstractCraftRaidWrapper {
    private static MethodHandle handle;

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
            if (handle == null) {
                Field field = CraftRaid.class.getDeclaredField("handle");
                field.setAccessible(true);
                handle = MethodHandles.lookup().unreflectGetter(field);
            }
            nmsRaid = (net.minecraft.world.entity.raid.Raid) handle.invokeExact((CraftRaid) raid);
        } catch (Throwable e) {
            e.printStackTrace();
            nmsRaid = null;
        }
        return new RaidWrapper(nmsRaid);
    }

}
