package com.mhfs.capacitors.tile.destillery;

import com.mhfs.capacitors.blocks.IOrientedBlock;
import com.mhfs.capacitors.misc.IRotatable;
import com.mhfs.capacitors.tile.lux.INeighbourEnergyHandler;

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

public class TileDistillery extends TileEntity implements IFluidHandler, IRotatable, INeighbourEnergyHandler {

	public final static int MAX_RF_PER_TICK = 80;
	public final static int RF_CAPACITY = 15000;
	public final static int TANK_CAPCITY = 2000;

	private FluidTank input, output;
	private long energy;

	public TileDistillery() {
		super();
		input = new FluidTank(TANK_CAPCITY);
		output = new FluidTank(TANK_CAPCITY);
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
		int stepsWithRF = (int) (Math.min(MAX_RF_PER_TICK, this.energy) / recipe.getRFCost());

		int stepsWithFluidIn = inputStack.amount / recipe.getInput().amount;
		int stepsWithFluidOut = (output.getCapacity() - output.getFluidAmount())
				/ recipe.getOutput().amount;

		int todo = Math.min(stepsWithRF,
				Math.min(stepsWithFluidIn, stepsWithFluidOut));
		input.drain(todo * recipe.getInput().amount, true);

		output.fill(
				new FluidStack(recipe.getOutput(), todo * recipe.getOutput().amount), true);
		this.energy -= todo * recipe.getRFCost();
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

	private IFluidTank getTankFromDirection(ForgeDirection from) {
		ForgeDirection orientation = getRotation();
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

	public ForgeDirection getRotation() {
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

		this.energy = tag.getLong("energy");
	}

	public void writeToNBT(NBTTagCompound tag) {
		super.writeToNBT(tag);

		NBTTagCompound inputTag = new NBTTagCompound();
		input.writeToNBT(inputTag);
		tag.setTag("input", inputTag);

		NBTTagCompound outputTag = new NBTTagCompound();
		output.writeToNBT(outputTag);
		tag.setTag("output", outputTag);

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

	public IFluidTank getInputTank() {
		return input;
	}
	
	public IFluidTank getOutputTank() {
		return output;
	}

	@Override
	public long getNeed() {
		return Math.min(MAX_RF_PER_TICK, RF_CAPACITY - this.energy);
	}
	
	public long getEnergyStored(){
		return this.energy;
	}
	
	public long getMaxEnergyStored(){
		return RF_CAPACITY;
	}

	@Override
	public long getMaxTransfer() {
		return MAX_RF_PER_TICK;
	}

	@Override
	public long drain(long amount) {
		return 0;
	}

	@Override
	public long fill(long amount) {
		this.energy += amount;
		if(this.energy > RF_CAPACITY)this.energy = RF_CAPACITY;
		return amount;
	}
}
