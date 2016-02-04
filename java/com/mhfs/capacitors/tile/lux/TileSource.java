package com.mhfs.capacitors.tile.lux;

import com.mhfs.capacitors.misc.BlockPos;

public class TileSource extends AbstractMonoconectedRoutingTile implements LuxHandler{
	
	public void updateEntity(){
		super.updateEntity();
		LuxHandler link = (LuxHandler)connection.getTileEntity(worldObj);
		for(BlockPos pos:drains){
			LuxDrain drain = (LuxDrain)pos.getTileEntity(worldObj);
			if(drain == null)continue;
			link.energyFlow(this.getPosition(), pos, drain.getNeed());
		}
	}

	@Override
	public void energyFlow(BlockPos lastHop, BlockPos dst, long amount) {
		return;
	}
}
