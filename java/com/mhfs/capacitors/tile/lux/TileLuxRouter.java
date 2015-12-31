package com.mhfs.capacitors.tile.lux;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.google.gson.Gson;
import com.mhfs.capacitors.misc.BlockPos;
import com.mhfs.capacitors.misc.HashSetHelper;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class TileLuxRouter extends TileEntity implements LuxHandler {

	private Set<BlockPos> connections;
	private Map<BlockPos, SucctionSpec> routes; // Maps Specs (including route)
												// to LuxDrain(BlockPos)
	private Set<BlockPos> toRender;

	public TileLuxRouter() {
		connections = new HashSet<BlockPos>();
		routes = new HashMap<BlockPos, SucctionSpec>();
		toRender = new HashSet<BlockPos>();
	}

	public void updateEntity() {
		toRender.clear();
	}

	@SuppressWarnings("unchecked")
	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);

		this.connections = HashSetHelper.nbtToBlockPosSet(tag.getCompoundTag("con"));

		String json = tag.getString("route");
		Gson gson = new Gson();
		this.routes = gson.fromJson(json, routes.getClass());

		this.toRender = HashSetHelper.nbtToBlockPosSet(tag.getCompoundTag("render"));
	}

	public void writeToNBT(NBTTagCompound tag) {
		super.writeToNBT(tag);

		Gson gson = new Gson();
		tag.setTag("con", HashSetHelper.blockPosSetToNBT(connections));
		tag.setString("route", gson.toJson(this.routes));
		tag.setTag("render", HashSetHelper.blockPosSetToNBT(toRender));
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
		return this.connections;
	}

	public BlockPos getPosition() {
		return new BlockPos(this.xCoord, this.yCoord, this.zCoord);
	}

	@Override
	public void drainSetup(World world, int value, BlockPos requester, BlockPos lastHop) {
		SucctionSpec available = routes.get(requester);
		if (available != null && available.sucction >= value)
			return;
		routes.put(requester, new SucctionSpec(lastHop, value));
		if (value == 1)
			return;
		for (BlockPos pos : connections) {
			TileLuxRouter foreign = (TileLuxRouter) pos.getTileEntity(world);
			if (foreign.equals(lastHop))
				continue;
			foreign.drainSetup(world, value - 1, requester, lastHop);
		}
		this.markDirty();
		this.worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
	}

	@Override
	public void handlerSetupRequest(World world, BlockPos pos) {
		LuxHandler requester = (LuxHandler) pos.getTileEntity(world);
		for (BlockPos drain : routes.keySet()) {
			SucctionSpec spec = routes.get(drain);
			if (spec.sucction > 1) {
				requester.drainSetup(world, spec.sucction - 1, drain, getPosition());
			}
		}
	}

	@Override
	public void handleDisconnect(World world, BlockPos handler, int level) {
		this.routes.clear();
		connections.remove(handler);
		for (BlockPos pos : connections) {
			LuxHandler foreign = (LuxHandler) pos.getTileEntity(world);
			foreign.handleDisconnect(world, handler, level - 1);
		}
		this.markDirty();
		this.worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
	}

	@Override
	public void energyFlow(World world, BlockPos dst, long amount) {
		BlockPos hopPos = routes.get(dst).lastHop;
		this.toRender.add(hopPos);
		LuxHandler hop = (LuxHandler) hopPos.getTileEntity(world);
		hop.energyFlow(world, dst, amount);
		this.markDirty();
		this.worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
	}

	public void connect(int x, int y, int z) {
		BlockPos foreign = new BlockPos(x, y, z);
		TileLuxRouter router = (TileLuxRouter) foreign.getTileEntity(worldObj);
		router.internalConnect(this);
		this.internalConnect(router);
	}

	private void internalConnect(TileLuxRouter foreign) {
		this.connections.add(foreign.getPosition());
		foreign.handlerSetupRequest(worldObj, getPosition());
		this.markDirty();
		this.worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
	}
}
