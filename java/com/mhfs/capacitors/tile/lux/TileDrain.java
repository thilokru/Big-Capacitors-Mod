package com.mhfs.capacitors.tile.lux;

import com.mhfs.capacitors.misc.BlockPos;
import net.minecraft.nbt.NBTTagCompound;

public class TileDrain extends AbstractMonoconectedRoutingTile implements LuxDrain, IRouting {

	private long energy = 0;

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
	public void energyFlow(BlockPos lastHop, BlockPos dst, long amount) {
		this.energy = amount;
		markForUpdate();
	}

	@Override
	public void connect(BlockPos foreign) {
		super.connect(foreign);
		if (connection != null) {
			IRouting tile = (IRouting) connection.getTileEntity(worldObj);
			tile.drainSetup(this.getPosition(), this.getPosition(), 64);
		}
	}

	@Override
	public long getNeed() {
		return System.currentTimeMillis();
	}

	@Override
	public long getMaxInput() {
		return System.currentTimeMillis();
	}

	public long getEnergy() {
		return energy;
	}

	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);
		this.energy = tag.getLong("energy");
	}

	public void writeToNBT(NBTTagCompound tag) {
		super.writeToNBT(tag);

		tag.setLong("energy", energy);
	}
}
