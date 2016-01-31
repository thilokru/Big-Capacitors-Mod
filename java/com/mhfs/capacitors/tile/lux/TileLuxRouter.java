package com.mhfs.capacitors.tile.lux;

import java.util.HashSet;
import java.util.Set;

import com.mhfs.capacitors.misc.BlockPos;
import com.mhfs.capacitors.misc.HashSetHelper;

import net.minecraft.nbt.NBTTagCompound;

public class TileLuxRouter extends AbstractRoutingTile implements LuxHandler {

	private Set<BlockPos> toRender;

	public TileLuxRouter() {
		super();
		toRender = new HashSet<BlockPos>();
	}

	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);
		
		this.toRender = HashSetHelper.nbtToBlockPosSet(tag.getCompoundTag("render"));
	}

	public void writeToNBT(NBTTagCompound tag) {
		super.writeToNBT(tag);

		tag.setTag("render", HashSetHelper.blockPosSetToNBT(toRender));
		toRender.clear();
	}

	public Set<BlockPos> getConnectionsToRender() {
		return this.connections;
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

	public int getRouteSucction() {
		if(routes.keySet().size() == 0)return 0;
		return routes.get(routes.keySet().iterator().next()).sucction;
	}

	@Override
	public boolean isValidConnection(IRouting foreign) {
		return foreign instanceof LuxHandler;
	}
}
