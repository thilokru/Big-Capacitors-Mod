package com.mhfs.capacitors.tile;

import com.mhfs.capacitors.Blocks;
import com.mhfs.capacitors.blocks.BlockCapacitor;
import com.mhfs.capacitors.misc.IRotatable;

import cofh.api.energy.IEnergyHandler;
import cofh.api.energy.IEnergyReceiver;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.event.world.BlockEvent.BreakEvent;

public class TileCapacitor extends TileEntity implements IEnergyHandler, IRotatable {

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

		TileEntity candidate = getConnectionCandidate();
		if (candidate != null && candidate instanceof IEnergyReceiver) {
			IEnergyReceiver con = (IEnergyReceiver) candidate;
			int transmittable = con.receiveEnergy(getRotation(), (int) Math.min(wrapper.getEnergyStored(), wrapper.getWholeCapacity()), true);
			int transmit = this.extractEnergy(getRotation().getOpposite(), transmittable, false);
			con.receiveEnergy(getRotation(), transmit, false);
		}
	}

	private void createEntity() {
		CapacitorWallWrapper instance = new CapacitorWallWrapper(worldObj, new BlockPos(xCoord, yCoord, zCoord));
		if (wrapper == null) {
			wrapper = instance;
		}
		this.markDirty();
		worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
	}

	private TileEntity getConnectionCandidate() {
		ForgeDirection face = getRotation().getOpposite();
		if (wrapper == null)
			return null;
		if (wrapper.canExtractEnergy(face)) {
			BlockPos pos = new BlockPos(xCoord, yCoord, zCoord);
			pos.goTowards(face, 1);
			return pos.getTileEntity(worldObj);
		}
		return null;
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

	@Override
	public boolean canConnectEnergy(ForgeDirection from) {
		BlockCapacitor cap = Blocks.capacitorIron;
		ForgeDirection orientation = cap.getOrientation(worldObj, xCoord, yCoord, zCoord);
		if (wrapper == null)
			return false;
		return from == orientation.getOpposite() && wrapper.canExtractEnergy(from);
	}

	@Override
	public int receiveEnergy(ForgeDirection from, int maxReceive, boolean simulate) {
		if (canConnectEnergy(from)) {
			int ret = wrapper.receiveEnergy(maxReceive, simulate);
			wrapper.updateEnergy(worldObj);
			return ret;
		} else
			return 0;
	}

	@Override
	public int extractEnergy(ForgeDirection from, int maxExtract, boolean simulate) {
		if (canConnectEnergy(from)) {
			int ret = wrapper.extractEnergy(maxExtract, simulate);
			wrapper.updateEnergy(worldObj);
			return ret;
		} else
			return 0;
	}

	@Override
	public int getEnergyStored(ForgeDirection from) {
		if (canConnectEnergy(from)) {
			return wrapper.getEnergyStored();
		} else
			return 0;
	}

	@Override
	public int getMaxEnergyStored(ForgeDirection from) {
		if (canConnectEnergy(from)) {
			return wrapper.getMaxEnergyStored();
		} else
			return 0;
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
}
