package com.mhfs.capacitors.tile.lux;

import java.util.HashSet;
import java.util.Set;

import com.mhfs.api.lux.AbstractRoutingTile;
import com.mhfs.api.lux.IRouting;
import com.mhfs.api.lux.LuxHandler;
import com.mhfs.capacitors.misc.BlockPos;
import com.mhfs.capacitors.misc.HashSetHelper;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;

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

	public Set<BlockPos> getPoweredConnections() {
		return this.toRender;
	}

	public Set<BlockPos> getConnections() {
		return this.connections;
	}

	public void resetPoweredState() {
		this.toRender.clear();
	}

	@Override
	public void energyFlow(BlockPos lastHop, BlockPos dst, long amount) {
		BlockPos hopPos = routes.get(dst).lastHop;
		this.toRender.add(hopPos);
		LuxHandler hop = (LuxHandler) hopPos.getTileEntity(this.worldObj);
		hop.energyFlow(this.getPosition(), dst, amount);
		this.markDirty();
		worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
	}

	public int getRouteSucction() {
		if (routes.keySet().size() == 0)
			return 0;
		return routes.get(routes.keySet().iterator().next()).sucction;
	}

	@Override
	public boolean isValidConnection(IRouting foreign) {
		return foreign instanceof LuxHandler;
	}

	@SideOnly(Side.CLIENT)
	public AxisAlignedBB getRenderBoundingBox() {
		return INFINITE_EXTENT_AABB;
	}
}
