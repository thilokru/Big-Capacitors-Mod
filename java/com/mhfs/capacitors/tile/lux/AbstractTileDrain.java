package com.mhfs.capacitors.tile.lux;

import com.mhfs.api.lux.AbstractMonoconnectedRoutingTile;
import com.mhfs.api.lux.IRouting;
import com.mhfs.api.lux.LuxDrain;
import com.mhfs.capacitors.misc.BlockPos;

public abstract class AbstractTileDrain extends AbstractMonoconnectedRoutingTile implements LuxDrain, IRouting {

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
		tile.drainSetup(this.getPosition(), this.getPosition(), 64);
	}

	@Override
	public void handleDisconnect(BlockPos handler, int level) {
		super.handleDisconnect(handler, level);
		if(connection == null)return;
		IRouting tile = (IRouting) connection.getTileEntity(worldObj);
		tile.drainSetup(this.getPosition(), this.getPosition(), 64);
		markForUpdate();
	}

	@Override
	public void handlerSetupRequest(BlockPos requester) {
		IRouting handler = (IRouting) requester.getTileEntity(worldObj);
		BlockPos position = getPosition();
		handler.drainSetup(position, position, 64);
	}

	@Override
	public void connect(BlockPos foreign) {
		super.connect(foreign);
		if (connection != null) {
			IRouting tile = (IRouting) connection.getTileEntity(worldObj);
			tile.drainSetup(this.getPosition(), this.getPosition(), 64);
		}
	}
}
