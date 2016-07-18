package com.mhfs.capacitors.tile;

import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;

public abstract class AdvTileEntity extends TileEntity{

	public NBTTagCompound getUpdateTag(){
		return this.writeToNBT(super.getUpdateTag());
	}
	
	public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt) {
		this.readFromNBT(pkt.getNbtCompound());
	}
	
	@Override
	public SPacketUpdateTileEntity getUpdatePacket() {
		return new SPacketUpdateTileEntity(this.pos, this.getBlockMetadata(), this.getUpdateTag());
	}
	
	protected void markForUpdate(){
		this.markDirty();
		IBlockState state = this.worldObj.getBlockState(this.getPos());;
		worldObj.notifyBlockUpdate(this.pos, state, state, 3);
	}
}
