package com.mhfs.capacitors.network;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class ClientGuiEvent extends Event {

	private ResourceLocation rl;
	private MessageContext ctx;
	
	public ClientGuiEvent(ResourceLocation rl, MessageContext ctx) {
		this.rl = rl;
		this.ctx = ctx;
	}
	
	public ResourceLocation getAction(){
		return rl;
	}
	
	public MessageContext getContext(){
		return ctx;
	}
}
