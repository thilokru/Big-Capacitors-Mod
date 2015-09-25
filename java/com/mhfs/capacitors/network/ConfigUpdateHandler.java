package com.mhfs.capacitors.network;

import com.mhfs.capacitors.BigCapacitorsMod;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;

public class ConfigUpdateHandler implements IMessageHandler<ConfigUpdateMessage, IMessage>{

	@Override
	public IMessage onMessage(ConfigUpdateMessage message,
			MessageContext ctx) {
		BigCapacitorsMod.instance.dielectricities = message.getDielectrica();
		BigCapacitorsMod.instance.voltages = message.getVoltages();
		return null;
	}

}
