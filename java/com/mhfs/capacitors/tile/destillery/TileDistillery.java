package com.mhfs.capacitors.tile.destillery;

import cofh.api.energy.EnergyStorage;
import cofh.api.energy.IEnergyReceiver;

import com.mhfs.capacitors.blocks.IOrientedBlock;

import net.minecraft.block.Block;
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
import net.minecraftforge.fluids.IFluidTank;

public class TileDistillery extends TileEntity implements IFluidHandler,
		IEnergyReceiver {

	public final static int MAX_RF_PER_TICK = 80;
	public final static int RF_CAPACITY = 15000;
	public final static int TANK_CAPCITY = 2000;

	private FluidTank input, output;
	private EnergyStorage storage;

	public TileDistillery() {
		input = new FluidTank(TANK_CAPCITY);
		output = new FluidTank(TANK_CAPCITY);
		storage = new EnergyStorage(RF_CAPACITY, MAX_RF_PER_TICK);
	}

	@Override
	public void updateEntity() {
		if (worldObj.isRemote)
			return;
		forceTransmit();
		FluidStack inputStack = input.getFluid();
		if (inputStack == null)
			return;
		DestilleryRecipe recipe = DestilleryRecipeRegistry.forInput(inputStack
				.getFluid());
		if (recipe == null)
			return;
		if (output.getFluid() != null
				&& output.getFluid().getFluid() != recipe.getOutput().getFluid())
			return;
		int stepsWithRF = Math.min(MAX_RF_PER_TICK, storage.getEnergyStored())
				/ recipe.getRFCost();

		int stepsWithFluidIn = inputStack.amount / recipe.getInput().amount;
		int stepsWithFluidOut = (output.getCapacity() - output.getFluidAmount())
				/ recipe.getOutput().amount;

		int todo = Math.min(stepsWithRF,
				Math.min(stepsWithFluidIn, stepsWithFluidOut));
		input.drain(todo * recipe.getInput().amount, true);

		output.fill(
				new FluidStack(recipe.getOutput(), todo * recipe.getOutput().amount), true);
		storage.extractEnergy(todo * recipe.getRFCost(), false);
	}
	
	private void forceTransmit(){
		this.markDirty();
		this.worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
	}

	@Override
	public int fill(ForgeDirection from, FluidStack resource, boolean doFill) {
		if (canFill(from, resource.getFluid())) {
			int filled = input.fill(resource, doFill);
			return filled;
		}
		return 0;
	}

	@Override
	public FluidStack drain(ForgeDirection from, FluidStack resource,
			boolean doDrain) {
		if (canDrain(from, resource.getFluid())) {
			return output.drain(resource.amount, doDrain);
		}
		return null;
	}

	@Override
	public FluidStack drain(ForgeDirection from, int maxDrain, boolean doDrain) {
		if (canDrain(from, null)) {
			return output.drain(maxDrain, doDrain);
		}
		return null;
	}

	@Override
	public boolean canFill(ForgeDirection from, Fluid fluid) {
		IFluidTank tank = getTankFromDirection(from);
		if (tank == null || tank == output) {
			return false;
		} else {
			if (tank.getFluid() == null)
				return true;
			Fluid tankFluid = tank.getFluid().getFluid();
			return tankFluid == fluid;
		}
	}

	@Override
	public boolean canDrain(ForgeDirection from, Fluid fluid) {
		IFluidTank tank = getTankFromDirection(from);
		if (tank == null || tank == input) {
			return false;
		} else {
			if (tank.getFluid() == null)
				return false;
			if (fluid == null)
				return true;
			Fluid tankFluid = tank.getFluid().getFluid();
			return tankFluid == fluid;
		}
	}

	@Override
	public FluidTankInfo[] getTankInfo(ForgeDirection from) {
		return new FluidTankInfo[] { input.getInfo(), output.getInfo() };
	}

	@Override
	public boolean canConnectEnergy(ForgeDirection from) {
		return getTankFromDirection(from) == null;
	}

	@Override
	public int receiveEnergy(ForgeDirection from, int maxReceive,
			boolean simulate) {
		return storage.receiveEnergy(maxReceive, simulate);
	}

	@Override
	public int getEnergyStored(ForgeDirection from) {
		return storage.getEnergyStored();
	}

	@Override
	public int getMaxEnergyStored(ForgeDirection from) {
		return storage.getMaxEnergyStored();
	}

	private IFluidTank getTankFromDirection(ForgeDirection from) {
		ForgeDirection orientation = getOrientation();
		ForgeDirection left = orientation.getRotation(ForgeDirection.DOWN);
		ForgeDirection right = left.getOpposite();

		if (from == left) {
			return input;
		} else if (from == right) {
			return output;
		} else {
			return null;
		}
	}

	public ForgeDirection getOrientation() {
		Block block = worldObj.getBlock(xCoord, yCoord, zCoord);
		if (block instanceof IOrientedBlock) {
			IOrientedBlock ori = (IOrientedBlock) block;
			return ori.getOrientation(worldObj, xCoord, yCoord, zCoord);
		} else {
			return ForgeDirection.UNKNOWN;
		}
	}

	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);

		FluidTank inputTank = new FluidTank(TANK_CAPCITY);
		inputTank.readFromNBT(tag.getCompoundTag("input"));
		this.input = inputTank;

		FluidTank outputTank = new FluidTank(TANK_CAPCITY);
		outputTank.readFromNBT(tag.getCompoundTag("output"));
		this.output = outputTank;

		EnergyStorage energyStorage = new EnergyStorage(RF_CAPACITY,
				MAX_RF_PER_TICK);
		energyStorage.readFromNBT(tag.getCompoundTag("energy"));
		this.storage = energyStorage;
	}

	public void writeToNBT(NBTTagCompound tag) {
		super.writeToNBT(tag);

		NBTTagCompound inputTag = new NBTTagCompound();
		input.writeToNBT(inputTag);
		tag.setTag("input", inputTag);

		NBTTagCompound outputTag = new NBTTagCompound();
		output.writeToNBT(outputTag);
		tag.setTag("output", outputTag);

		NBTTagCompound energyTag = new NBTTagCompound();
		storage.writeToNBT(energyTag);
		tag.setTag("energy", energyTag);
	}

	public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity pkt) {
		this.readFromNBT(pkt.func_148857_g());
	}

	public Packet getDescriptionPacket() {
		NBTTagCompound tag = new NBTTagCompound();
		writeToNBT(tag);
		return new S35PacketUpdateTileEntity(xCoord, yCoord, zCoord, 1, tag);
	}

	public FluidStack getInputStack() {
		return input.getFluid();
	}
	
	public FluidStack getOutputStack() {
		return output.getFluid();
	}
}
