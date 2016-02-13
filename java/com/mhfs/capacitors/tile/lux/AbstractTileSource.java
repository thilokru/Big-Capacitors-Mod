package com.mhfs.capacitors.tile.lux;

import com.mhfs.api.lux.AbstractMonoconnectedRoutingTile;
import com.mhfs.api.lux.LuxDrain;
import com.mhfs.api.lux.LuxHandler;
import net.minecraft.util.BlockPos;

public abstract class AbstractTileSource extends AbstractMonoconnectedRoutingTile implements LuxHandler{
	
	public void update(){
		super.update();
		if(connection == null)return;
		LuxHandler link = (LuxHandler)this.worldObj.getTileEntity(connection);
		for(BlockPos pos:drains){
			LuxDrain drain = (LuxDrain)this.worldObj.getTileEntity(pos);
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
