package com.mhfs.api.lux;

import java.util.HashSet;
import java.util.Set;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;

public class LuxHandlerImpl implements ILuxHandler{

	private TileEntity local;
	private long need;
	private long buffer;
	
	private Set<BlockPos> active;
	
	public LuxHandlerImpl(TileEntity local){
		this.local = local;
		this.active = new HashSet<BlockPos>();
	}
	
	@Override
	public void energyFlow(BlockPos lastHop, BlockPos dst, long amount) {
		IRouting router = local.getCapability(LuxAPI.ROUTING_CAPABILITY, null);
		if(router == null)return;
		if(dst.equals(local.getPos())){
			buffer += amount;
		} else {
			BlockPos next = router.route(dst);
			if(next == null)return;
			ILuxHandler lux = local.getWorld().getTileEntity(next).getCapability(LuxAPI.LUX_FLOW_CAPABILITY, null);
			if(lux == null)return;
			this.active.add(next);
			lux.energyFlow(local.getPos(), dst, amount);
		}
	}
	
	public void setNeed(int need){
		this.need = need;
	}

	@Override
	public long getNeed() {
		return this.need;
	}
	
	public long drain(long maxAmount, boolean simulate){
		long toDrain = Math.min(buffer, maxAmount);
		if(!simulate){
			buffer -= toDrain;
		}
		return toDrain;
	}
	
	@Override
	public Set<BlockPos> getActive(){
		return active;
	}
	
	@Override
	public void resetActive(){
		this.active.clear();
	}

	@Override
	public BlockPos[] getActiveConnections() {
		return active.toArray(new BlockPos[0]);
	}

	@Override
	public void addActiveConnection(BlockPos active) {
		this.active.add(active);
	}

}
