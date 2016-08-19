package com.mhfs.capacitors.tile;

import com.mhfs.api.helper.Helper;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;

public abstract class AdvTileEntity extends TileEntity {

	public NBTTagCompound getUpdateTag() {
		return this.writeToNBT(super.getUpdateTag());
	}

	public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt) {
		this.readFromNBT(pkt.getNbtCompound());
	}

	@Override
	public SPacketUpdateTileEntity getUpdatePacket() {
		return new SPacketUpdateTileEntity(this.pos, this.getBlockMetadata(), this.getUpdateTag());
	}

	protected void sendUpdate() {
		this.markDirty();
		Helper.sendUpdate(this);
	}
}
