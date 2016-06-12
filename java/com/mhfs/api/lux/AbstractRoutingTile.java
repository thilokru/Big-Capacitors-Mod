package com.mhfs.api.lux;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.mhfs.capacitors.misc.HashSetHelper;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.ITickable;

public abstract class AbstractRoutingTile extends TileEntity implements IRouting, ITickable{

	protected Set<BlockPos> connections;
	protected Map<BlockPos, SucctionSpec> routes; // Maps Specs (including route)
												// to LuxDrain(BlockPos)
	protected Set<BlockPos> disconnecting;

	public AbstractRoutingTile() {
		connections = new HashSet<BlockPos>();
		routes = new HashMap<BlockPos, SucctionSpec>();
		disconnecting = new HashSet<BlockPos>();
	}

	@Override
	public void update() {
		disconnecting.clear();
		if (!worldObj.isRemote) {
			for (BlockPos pos : connections) {
				TileEntity tile = this.worldObj.getTileEntity(pos);
				if(!(tile instanceof IRouting))continue;
				IRouting foreign = (IRouting) tile;
				for (BlockPos requester : routes.keySet()) {
					int sucction = routes.get(requester).sucction;
					if(sucction <= 1)continue;
					foreign.drainSetup(requester, this.getPosition(), routes.get(requester).sucction - 1);
				}
			}
		}
	}

	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);

		this.connections = HashSetHelper.nbtToBlockPosSet(tag.getCompoundTag("con"));

		String json = tag.getString("route");
		Gson gson = new Gson();
		Type routeType = new TypeToken<HashMap<BlockPos, SucctionSpec>>(){}.getType();
		this.routes = gson.fromJson(json, routeType);
	}

	public void writeToNBT(NBTTagCompound tag) {
		super.writeToNBT(tag);

		Gson gson = new GsonBuilder().enableComplexMapKeySerialization().create();
		tag.setTag("con", HashSetHelper.blockPosSetToNBT(connections));
		Type routeType = new TypeToken<HashMap<BlockPos, SucctionSpec>>(){}.getType();
		String json = gson.toJson(this.routes, routeType);
		tag.setString("route", json);
	}

	public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt) {
		this.readFromNBT(pkt.getNbtCompound());
	}

	public Packet<?> getDescriptionPacket() {
		NBTTagCompound tag = new NBTTagCompound();
		writeToNBT(tag);
		return new SPacketUpdateTileEntity(this.pos, 1, tag);
	}

	@Override
	public BlockPos getPosition() {
		return new BlockPos(this.pos);
	}

	@Override
	public void drainSetup(BlockPos requester, BlockPos lastHop, int value) {
		SucctionSpec available = routes.get(requester);
		if (available != null && available.lastHop != lastHop && available.sucction >= value)
			return;
		routes.put(requester, new SucctionSpec(lastHop, value));
		this.markDirty();
		this.worldObj.markBlockForUpdate(this.pos);
	}

	@Override
	public void handlerSetupRequest(BlockPos pos) {
		IRouting requester = (IRouting) worldObj.getTileEntity(pos);
		for (BlockPos drain : routes.keySet()) {
			SucctionSpec spec = routes.get(drain);
			if (spec.sucction > 1) {
				requester.drainSetup(drain, getPosition(), spec.sucction - 1);
			}
		}
	}

	@Override
	public void handleDisconnect(BlockPos handler, int level) {
		if (disconnecting.contains(handler))
			return;
		this.routes.clear();
		disconnecting.add(handler);
		connections.remove(handler);
		for (BlockPos pos : connections) {
			IRouting foreign = (IRouting) this.worldObj.getTileEntity(pos);
			if(foreign == null)continue;
			if (level > 1)
				foreign.handleDisconnect(handler, level - 1);
		}
		this.markDirty();
		this.worldObj.markBlockForUpdate(this.pos);
	}

	public void connect(BlockPos pos) {
		if(worldObj.isRemote)return;
		IRouting router = (IRouting) this.worldObj.getTileEntity(pos);
		if(!isValidConnection(router))return;
		if(router == null || connections.contains(pos))return;
		this.connections.add(pos);
		router.handlerSetupRequest(this.getPosition());
		router.connect(this.getPosition());
		this.markDirty();
		this.worldObj.markBlockForUpdate(this.pos);
	}
	
	/**
	 * Simply a callback to check if a connection is vaild.
	 * E.g. to check if your energy node tries to connect to a fluid network.
	 * @param foreign
	 * @return
	 */
	public abstract boolean isValidConnection(IRouting foreign);

	/**
	 * You need to call this when the block is destroyed in order to get the routing working.
	 */
	public void onDestroy() {
		Set<BlockPos> clone = new HashSet<BlockPos>();
		clone.addAll(connections);
		for (BlockPos pos : clone) {
			IRouting foreign = (IRouting) this.worldObj.getTileEntity(pos);
			if(foreign == null)continue;
			foreign.handleDisconnect(this.getPosition(), 64);
		}
	}
}
