package com.mhfs.capacitors.tile;

import com.mhfs.capacitors.BigCapacitorsMod;
import com.mhfs.capacitors.Fluids;

import cofh.api.energy.IEnergyProvider;
import cofh.api.energy.IEnergyReceiver;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ITickable;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidHandler;

public class TileTomahawk extends TileEntity implements IFluidHandler, IEnergyReceiver, IEnergyProvider, ITickable {

	private int energy;
	private FluidTank hydrogenTank;
	
	private double temperature; //UNIT: °C
	
	private boolean formed;

	private final static int MAX_ENERGY = 5000000;
	private final static int MAX_GAS_VOLUME = 3000;
	private final static int MAX_RF_DRAIN = 800;
	
	private final static double FUSION_START_TEMP = 10000000;
	private final static double ROOM_TEMP = 20;
	private final static double KELVIN_PER_RF = 10;
	private final static double LOSS_FACTOR = 0.00000001D;
	
	private final static int RF_PER_MB_HYDROGEN = 1500000;

	public TileTomahawk() {
		hydrogenTank = new FluidTank(MAX_GAS_VOLUME);
		temperature = ROOM_TEMP;
	}

	public void update() {
		formed = checkFormed();
		if(worldObj.isRemote)return;
		if(formed){
			if(temperature >= FUSION_START_TEMP && hydrogenTank.getFluidAmount() > 0){
				FluidStack stack = hydrogenTank.drain(1, true);
				int drain = (stack == null)?0:stack.amount;
				if(drain == 1){
					energy += RF_PER_MB_HYDROGEN;
					if(energy > MAX_ENERGY){
						energy = MAX_ENERGY;
					}
				}
			}else{
				long extract = Math.min(MAX_RF_DRAIN, energy);
				energy -= extract;
				temperature += extract * KELVIN_PER_RF;
				temperature -= (temperature - ROOM_TEMP)*LOSS_FACTOR;
			}
			this.markDirty();
			worldObj.markBlockForUpdate(this.pos);
		}else{
			if(temperature > ROOM_TEMP){
				this.temperature = ROOM_TEMP;
				this.markDirty();
				worldObj.markBlockForUpdate(this.pos);
			}
			if(energy > 0){
				energy = 0;
				this.markDirty();
				worldObj.markBlockForUpdate(this.pos);
			}
		}
	}
	
	private boolean checkFormed() {
		return BigCapacitorsMod.instance.fusionReactorMulti.complete(this.pos, worldObj);
	}

	public boolean isFormed(){
		return formed;
	}
	
	public FluidTank getHydrogenTank(){
		return hydrogenTank;
	}

	@Override
	public int fill(EnumFacing from, FluidStack resource, boolean doFill) {
		if (resource.getFluid() == Fluids.gasHydrogen) {
			return hydrogenTank.fill(resource, doFill);
		}
		return 0;
	}

	@Override
	public FluidStack drain(EnumFacing from, FluidStack resource, boolean doDrain) {
		if (resource.getFluid() == Fluids.gasHydrogen) {
			return hydrogenTank.drain(resource.amount, doDrain);
		}
		return null;
	}
	

	@Override
	public FluidStack drain(EnumFacing from, int maxDrain, boolean doDrain) {
		return hydrogenTank.drain(maxDrain, doDrain);
	}
	

	@Override
	public boolean canFill(EnumFacing from, Fluid fluid) {
		return fluid == Fluids.gasHydrogen;
	}
	

	@Override
	public boolean canDrain(EnumFacing from, Fluid fluid) {
		return fluid == Fluids.gasHydrogen;
	}
	

	@Override
	public FluidTankInfo[] getTankInfo(EnumFacing from) {
		return new FluidTankInfo[] { hydrogenTank.getInfo()};
	}
	
	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);
		this.hydrogenTank.readFromNBT(tag);
		this.energy = tag.getInteger("energy");
		this.temperature = tag.getDouble("temperature");
	}

	public void writeToNBT(NBTTagCompound tag) {
		super.writeToNBT(tag);
		this.hydrogenTank.writeToNBT(tag);
		tag.setLong("energy", this.energy);
		tag.setDouble("temperature", this.temperature);
	}
	
	public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity pkt) {
		this.readFromNBT(pkt.getNbtCompound());
	}
	
	public Packet<?> getDescriptionPacket() {
		NBTTagCompound tag = new NBTTagCompound();
		writeToNBT(tag);
		return new S35PacketUpdateTileEntity(this.pos, 1, tag);
	}

	public double getTemperature() {
		return temperature;
	}
	
	public long getEnergyStored(){
		return energy;
	}
	
	public long getMaxEnergyStored(){
		return MAX_ENERGY;
	}
	
	@Override
	public int getEnergyStored(EnumFacing from) {
		return (int)energy;
	}

	@Override
	public int getMaxEnergyStored(EnumFacing from) {
		return MAX_ENERGY;
	}

	@Override
	public boolean canConnectEnergy(EnumFacing from) {
		return true;
	}

	@Override
	public int receiveEnergy(EnumFacing from, int maxReceive, boolean simulate) {
		int amount = Math.min(MAX_ENERGY - energy, maxReceive);
		if(!simulate){
			energy += amount;
		}
		return amount;
	}

	@Override
	public int extractEnergy(EnumFacing from, int maxExtract, boolean simulate) {
		int amount = Math.min(MAX_ENERGY, maxExtract);
		if(!simulate){
			energy -= amount;
		}
		return amount;
	}
}
