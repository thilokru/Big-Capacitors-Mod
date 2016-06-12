package com.mhfs.api.lux;

import net.minecraft.util.math.BlockPos;

public abstract class AbstractTileDrain extends AbstractMonoconnectedRoutingTile implements ILuxDrain, IRouting {

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
		tile.drainSetup(this.getPosition(), this.getPosition(), 64);
	}

	@Override
	public void handleDisconnect(BlockPos handler, int level) {
		super.handleDisconnect(handler, level);
		if(connection == null)return;
		IRouting tile = (IRouting) this.worldObj.getTileEntity(connection);
		tile.drainSetup(this.getPosition(), this.getPosition(), 64);
		markForUpdate();
	}

	@Override
	public void handlerSetupRequest(BlockPos requester) {
		IRouting handler = (IRouting) this.worldObj.getTileEntity(requester);
		BlockPos position = getPosition();
		handler.drainSetup(position, position, 64);
	}

	@Override
	public void connect(BlockPos foreign) {
		super.connect(foreign);
		if (connection != null) {
			IRouting tile = (IRouting) this.worldObj.getTileEntity(connection);
			tile.drainSetup(this.getPosition(), this.getPosition(), 64);
		}
	}
}
