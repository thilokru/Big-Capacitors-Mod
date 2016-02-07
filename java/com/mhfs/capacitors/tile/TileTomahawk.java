package com.mhfs.capacitors.tile;

import com.mhfs.capacitors.BigCapacitorsMod;
import com.mhfs.capacitors.Fluids;
import com.mhfs.capacitors.misc.BlockPos;
import com.mhfs.capacitors.tile.lux.INeighbourEnergyHandler;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidHandler;

public class TileTomahawk extends TileEntity implements IFluidHandler, INeighbourEnergyHandler {

	private long energy;
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

	public void updateEntity() {
		super.updateEntity();
		formed = checkFormed();
		if(worldObj.isRemote)return;
		if(formed){
			if(temperature >= FUSION_START_TEMP && hydrogenTank.getFluidAmount() > 0){
				int drain = hydrogenTank.drain(1, true).amount;
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
			worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
		}else{
			if(temperature > ROOM_TEMP){
				this.temperature = ROOM_TEMP;
				this.markDirty();
				worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
			}
			if(energy > 0){
				energy = 0;
				this.markDirty();
				worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
			}
		}
	}
	
	private boolean checkFormed() {
		return BigCapacitorsMod.instance.fusionReactorMulti.complete(new BlockPos(xCoord, yCoord, zCoord), worldObj);
	}

	public boolean isFormed(){
		return formed;
	}
	
	public FluidTank getHydrogenTank(){
		return hydrogenTank;
	}

	@Override
	public int fill(ForgeDirection from, FluidStack resource, boolean doFill) {
		if (resource.getFluid() == Fluids.gasHydrogen) {
			return hydrogenTank.fill(resource, doFill);
		}
		return 0;
	}

	@Override
	public FluidStack drain(ForgeDirection from, FluidStack resource, boolean doDrain) {
		if (resource.getFluid() == Fluids.gasHydrogen) {
			return hydrogenTank.drain(resource.amount, doDrain);
		}
		return null;
	}
	

	@Override
	public FluidStack drain(ForgeDirection from, int maxDrain, boolean doDrain) {
		return hydrogenTank.drain(maxDrain, doDrain);
	}
	

	@Override
	public boolean canFill(ForgeDirection from, Fluid fluid) {
		return fluid == Fluids.gasHydrogen;
	}
	

	@Override
	public boolean canDrain(ForgeDirection from, Fluid fluid) {
		return fluid == Fluids.gasHydrogen;
	}
	

	@Override
	public FluidTankInfo[] getTankInfo(ForgeDirection from) {
		return new FluidTankInfo[] { hydrogenTank.getInfo()};
	}
	
	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);
		this.hydrogenTank.readFromNBT(tag);
		this.energy = tag.getLong("energy");
		this.temperature = tag.getDouble("temperature");
	}

	public void writeToNBT(NBTTagCompound tag) {
		super.writeToNBT(tag);
		this.hydrogenTank.writeToNBT(tag);
		tag.setLong("energy", this.energy);
		tag.setDouble("temperature", this.temperature);
	}
	
	public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity pkt) {
		this.readFromNBT(pkt.func_148857_g());
	}
	
	public Packet getDescriptionPacket() {
		NBTTagCompound tag = new NBTTagCompound();
		writeToNBT(tag);
		return new S35PacketUpdateTileEntity(xCoord, yCoord, zCoord, 1, tag);
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
	public long getNeed() {
		return MAX_ENERGY - energy;
	}

	@Override
	public long getMaxTransfer() {
		return MAX_ENERGY;
	}

	@Override
	public long drain(long amount) {
		return Math.min(amount, energy);
	}

	@Override
	public long fill(long amount) {
		energy += amount;
		if(energy > MAX_ENERGY){
			energy = MAX_ENERGY;
		}
		return amount;
	}

}
