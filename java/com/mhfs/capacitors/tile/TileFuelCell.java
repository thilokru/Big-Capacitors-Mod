package com.mhfs.capacitors.tile;

import com.mhfs.capacitors.Fluids;
import com.mhfs.capacitors.blocks.BlockFuelCell;
import com.mhfs.capacitors.misc.IRotatable;
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

public class TileFuelCell extends TileEntity implements IFluidHandler, INeighbourEnergyHandler, IRotatable {
	
	private long energy;
	private FluidTank hydrogen, oxygen, water;
	
	public final static long MAX_ENERGY = 120000, MAX_TRANSFER = 800;
	
	public TileFuelCell(){
		hydrogen = new FluidTank(2000);
		oxygen = new FluidTank(2000);
		water = new FluidTank(2000);
	}
	
	public void updateEntity(){
		if(worldObj.isRemote)return;
		long en = Math.min(80, energy);
		FluidStack wa = water.drain(1, false);
		if(en == 80 && wa != null && wa.amount == 1){
			energy -= 80;
			water.drain(1, true);
			hydrogen.fill(new FluidStack(Fluids.gasHydrogen, 10), true);
		}
		this.markDirty();
		this.worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
	}
	
	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);

		this.water.readFromNBT(tag.getCompoundTag("water"));
		this.hydrogen.readFromNBT(tag.getCompoundTag("hydrogen"));
		this.energy = tag.getLong("energy");
	}

	public void writeToNBT(NBTTagCompound tag) {
		super.writeToNBT(tag);

		NBTTagCompound waterNBT = new NBTTagCompound();
		water.writeToNBT(waterNBT);
		tag.setTag("water", waterNBT);
		
		NBTTagCompound hydrogenNBT = new NBTTagCompound();
		hydrogen.writeToNBT(hydrogenNBT);
		tag.setTag("hydrogen", hydrogenNBT);
		
		tag.setLong("energy", energy);
	}

	public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity pkt) {
		this.readFromNBT(pkt.func_148857_g());
	}

	public Packet getDescriptionPacket() {
		NBTTagCompound tag = new NBTTagCompound();
		writeToNBT(tag);
		return new S35PacketUpdateTileEntity(xCoord, yCoord, zCoord, 1, tag);
	}

	@Override
	public ForgeDirection getRotation() {
		return ((BlockFuelCell) worldObj.getBlock(xCoord, yCoord, zCoord)).getOrientation(worldObj, xCoord, yCoord, zCoord);
	}

	@Override
	public int fill(ForgeDirection from, FluidStack resource, boolean doFill) {
		if(!canFill(from, resource.getFluid()))return 0;
		return water.fill(resource, doFill);
	}

	@Override
	public FluidStack drain(ForgeDirection from, FluidStack resource, boolean doDrain) {
		if(!canDrain(from, resource.getFluid()))return null;
		FluidTank tank = getTankForDirection(from);
		if(tank.getFluidAmount() == 0 || tank.getFluid().getFluid() != resource.getFluid())return null;
		return tank.drain(resource.amount, doDrain);
	}

	@Override
	public FluidStack drain(ForgeDirection from, int maxDrain, boolean doDrain) {
		if(!canDrain(from, null))return null;
		FluidTank tank = getTankForDirection(from);
		if(tank.getFluidAmount() == 0)return null;
		return tank.drain(maxDrain, doDrain);
	}

	@Override
	public boolean canFill(ForgeDirection from, Fluid fluid) {
		return from == ForgeDirection.UP && fluid.equals(Fluids.fluidDestilledWater);
	}

	@Override
	public boolean canDrain(ForgeDirection from, Fluid fluid) {
		FluidTank tank = getTankForDirection(from);
		if(tank == null)return false;
		return fluid == null || tank.getFluid().getFluid() == fluid;
	}
	
	private FluidTank getTankForDirection(ForgeDirection from){
		ForgeDirection orientation = getRotation();
		ForgeDirection hydrOrientation = orientation.getRotation(ForgeDirection.UP);
		ForgeDirection oxOrientation = hydrOrientation.getOpposite();
		if(from == oxOrientation){
			return oxygen;
		}else if(from == hydrOrientation){
			return hydrogen;
		}
		return null;
	}

	@Override
	public FluidTankInfo[] getTankInfo(ForgeDirection from) {
		return new FluidTankInfo[]{water.getInfo(), hydrogen.getInfo(), oxygen.getInfo()};
	}

	public FluidTank getInputTank() {
		return water;
	}

	public FluidTank getHydrogenTank() {
		return hydrogen;
	}

	@Override
	public long getNeed() {
		return Math.min(MAX_TRANSFER, MAX_ENERGY - energy);
	}

	@Override
	public long getMaxTransfer() {
		return MAX_TRANSFER;
	}

	@Override
	public long getEnergyStored() {
		return energy;
	}

	@Override
	public long getMaxEnergyStored() {
		return MAX_ENERGY;
	}

	@Override
	public long drain(long amount) {
		return 0;
	}

	@Override
	public long fill(long amount) {
		long accepted = Math.min(getNeed(), amount);
		energy += accepted;
		if(energy > MAX_ENERGY){
			energy = MAX_ENERGY;
		}
		return accepted;
	}

}
