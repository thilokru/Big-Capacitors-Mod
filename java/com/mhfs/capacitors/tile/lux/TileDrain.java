package com.mhfs.capacitors.tile.lux;

import com.mhfs.capacitors.misc.BlockPos;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;

public class TileDrain extends TileEntity implements LuxDrain, IRouting{
	
	private BlockPos connection;
	private long energy = 0;
	
	public void updateEntity(){
		if(worldObj.isRemote)return;
		if(connection == null)return;
		IRouting tile = (IRouting)connection.getTileEntity(worldObj);
		if(tile == null){
			connection = null;
			return;
		}
		tile.drainSetup(this.getPosition(), this.getPosition(), 64);
	}

	@Override
	public void drainSetup(BlockPos requester, BlockPos lastHop, int value) {
		return;
	}

	@Override
	public void handlerSetupRequest(BlockPos requester) {
		IRouting handler = (IRouting)requester.getTileEntity(worldObj);
		BlockPos position = getPosition();
		handler.drainSetup(position, position, 64);
	}

	@Override
	public void handleDisconnect(BlockPos handler, int level) {
		if(handler == connection){
			connection = null;
			return;
		}
	}

	@Override
	public void energyFlow(BlockPos lastHop, BlockPos dst, long amount) {
		this.energy = amount;
		this.markDirty();
		this.worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
	}

	@Override
	public long getNeed() {
		return System.currentTimeMillis();
	}

	@Override
	public long getMaxInput() {
		return System.currentTimeMillis();
	}
	
	public long getEnergy(){
		return energy;
	}

	public void connect(BlockPos pos) {
		if(worldObj.isRemote)return;
		AbstractRoutingTile router = (AbstractRoutingTile) pos.getTileEntity(worldObj);
		if(router == null || connection.equals(pos))return;
		if(connection != null && !pos.equals(connection)){
			IRouting handler = (IRouting)connection.getTileEntity(worldObj);
			if(handler != null){
				handler.handleDisconnect(this.getPosition(), 64);
			}
		}
		router.handlerSetupRequest(this.getPosition());
		router.connect(this.getPosition());
		this.markDirty();
		this.worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
	}

	@Override
	public BlockPos getPosition() {
		return new BlockPos(this.xCoord, this.yCoord, this.zCoord);
	}
	
	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);
		this.energy = tag.getLong("energy");
		if(tag.hasKey("connection")){
			connection = BlockPos.fromNBT(tag, "connection");
		}
	}

	public void writeToNBT(NBTTagCompound tag) {
		super.writeToNBT(tag);
		
		tag.setLong("energy", energy);
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

	public void onDestroy() {
		if(connection == null)return;
		IRouting router = (IRouting) connection.getTileEntity(worldObj);
		if(router == null)return;
		router.handleDisconnect(getPosition(), 64);
	}

}
