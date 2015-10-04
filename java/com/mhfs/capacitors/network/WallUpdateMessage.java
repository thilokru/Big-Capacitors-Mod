package com.mhfs.capacitors.network;

import com.google.gson.Gson;
import com.mhfs.capacitors.tile.CapacitorWallWrapper;

import io.netty.buffer.ByteBuf;
import cpw.mods.fml.common.network.simpleimpl.IMessage;

public class WallUpdateMessage implements IMessage {

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

}
