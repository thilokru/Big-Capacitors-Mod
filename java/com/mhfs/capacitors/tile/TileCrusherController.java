package com.mhfs.capacitors.tile;

import com.mhfs.capacitors.misc.Lo;

import cofh.api.energy.IEnergyReceiver;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;

public class TileCrusherController extends TileEntity implements IEnergyReceiver{

	private EnumFacing facing;
	
	public TileCrusherController(){}
	
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
