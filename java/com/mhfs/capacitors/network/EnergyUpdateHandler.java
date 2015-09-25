package com.mhfs.capacitors.network;

import java.util.HashMap;

import com.mhfs.capacitors.BigCapacitorsMod;
import com.mhfs.capacitors.tile.EntityCapacitor;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;

public class EnergyUpdateHandler implements IMessageHandler<EnergyUpdateMessage, IMessage> {

	@Override
	public IMessage onMessage(EnergyUpdateMessage message, MessageContext ctx) {
		HashMap<Integer, EntityCapacitor> worldCaps = BigCapacitorsMod.instance.worldCapacitors;
		if(worldCaps != null){
			if(worldCaps.get(message.getEntityID()) == null)return null;
			worldCaps.get(message.getEntityID()).onPacket(message);
		}
		return null;
	}

}
