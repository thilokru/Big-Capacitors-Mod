package com.mhfs.capacitors.tile.lux;

import com.mhfs.api.lux.AbstractMonoconnectedRoutingTile;
import com.mhfs.api.lux.IRouting;
import com.mhfs.api.lux.LuxDrain;
import com.mhfs.api.lux.LuxHandler;
import com.mhfs.capacitors.blocks.IOrientedBlock;
import com.mhfs.capacitors.misc.IRotatable;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;

public class TileEnergyTransciever extends AbstractMonoconnectedRoutingTile implements LuxDrain, IRotatable{
	
	private boolean isDrain = true;
	
	public void update() {
		super.update();
		if (worldObj.isRemote)
			return;
		if (connection == null)
			return;
		IRouting tile = (IRouting) this.worldObj.getTileEntity(connection);
		if (tile == null) {
			connection = null;
			return;
		}
		if(isDrain){
			tile.drainSetup(this.getPosition(), this.getPosition(), 64);
		}else{
			LuxHandler link = (LuxHandler)this.worldObj.getTileEntity(connection);
			for(BlockPos pos:drains){
				LuxDrain drain = (LuxDrain)this.worldObj.getTileEntity(pos);
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
		IRouting handler = (IRouting)this.worldObj.getTileEntity(connection);
		if(handler == null)return;
		handler.handleDisconnect(this.getPosition(), 64);
		handler.connect(this.getPosition());
	}
	
	public INeighbourEnergyHandler getConnectedTile(){
		if(this.blockType == null)return null;
		EnumFacing direction = ((IOrientedBlock)this.blockType).getOrientation(worldObj, this.pos);
		BlockPos tilePos = this.getPosition().offset(direction);
		TileEntity entity = this.worldObj.getTileEntity(tilePos);
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

	@Override
	public EnumFacing getRotation() {
		return ((IOrientedBlock)this.blockType).getOrientation(worldObj, this.pos);
	}

}
