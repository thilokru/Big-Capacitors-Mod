package com.mhfs.api.lux;

import java.util.HashSet;
import java.util.Set;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ITickable;

/**
 * This class provides an example implementation for a monoconnected network member.
 * It does not route on its own, it lets a normal AbstractRoutingTile do the work.
 * Therefore no direct links are allowed.
 * @author Thilo
 *
 */
public abstract class AbstractMonoconnectedRoutingTile extends TileEntity implements IRouting, ITickable{

	protected BlockPos connection;
	protected Set<BlockPos> drains;
	
	public AbstractMonoconnectedRoutingTile(){
		drains = new HashSet<BlockPos>();
	}
	
	/**
	 * Fetches routing information
	 */
	@Override
	public void update(){
		if(worldObj.isRemote)return;
		this.drains.clear();
		if(connection == null)return;
		ILuxHandler link = (ILuxHandler)this.worldObj.getTileEntity(connection);
		if(link == null){
			connection = null;
			markForUpdate();
			return;
		}
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
		IRouting router = (IRouting) worldObj.getTileEntity(pos);
		if(router == null || router.equals(this) || pos.equals(this.connection))return;
		if(router instanceof AbstractMonoconnectedRoutingTile)return;
		if(connection != null && !pos.equals(connection)){
			IRouting handler = (IRouting)this.worldObj.getTileEntity(connection);
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
		return new BlockPos(this.pos);
	}
	
	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);
		if(tag.hasKey("connection")){
			connection = BlockPos.fromLong(tag.getLong("connection"));
		}else{
			connection = null;
		}
	}

	public void writeToNBT(NBTTagCompound tag) {
		super.writeToNBT(tag);
		
		if(connection == null)return;
		tag.setLong("connection", connection.toLong());
	}

	public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity pkt) {
		this.readFromNBT(pkt.getNbtCompound());
	}

	public Packet<?> getDescriptionPacket() {
		NBTTagCompound tag = new NBTTagCompound();
		writeToNBT(tag);
		return new S35PacketUpdateTileEntity(this.pos, this.getBlockMetadata(), tag);
	}
	
	protected void markForUpdate(){
		this.markDirty();
		worldObj.markBlockForUpdate(this.pos);
	}

	public void onDestroy() {
		if(connection == null)return;
		IRouting router = (IRouting) this.worldObj.getTileEntity(connection);
		if(router == null)return;
		router.handleDisconnect(getPosition(), 64);
	}
}
