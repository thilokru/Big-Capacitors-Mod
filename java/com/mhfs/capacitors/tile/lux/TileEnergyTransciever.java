package com.mhfs.capacitors.tile.lux;

import com.mhfs.api.lux.AbstractMonoconnectedRoutingTile;
import com.mhfs.api.lux.IRouting;
import com.mhfs.api.lux.LuxDrain;
import com.mhfs.api.lux.LuxHandler;
import com.mhfs.capacitors.blocks.IOrientedBlock;
import com.mhfs.capacitors.misc.BlockPos;

import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;

public class TileEnergyTransciever extends AbstractMonoconnectedRoutingTile implements LuxDrain{
	
	private boolean isDrain = true;
	
	public void updateEntity() {
		super.updateEntity();
		if (worldObj.isRemote)
			return;
		if (connection == null)
			return;
		IRouting tile = (IRouting) connection.getTileEntity(worldObj);
		if (tile == null) {
			connection = null;
			return;
		}
		if(isDrain){
			tile.drainSetup(this.getPosition(), this.getPosition(), 64);
		}else{
			LuxHandler link = (LuxHandler)connection.getTileEntity(worldObj);
			for(BlockPos pos:drains){
				LuxDrain drain = (LuxDrain)pos.getTileEntity(worldObj);
				if(drain == null)continue;
				link.energyFlow(this.getPosition(), pos, getEnergyForTarget(drain.getMaxInput(), drain.getNeed(), drains.size()));
			}
		}
	}
	
	private long getEnergyForTarget(long maxInput, long need, int drainCount) {
		INeighbourEnergyHandler handler = getConnectedTile();
		if(handler == null)return 0;
		long amount = Math.min(maxInput, Math.min(need, handler.getEnergyStored()/drainCount));
		handler.drain(amount);
		return amount;
	}

	public boolean isDrain(){
		return isDrain;
	}
	
	public void switchMode(){
		isDrain = !isDrain;
		if(this.connection == null)return;
		IRouting handler = (IRouting)this.connection.getTileEntity(worldObj);
		if(handler == null)return;
		handler.handleDisconnect(this.getPosition(), 64);
		handler.connect(this.getPosition());
	}
	
	public INeighbourEnergyHandler getConnectedTile(){
		if(this.blockType == null)return null;
		ForgeDirection direction = ((IOrientedBlock)this.blockType).getOrientation(worldObj, xCoord, yCoord, zCoord);
		BlockPos tilePos = this.getPosition().clone().goTowards(direction, 1);
		TileEntity entity = tilePos.getTileEntity(worldObj);
		if(entity instanceof INeighbourEnergyHandler){
			INeighbourEnergyHandler handler = (INeighbourEnergyHandler)entity;
			return handler;
		}
		return null;
	}

	@Override
	public void energyFlow(BlockPos lastHop, BlockPos dst, long amount) {
		if(isDrain){
			INeighbourEnergyHandler handler = getConnectedTile();
			if(handler == null)return;
			handler.fill(amount);
		}
	}

	@Override
	public long getNeed() {
		INeighbourEnergyHandler handler = getConnectedTile();
		if(handler == null)return 0;
		return handler.getNeed();
	}

	@Override
	public long getMaxInput() {
		INeighbourEnergyHandler handler = getConnectedTile();
		if(handler == null)return 0;
		return handler.getMaxTransfer();
	}

}
