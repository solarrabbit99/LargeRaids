package com.solarrabbit.largeraids.v1_17.nms;

import com.solarrabbit.largeraids.nms.AbstractBlockPositionWrapper;

import net.minecraft.core.BlockPos;

public class BlockPositionWrapper implements AbstractBlockPositionWrapper {
    final BlockPos blockPos;

    public BlockPositionWrapper(double x, double y, double z) {
        this.blockPos = new BlockPos(x, y, z);
    }
}
