package com.solarrabbit.largeraids.v1_14.nms;

import com.mojang.authlib.GameProfile;
import com.solarrabbit.largeraids.nms.AbstractMinecraftServerWrapper;
import com.solarrabbit.largeraids.nms.AbstractPlayerEntityWrapper;
import com.solarrabbit.largeraids.nms.AbstractWorldServerWrapper;

import net.minecraft.server.v1_14_R1.EntityPlayer;
import net.minecraft.server.v1_14_R1.PlayerInteractManager;

public class PlayerEntityWrapper implements AbstractPlayerEntityWrapper {
    final EntityPlayer player;

    public PlayerEntityWrapper(AbstractMinecraftServerWrapper server, AbstractWorldServerWrapper world,
            GameProfile profile) {
        this.player = new EntityPlayer(((MinecraftServerWrapper) server).server, ((WorldServerWrapper) world).server,
                profile, new PlayerInteractManager(((WorldServerWrapper) world).server));
    }

    @Override
    public void setPosition(double x, double y, double z) {
        this.player.setPosition(x, y, z);
    }
}
