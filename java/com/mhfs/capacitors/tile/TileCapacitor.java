package com.mhfs.capacitors.tile;

import com.mhfs.capacitors.Blocks;
import com.mhfs.capacitors.misc.IRotatable;

import cofh.api.energy.IEnergyProvider;
import cofh.api.energy.IEnergyReceiver;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ITickable;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.event.world.BlockEvent.BreakEvent;

public class TileCapacitor extends TileEntity implements IEnergyProvider, IEnergyReceiver, IRotatable, ITickable {

	private CapacitorWallWrapper wrapper;
	private boolean isLoading, isFirstTick = true;

	public TileCapacitor() {
		this(true);
	}

	public TileCapacitor(boolean isLoading) {
		super();
		this.isLoading = isLoading;
	}

	@Override
	public void update() {
		if (wrapper == null) {
			createEntity();
		}

		if (worldObj.isRemote)
			return;

		if (this.isFirstTick) {
			this.isFirstTick = false;
			wrapper.checkJoin(worldObj, true);
		}

		wrapper.setupCapacity(worldObj);
		wrapper.updateEnergy(worldObj);
	}

	private void createEntity() {
		CapacitorWallWrapper instance = new CapacitorWallWrapper(worldObj, this.pos);
		if (wrapper == null) {
			wrapper = instance;
		}
		this.markDirty();
		worldObj.markBlockForUpdate(this.pos);
	}

	public EnumFacing getRotation() {
		return Blocks.capacitorIron.getOrientation(worldObj, this.pos);
	}

	public void onBreak(BreakEvent event) {
		if (wrapper != null) {
			if (event != null) {
				wrapper.leave(new BlockPos(this.pos), worldObj, event.getPlayer());
			} else {
				wrapper.leave(new BlockPos(this.pos), worldObj, null);
			}
		}
	}

	public void onEntityChange(CapacitorWallWrapper cap) {
		this.wrapper = cap;
		this.markDirty();
		worldObj.markBlockForUpdate(this.pos);
	}

	public CapacitorWallWrapper getEntityCapacitor() {
		return wrapper;
	}

	public void onRotate() {
		onBreak(null);
		wrapper = null;
		createEntity();
	}

	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);
		if (this.isLoading && tag.hasKey("multi")) {
			wrapper = CapacitorWallWrapper.fromNBT(tag.getCompoundTag("multi"));
		}
	}

	public void writeToNBT(NBTTagCompound tag) {
		super.writeToNBT(tag);
		if (wrapper != null) {
			tag.setTag("multi", wrapper.getNBTRepresentation());
		}
	}

	@Override
	public boolean canConnectEnergy(EnumFacing from) {
		return from == getRotation().getOpposite();
	}

	@Override
	public int receiveEnergy(EnumFacing from, int maxReceive, boolean simulate) {
		if (canConnectEnergy(from)) {
			int ret = wrapper.fill(maxReceive, simulate);
			if(!simulate)wrapper.updateEnergy(worldObj);
			return ret;
		}
		return 0;
	}
	
	@Override
	public int extractEnergy(EnumFacing from, int maxExtract, boolean simulate) {
		if (canConnectEnergy(from)) {
			int ret = wrapper.drain(maxExtract, simulate);
			if(!simulate)wrapper.updateEnergy(worldObj);
			return ret;
		}
		return 0;
	}

	@Override
	public int getEnergyStored(EnumFacing from) {
		return (int)wrapper.getEnergyStored();
	}

	@Override
	public int getMaxEnergyStored(EnumFacing from) {
		return (int)wrapper.getMaxEnergyStored();
	}
}
