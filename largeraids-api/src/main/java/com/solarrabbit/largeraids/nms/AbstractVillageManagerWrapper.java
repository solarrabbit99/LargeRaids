package com.solarrabbit.largeraids.nms;

import java.util.Optional;
import java.util.function.Predicate;

public interface AbstractVillageManagerWrapper {
    void add(AbstractBlockPositionWrapper blockPos, AbstractPoiTypeWrapper poiType);

    Optional<? extends AbstractBlockPositionWrapper> take(Predicate<? super AbstractPoiTypeWrapper> poiPred,
            Predicate<? super AbstractBlockPositionWrapper> blockPosPred, AbstractBlockPositionWrapper blockPos,
            int d);

    void remove(AbstractBlockPositionWrapper blockPos);
}
