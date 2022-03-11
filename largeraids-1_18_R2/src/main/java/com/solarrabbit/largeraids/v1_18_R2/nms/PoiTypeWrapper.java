package com.solarrabbit.largeraids.v1_18_R2.nms;

import java.util.function.Predicate;

import com.solarrabbit.largeraids.nms.AbstractPoiTypeWrapper;

import net.minecraft.world.entity.ai.village.poi.VillagePlaceType;

public class PoiTypeWrapper implements AbstractPoiTypeWrapper {
    public static final PoiTypeWrapper MASON = new PoiTypeWrapper(VillagePlaceType.m);
    final VillagePlaceType poiType;

    PoiTypeWrapper(VillagePlaceType poiType) {
        this.poiType = poiType;
    }

    @Override
    public Predicate<? super AbstractPoiTypeWrapper> getPredicate() {
        return (poiTypeWrapper) -> poiType.c().test(((PoiTypeWrapper) poiTypeWrapper).poiType);
    }

}
