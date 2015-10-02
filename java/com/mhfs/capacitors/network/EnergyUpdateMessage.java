package com.mhfs.capacitors.network;

import io.netty.buffer.ByteBuf;
import cpw.mods.fml.common.network.simpleimpl.IMessage;

public class EnergyUpdateMessage implements IMessage {

	private long energy, capacity;
	private int entityID;
	private boolean grounded;
	
	public EnergyUpdateMessage(long energy, long capacity, boolean grounded, int entityID){
		this.energy = energy;
		this.capacity = capacity;
		this.entityID = entityID;
		this.grounded = grounded;
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
	
	public boolean isGrounded(){
		return grounded;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		this.energy = buf.readLong();
		this.capacity = buf.readLong();
		this.grounded = buf.readBoolean();
		this.entityID = buf.readInt();
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeLong(energy);
		buf.writeLong(capacity);
		buf.writeBoolean(grounded);
		buf.writeInt(entityID);
	}

}
