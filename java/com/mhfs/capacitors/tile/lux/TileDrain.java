package com.mhfs.capacitors.tile.lux;

import com.mhfs.capacitors.misc.BlockPos;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;

public class TileDrain extends TileEntity implements LuxDrain{
	
	private BlockPos connection;
	
	public void updateEntity(){
		if(worldObj.isRemote)return;
		if(connection == null)return;
		LuxHandler tile = (LuxHandler)connection.getTileEntity(worldObj);
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
		LuxHandler handler = (LuxHandler)requester.getTileEntity(worldObj);
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
	public void energyFlow(BlockPos dst, long amount) {
		return;
	}

	@Override
	public long getNeed() {
		return 0;
	}

	@Override
	public long getMaxInput() {
		return 0;
	}

	@Override
	public void connect(int x, int y, int z) {
		BlockPos foreign = new BlockPos(x, y, z);
		LuxHandler router = (LuxHandler) foreign.getTileEntity(worldObj);
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
