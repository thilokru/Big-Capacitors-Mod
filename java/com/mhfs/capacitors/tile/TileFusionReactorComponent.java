package com.mhfs.capacitors.tile;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;

public class TileFusionReactorComponent extends TileEntity{

	private BlockPos controller;

	public TileTomahawk getController() {
		return (TileTomahawk) controller.getTileEntity(worldObj);
	}
	
	public void setController(BlockPos location){
		if(worldObj.isRemote)return;
		this.controller = location;
		this.markDirty();
		this.worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
	}
	
	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);
		this.controller = BlockPos.fromNBT(tag, "coord");
	}

	public void writeToNBT(NBTTagCompound tag) {
		super.writeToNBT(tag);
		controller.writeToNBT(tag, "coord");
	}
	
	public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity pkt) {
		this.readFromNBT(pkt.func_148857_g());
	}
	
	public Packet getDescriptionPacket() {
		NBTTagCompound tag = new NBTTagCompound();
		writeToNBT(tag);
		return new S35PacketUpdateTileEntity(xCoord, yCoord, zCoord, 1, tag);
	}
}
