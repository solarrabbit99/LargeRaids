package com.solarrabbit.largeraids.v1_18_R2.nms;

import java.util.Optional;
import java.util.function.Predicate;

import javax.annotation.Nonnull;

import com.solarrabbit.largeraids.nms.AbstractBlockPositionWrapper;
import com.solarrabbit.largeraids.nms.AbstractPoiTypeWrapper;
import com.solarrabbit.largeraids.nms.AbstractVillageManagerWrapper;

import net.minecraft.core.BlockPosition;
import net.minecraft.world.entity.ai.village.poi.VillagePlace;

public class VillageManagerWrapper implements AbstractVillageManagerWrapper {
    private final VillagePlace poiManager;

    VillageManagerWrapper(VillagePlace poiManager) {
        this.poiManager = poiManager;
    }

    @Override
    public void add(AbstractBlockPositionWrapper blockPos, AbstractPoiTypeWrapper poiType) {
        poiManager.a(((BlockPositionWrapper) blockPos).blockPos, ((PoiTypeWrapper) poiType).poiType);
    }

    @Override
    public Optional<BlockPositionWrapper> take(@Nonnull Predicate<? super AbstractPoiTypeWrapper> poiPred,
            @Nonnull Predicate<? super AbstractBlockPositionWrapper> blockPosPred,
            AbstractBlockPositionWrapper blockPos,
            int d) {
        Optional<BlockPosition> res = poiManager.a(poiType -> poiPred.test(new PoiTypeWrapper(poiType)),
                pos -> blockPosPred.test(new BlockPositionWrapper(pos)),
                ((BlockPositionWrapper) blockPos).blockPos, d);
        return res.map(BlockPositionWrapper::new);
    }

    @Override
    public void remove(AbstractBlockPositionWrapper blockPos) {
        poiManager.a(((BlockPositionWrapper) blockPos).blockPos);
    }

}
