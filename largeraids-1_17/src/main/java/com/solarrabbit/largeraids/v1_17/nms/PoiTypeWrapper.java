package com.solarrabbit.largeraids.v1_17.nms;

import java.util.function.Predicate;

import com.solarrabbit.largeraids.nms.AbstractPoiTypeWrapper;

import net.minecraft.world.entity.ai.village.poi.PoiType;

public class PoiTypeWrapper implements AbstractPoiTypeWrapper {
    public static final PoiTypeWrapper MASON = new PoiTypeWrapper(PoiType.MASON);
    final PoiType poiType;

    PoiTypeWrapper(PoiType poiType) {
        this.poiType = poiType;
    }

    @Override
    public Predicate<? super AbstractPoiTypeWrapper> getPredicate() {
        return (poiTypeWrapper) -> poiType.getPredicate().test(((PoiTypeWrapper) poiTypeWrapper).poiType);
    }

}
