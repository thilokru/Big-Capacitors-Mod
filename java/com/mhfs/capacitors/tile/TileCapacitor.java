package com.mhfs.capacitors.tile;

import com.mhfs.capacitors.Blocks;
import com.mhfs.capacitors.misc.IRotatable;

import cofh.api.energy.IEnergyProvider;
import cofh.api.energy.IEnergyReceiver;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ITickable;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.event.world.BlockEvent.BreakEvent;

public class TileCapacitor extends AdvTileEntity implements IEnergyProvider, IEnergyReceiver, IRotatable, ITickable {

	private CapacitorWallWrapper wrapper;
	private boolean isFirstTick = true;

	@Override
	public void update() {
		if (wrapper == null || !wrapper.isMember(this.pos)) {
			createEntity();
		}
		if(isFirstTick){
			wrapper.checkJoin(worldObj, isFirstTick);
		}
		if (worldObj.isRemote)
			return;

		wrapper.setupCapacity(worldObj);
		wrapper.updateEnergy(worldObj);
		isFirstTick = false;
	}

	private void createEntity() {
		wrapper = new CapacitorWallWrapper(this);
		wrapper.checkJoin(worldObj, this.isFirstTick);
		markForUpdate();
	}

	public EnumFacing getRotation() {
		return Blocks.capacitorIron.getOrientation(worldObj, this.pos);
	}

	public void onBreak(BreakEvent event) {
		if (wrapper != null) {
			if (event != null) {
				wrapper.leave(this.pos, worldObj, event.getPlayer());
			} else {
				wrapper.leave(this.pos, worldObj, null);
			}
		}
	}

	public void onEntityChange(CapacitorWallWrapper cap) {
		this.wrapper = cap;
		markForUpdate();
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
		if (tag.hasKey("multi") && wrapper == null) {
			wrapper = CapacitorWallWrapper.fromNBT(tag.getCompoundTag("multi"));
		} else {
			wrapper = null;
		}
	}

	public NBTTagCompound writeToNBT(NBTTagCompound tag) {
		super.writeToNBT(tag);
		if (wrapper != null) {
			tag.setTag("multi", wrapper.getNBTRepresentation());
		}
		return tag;
	}

	@Override
	public boolean canConnectEnergy(EnumFacing from) {
		return from == getRotation().getOpposite();
	}

	@Override
	public int receiveEnergy(EnumFacing from, int maxReceive, boolean simulate) {
		if (canConnectEnergy(from) && wrapper != null) {
			int ret = wrapper.fill(maxReceive, simulate);
			if(!simulate){
				wrapper.updateEnergy(worldObj);
				wrapper.updateBlocks(worldObj);
			}
			return ret;
		}
		return 0;
	}
	
	@Override
	public int extractEnergy(EnumFacing from, int maxExtract, boolean simulate) {
		if (canConnectEnergy(from) && wrapper != null) {
			int ret = wrapper.drain(maxExtract, simulate);
			if(!simulate){
				wrapper.updateEnergy(worldObj);
				wrapper.updateBlocks(worldObj);
			}
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
