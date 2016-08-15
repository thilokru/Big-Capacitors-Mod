package com.mhfs.capacitors.tile;

import java.util.UUID;

import com.mhfs.capacitors.Blocks;
import com.mhfs.capacitors.misc.IRotatable;
import com.mhfs.capacitors.world.CapacitorWorldData;

import cofh.api.energy.IEnergyProvider;
import cofh.api.energy.IEnergyReceiver;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ITickable;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.event.world.BlockEvent.BreakEvent;

public class TileCapacitor extends AdvTileEntity implements IEnergyProvider, IEnergyReceiver, IRotatable, ITickable {

	private UUID wrapperID;

	@Override
	public void update() {
		if (worldObj.isRemote)
			return;
		if (wrapperID == null || getEntityCapacitor() == null) {
			createEntity();
		}
		getEntityCapacitor().setupCapacity(worldObj);
		getEntityCapacitor().updateEnergy(worldObj);
	}
	
	private CapacitorWorldData getWorldData() {
		return (CapacitorWorldData) this.worldObj.getPerWorldStorage().getOrLoadData(CapacitorWorldData.class, CapacitorWorldData.NAME);
	}

	private void createEntity() {
		wrapperID = getWorldData().newCCW(this);
		getEntityCapacitor().onTileBind();
		getEntityCapacitor().checkJoin(worldObj);
		this.sendUpdate();
	}

	public EnumFacing getRotation() {
		return Blocks.capacitorIron.getOrientation(worldObj, this.pos);
	}

	public void onBreak(BreakEvent event) {
		CapacitorWallWrapper wrapper = getEntityCapacitor();
		if (wrapper != null) {
			if (event != null) {
				wrapper.leave(this.pos, worldObj, event.getPlayer());
			} else {
				wrapper.leave(this.pos, worldObj, null);
			}
		}
		getWorldData().onCWWUnbind(wrapperID);
	}

	public void onEntityChange(UUID id) {
		if(id != null && id.equals(this.wrapperID)) return;
		getWorldData().onCWWUnbind(wrapperID);
		this.wrapperID = id;
		getWorldData().onCWWBind(id);
		this.sendUpdate();
	}

	public CapacitorWallWrapper getEntityCapacitor() {
		return getWorldData().getCWW(wrapperID);
	}

	public void onRotate() {
		onBreak(null);
		getWorldData().onCWWUnbind(wrapperID);
		createEntity();
	}

	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);
		this.wrapperID = tag.hasKey("cwwID") ? UUID.fromString(tag.getString("cwwID")) : null;
	}

	public NBTTagCompound writeToNBT(NBTTagCompound tag) {
		super.writeToNBT(tag);
		if(wrapperID != null)tag.setString("cwwID", wrapperID.toString());
		return tag;
	}

	@Override
	public boolean canConnectEnergy(EnumFacing from) {
		return from == getRotation().getOpposite();
	}

	@Override
	public int receiveEnergy(EnumFacing from, int maxReceive, boolean simulate) {
		CapacitorWallWrapper wrapper = getEntityCapacitor();
		if (canConnectEnergy(from) && wrapper != null) {
			int ret = wrapper.fill(maxReceive, simulate);
			if(!simulate){
				wrapper.updateEnergy(worldObj);
			}
			return ret;
		}
		return 0;
	}
	
	@Override
	public int extractEnergy(EnumFacing from, int maxExtract, boolean simulate) {
		CapacitorWallWrapper wrapper = getEntityCapacitor();
		if (canConnectEnergy(from)) {
			int ret = wrapper.drain(maxExtract, simulate);
			if(!simulate){
				wrapper.updateEnergy(worldObj);
			}
			return ret;
		}
		return 0;
	}

	@Override
	public int getEnergyStored(EnumFacing from) {
		CapacitorWallWrapper wrapper = getEntityCapacitor();
		return (int)wrapper.getEnergyStored();
	}

	@Override
	public int getMaxEnergyStored(EnumFacing from) {
		CapacitorWallWrapper wrapper = getEntityCapacitor();
		return (int)wrapper.getMaxEnergyStored();
	}
}
