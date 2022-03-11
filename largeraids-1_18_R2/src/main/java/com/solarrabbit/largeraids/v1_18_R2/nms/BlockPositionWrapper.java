package com.solarrabbit.largeraids.v1_18_R2.nms;

import static java.util.Objects.requireNonNull;

import javax.annotation.Nonnull;

import com.solarrabbit.largeraids.nms.AbstractBlockPositionWrapper;

import net.minecraft.core.BlockPosition;

public class BlockPositionWrapper implements AbstractBlockPositionWrapper {
    final BlockPosition blockPos;

    public BlockPositionWrapper(double x, double y, double z) {
        this.blockPos = new BlockPosition(x, y, z);
    }

    BlockPositionWrapper(@Nonnull BlockPosition blockPos) {
        requireNonNull(blockPos);
        this.blockPos = blockPos;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof BlockPositionWrapper))
            return false;
        return this.blockPos.equals(((BlockPositionWrapper) obj).blockPos);
    }
}
