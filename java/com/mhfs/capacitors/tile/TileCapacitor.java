package com.mhfs.capacitors.tile;

import com.mhfs.capacitors.Blocks;
import com.mhfs.capacitors.misc.BlockPos;
import com.mhfs.capacitors.misc.IRotatable;
import com.mhfs.capacitors.tile.lux.INeighbourEnergyHandler;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.event.world.BlockEvent.BreakEvent;

public class TileCapacitor extends TileEntity implements INeighbourEnergyHandler, IRotatable {

	private CapacitorWallWrapper wrapper;
	private boolean isLoading, isFirstTick = true;
	
	public TileCapacitor(){
		this(true);
	}

	public TileCapacitor(boolean isLoading) {
		super();
		this.isLoading = isLoading;
	}

	@Override
	public void updateEntity() {
		if (wrapper == null) {
			createEntity();
		}
		
		if (worldObj.isRemote)return;
		
		if(this.isFirstTick){
			this.isFirstTick = false;
			wrapper.checkJoin(worldObj, true);
		}
		
		wrapper.setupCapacity(worldObj);
		wrapper.updateEnergy(worldObj);
	}

	private void createEntity() {
		CapacitorWallWrapper instance = new CapacitorWallWrapper(worldObj, new BlockPos(xCoord, yCoord, zCoord));
		if (wrapper == null) {
			wrapper = instance;
		}
		this.markDirty();
		worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
	}

	public ForgeDirection getRotation() {
		return Blocks.capacitorIron.getOrientation(worldObj, xCoord, yCoord, zCoord);
	}

	public void onBreak(BreakEvent event) {
		if (wrapper != null) {
			if (event != null) {
				wrapper.leave(new BlockPos(xCoord, yCoord, zCoord), worldObj, event.getPlayer());
			} else {
				wrapper.leave(new BlockPos(xCoord, yCoord, zCoord), worldObj, null);
			}
		}
	}

	public void onEntityChange(CapacitorWallWrapper cap) {
		this.wrapper = cap;
		this.markDirty();
		worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
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
		if(wrapper != null){
			tag.setTag("multi", wrapper.getNBTRepresentation());
		}
	}

	@Override
	public long getNeed() {
		return wrapper.getNeed();
	}

	@Override
	public long getMaxTransfer() {
		return wrapper.getMaxTransfer();
	}

	@Override
	public long getEnergyStored() {
		return wrapper.getEnergyStored();
	}

	@Override
	public long getMaxEnergyStored() {
		return wrapper.getMaxEnergyStored();
	}

	@Override
	public long drain(long amount) {
		long ret = wrapper.drain(amount);
		wrapper.updateEnergy(worldObj);
		return ret;
	}

	@Override
	public long fill(long amount) {
		long ret = wrapper.fill(amount);
		wrapper.updateEnergy(worldObj);
		return ret;
	}
}
