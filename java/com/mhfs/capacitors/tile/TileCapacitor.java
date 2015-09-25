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

	private EntityCapacitor wholeCapacitor;

	public TileCapacitor() {
		super();
	}

	@Override
	public void updateEntity() {
		if (wholeCapacitor == null) {
			createEntity();
		}
		if (BigCapacitorsMod.instance.worldCapacitors.get(this.wholeCapacitor.hashCode()) != null) {
			if (BigCapacitorsMod.instance.worldCapacitors.get(wholeCapacitor.hashCode()) != wholeCapacitor) {
				this.wholeCapacitor = BigCapacitorsMod.instance.worldCapacitors.get(this.wholeCapacitor.hashCode());
			}
		}
		if (worldObj.isRemote)
			return;
		TileEntity candidate = getConnectionCandidate();
		if (candidate != null && candidate instanceof IEnergyReceiver) {
			IEnergyReceiver con = (IEnergyReceiver) candidate;
			CapacitorEnergyStorage storage = wholeCapacitor.getStorage();
			int transmittable = con.receiveEnergy(getOrientation(), (int) Math.min(storage.getEnergyStored(), storage.getWholeCapacity()), true);
			int transmit = this.extractEnergy(getOrientation().getOpposite(), transmittable, false);
			con.receiveEnergy(getOrientation(), transmit, false);
		}
	}

	private void createEntity() {
		EntityCapacitor instance = new EntityCapacitor(worldObj, new BlockPos(xCoord, yCoord, zCoord));
		if (wholeCapacitor == null) {
			wholeCapacitor = instance;
		}
		this.markDirty();
		worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
	}

	private TileEntity getConnectionCandidate() {
		ForgeDirection face = getOrientation().getOpposite();
		if (wholeCapacitor == null)
			return null;
		if (wholeCapacitor.canExtractEnergy(face)) {
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
		if (wholeCapacitor != null){
			if(event != null){
				wholeCapacitor.leave(new BlockPos(xCoord, yCoord, zCoord), worldObj, event.getPlayer());
			}else{
				wholeCapacitor.leave(new BlockPos(xCoord, yCoord, zCoord), worldObj, null);
			}
		}
	}

	public void onEntityChange(EntityCapacitor cap) {
		this.wholeCapacitor = cap;
		this.markDirty();
		worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
	}

	public EntityCapacitor getEntityCapacitor() {
		return wholeCapacitor;
	}

	@Override
	public boolean canConnectEnergy(ForgeDirection from) {
		BlockCapacitor cap = BigCapacitorsMod.instance.capacitorIron;
		ForgeDirection orientation = cap.getOrientation(worldObj, xCoord, yCoord, zCoord);
		if (wholeCapacitor == null)
			return false;
		return from == orientation.getOpposite() && wholeCapacitor.canExtractEnergy(from);
	}

	@Override
	public int receiveEnergy(ForgeDirection from, int maxReceive, boolean simulate) {
		if (canConnectEnergy(from)) {
			int ret = wholeCapacitor.getStorage().receiveEnergy(maxReceive, simulate);
			wholeCapacitor.updateEnergy(worldObj);
			return ret;
		} else
			return 0;
	}

	@Override
	public int extractEnergy(ForgeDirection from, int maxExtract, boolean simulate) {
		if (canConnectEnergy(from)) {
			int ret = wholeCapacitor.getStorage().extractEnergy(maxExtract, simulate);
			wholeCapacitor.updateEnergy(worldObj);
			return ret;
		} else
			return 0;
	}

	@Override
	public int getEnergyStored(ForgeDirection from) {
		if (canConnectEnergy(from)) {
			return wholeCapacitor.getStorage().getEnergyStored();
		} else
			return 0;
	}

	@Override
	public int getMaxEnergyStored(ForgeDirection from) {
		if (canConnectEnergy(from)) {
			return wholeCapacitor.getStorage().getMaxEnergyStored();
		} else
			return 0;
	}

	public void onRotate() {
		onBreak(null);
		wholeCapacitor = null;
		createEntity();
	}

	public void readFromNBT(NBTTagCompound tag) {
		if (BigCapacitorsMod.instance.worldCapacitors == null) {
			BigCapacitorsMod.instance.worldCapacitors = new HashMap<Integer, EntityCapacitor>();
		}
		super.readFromNBT(tag);
		int id = tag.getInteger("multi-id");
		EntityCapacitor cap = BigCapacitorsMod.instance.worldCapacitors.get(id);
		if (cap == null && tag.getBoolean("multi-present")) {
			cap = EntityCapacitor.fromNBT(tag.getCompoundTag("multi"));
			BigCapacitorsMod.instance.worldCapacitors.put(id, cap);
		}
		wholeCapacitor = cap;
	}

	public void writeToNBT(NBTTagCompound tag) {
		super.writeToNBT(tag);
		if (wholeCapacitor != null) {
			tag.setBoolean("multi-present", true);
			tag.setTag("multi", wholeCapacitor.getNBTRepresentation());
			tag.setInteger("multi-id", wholeCapacitor.hashCode());
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
