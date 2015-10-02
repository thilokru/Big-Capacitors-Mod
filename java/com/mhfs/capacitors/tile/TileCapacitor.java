package com.mhfs.capacitors.tile;

import java.util.HashMap;

import com.mhfs.capacitors.BigCapacitorsMod;
import com.mhfs.capacitors.blocks.BlockCapacitor;

import cofh.api.energy.IEnergyHandler;
import cofh.api.energy.IEnergyReceiver;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.event.world.BlockEvent.BreakEvent;

public class TileCapacitor extends TileEntity implements IEnergyHandler {

	private CapacitorWallWrapper wrapper;

	public TileCapacitor() {
		super();
	}

	@Override
	public void updateEntity() {
		if (wrapper == null) {
			createEntity();
		}
		if (BigCapacitorsMod.instance.worldCapacitors.get(this.wrapper.hashCode()) != null) {
			if (BigCapacitorsMod.instance.worldCapacitors.get(wrapper.hashCode()) != wrapper) {
				this.wrapper = BigCapacitorsMod.instance.worldCapacitors.get(this.wrapper.hashCode());
			}
		}
		if (worldObj.isRemote)
			return;
		TileEntity candidate = getConnectionCandidate();
		if (candidate != null && candidate instanceof IEnergyReceiver) {
			IEnergyReceiver con = (IEnergyReceiver) candidate;
			int transmittable = con.receiveEnergy(getOrientation(), (int) Math.min(wrapper.getEnergyStored(), wrapper.getWholeCapacity()), true);
			int transmit = this.extractEnergy(getOrientation().getOpposite(), transmittable, false);
			con.receiveEnergy(getOrientation(), transmit, false);
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
		ForgeDirection face = getOrientation().getOpposite();
		if (wrapper == null)
			return null;
		if (wrapper.canExtractEnergy(face)) {
			BlockPos pos = new BlockPos(xCoord, yCoord, zCoord);
			pos.goTowards(face, 1);
			return pos.getTileEntity(worldObj);
		}
		return null;
	}

	public ForgeDirection getOrientation() {
		return BigCapacitorsMod.instance.capacitorIron.getOrientation(worldObj, xCoord, yCoord, zCoord);
	}

	public void onBreak(BreakEvent event) {
		if (wrapper != null){
			if(event != null){
			wrapper.leave(new BlockPos(xCoord, yCoord, zCoord), worldObj, event.getPlayer());
			}else{
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
		BlockCapacitor cap = BigCapacitorsMod.instance.capacitorIron;
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
		if (BigCapacitorsMod.instance.worldCapacitors == null) {
			BigCapacitorsMod.instance.worldCapacitors = new HashMap<Integer, CapacitorWallWrapper>();
		}
		super.readFromNBT(tag);
		int id = tag.getInteger("multi-id");
		CapacitorWallWrapper cap = BigCapacitorsMod.instance.worldCapacitors.get(id);
		if (cap == null && tag.getBoolean("multi-present")) {
			cap = CapacitorWallWrapper.fromNBT(tag.getCompoundTag("multi"));
			BigCapacitorsMod.instance.worldCapacitors.put(id, cap);
		}
		wrapper = cap;
	}

	public void writeToNBT(NBTTagCompound tag) {
		super.writeToNBT(tag);
		if (wrapper != null) {
			tag.setBoolean("multi-present", true);
			tag.setTag("multi", wrapper.getNBTRepresentation());
			tag.setInteger("multi-id", wrapper.hashCode());
		} else {
			tag.setBoolean("multi-present", false);
		}
	}

	public Packet getDescriptionPacket() {
		NBTTagCompound syncData = new NBTTagCompound();
		writeToNBT(syncData);
		return new S35PacketUpdateTileEntity(this.xCoord, this.yCoord, this.zCoord, 1, syncData);
	}

	public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity pkt) {
		readFromNBT(pkt.func_148857_g());
	}
}
