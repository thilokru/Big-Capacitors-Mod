package com.mhfs.capacitors.tile;

import com.mhfs.capacitors.BigCapacitorsMod;

import cofh.api.energy.IEnergyReceiver;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;

public class TileCrusherController extends TileEntity implements IEnergyReceiver, ITickable {

	private EnumFacing facing;

	public TileCrusherController() {
	}

	public void update() {
		if (!BigCapacitorsMod.instance.crusherMulti.complete(getPos(), getWorld(), facing)) {
			getWorld().removeTileEntity(getPos());
			return;
		}
	}
	
	

	public void setMultiblockRotation(EnumFacing facing) {
		this.facing = facing;
	}

	@Override
	public int getEnergyStored(EnumFacing from) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getMaxEnergyStored(EnumFacing from) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean canConnectEnergy(EnumFacing from) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public int receiveEnergy(EnumFacing from, int maxReceive, boolean simulate) {
		// TODO Auto-generated method stub
		return 0;
	}

}
