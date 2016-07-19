package com.mhfs.capacitors.tile;

import com.mhfs.capacitors.BigCapacitorsMod;
import com.mhfs.capacitors.blocks.BlockMachineController;

import cofh.api.energy.IEnergyReceiver;
import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;

public class TileCrusherController extends AdvTileEntity implements IEnergyReceiver, ITickable {

	private EnumFacing facing;

	public TileCrusherController() {}

	public void update() {
		if(!isMultiblockComplete())
			return;
	}
	
	private boolean isMultiblockComplete(){
		if(facing == null){
			facing = BigCapacitorsMod.instance.crusherMulti.getCompletedRotation(getPos(), getWorld());
			if(facing == null){
				selfDestruct();
				return false;
			}
		}
		if (!BigCapacitorsMod.instance.crusherMulti.complete(getPos(), getWorld(), facing)) {
			selfDestruct();
			return false;
		}
		return true;
	}
	
	private void selfDestruct(){
		getWorld().removeTileEntity(getPos());
		IBlockState state = getWorld().getBlockState(getPos());
		state.withProperty(BlockMachineController.USED_TE, 0);
		getWorld().setBlockState(getPos(), state);
	}

	public NBTTagCompound writeToNBT(NBTTagCompound tag) {
		super.writeToNBT(tag);
		tag.setInteger("rotation", facing.getIndex());
		return tag;
	}

	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);
		this.facing = EnumFacing.getFront(tag.getInteger("rotation"));
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
