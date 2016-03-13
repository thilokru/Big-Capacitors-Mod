package com.mhfs.capacitors.tile.destillery;

import com.mhfs.capacitors.blocks.IOrientedBlock;
import com.mhfs.capacitors.misc.IRotatable;
import cofh.api.energy.IEnergyReceiver;
import net.minecraft.block.Block;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumFacing.Axis;
import net.minecraft.util.ITickable;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidHandler;
import net.minecraftforge.fluids.IFluidTank;

public class TileDistillery extends TileEntity implements IFluidHandler, IRotatable, IEnergyReceiver, ITickable {

	public final static int MAX_RF_PER_TICK = 80;
	public final static int RF_CAPACITY = 15000;
	public final static int TANK_CAPCITY = 2000;

	private FluidTank input, output;
	private int energy;

	public TileDistillery() {
		super();
		input = new FluidTank(TANK_CAPCITY);
		output = new FluidTank(TANK_CAPCITY);
	}

	@Override
	public void update() {
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
		this.worldObj.markBlockForUpdate(this.pos);
	}

	@Override
	public int fill(EnumFacing from, FluidStack resource, boolean doFill) {
		if (canFill(from, resource.getFluid())) {
			int filled = input.fill(resource, doFill);
			return filled;
		}
		return 0;
	}

	@Override
	public FluidStack drain(EnumFacing from, FluidStack resource,
			boolean doDrain) {
		if (canDrain(from, resource.getFluid())) {
			return output.drain(resource.amount, doDrain);
		}
		return null;
	}

	@Override
	public FluidStack drain(EnumFacing from, int maxDrain, boolean doDrain) {
		if (canDrain(from, null)) {
			return output.drain(maxDrain, doDrain);
		}
		return null;
	}

	@Override
	public boolean canFill(EnumFacing from, Fluid fluid) {
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
	public boolean canDrain(EnumFacing from, Fluid fluid) {
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
	public FluidTankInfo[] getTankInfo(EnumFacing from) {
		return new FluidTankInfo[] { input.getInfo(), output.getInfo() };
	}

	private IFluidTank getTankFromDirection(EnumFacing from) {
		EnumFacing orientation = getRotation();
		if(orientation == null)return null;
		EnumFacing left = orientation.rotateAround(Axis.Y);
		EnumFacing right = left.getOpposite();

		if (from == left) {
			return input;
		} else if (from == right) {
			return output;
		} else {
			return null;
		}
	}

	public EnumFacing getRotation() {
		Block block = worldObj.getBlockState(pos).getBlock();
		if (block instanceof IOrientedBlock) {
			IOrientedBlock ori = (IOrientedBlock) block;
			return ori.getOrientation(worldObj, pos);
		} else {
			return null;
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

		this.energy = tag.getInteger("energy");
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
		this.readFromNBT(pkt.getNbtCompound());
	}

	public Packet<?> getDescriptionPacket() {
		NBTTagCompound tag = new NBTTagCompound();
		writeToNBT(tag);
		return new S35PacketUpdateTileEntity(pos, 1, tag);
	}

	public IFluidTank getInputTank() {
		return input;
	}
	
	public IFluidTank getOutputTank() {
		return output;
	}
	
	public long getEnergyStored(){
		return this.energy;
	}
	
	public long getMaxEnergyStored(){
		return RF_CAPACITY;
	}

	@Override
	public int getEnergyStored(EnumFacing from) {
		return energy;
	}

	@Override
	public int getMaxEnergyStored(EnumFacing from) {
		return RF_CAPACITY;
	}

	@Override
	public boolean canConnectEnergy(EnumFacing from) {
		return from == EnumFacing.DOWN;
	}

	@Override
	public int receiveEnergy(EnumFacing from, int maxReceive, boolean simulate) {
		int amount = Math.min(MAX_RF_PER_TICK, Math.min(RF_CAPACITY - energy, maxReceive));
		if(!simulate){
			energy += amount;
		}
		return amount;
	}
}
