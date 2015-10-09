package com.mhfs.capacitors.network;

import net.minecraft.client.Minecraft;
import net.minecraft.world.World;

import com.mhfs.capacitors.tile.CapacitorWallWrapper;
import com.mhfs.capacitors.tile.TileCapacitor;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;

public class WallUpdateHandler implements IMessageHandler<WallUpdateMessage, IMessage> {

	@Override
	public IMessage onMessage(WallUpdateMessage message, MessageContext ctx) {
		World world = Minecraft.getMinecraft().theWorld;
		TileCapacitor cap = (TileCapacitor) message.getWrapper().getRandomBlock().getTileEntity(world);
		if(cap == null)return null;
		CapacitorWallWrapper local = cap.getEntityCapacitor();
		if (local != null) {
				local.sync(message.getWrapper());
		}
		return null;
	}

}
