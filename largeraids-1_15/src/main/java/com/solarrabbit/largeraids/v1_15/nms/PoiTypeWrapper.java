package com.solarrabbit.largeraids.v1_15.nms;

import java.util.function.Predicate;

import com.solarrabbit.largeraids.nms.AbstractPoiTypeWrapper;

import net.minecraft.server.v1_15_R1.VillagePlaceType;

public class PoiTypeWrapper implements AbstractPoiTypeWrapper {
    // TODO questionable
    public static final PoiTypeWrapper MASON = new PoiTypeWrapper(VillagePlaceType.g);
    final VillagePlaceType poiType;

    PoiTypeWrapper(VillagePlaceType poiType) {
        this.poiType = poiType;
    }

    @Override
    public Predicate<? super AbstractPoiTypeWrapper> getPredicate() {
        return (poiTypeWrapper) -> poiType.c().test(((PoiTypeWrapper) poiTypeWrapper).poiType);
    }

}
