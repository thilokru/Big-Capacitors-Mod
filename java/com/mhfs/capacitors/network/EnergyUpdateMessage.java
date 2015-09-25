package com.mhfs.capacitors.network;

import net.minecraftforge.common.util.ForgeDirection;
import io.netty.buffer.ByteBuf;
import cpw.mods.fml.common.network.simpleimpl.IMessage;

public class EnergyUpdateMessage implements IMessage {

	private long energy, capacity;
	private int entityID;
	private ForgeDirection allowedExtract;
	
	public EnergyUpdateMessage(long energy, long capacity, ForgeDirection allowedExctract, int entityID){
		this.energy = energy;
		this.capacity = capacity;
		this.entityID = entityID;
		this.allowedExtract = allowedExctract;
	}
	
	/**
	 * Only for Receiving
	 */
	public EnergyUpdateMessage(){}
	
	public long getEnergy(){
		return energy;
	}
	
	public long getCapacity(){
		return capacity;
	}
	
	public int getEntityID(){
		return entityID;
	}
	
	public ForgeDirection getAllowedExtract(){
		return allowedExtract;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		this.energy = buf.readLong();
		this.capacity = buf.readLong();
		this.allowedExtract = ForgeDirection.getOrientation(buf.readInt());
		this.entityID = buf.readInt();
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeLong(energy);
		buf.writeLong(capacity);
		buf.writeInt(allowedExtract.ordinal());
		buf.writeInt(entityID);
	}

}
