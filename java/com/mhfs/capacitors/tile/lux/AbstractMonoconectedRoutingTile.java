package com.mhfs.capacitors.tile.lux;

import java.util.HashSet;
import java.util.Set;

import com.mhfs.capacitors.misc.BlockPos;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;

public abstract class AbstractMonoconectedRoutingTile extends TileEntity implements IRouting{

	protected BlockPos connection;
	protected Set<BlockPos> drains;
	
	public AbstractMonoconectedRoutingTile(){
		drains = new HashSet<BlockPos>();
	}
	
	/**
	 * Fetches routing information
	 */
	public void updateEntity(){
		if(worldObj.isRemote)return;
		this.drains.clear();
		if(connection == null)return;
		LuxHandler link = (LuxHandler)connection.getTileEntity(worldObj);
		if(link == null)return;
		link.handlerSetupRequest(getPosition());
	}
	
	@Override
	public void handlerSetupRequest(BlockPos requester) {}

	@Override
	public void drainSetup(BlockPos requester, BlockPos lastHop, int value) {
		drains.add(requester);
		markForUpdate();
	}

	@Override
	public void handleDisconnect(BlockPos handler, int level) {
		drains.remove(handler);
		if(handler.equals(connection)){
			connection = null;
			return;
		}
		markForUpdate();
	}

	public void connect(BlockPos pos) {
		if(worldObj.isRemote)return;
		AbstractRoutingTile router = (AbstractRoutingTile) pos.getTileEntity(worldObj);
		if(router == null)return;
		if(connection != null && !pos.equals(connection)){
			IRouting handler = (IRouting)connection.getTileEntity(worldObj);
			if(handler != null){
				handler.handleDisconnect(this.getPosition(), 64);
			}
		}
		connection = pos;
		router.handlerSetupRequest(this.getPosition());
		router.connect(this.getPosition());
		markForUpdate();
	}

	@Override
	public BlockPos getPosition() {
		return new BlockPos(this.xCoord, this.yCoord, this.zCoord);
	}
	
	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);
		if(tag.hasKey("connection")){
			connection = BlockPos.fromNBT(tag, "connection");
		}
	}

	public void writeToNBT(NBTTagCompound tag) {
		super.writeToNBT(tag);
		
		if(connection == null)return;
		connection.writeToNBT(tag, "connection");
	}

	public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity pkt) {
		this.readFromNBT(pkt.func_148857_g());
	}

	public Packet getDescriptionPacket() {
		NBTTagCompound tag = new NBTTagCompound();
		writeToNBT(tag);
		return new S35PacketUpdateTileEntity(xCoord, yCoord, zCoord, 1, tag);
	}
	
	protected void markForUpdate(){
		this.markDirty();
		worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
	}

	public void onDestroy() {
		if(connection == null)return;
		IRouting router = (IRouting) connection.getTileEntity(worldObj);
		if(router == null)return;
		router.handleDisconnect(getPosition(), 64);
	}
}
