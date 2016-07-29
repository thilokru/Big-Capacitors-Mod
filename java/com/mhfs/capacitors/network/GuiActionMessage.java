package com.mhfs.capacitors.network;

import com.google.common.base.Charsets;

import io.netty.buffer.ByteBuf;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class GuiActionMessage implements IMessage, IMessageHandler<GuiActionMessage, IMessage>{

	private ResourceLocation action;
	
	public GuiActionMessage(ResourceLocation action) {
		this.action = action;
	}
	
	/**
	 * @deprecated just for deserialization
	 */
	@Deprecated()
	public GuiActionMessage(){}
	
	@Override
	public IMessage onMessage(GuiActionMessage message, MessageContext ctx) {
		MinecraftForge.EVENT_BUS.post(new ClientGuiEvent(message.action, ctx));
		return null;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		int length = buf.readInt();
		byte[] bytes = new byte[length];
		buf.readBytes(bytes);
		
		String data = new String(bytes, Charsets.UTF_16);
		action = new ResourceLocation(data);
	}

	@Override
	public void toBytes(ByteBuf buf) {
		byte[] bytes = action.toString().getBytes(Charsets.UTF_16);
		buf.writeInt(bytes.length);
		buf.writeBytes(bytes);
	}

}
