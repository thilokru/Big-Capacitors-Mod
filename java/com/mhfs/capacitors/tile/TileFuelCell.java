package com.mhfs.capacitors.tile;

import com.mhfs.capacitors.Fluids;
import com.mhfs.capacitors.blocks.BlockFuelCell;
import com.mhfs.capacitors.misc.IRotatable;
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

public class TileFuelCell extends TileEntity implements IFluidHandler, IEnergyReceiver, IRotatable, ITickable{
	
	private int energy;
	private FluidTank hydrogen, oxygen, water;
	
	public final static int MAX_ENERGY = 120000, MAX_TRANSFER = 800;
	
	public TileFuelCell(){
		hydrogen = new FluidTank(2000);
		oxygen = new FluidTank(2000);
		water = new FluidTank(2000);
	}
	
	public void update(){
		if(worldObj.isRemote)return;
		long en = Math.min(80, energy);
		FluidStack wa = water.drain(1, false);
		if(en == 80 && wa != null && wa.amount == 1){
			energy -= 80;
			water.drain(1, true);
			hydrogen.fill(new FluidStack(Fluids.gasHydrogen, 10), true);
		}
		this.markDirty();
		this.worldObj.markBlockForUpdate(this.pos);
	}
	
	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);

		this.water.readFromNBT(tag.getCompoundTag("water"));
		this.hydrogen.readFromNBT(tag.getCompoundTag("hydrogen"));
		this.energy = tag.getInteger("energy");
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
		this.readFromNBT(pkt.getNbtCompound());
	}

	public Packet<?> getDescriptionPacket() {
		NBTTagCompound tag = new NBTTagCompound();
		writeToNBT(tag);
		return new S35PacketUpdateTileEntity(this.pos, 1, tag);
	}

	@Override
	public EnumFacing getRotation() {
		return ((BlockFuelCell) this.blockType).getOrientation(worldObj, this.pos);
	}

	@Override
	public int fill(EnumFacing from, FluidStack resource, boolean doFill) {
		if(!canFill(from, resource.getFluid()))return 0;
		return water.fill(resource, doFill);
	}

	@Override
	public FluidStack drain(EnumFacing from, FluidStack resource, boolean doDrain) {
		if(!canDrain(from, resource.getFluid()))return null;
		FluidTank tank = getTankForDirection(from);
		if(tank.getFluidAmount() == 0 || tank.getFluid().getFluid() != resource.getFluid())return null;
		return tank.drain(resource.amount, doDrain);
	}

	@Override
	public FluidStack drain(EnumFacing from, int maxDrain, boolean doDrain) {
		if(!canDrain(from, null))return null;
		FluidTank tank = getTankForDirection(from);
		if(tank.getFluidAmount() == 0)return null;
		return tank.drain(maxDrain, doDrain);
	}

	@Override
	public boolean canFill(EnumFacing from, Fluid fluid) {
		return from == EnumFacing.UP && fluid.equals(Fluids.fluidDestilledWater);
	}

	@Override
	public boolean canDrain(EnumFacing from, Fluid fluid) {
		FluidTank tank = getTankForDirection(from);
		if(tank == null)return false;
		return fluid == null || tank.getFluid().getFluid() == fluid;
	}
	
	private FluidTank getTankForDirection(EnumFacing from){
		EnumFacing orientation = getRotation();
		EnumFacing hydrOrientation = orientation.rotateY();
		EnumFacing oxOrientation = hydrOrientation.getOpposite();
		if(from == oxOrientation){
			return oxygen;
		}else if(from == hydrOrientation){
			return hydrogen;
		}
		return null;
	}

	@Override
	public FluidTankInfo[] getTankInfo(EnumFacing from) {
		return new FluidTankInfo[]{water.getInfo(), hydrogen.getInfo(), oxygen.getInfo()};
	}

	public FluidTank getInputTank() {
		return water;
	}

	public FluidTank getHydrogenTank() {
		return hydrogen;
	}


	@Override
	public int getEnergyStored(EnumFacing from) {
		return energy;
	}

	@Override
	public int getMaxEnergyStored(EnumFacing from) {
		return MAX_ENERGY;
	}

	@Override
	public boolean canConnectEnergy(EnumFacing from) {
		return from == EnumFacing.DOWN;
	}

	@Override
	public int receiveEnergy(EnumFacing from, int maxReceive, boolean simulate) {
		int amount = Math.min(MAX_TRANSFER, Math.min(MAX_ENERGY - energy, maxReceive));
		if(!simulate){
			energy += amount;
		}
		return amount;
	}

}
