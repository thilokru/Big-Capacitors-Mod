package com.mhfs.capacitors.tile.lux;

import java.util.HashSet;
import java.util.Set;

import com.mhfs.api.lux.AbstractRoutingTile;
import com.mhfs.api.lux.IRouting;
import com.mhfs.api.lux.ILuxHandler;
import com.mhfs.capacitors.misc.HashSetHelper;
import com.mhfs.capacitors.render.IConnected;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;

public class TileLuxRouter extends AbstractRoutingTile implements ILuxHandler, IConnected {

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

	public Set<BlockPos> getActiveConnections() {
		return this.toRender;
	}

	public Set<BlockPos> getConnections() {
		return this.connections;
	}

	public void resetConnectionState() {
		this.toRender.clear();
	}

	@Override
	public void energyFlow(BlockPos lastHop, BlockPos dst, long amount) {
		BlockPos hopPos = routes.get(dst).lastHop;
		this.toRender.add(hopPos);
		ILuxHandler hop = (ILuxHandler) this.worldObj.getTileEntity(hopPos);
		hop.energyFlow(this.getPosition(), dst, amount);
		this.markDirty();
		worldObj.markBlockForUpdate(this.pos);
	}

	public int getRouteSucction() {
		if (routes.keySet().size() == 0)
			return 0;
		return routes.get(routes.keySet().iterator().next()).sucction;
	}

	@Override
	public boolean isValidConnection(IRouting foreign) {
		return foreign instanceof ILuxHandler;
	}

	@SideOnly(Side.CLIENT)
	public AxisAlignedBB getRenderBoundingBox() {
		return INFINITE_EXTENT_AABB;
	}
}
