package com.mhfs.capacitors.tile;

import net.minecraft.nbt.NBTTagCompound;
import cofh.api.energy.IEnergyStorage;

public class CapacitorEnergyStorage implements IEnergyStorage {
	
	private long energy, capacity;
	
	public CapacitorEnergyStorage(long capacity){
		this.capacity = capacity;
		this.energy = 0;
	}
	
	private CapacitorEnergyStorage(){}
	
	public static CapacitorEnergyStorage readFromNBT(NBTTagCompound nbt) {
		CapacitorEnergyStorage storage = new CapacitorEnergyStorage();
		
		storage.capacity = nbt.getLong("capacity");
		storage.energy = nbt.getLong("energy");

		if (storage.energy > storage.capacity) {
			storage.energy = storage.capacity;
		}
		return storage;
	}

	public NBTTagCompound writeToNBT(NBTTagCompound nbt) {

		if (energy < 0) {
			energy = 0;
		}
		nbt.setLong("capacity", capacity);
		nbt.setLong("energy", energy);
		return nbt;
	}

	@Override
	public int receiveEnergy(int maxReceive, boolean simulate) {
		long pot = Math.min(capacity - energy, maxReceive);
		int receive;
		if(pot > Integer.MAX_VALUE){
			receive = Integer.MAX_VALUE;
		}else{
			receive = (int) pot;
		}
		
		if(!simulate){
			energy += receive;
		}
		return receive;
	}

	@Override
	public int extractEnergy(int maxExtract, boolean simulate) {
		long pot = Math.min(energy, maxExtract);
		int extract;
		if(pot > Integer.MAX_VALUE){
			extract = Integer.MAX_VALUE;
		}else{
			extract = (int) pot;
		}
		
		if(!simulate){
			energy -= extract;
		}
		return extract;
	}

	@Override
	public int getEnergyStored() {
		if(energy > Integer.MAX_VALUE){
			return Integer.MAX_VALUE;
		}
		return (int) energy;
	}

	@Override
	public int getMaxEnergyStored() {
		if(capacity > Integer.MAX_VALUE){
			return Integer.MAX_VALUE;
		}
		return (int) capacity;
	}
	
	public long getAllEnergyStored(){
		return energy;
	}

	public long getWholeCapacity(){
		return capacity;
	}
	
	public void setEnergyStored(long energy){
		this.energy = energy;
		if(energy > capacity){
			this.energy = capacity;
		}
	}
	
	public void setCapacity(long capacity){
		this.capacity = capacity;
		if(energy > capacity){
			this.energy = capacity;
		}
	}
}
