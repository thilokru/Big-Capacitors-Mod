package com.mhfs.capacitors.tile;

import com.mhfs.capacitors.Fluids;

import cofh.api.energy.EnergyStorage;
import cofh.api.energy.IEnergyHandler;
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

public class TileTomahawk extends TileEntity implements IEnergyHandler, IFluidHandler {

	private EnergyStorage storage;
	private FluidTank hydrogenTank;
	
	private double temperature; //UNIT: °C
	
	private boolean formed;

	private final static int MAX_ENERGY = 5000000;
	private final static int MAX_GAS_VOLUME = 3000;
	
	private final static double FUSION_START_TEMP = 10000000;
	private final static double ROOM_TEMP = 20;
	private final static int MAX_RF_DRAIN = 80;
	private final static double KELVIN_PER_RF = 10;
	private final static double LOSS_FACTOR = 0.00001D;
	
	private final static int RF_PER_MB_HYDROGEN = 1500000;

	public TileTomahawk() {
		storage = new EnergyStorage(MAX_ENERGY, Integer.MAX_VALUE);

		hydrogenTank = new FluidTank(MAX_GAS_VOLUME);
		
		temperature = ROOM_TEMP;
	}

	public void update() {
		formed = checkFormed();
		if(worldObj.isRemote)return;
		if(formed){
			if(temperature >= FUSION_START_TEMP && hydrogenTank.getFluidAmount() > 0){
				int drain = hydrogenTank.drain(1, true).amount;
				if(drain == 1){
					storage.receiveEnergy(RF_PER_MB_HYDROGEN, false);
				}
			}else{
				int extract = storage.extractEnergy(MAX_RF_DRAIN, false);
				temperature += extract * KELVIN_PER_RF;
				temperature -= temperature * LOSS_FACTOR;
			}
		}
	}
	
	private boolean checkFormed() {
		return false;
	}

	public boolean isFormed(){
		return formed;
	}

	@Override
	public boolean canConnectEnergy(ForgeDirection from) {
		return true;
	}

	@Override
	public int extractEnergy(ForgeDirection from, int maxExtract, boolean simulate) {
		return storage.extractEnergy(maxExtract, simulate);
	}

	@Override
	public int getEnergyStored(ForgeDirection from) {
		return storage.getEnergyStored();
	}

	@Override
	public int getMaxEnergyStored(ForgeDirection from) {
		return storage.getMaxEnergyStored();
	}

	@Override
	public int receiveEnergy(ForgeDirection from, int maxReceive, boolean simulate) {
		return storage.receiveEnergy(maxReceive, simulate);
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
		return fluid == Fluids.gasHelium || fluid == Fluids.gasHydrogen;
	}
	

	@Override
	public boolean canDrain(ForgeDirection from, Fluid fluid) {
		return fluid == Fluids.gasHelium || fluid == Fluids.gasHydrogen;
	}
	

	@Override
	public FluidTankInfo[] getTankInfo(ForgeDirection from) {
		return new FluidTankInfo[] { hydrogenTank.getInfo()};
	}
	
	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);
		this.hydrogenTank.readFromNBT(tag);
		this.storage.readFromNBT(tag);
		this.temperature = tag.getDouble("temperature");
	}

	public void writeToNBT(NBTTagCompound tag) {
		super.writeToNBT(tag);
		this.hydrogenTank.writeToNBT(tag);
		this.storage.writeToNBT(tag);
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

}
