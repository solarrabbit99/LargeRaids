package com.solarrabbit.largeraids.v1_14.nms;

import com.solarrabbit.largeraids.nms.AbstractBlockPositionWrapper;

import net.minecraft.server.v1_14_R1.BlockPosition;

public class BlockPositionWrapper implements AbstractBlockPositionWrapper {
    final BlockPosition blockPos;

    public BlockPositionWrapper(double x, double y, double z) {
        this.blockPos = new BlockPosition(x, y, z);
    }
}
