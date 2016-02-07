package com.mhfs.capacitors.tile.lux;

import com.mhfs.api.lux.AbstractMonoconnectedRoutingTile;
import com.mhfs.api.lux.LuxDrain;
import com.mhfs.api.lux.LuxHandler;
import com.mhfs.capacitors.misc.BlockPos;

public abstract class AbstractTileSource extends AbstractMonoconnectedRoutingTile implements LuxHandler{
	
	public void updateEntity(){
		super.updateEntity();
		if(connection == null)return;
		LuxHandler link = (LuxHandler)connection.getTileEntity(worldObj);
		for(BlockPos pos:drains){
			LuxDrain drain = (LuxDrain)pos.getTileEntity(worldObj);
			if(drain == null)continue;
			link.energyFlow(this.getPosition(), pos, getEnergyForTarget(drain.getMaxInput(), drain.getNeed(), drains.size()));
		}
	}
	
	protected abstract long getEnergyForTarget(long maxInput, long need, int drainCount);

	@Override
	public void energyFlow(BlockPos lastHop, BlockPos dst, long amount) {
		return;
	}
}
