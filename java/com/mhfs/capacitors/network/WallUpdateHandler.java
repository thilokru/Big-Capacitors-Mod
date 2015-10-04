package com.mhfs.capacitors.network;

import java.util.HashMap;

import com.mhfs.capacitors.BigCapacitorsMod;
import com.mhfs.capacitors.tile.CapacitorWallWrapper;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;

public class WallUpdateHandler implements IMessageHandler<WallUpdateMessage, IMessage> {

	@Override
	public IMessage onMessage(WallUpdateMessage message, MessageContext ctx) {
		HashMap<Integer, CapacitorWallWrapper> worldCaps = BigCapacitorsMod.instance.worldCapacitors;
		if (worldCaps != null) {
			CapacitorWallWrapper local = worldCaps.get(message.getWrapper().hashCode());
			if (local == null) {
				worldCaps.put(message.getWrapper().hashCode(), message.getWrapper());
			} else {
				local.sync(message.getWrapper());
			}
		}
		return null;
	}

}
