package com.mhfs.capacitors.network;

import net.minecraft.client.Minecraft;
import net.minecraft.world.World;

import com.google.gson.Gson;
import com.mhfs.capacitors.tile.CapacitorWallWrapper;
import com.mhfs.capacitors.tile.TileCapacitor;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class WallUpdateMessage implements IMessage, IMessageHandler<WallUpdateMessage, IMessage> {

	private CapacitorWallWrapper wrapper;
	
	public WallUpdateMessage(CapacitorWallWrapper wrapper){
		this.wrapper = wrapper;
	}
	
	/**
	 * Only for Receiving
	 */
	public WallUpdateMessage(){}
	
	public CapacitorWallWrapper getWrapper(){
		return wrapper;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		Gson gson = new Gson();
		int length = buf.readInt();
		byte[] bytes = new byte[length];
		buf.readBytes(bytes);
		String json = new String(bytes);
		wrapper = gson.fromJson(json, CapacitorWallWrapper.class);
	}

	@Override
	public void toBytes(ByteBuf buf) {
		Gson gson = new Gson();
		String json = gson.toJson(wrapper);
		byte[] bytes = json.getBytes();
		buf.writeInt(bytes.length);
		buf.writeBytes(bytes);
	}
	
	@Override
	public IMessage onMessage(WallUpdateMessage message, MessageContext ctx) {
		World world = Minecraft.getMinecraft().theWorld;
		if(world == null)return null;
		TileCapacitor cap = (TileCapacitor) world.getTileEntity(message.getWrapper().getRandomBlock());
		if(cap == null)return null;
		CapacitorWallWrapper local = cap.getEntityCapacitor();
		if (local != null) {
				local.sync(message.getWrapper());
		}
		return null;
	}

}
