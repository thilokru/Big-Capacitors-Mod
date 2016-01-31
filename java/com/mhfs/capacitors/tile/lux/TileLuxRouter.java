package com.mhfs.capacitors.tile.lux;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.mhfs.capacitors.misc.BlockPos;
import com.mhfs.capacitors.misc.HashSetHelper;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;

public class TileLuxRouter extends TileEntity implements LuxHandler {

	private Set<BlockPos> connections;
	private Map<BlockPos, SucctionSpec> routes; // Maps Specs (including route)
												// to LuxDrain(BlockPos)
	private Set<BlockPos> toRender;
	private Set<BlockPos> disconnecting;

	public TileLuxRouter() {
		connections = new HashSet<BlockPos>();
		routes = new HashMap<BlockPos, SucctionSpec>();
		toRender = new HashSet<BlockPos>();
		disconnecting = new HashSet<BlockPos>();
	}

	public void updateEntity() {
		disconnecting.clear();
		if (!worldObj.isRemote) {
			for (BlockPos pos : connections) {
				LuxHandler foreign = (LuxHandler) pos.getTileEntity(this.worldObj);
				if(foreign == null){
					continue;
				}
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
		this.toRender = HashSetHelper.nbtToBlockPosSet(tag.getCompoundTag("render"));
	}

	public void writeToNBT(NBTTagCompound tag) {
		super.writeToNBT(tag);

		Gson gson = new GsonBuilder().enableComplexMapKeySerialization().create();
		tag.setTag("con", HashSetHelper.blockPosSetToNBT(connections));
		Type routeType = new TypeToken<HashMap<BlockPos, SucctionSpec>>(){}.getType();
		String json = gson.toJson(this.routes, routeType);
		tag.setString("route", json);
		tag.setTag("render", HashSetHelper.blockPosSetToNBT(toRender));
		toRender.clear();
	}

	public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity pkt) {
		this.readFromNBT(pkt.func_148857_g());
	}

	public Packet getDescriptionPacket() {
		NBTTagCompound tag = new NBTTagCompound();
		writeToNBT(tag);
		return new S35PacketUpdateTileEntity(xCoord, yCoord, zCoord, 1, tag);
	}

	public Set<BlockPos> getConnectionsToRender() {
		return this.toRender;
	}

	public BlockPos getPosition() {
		return new BlockPos(this.xCoord, this.yCoord, this.zCoord);
	}

	@Override
	public void drainSetup(BlockPos requester, BlockPos lastHop, int value) {
		SucctionSpec available = routes.get(requester);
		if (available != null && available.lastHop != lastHop && available.sucction >= value)
			return;
		routes.put(requester, new SucctionSpec(lastHop, value));
		this.markDirty();
		this.worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
	}

	@Override
	public void handlerSetupRequest(BlockPos pos) {
		LuxHandler requester = (LuxHandler) pos.getTileEntity(this.worldObj);
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
		this.connections.remove(handler);
		this.routes.clear();
		disconnecting.add(handler);
		connections.remove(handler);
		for (BlockPos pos : connections) {
			LuxHandler foreign = (LuxHandler) pos.getTileEntity(this.worldObj);
			if(foreign == null)continue;
			if (level > 1)
				foreign.handleDisconnect(handler, level - 1);
		}
		this.markDirty();
		this.worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
	}

	@Override
	public void energyFlow(BlockPos lastHop, BlockPos dst, long amount) {
		this.toRender.add(lastHop);
		BlockPos hopPos = routes.get(dst).lastHop;
		this.toRender.add(hopPos);
		LuxHandler hop = (LuxHandler) hopPos.getTileEntity(this.worldObj);
		hop.energyFlow(this.getPosition(), dst, amount);
		this.markDirty();
		this.worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
	}

	public void connect(int x, int y, int z) {
		if(worldObj.isRemote)return;
		BlockPos foreign = new BlockPos(x, y, z);
		LuxHandler router = (LuxHandler) foreign.getTileEntity(worldObj);
		if(router == null)return;
		router.internalConnect(this);
		this.internalConnect(router);
	}

	public void internalConnect(LuxHandler foreign) {
		this.connections.add(foreign.getPosition());
		foreign.handlerSetupRequest(getPosition());
		this.markDirty();
		this.worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
	}

	public void onDestroy() {
		for (BlockPos pos : connections) {
			LuxHandler foreign = (LuxHandler) pos.getTileEntity(this.worldObj);
			if(foreign == null)continue;
			foreign.handleDisconnect(this.getPosition(), 64);
		}
	}

	public int getRouteSucction() {
		if(routes.keySet().size() == 0)return 0;
		return routes.get(routes.keySet().iterator().next()).sucction;
	}
}
