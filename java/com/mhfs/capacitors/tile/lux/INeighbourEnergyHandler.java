package com.mhfs.capacitors.tile.lux;

public interface INeighbourEnergyHandler {
	
	public long getNeed();
	
	public long getMaxTransfer();
	
	public long getEnergyStored();
	
	public long getMaxEnergyStored();
	
	public long drain(long amount);
	
	public long fill(long amount);
}
