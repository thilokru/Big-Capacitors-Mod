package com.mhfs.capacitors.tile.lux;

import java.util.HashSet;
import java.util.Set;

import com.mhfs.capacitors.misc.BlockPos;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;

public class TileSource extends TileEntity implements LuxHandler{
	
	public BlockPos connection;
	public Set<BlockPos> drains;
	
	public TileSource(){
		drains = new HashSet<BlockPos>();
	}
	
	public void updateEntity(){
		if(worldObj.isRemote)return;
		this.drains.clear();
		if(connection == null)return;
		LuxHandler link = (LuxHandler)connection.getTileEntity(worldObj);
		if(link == null)return;
		link.handlerSetupRequest(getPosition());
		for(BlockPos pos:drains){
			LuxDrain drain = (LuxDrain)pos.getTileEntity(worldObj);
			if(drain == null)continue;
			link.energyFlow(this.getPosition(), pos, drain.getNeed());
		}
	}

	@Override
	public void drainSetup(BlockPos requester, BlockPos lastHop, int value) {
		drains.add(requester);
	}

	@Override
	public void handlerSetupRequest(BlockPos requester) {
		return;
	}

	@Override
	public void handleDisconnect(BlockPos handler, int level) {
		drains.remove(handler);
	}

	@Override
	public void energyFlow(BlockPos lastHop, BlockPos dst, long amount) {
		return;
	}

	@Override
	public void connect(int x, int y, int z) {
		BlockPos foreign = new BlockPos(x, y, z);
		LuxHandler router = (LuxHandler) foreign.getTileEntity(worldObj);
		if(router == null)return;
		router.internalConnect(this);
		this.internalConnect(router);
	}

	@Override
	public void internalConnect(LuxHandler foreign) {
		BlockPos newPos = foreign.getPosition();
		if(connection != null && !newPos.equals(connection)){
			LuxHandler handler = (LuxHandler)connection.getTileEntity(worldObj);
			if(handler != null){
				handler.handleDisconnect(this.getPosition(), 64);
			}
		}
		connection = newPos;
		foreign.handlerSetupRequest(getPosition());
		this.markDirty();
		this.worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
	}

	@Override
	public BlockPos getPosition() {
		return new BlockPos(this.xCoord, this.yCoord, this.zCoord);
	}

	public void onDestroy() {
		if(connection == null)return;
		LuxHandler router = (LuxHandler) connection.getTileEntity(worldObj);
		if(router == null)return;
		router.handleDisconnect(getPosition(), 64);
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

}
