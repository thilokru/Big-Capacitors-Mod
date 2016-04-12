package com.mhfs.capacitors.tile;

import com.mhfs.capacitors.Blocks;
import com.mhfs.capacitors.blocks.BlockStirlingEngine;

import cofh.api.energy.IEnergyProvider;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;

public class TileStirlingEngine extends TileEntity implements IEnergyProvider, ITickable{
	
	public final static int MAX_ENERGY = 80000;
	public final static int MAX_TRANSFER = 80;
	
	private int energy;
	
	@Override
	public void update() {
		if(worldObj.isRemote)return;
		if(active()){
			energy += 40;
			this.markDirty();
			worldObj.markBlockForUpdate(pos);
		}
	}

	@Override
	public int getEnergyStored(EnumFacing from) {
		return energy;
	}

	@Override
	public int getMaxEnergyStored(EnumFacing from) {
		return MAX_ENERGY;
	}

	@Override
	public boolean canConnectEnergy(EnumFacing from) {
		return from == getFacing();
	}

	@Override
	public int extractEnergy(EnumFacing from, int maxExtract, boolean simulate) {
		if(canConnectEnergy(from)){
			int extractable = Math.min(energy, Math.min(maxExtract, MAX_TRANSFER));
			if(!simulate){
				energy -= extractable;
			}
			return extractable;
		}
		return 0;
	}
	
	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);
		this.energy = tag.getInteger("energy");
	}

	public void writeToNBT(NBTTagCompound tag) {
		super.writeToNBT(tag);
		tag.setLong("energy", energy);
	}

	public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity pkt) {
		this.readFromNBT(pkt.getNbtCompound());
	}

	public Packet<?> getDescriptionPacket() {
		NBTTagCompound tag = new NBTTagCompound();
		writeToNBT(tag);
		return new S35PacketUpdateTileEntity(this.pos, 1, tag);
	}
	
	private EnumFacing getFacing(){
		try{
			return worldObj.getBlockState(pos).getValue(BlockStirlingEngine.ORIENTATION);
		}catch(NullPointerException npe){
			return null;
		}
	}
	
	private boolean active(){
		return worldObj.getBlockState(pos.offset(EnumFacing.DOWN)).getBlock() == Blocks.lava;
	}

}
