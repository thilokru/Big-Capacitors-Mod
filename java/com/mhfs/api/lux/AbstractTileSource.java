package com.mhfs.api.lux;

import net.minecraft.util.BlockPos;

public abstract class AbstractTileSource extends AbstractMonoconnectedRoutingTile implements ILuxHandler{
	
	public void update(){
		super.update();
		if(connection == null)return;
		ILuxHandler link = (ILuxHandler)this.worldObj.getTileEntity(connection);
		for(BlockPos pos:drains){
			ILuxDrain drain = (ILuxDrain)this.worldObj.getTileEntity(pos);
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